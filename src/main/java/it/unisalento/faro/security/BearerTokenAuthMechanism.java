package it.unisalento.faro.security;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class BearerTokenAuthMechanism implements HttpAuthenticationMechanism {

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context,
                                              IdentityProviderManager identityProviderManager) {
        String authHeader = context.request().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // nessun token, lascia passare (sarà la policy a decidere)
            return Uni.createFrom().optional(Optional.empty());
        }

        String token = authHeader.substring(7).trim();

        return identityProviderManager.authenticate(
                new TokenAuthenticationRequest(
                        new io.quarkus.security.credential.TokenCredential(token, "bearer")
                )
        );
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().item(
                new ChallengeData(401, "WWW-Authenticate", "Bearer realm=\"FARO\"")
        );
    }

    @Override
    public Set<Class<? extends io.quarkus.security.identity.request.AuthenticationRequest>> getCredentialTypes() {
        return Collections.singleton(TokenAuthenticationRequest.class);
    }
}