package it.unisalento.faro.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityBeansProducer {

    @Produces
    @ApplicationScoped
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}