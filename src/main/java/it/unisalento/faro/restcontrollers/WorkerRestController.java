package it.unisalento.faro.restcontrollers;

import it.unisalento.faro.dto.main.WorkerDTO;
import it.unisalento.faro.dto.main.list.WorkersListDTO;
import it.unisalento.faro.dto.responseDTO.WorkerResponseDTO;
import it.unisalento.faro.service.WorkerService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @RolesAllowed("ADMIN")
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

    @RolesAllowed({"ADMIN", "WORKER"})
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

    @RolesAllowed({"ADMIN", "WORKER"})
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

    @RolesAllowed({"ADMIN", "WORKER"})
    @RequestMapping(value = "/by-ids",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByIds(@RequestParam("ids") String rawIds) {
        WorkerResponseDTO responseDTO = new WorkerResponseDTO();

        List<String> ids = new ArrayList<>();
        for (String id : rawIds.split(",")) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty()) {
                ids.add(trimmed);
            }
        }

        try {
            List<WorkerDTO> workers = workerService.getWorkersByIds(ids);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(workers);

            responseDTO.setResult(WorkerResponseDTO.OK);
            responseDTO.setMessage("Ecco i worker richiesti");
            responseDTO.setWorkers(listDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setResult(-1);
            responseDTO.setMessage("Uno o più id non validi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

    @RolesAllowed("ADMIN")
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

    @RolesAllowed({"ADMIN", "WORKER"})
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

    @RolesAllowed("ADMIN")
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

}