package it.unisalento.faro.dto.main;

import java.util.List;

public class WorkersListDTO {

    private List<WorkerDTO> workersList;

    public List<WorkerDTO> getWorkersList() {
        return workersList;
    }

    public void setWorkersList(List<WorkerDTO> workersList) {
        this.workersList = workersList;
    }
}
