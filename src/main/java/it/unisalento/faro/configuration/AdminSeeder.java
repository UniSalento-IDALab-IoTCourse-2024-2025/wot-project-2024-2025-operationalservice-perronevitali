package it.unisalento.faro.configuration;

import it.unisalento.faro.domain.Admin;
import it.unisalento.faro.domain.Role;
import it.unisalento.faro.repositories.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;

@ApplicationScoped
public class AdminSeeder {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    void onStart(@Observes StartupEvent event) {
        long adminCount = userRepository.count("_t", "admin");
        if (adminCount == 0) {
            try {
                Admin admin = new Admin();
                admin.setNome("Default");
                admin.setCognome("Admin");
                admin.setEmail("admin@faro.it");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setManagedAreaId(null);
                userRepository.persist(admin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}