package it.unisalento.faro.dto.messagesDTO;

public class AreaSafeMessage {
    private String areaId;
    private String areaName;

    public AreaSafeMessage() {}
    public AreaSafeMessage(String areaId, String areaName) {
        this.areaId = areaId;
        this.areaName = areaName;
    }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
}