package it.unisalento.faro.restcontrollers;

import it.unisalento.faro.dto.main.UserDTO;
import it.unisalento.faro.dto.main.list.UsersListDTO;
import it.unisalento.faro.dto.responseDTO.UserResponseDTO;
import it.unisalento.faro.exceptions.EmailChangeNotAllowedException;
import it.unisalento.faro.exceptions.UserNotFoundException;
import it.unisalento.faro.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    UserService userService;

    // NUOVO - elenco di tutti gli utenti (admin + worker misti): solo ADMIN.
    // E' anche l'endpoint che finora restituiva UserDTO senza campo "role" -
    // ora lo restituisce, risolvendo il problema del frontend che non poteva
    // distinguere admin da worker in questa lista.
    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        UserResponseDTO responseDTO = new UserResponseDTO();

        UsersListDTO listDTO = new UsersListDTO();
        listDTO.setUsersList(userService.getAllUsers());

        responseDTO.setResult(UserResponseDTO.OK);
        responseDTO.setMessage("Ecco tutti gli utenti");
        responseDTO.setUsers(listDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @RolesAllowed({"ADMIN", "WORKER"})
    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable String id) {
        UserResponseDTO responseDTO = new UserResponseDTO();

        try {
            UserDTO userDto = userService.getUserById(id);

            List<UserDTO> list = new ArrayList<>();
            list.add(userDto);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Ecco gli utenti richiesti");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RolesAllowed({"ADMIN", "WORKER"})
    @RequestMapping(value = "/email",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByEmail(@RequestParam("email") String rawEmail) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        String decodedEmail = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8);

        try {
            UserDTO userDto = userService.getUserByEmail(decodedEmail);

            List<UserDTO> list = new ArrayList<>();
            list.add(userDto);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Ecco gli utenti richiesti");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RolesAllowed({"ADMIN", "WORKER"})
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@PathVariable String id, @RequestBody UserDTO userDto) {
        UserResponseDTO responseDTO = new UserResponseDTO();

        try {
            UserDTO updated = userService.updateUserById(id, userDto);

            List<UserDTO> list = new ArrayList<>();
            list.add(updated);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Utente aggiornato con successo");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        } catch (EmailChangeNotAllowedException e) {
            responseDTO.setResult(UserResponseDTO.CANT_EDIT_EMAIL);
            responseDTO.setMessage("Non è possibile modificare la propria email");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RolesAllowed({"ADMIN", "WORKER"})
    @RequestMapping(value = "/email",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateByEmail(@RequestParam("email") String rawEmail, @RequestBody UserDTO userDto) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        String decodedEmail = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8);

        try {
            UserDTO updated = userService.updateUserByEmail(decodedEmail, userDto);

            List<UserDTO> list = new ArrayList<>();
            list.add(updated);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Utente aggiornato con successo");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        } catch (EmailChangeNotAllowedException e) {
            responseDTO.setResult(UserResponseDTO.CANT_EDIT_EMAIL);
            responseDTO.setMessage("Non è possibile modificare la propria email");
        }

        return ResponseEntity.ok(responseDTO);
    }

    // NUOVO - eliminazione: solo ADMIN
    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        UserResponseDTO responseDTO = new UserResponseDTO();

        try {
            UserDTO deleted = userService.deleteUserById(id);

            List<UserDTO> list = new ArrayList<>();
            list.add(deleted);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Ecco gli utenti rimossi");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/email",
            method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteByEmail(@RequestParam("email") String rawEmail) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        String decodedEmail = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8);

        try {
            UserDTO deleted = userService.deleteUserByEmail(decodedEmail);

            List<UserDTO> list = new ArrayList<>();
            list.add(deleted);

            UsersListDTO listDTO = new UsersListDTO();
            listDTO.setUsersList(list);

            responseDTO.setResult(UserResponseDTO.OK);
            responseDTO.setMessage("Ecco gli utenti rimossi");
            responseDTO.setUsers(listDTO);
        } catch (UserNotFoundException e) {
            responseDTO.setResult(UserResponseDTO.USER_NOT_FOUND);
            responseDTO.setMessage("Nessun utente trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }
}