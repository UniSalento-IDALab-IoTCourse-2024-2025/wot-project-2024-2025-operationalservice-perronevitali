package it.unisalento.faro.dto.messagesDTO;

public class TaskAssignedMessage {
    private String taskId;
    private String description;
    private String areaId;
    private String areaName;
    private String assignedAt;
    private String riskDescription;

    public TaskAssignedMessage() {}
    public TaskAssignedMessage(String taskId, String description,
                               String areaId, String areaName,
                               String assignedAt, String riskDescription) {
        this.taskId = taskId;
        this.description = description;
        this.areaId = areaId;
        this.areaName = areaName;
        this.assignedAt = assignedAt;
        this.riskDescription = riskDescription;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public String getAssignedAt() { return assignedAt; }
    public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }

    public String getRiskDescription() { return riskDescription; }
    public void setRiskDescription(String riskDescription) { this.riskDescription = riskDescription; }
}
