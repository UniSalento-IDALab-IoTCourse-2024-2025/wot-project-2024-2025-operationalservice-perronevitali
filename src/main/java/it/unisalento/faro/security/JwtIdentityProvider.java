package it.unisalento.faro.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import it.unisalento.faro.domain.Admin;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.repositories.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class JwtIdentityProvider implements IdentityProvider<TokenAuthenticationRequest> {

    @Inject
    JwtUtilities jwtUtilities;

    @Inject
    UserRepository userRepository;

    @Override
    public Class<TokenAuthenticationRequest> getRequestType() {
        return TokenAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TokenAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        String token = request.getToken().getToken();
        String username = jwtUtilities.extractUsername(token);

        if (username == null || !jwtUtilities.validateToken(token, username)) {
            return Uni.createFrom().failure(
                    new AuthenticationFailedException("Token JWT non valido o scaduto")
            );
        }

        return context.runBlocking(() -> {
            Optional<User> userOptional = userRepository.findByEmail(username);

            if (userOptional.isEmpty()) {
                throw new AuthenticationFailedException("Utente non trovato: " + username);
            }

            User user = userOptional.get();

            String ruolo;
            if (user instanceof Admin) {
                ruolo = "ADMIN";
            } else {
                ruolo = "WORKER";
            }

            return QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(username))
                    .addRole(ruolo)
                    .build();
        });
    }
}