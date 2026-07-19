package it.unisalento.faro.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(value = "admin")
@MongoEntity(collection = "users")
public class Admin extends User {
    private String managedAreaId;

    public String getManagedAreaId() {
        return managedAreaId;
    }
    public void setManagedAreaId(String managedAreaId) {
        this.managedAreaId = managedAreaId;
    }

    /** true se l'admin non ha restrizioni di scope (vede/gestisce tutte le aree) */
    public boolean isGlobal() {
        return managedAreaId == null;
    }
}