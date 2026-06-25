package it.unisalento.faro.dto.otherDTO;

public class PositionUpdateDTO {

    private String areaId;
    private long timestamp;

    public String getAreaId() {
        return areaId;
    }
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}