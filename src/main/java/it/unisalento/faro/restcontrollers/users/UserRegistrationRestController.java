package it.unisalento.faro.restcontrollers.users;

import it.unisalento.faro.dto.login_and_registration.WorkerRegistrationDTO;
import it.unisalento.faro.dto.main.WorkerDTO;
import it.unisalento.faro.dto.main.list.WorkersListDTO;
import it.unisalento.faro.dto.responseDTO.RegistrationResponseDTO;
import it.unisalento.faro.exceptions.EmailAlreadyExistsException;
import it.unisalento.faro.service.users.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/registration")
public class UserRegistrationRestController {

    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody WorkerRegistrationDTO registrationDTO) {
        RegistrationResponseDTO resultDTO = new RegistrationResponseDTO();

        try {
            WorkerDTO registered = workerService.register(registrationDTO);

            List<WorkerDTO> list = new ArrayList<>();
            list.add(registered);

            WorkersListDTO listDTO = new WorkersListDTO();
            listDTO.setWorkersList(list);

            resultDTO.setResult(RegistrationResponseDTO.OK);
            resultDTO.setMessage("L'utente è stato registrato con successo");
            resultDTO.setUsers(listDTO);
            return ResponseEntity.ok(resultDTO);
        } catch (EmailAlreadyExistsException e) {
            resultDTO.setResult(RegistrationResponseDTO.EMAIL_ALREADY_EXIST);
            resultDTO.setMessage("La mail è già associata ad un altro utente");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resultDTO);
        }
    }
}