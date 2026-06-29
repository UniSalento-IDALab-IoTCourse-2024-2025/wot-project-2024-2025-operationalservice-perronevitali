package it.unisalento.faro.dto.messagesDTO;

public class DirectMessage {
    private String senderId;
    private String text;

    public DirectMessage() {}
    public DirectMessage(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
