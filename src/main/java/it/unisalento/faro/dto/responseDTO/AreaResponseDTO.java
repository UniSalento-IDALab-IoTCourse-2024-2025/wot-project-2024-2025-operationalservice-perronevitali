package it.unisalento.faro.dto.responseDTO;

import it.unisalento.faro.dto.main.list.AreasListDTO;
import it.unisalento.faro.dto.main.list.WorkersListDTO;

public class AreaResponseDTO extends BaseResponseDTO {
    public static final int AREA_NOT_FOUND = 1;

    private AreasListDTO areas;
    private WorkersListDTO workers;

    public AreasListDTO getAreas() { return areas; }
    public void setAreas(AreasListDTO areas) { this.areas = areas; }
    public WorkersListDTO getWorkers() { return workers; }
    public void setWorkers(WorkersListDTO workers) { this.workers = workers; }
}