package it.unisalento.faro.dto.messagesDTO;

import java.time.Instant;

public class FaroMessage {
    private String type;
    private Object payload;
    private String timestamp;

    public FaroMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
        this.timestamp = Instant.now().toString();
    }

    public String getType() { return type; }
    public Object getPayload() { return payload; }
    public String getTimestamp() { return timestamp; }
}

