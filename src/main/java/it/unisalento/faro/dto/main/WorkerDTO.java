package it.unisalento.faro.dto.main;

import java.util.ArrayList;

public class WorkerDTO extends UserDTO {

    private ArrayList<String> authorizedAreaIds;

    public ArrayList<String> getAuthorizedAreaIds() {
        return authorizedAreaIds;
    }

    public void setAuthorizedAreaIds(ArrayList<String> authorizedAreaIds) {
        this.authorizedAreaIds = authorizedAreaIds;
    }
}