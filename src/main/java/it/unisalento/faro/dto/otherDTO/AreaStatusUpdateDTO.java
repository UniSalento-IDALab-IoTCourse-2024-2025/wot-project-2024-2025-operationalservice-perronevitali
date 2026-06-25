package it.unisalento.faro.dto.otherDTO;

public class AreaStatusUpdateDTO {

    private String areaId;
    private int status;

    public String getAreaId() {
        return areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}