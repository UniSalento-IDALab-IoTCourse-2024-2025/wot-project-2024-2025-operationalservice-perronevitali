package it.unisalento.faro.service;

import it.unisalento.faro.domain.Admin;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.dto.main.LoginDTO;
import it.unisalento.faro.dto.main.UserDTO;
import it.unisalento.faro.exceptions.EmailChangeNotAllowedException;
import it.unisalento.faro.exceptions.UserNotFoundException;
import it.unisalento.faro.repositories.UserRepository;
import it.unisalento.faro.security.JwtUtilities;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    JwtUtilities jwtUtilities;

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

        if (userOptional.isEmpty()
                || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            return Optional.empty();
        }

        User user = userOptional.get();

        String ruolo;
        if (user instanceof Admin) {
            ruolo = "ADMIN";
        } else {
            ruolo = "WORKER";
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("ruolo", ruolo);

        return Optional.of(jwtUtilities.generateToken(user.getEmail(), claims));
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
        dto.setPassword(user.getPassword());
        return dto;
    }
}