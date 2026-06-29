package it.unisalento.faro.dto.messagesDTO;

public class PositionUpdateMessage {
    private String areaId;

    public PositionUpdateMessage() {}
    public PositionUpdateMessage(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }
}
