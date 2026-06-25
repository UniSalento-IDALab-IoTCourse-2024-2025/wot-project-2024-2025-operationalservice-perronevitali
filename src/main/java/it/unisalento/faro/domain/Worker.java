package it.unisalento.faro.domain;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import java.util.ArrayList;

@BsonDiscriminator(value = "worker")
public class Worker extends User {

    private String currentAreaId;
    private ArrayList<String> authorizedAreaIds;

    public String getCurrentAreaId() {
        return currentAreaId;
    }
    public void setCurrentAreaId(String currentAreaId) {
        this.currentAreaId = currentAreaId;
    }
    public ArrayList<String> getAuthorizedAreaIds() {
        return authorizedAreaIds;
    }
    public void setAuthorizedAreaIds(ArrayList<String> authorizedAreaIds) {
        this.authorizedAreaIds = authorizedAreaIds;
    }
}