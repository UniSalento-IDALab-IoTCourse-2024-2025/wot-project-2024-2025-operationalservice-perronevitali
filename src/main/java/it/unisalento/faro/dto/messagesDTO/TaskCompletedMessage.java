package it.unisalento.faro.dto.messagesDTO;

public class TaskCompletedMessage {
    private String taskId;
    private String completedAt;

    public TaskCompletedMessage() {}
    public TaskCompletedMessage(String taskId, String completedAt) {
        this.taskId = taskId;
        this.completedAt = completedAt;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}
