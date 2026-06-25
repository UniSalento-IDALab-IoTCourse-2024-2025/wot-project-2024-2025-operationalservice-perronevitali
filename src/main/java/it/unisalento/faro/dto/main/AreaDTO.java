package it.unisalento.faro.dto.main;

import java.util.ArrayList;

public class AreaDTO {
    private String id;
    private String name;
    private String beaconMAC;
    private double thresholdTemperature;
    private double thresholdHumidity;
    private String ipRaspberry;
    private double totalDangerIndex;
    private int status;
    private double currentTemperature;
    private double currentHumidity;
    private ArrayList<String> workerIdsInArea;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeaconMAC() {
        return beaconMAC;
    }

    public void setBeaconMAC(String beaconMAC) {
        this.beaconMAC = beaconMAC;
    }

    public double getThresholdTemperature() {
        return thresholdTemperature;
    }

    public void setThresholdTemperature(double thresholdTemperature) {
        this.thresholdTemperature = thresholdTemperature;
    }

    public double getThresholdHumidity() {
        return thresholdHumidity;
    }

    public void setThresholdHumidity(double thresholdHumidity) {
        this.thresholdHumidity = thresholdHumidity;
    }

    public String getIpRaspberry() {
        return ipRaspberry;
    }

    public void setIpRaspberry(String ipRaspberry) {
        this.ipRaspberry = ipRaspberry;
    }

    public double getTotalDangerIndex() {
        return totalDangerIndex;
    }

    public void setTotalDangerIndex(double totalDangerIndex) {
        this.totalDangerIndex = totalDangerIndex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public double getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(double currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public ArrayList<String> getWorkerIdsInArea() {
        return workerIdsInArea;
    }

    public void setWorkerIdsInArea(ArrayList<String> workerIdsInArea) {
        this.workerIdsInArea = workerIdsInArea;
    }
}