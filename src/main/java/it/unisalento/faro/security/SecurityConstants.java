package it.unisalento.faro.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecurityConstants {

    @ConfigProperty(name = "jwt.secret")
    String jwtSecret;

    public String getJwtSecret() {
        return jwtSecret;
    }
}