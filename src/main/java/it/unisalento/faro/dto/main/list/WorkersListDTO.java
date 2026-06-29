package it.unisalento.faro.dto.main.list;

import it.unisalento.faro.dto.main.WorkerDTO;

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
