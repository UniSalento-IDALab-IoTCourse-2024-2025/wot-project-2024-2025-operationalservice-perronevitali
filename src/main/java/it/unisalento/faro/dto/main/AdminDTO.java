package it.unisalento.faro.dto.main;

public class AdminDTO extends UserDTO{
    private String managedAreaId;

    public String getManagedAreaId() {
        return managedAreaId;
    }

    public void setManagedAreaId(String managedAreaId) {
        this.managedAreaId = managedAreaId;
    }
}
