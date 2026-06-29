package it.unisalento.faro.dto.messagesDTO;

public class AreaAlertMessage {
    private String areaId;
    private String areaName;
    private int status;
    private double currentTemperature;
    private double currentHumidity;

    public AreaAlertMessage() {}
    public AreaAlertMessage(String areaId, String areaName, int status,
                            double currentTemperature, double currentHumidity) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.status = status;
        this.currentTemperature = currentTemperature;
        this.currentHumidity = currentHumidity;
    }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public double getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(double currentTemperature) { this.currentTemperature = currentTemperature; }

    public double getCurrentHumidity() { return currentHumidity; }
    public void setCurrentHumidity(double currentHumidity) { this.currentHumidity = currentHumidity; }
}