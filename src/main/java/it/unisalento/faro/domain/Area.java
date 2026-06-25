package it.unisalento.faro.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.util.ArrayList;

@MongoEntity(collection = "area")
public class Area {

    public static final int OK = 0;
    public static final int ALERT = 1;
    public static final int DANGER = 99;

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
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