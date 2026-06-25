package it.unisalento.faro.dto.responseDTO;

import it.unisalento.faro.dto.main.WorkersListDTO;

public class WorkerResponseDTO extends BaseResponseDTO {
    public static final int WORKER_NOT_FOUND = 1;
    public static final int EMAIL_ALREADY_EXISTS = 2;

    private WorkersListDTO workers;

    public WorkersListDTO getWorkers() { return workers; }
    public void setWorkers(WorkersListDTO workers) { this.workers = workers; }
}