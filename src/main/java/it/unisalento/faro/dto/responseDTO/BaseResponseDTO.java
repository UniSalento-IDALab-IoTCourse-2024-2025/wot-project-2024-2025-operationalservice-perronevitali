package it.unisalento.faro.dto.responseDTO;

public abstract class BaseResponseDTO {
    public static final int OK = 0;
    public static final int NOT_FOUND = 1;
    public static final int CONFLICT = 2;

    private int result;
    private String message;

    public int getResult() { return result; }
    public void setResult(int result) { this.result = result; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}