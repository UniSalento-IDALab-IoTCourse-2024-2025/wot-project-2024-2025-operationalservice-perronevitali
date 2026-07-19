package it.unisalento.faro.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JwtIdentityProvider implements IdentityProvider<TokenAuthenticationRequest> {
    @Inject
    JwtUtilities jwtUtilities;

    @Override
    public Class<TokenAuthenticationRequest> getRequestType() {
        return TokenAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TokenAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        String token = request.getToken().getToken();
        String username;
        try {
            username = jwtUtilities.extractUsername(token);
        } catch (Exception e) {
            return Uni.createFrom().failure(
                    new AuthenticationFailedException("Errore parsing JWT: " + e.getMessage())
            );
        }
        if (username == null || !jwtUtilities.validateToken(token, username)) {
            return Uni.createFrom().failure(
                    new AuthenticationFailedException("Token JWT non valido o scaduto")
            );
        }
        return context.runBlocking(() -> {
            try {
                String ruolo = jwtUtilities.extractClaim(token, claims ->
                        claims.get("ruolo", String.class)
                );
                if (ruolo == null || ruolo.isEmpty()) {
                    throw new AuthenticationFailedException(
                            "Claim 'ruolo' mancante nel token JWT"
                    );
                }
                String userId = jwtUtilities.extractClaim(token, claims ->
                        claims.get("userId", String.class)
                );
                if (userId == null || userId.isEmpty()) {
                    throw new AuthenticationFailedException(
                            "Claim 'userId' mancante nel token JWT"
                    );
                }

                String managedAreaId;
                try {
                    managedAreaId = jwtUtilities.extractClaim(token, claims ->
                            claims.get("managedAreaId", String.class)
                    );
                } catch (Exception e) {
                    managedAreaId = null;
                }

                var identityBuilder = QuarkusSecurityIdentity.builder()
                        .setPrincipal(new QuarkusPrincipal(username))
                        .addRole(ruolo)
                        .addAttribute("userId", userId);

                if (managedAreaId != null) {
                    identityBuilder.addAttribute("managedAreaId", managedAreaId);
                }

                return identityBuilder.build();
            } catch (AuthenticationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new AuthenticationFailedException("Errore costruzione identity: " + e.getMessage());
            }
        });
    }
}