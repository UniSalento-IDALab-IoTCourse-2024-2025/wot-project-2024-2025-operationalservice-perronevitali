package it.unisalento.faro.restcontrollers.users;

import it.unisalento.faro.dto.main.WorkerDTO;
import it.unisalento.faro.dto.main.list.WorkersListDTO;
import it.unisalento.faro.dto.responseDTO.WorkerResponseDTO;
import it.unisalento.faro.service.users.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerRestController {

    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        WorkersListDTO listDTO = new WorkersListDTO();
        listDTO.setWorkersList(workerService.getAllWorkers());

        responseDTO.setResult(WorkerResponseDTO.OK);
        responseDTO.setMessage("Ecco tutti i worker");
        responseDTO.setWorkers(listDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable String id) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        try {
            WorkerDTO dto = workerService.getWorkerById(id);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(dto);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Ecco il worker richiesto");
            responseDTO.setWorkers(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/email",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByEmail(@RequestParam("email") String rawEmail) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();
        String email = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8);

        try {
            WorkerDTO dto = workerService.getWorkerByEmail(email);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(dto);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Ecco il worker richiesto");
            responseDTO.setWorkers(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@PathVariable String id, @RequestBody WorkerDTO workerDto) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        try {
            WorkerDTO updated = workerService.updateWorkerById(id, workerDto);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(updated);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Worker aggiornato con successo");
            responseDTO.setWorkers(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}/areas",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignAreas(@PathVariable String id, @RequestBody ArrayList<String> areaIds) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        try {
            WorkerDTO updated = workerService.assignAreas(id, areaIds);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(updated);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Aree assegnate con successo");
            responseDTO.setWorkers(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        try {
            WorkerDTO deleted = workerService.deleteWorkerById(id);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(deleted);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Worker eliminato con successo");
            responseDTO.setWorkers(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}/areas/authorized",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAuthorizedAreas(@PathVariable String id) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        try {
            List<String> areaIds = workerService.getAuthorizedAreaIds(id);
            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage(String.join(",", areaIds));
        } catch (Exception e) {
            responseDTO.setResult(WorkerResponseDTO.WORKER_NOT_FOUND);
            responseDTO.setMessage("Worker non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }
}