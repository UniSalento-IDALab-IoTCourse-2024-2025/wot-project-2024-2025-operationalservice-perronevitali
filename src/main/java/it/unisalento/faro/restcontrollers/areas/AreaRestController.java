package it.unisalento.faro.restcontrollers.areas;

import it.unisalento.faro.dto.main.AreaDTO;
import it.unisalento.faro.dto.main.list.AreasListDTO;
import it.unisalento.faro.dto.main.WorkerDTO;
import it.unisalento.faro.dto.main.list.WorkersListDTO;
import it.unisalento.faro.dto.responseDTO.AreaResponseDTO;
import it.unisalento.faro.exceptions.AreaNotFoundException;
import it.unisalento.faro.service.areas.AreaService;
import it.unisalento.faro.service.users.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaRestController {

    @Autowired
    AreaService areaService;

    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        List<AreaDTO> areas = areaService.getAllAreas();

        AreasListDTO listDTO = new AreasListDTO();
        listDTO.setAreasList(areas);

        responseDTO.setResult(AreaResponseDTO.OK);
        responseDTO.setMessage("Ecco tutte le aree");
        responseDTO.setAreas(listDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable String id) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            AreaDTO dto = areaService.getAreaById(id);

            List<AreaDTO> list = new ArrayList<>();
            list.add(dto);

            AreasListDTO listDTO = new AreasListDTO();
            listDTO.setAreasList(list);

            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage("Ecco l'area richiesta");
            responseDTO.setAreas(listDTO);
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody AreaDTO areaDto) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        AreaDTO created = areaService.createArea(areaDto);

        List<AreaDTO> list = new ArrayList<>();
        list.add(created);

        AreasListDTO listDTO = new AreasListDTO();
        listDTO.setAreasList(list);

        responseDTO.setResult(AreaResponseDTO.OK);
        responseDTO.setMessage("Area creata con successo");
        responseDTO.setAreas(listDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@PathVariable String id, @RequestBody AreaDTO areaDto) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            AreaDTO updated = areaService.updateAreaById(id, areaDto);

            List<AreaDTO> list = new ArrayList<>();
            list.add(updated);

            AreasListDTO listDTO = new AreasListDTO();
            listDTO.setAreasList(list);

            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage("Area aggiornata con successo");
            responseDTO.setAreas(listDTO);
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            AreaDTO deleted = areaService.deleteAreaById(id);

            List<AreaDTO> list = new ArrayList<>();
            list.add(deleted);

            AreasListDTO listDTO = new AreasListDTO();
            listDTO.setAreasList(list);

            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage("Area eliminata con successo");
            responseDTO.setAreas(listDTO);
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}/workers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWorkersInArea(@PathVariable String id) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            areaService.getAreaById(id);

            List<WorkerDTO> workers = workerService.getAllWorkersByArea(id);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(workers);

            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage("Ecco i worker nell'area");
            responseDTO.setWorkers(listDTO);
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }

    // conta worker presenti nell'area
    @RequestMapping(value = "/{id}/workers/count",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWorkerCount(@PathVariable String id) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            int count = areaService.getWorkerCount(id);
            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage(String.valueOf(count));
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }

    // lista worker autorizzati nell'area
    @RequestMapping(value = "/{id}/workers/authorized",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAuthorizedWorkers(@PathVariable String id) {
        AreaResponseDTO responseDTO = new AreaResponseDTO();

        try {
            areaService.getAreaById(id);

            List<WorkerDTO> workers = workerService.getAuthorizedWorkersByArea(id);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(workers);

            responseDTO.setResult(AreaResponseDTO.OK);
            responseDTO.setMessage("Ecco i worker autorizzati nell'area");
            responseDTO.setWorkers(listDTO);
        } catch (AreaNotFoundException e) {
            responseDTO.setResult(AreaResponseDTO.AREA_NOT_FOUND);
            responseDTO.setMessage("Area non trovata");
        }

        return ResponseEntity.ok(responseDTO);
    }
}