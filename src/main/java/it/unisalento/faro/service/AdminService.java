package it.unisalento.faro.service;

import io.jsonwebtoken.io.IOException;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQManager;
import it.unisalento.faro.domain.Admin;
import it.unisalento.faro.domain.Role;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.dto.login_and_registration.AdminRegistrationDTO;
import it.unisalento.faro.dto.main.AdminDTO;
import it.unisalento.faro.exceptions.EmailAlreadyExistsException;
import it.unisalento.faro.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AdminService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    RabbitMQManager rabbitMQManager;

    public List<AdminDTO> getAllAdmins() {
        List<User> admins = userRepository.list("_t", "admin");
        List<AdminDTO> result = new ArrayList<>();
        for (User user : admins) {
            AdminDTO dto = toAdminDTO(user);
            result.add(dto);
        }
        return result;
    }

    public AdminDTO getAdminById(String id) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Admin)) {
            throw new Exception("Admin non trovato");
        }
        return toAdminDTO(user);
    }

    public AdminDTO getAdminByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !(user instanceof Admin)) {
            throw new Exception("Admin non trovato");
        }
        return toAdminDTO(user);
    }

    public AdminDTO createAdmin(AdminRegistrationDTO registrationDTO) throws EmailAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByEmail(registrationDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        Admin admin = new Admin();
        admin.setNome(registrationDTO.getNome());
        admin.setCognome(registrationDTO.getCognome());
        admin.setEmail(registrationDTO.getEmail());
        admin.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        admin.setRole(Role.ADMIN);
        admin.setManagedAreaId(registrationDTO.getManagedAreaId());

        userRepository.persist(admin);

        try {
            rabbitMQManager.declareUserQueue(admin.getId());
        } catch (IOException | java.io.IOException e) {
            e.printStackTrace();
        }

        return toAdminDTO(admin);
    }

    public AdminDTO updateAdminById(String id, AdminDTO adminDto) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Admin)) {
            throw new Exception("Admin non trovato");
        }

        Admin admin = (Admin) user;
        admin.setNome(adminDto.getNome());
        admin.setCognome(adminDto.getCognome());

        userRepository.update(admin);
        return toAdminDTO(admin);
    }

    public AdminDTO assignArea(String id, String areaId) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Admin)) {
            throw new Exception("Admin non trovato");
        }

        Admin admin = (Admin) user;
        admin.setManagedAreaId(areaId);

        userRepository.update(admin);
        return toAdminDTO(admin);
    }

    public AdminDTO deleteAdminById(String id) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Admin)) {
            throw new Exception("Admin non trovato");
        }

        userRepository.deleteById(id);
        return toAdminDTO(user);
    }

    public AdminDTO toAdminDTO(User user) {
        Admin admin = (Admin) user;
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setNome(admin.getNome());
        dto.setCognome(admin.getCognome());
        dto.setEmail(admin.getEmail());
        dto.setCurrentAreaId(admin.getCurrentAreaId());
        if (admin.getRole() != null) {
            dto.setRole(admin.getRole().name());
        } else {
            dto.setRole("ADMIN");
        }
        dto.setManagedAreaId(admin.getManagedAreaId());
        return dto;
    }
}