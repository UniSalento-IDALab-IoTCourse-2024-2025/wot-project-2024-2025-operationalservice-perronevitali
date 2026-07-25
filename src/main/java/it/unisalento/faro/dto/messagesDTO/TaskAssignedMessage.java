package it.unisalento.faro.dto.messagesDTO;

// SHARED
public class TaskAssignedMessage {

    private String taskId;
    private String taskName;
    private String description;
    private String operationType;
    private String originAreaId;
    private String destinationAreaId;
    private String originAreaName;      // null se originAreaId è null
    private String destinationAreaName; // null se destinationAreaId è null
    private String itemId;
    private String substanceName;
    private double substanceQuantity;
    private String formulaVerdict;
    private double taskLwhi;
    private double areaDangerIndexSnapshot;
    private String mlVerdict;
    private boolean mlVerdictAvailable;
    private String riskDescription;

    public TaskAssignedMessage() {}

    public TaskAssignedMessage(String taskId, String taskName, String description,
                               String operationType, String originAreaId, String destinationAreaId,
                               String originAreaName, String destinationAreaName, String itemId,
                               String substanceName, double substanceQuantity,
                               String formulaVerdict, double taskLwhi,
                               double areaDangerIndexSnapshot, String mlVerdict,
                               boolean mlVerdictAvailable, String riskDescription) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.description = description;
        this.operationType = operationType;
        this.originAreaId = originAreaId;
        this.destinationAreaId = destinationAreaId;
        this.originAreaName = originAreaName;
        this.destinationAreaName = destinationAreaName;
        this.itemId = itemId;
        this.substanceName = substanceName;
        this.substanceQuantity = substanceQuantity;
        this.formulaVerdict = formulaVerdict;
        this.taskLwhi = taskLwhi;
        this.areaDangerIndexSnapshot = areaDangerIndexSnapshot;
        this.mlVerdict = mlVerdict;
        this.mlVerdictAvailable = mlVerdictAvailable;
        this.riskDescription = riskDescription;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOriginAreaId() {
        return originAreaId;
    }

    public void setOriginAreaId(String originAreaId) {
        this.originAreaId = originAreaId;
    }

    public String getDestinationAreaId() {
        return destinationAreaId;
    }

    public void setDestinationAreaId(String destinationAreaId) {
        this.destinationAreaId = destinationAreaId;
    }

    public String getOriginAreaName() {
        return originAreaName;
    }

    public void setOriginAreaName(String originAreaName) {
        this.originAreaName = originAreaName;
    }

    public String getDestinationAreaName() {
        return destinationAreaName;
    }

    public void setDestinationAreaName(String destinationAreaName) {
        this.destinationAreaName = destinationAreaName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSubstanceName() {
        return substanceName;
    }

    public void setSubstanceName(String substanceName) {
        this.substanceName = substanceName;
    }

    public double getSubstanceQuantity() {
        return substanceQuantity;
    }

    public void setSubstanceQuantity(double substanceQuantity) {
        this.substanceQuantity = substanceQuantity;
    }

    public String getFormulaVerdict() {
        return formulaVerdict;
    }

    public void setFormulaVerdict(String formulaVerdict) {
        this.formulaVerdict = formulaVerdict;
    }

    public double getTaskLwhi() {
        return taskLwhi;
    }

    public void setTaskLwhi(double taskLwhi) {
        this.taskLwhi = taskLwhi;
    }

    public double getAreaDangerIndexSnapshot() {
        return areaDangerIndexSnapshot;
    }

    public void setAreaDangerIndexSnapshot(double areaDangerIndexSnapshot) {
        this.areaDangerIndexSnapshot = areaDangerIndexSnapshot;
    }

    public String getMlVerdict() {
        return mlVerdict;
    }

    public void setMlVerdict(String mlVerdict) {
        this.mlVerdict = mlVerdict;
    }

    public boolean isMlVerdictAvailable() {
        return mlVerdictAvailable;
    }

    public void setMlVerdictAvailable(boolean mlVerdictAvailable) {
        this.mlVerdictAvailable = mlVerdictAvailable;
    }

    public String getRiskDescription() {
        return riskDescription;
    }

    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }
}