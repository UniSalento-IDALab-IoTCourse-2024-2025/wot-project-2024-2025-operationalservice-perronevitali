package it.unisalento.faro.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.unisalento.faro.domain.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, String> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}