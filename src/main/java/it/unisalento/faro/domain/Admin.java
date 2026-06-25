package it.unisalento.faro.domain;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(value = "admin")
public class Admin extends User {

    private String managedAreaId;

    public String getManagedAreaId() {
        return managedAreaId;
    }
    public void setManagedAreaId(String managedAreaId) {
        this.managedAreaId = managedAreaId;
    }
}