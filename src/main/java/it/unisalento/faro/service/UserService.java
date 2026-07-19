package it.unisalento.faro.service;

import it.unisalento.faro.configuration.rabbitmq.RabbitMQConstants;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQManager;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQMessageTypes;
import it.unisalento.faro.domain.Admin;
import it.unisalento.faro.domain.Role;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.domain.Worker;
import it.unisalento.faro.dto.login_and_registration.LoginDTO;
import it.unisalento.faro.dto.main.UserDTO;
import it.unisalento.faro.dto.messagesDTO.AreaUnauthorizedMessage;
import it.unisalento.faro.dto.messagesDTO.FaroMessage;
import it.unisalento.faro.dto.otherDTO.PositionUpdateDTO;
import it.unisalento.faro.exceptions.EmailChangeNotAllowedException;
import it.unisalento.faro.exceptions.UserNotFoundException;
import it.unisalento.faro.repositories.UserRepository;
import it.unisalento.faro.security.JwtUtilities;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    JwtUtilities jwtUtilities;

    @Inject
    RabbitMQManager rabbitMQManager;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.listAll();
        List<UserDTO> result = new ArrayList<>();

        for (User user : users) {
            UserDTO dto = toUserDTO(user);
            result.add(dto);
        }

        return result;
    }

    public UserDTO getUserById(String id) throws UserNotFoundException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return toUserDTO(user);
    }

    public UserDTO getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return toUserDTO(user);
    }

    public UserDTO updateUserById(String id, UserDTO userDto) throws UserNotFoundException, EmailChangeNotAllowedException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return applyUpdate(user, userDto);
    }

    public UserDTO updateUserByEmail(String email, UserDTO userDto) throws UserNotFoundException, EmailChangeNotAllowedException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return applyUpdate(user, userDto);
    }

    public UserDTO deleteUserById(String id) throws UserNotFoundException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
        return toUserDTO(user);
    }

    public UserDTO deleteUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(user.getId());
        return toUserDTO(user);
    }

    public Optional<String> authenticate(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            return Optional.empty();
        }

        User user = userOptional.get();
        Role role = user.getRole();
        if (role == null) {
            if (user instanceof Admin) {
                role = Role.ADMIN;
            } else {
                role = Role.WORKER;
            }
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("ruolo", role.name());

        if (role == Role.ADMIN && user instanceof Admin admin) {
            if (admin.getManagedAreaId() != null) {
                claims.put("managedAreaId", admin.getManagedAreaId());
            }
        }

        return Optional.of(jwtUtilities.generateToken(user.getEmail(), claims));
    }

    public UserDTO updateCurrentArea(String userId, String areaId, String previousAreaId) throws Exception {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }

        user.setCurrentAreaId(areaId);
        userRepository.update(user);

        boolean isUnauthorized = false;
        if (user instanceof Worker worker) {
            isUnauthorized = worker.getAuthorizedAreaIds() == null
                    || !worker.getAuthorizedAreaIds().contains(areaId);

            if (isUnauthorized) {
                try {
                    rabbitMQManager.publish(
                            RabbitMQConstants.EXCHANGE_AREAS,
                            "area." + areaId,
                            RabbitMQMessageTypes.AREA_UNAUTHORIZED,
                            new FaroMessage(
                                    RabbitMQMessageTypes.AREA_UNAUTHORIZED,
                                    new AreaUnauthorizedMessage(areaId)
                            )
                    );
                    System.out.println("AREA_UNAUTHORIZED pubblicato per area: " + areaId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            PositionUpdateDTO positionUpdate = new PositionUpdateDTO(
                    userId, areaId, previousAreaId, isUnauthorized
            );
            rabbitMQManager.publish(
                    RabbitMQConstants.EXCHANGE_AREA_UPDATES,
                    RabbitMQConstants.ROUTING_KEY_POSITION,
                    RabbitMQMessageTypes.POSITION_UPDATE,
                    positionUpdate
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toUserDTO(user);
    }

    private UserDTO applyUpdate(User user, UserDTO userDto) throws EmailChangeNotAllowedException {
        if (!user.getEmail().equals(userDto.getEmail())) {
            throw new EmailChangeNotAllowedException();
        }

        user.setNome(userDto.getNome());
        user.setCognome(userDto.getCognome());
        user.setEmail(userDto.getEmail());

        userRepository.update(user);
        return toUserDTO(user);
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setCognome(user.getCognome());
        dto.setEmail(user.getEmail());
        dto.setCurrentAreaId(user.getCurrentAreaId());
        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        } else if (user instanceof Admin) {
            dto.setRole("ADMIN");
        } else {
            dto.setRole("WORKER");
        }
        return dto;
    }
}