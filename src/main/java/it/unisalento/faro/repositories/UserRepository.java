package it.unisalento.faro.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.unisalento.faro.domain.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, String> {

    public User findById(String id) {
        return find("_id", new ObjectId(id)).firstResult();
    }

    public List<User> findByIds(List<String> ids) {
        List<ObjectId> objectIds = new ArrayList<>();
        for (String id : ids) {
            objectIds.add(new ObjectId(id));
        }
        return list(new Document("_id", new Document("$in", objectIds)));
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public boolean deleteById(String id) {
        return delete("_id", new ObjectId(id)) > 0;
    }
}