package it.unisalento.faro.dto.main;

public class PushTokenDTO {

    private String pushToken;

    public PushTokenDTO() {
    }

    public PushTokenDTO(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}