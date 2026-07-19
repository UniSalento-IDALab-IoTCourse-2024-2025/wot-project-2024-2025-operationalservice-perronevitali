package it.unisalento.faro.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import java.util.ArrayList;

@BsonDiscriminator(value = "worker")
@MongoEntity(collection = "users")
public class Worker extends User {

    private ArrayList<String> authorizedAreaIds;

    public ArrayList<String> getAuthorizedAreaIds() {
        return authorizedAreaIds;
    }

    public void setAuthorizedAreaIds(ArrayList<String> authorizedAreaIds) {
        this.authorizedAreaIds = authorizedAreaIds;
    }
}