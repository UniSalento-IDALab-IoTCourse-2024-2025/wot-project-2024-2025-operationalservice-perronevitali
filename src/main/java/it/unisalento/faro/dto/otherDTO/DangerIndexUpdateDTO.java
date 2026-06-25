package it.unisalento.faro.dto.otherDTO;

public class DangerIndexUpdateDTO {

    private String areaId;
    private double dangerIndex;

    public String getAreaId() {
        return areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
    public double getDangerIndex() {
        return dangerIndex;
    }
    public void setDangerIndex(double dangerIndex) {
        this.dangerIndex = dangerIndex;
    }
}