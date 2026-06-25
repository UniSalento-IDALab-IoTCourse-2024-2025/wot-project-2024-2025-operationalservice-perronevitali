package it.unisalento.faro.dto.main;

import java.util.ArrayList;

public class WorkerDTO extends UserDTO {

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