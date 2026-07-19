package it.unisalento.faro.restcontrollers;

import it.unisalento.faro.dto.login_and_registration.AdminRegistrationDTO;
import it.unisalento.faro.dto.main.AdminDTO;
import it.unisalento.faro.dto.main.list.AdminsListDTO;
import it.unisalento.faro.dto.responseDTO.AdminResponseDTO;
import it.unisalento.faro.exceptions.EmailAlreadyExistsException;
import it.unisalento.faro.service.AdminService;
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
@RequestMapping("/api/admins")
@RolesAllowed("ADMIN")
public class AdminRestController {

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        AdminsListDTO listDTO = new AdminsListDTO();
        listDTO.setAdminsList(adminService.getAllAdmins());

        responseDTO.setResult(AdminResponseDTO.OK);
        responseDTO.setMessage("Ecco tutti gli admin");
        responseDTO.setAdmins(listDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable String id) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        try {
            AdminDTO dto = adminService.getAdminById(id);

            List<AdminDTO> list = new ArrayList<>();
            list.add(dto);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Ecco l'admin richiesto");
            responseDTO.setAdmins(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(AdminResponseDTO.ADMIN_NOT_FOUND);
            responseDTO.setMessage("Admin non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/email",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByEmail(@RequestParam("email") String rawEmail) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();
        String email = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8);

        try {
            AdminDTO dto = adminService.getAdminByEmail(email);

            List<AdminDTO> list = new ArrayList<>();
            list.add(dto);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Ecco l'admin richiesto");
            responseDTO.setAdmins(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(AdminResponseDTO.ADMIN_NOT_FOUND);
            responseDTO.setMessage("Admin non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody AdminRegistrationDTO registrationDTO) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        try {
            AdminDTO created = adminService.createAdmin(registrationDTO);

            List<AdminDTO> list = new ArrayList<>();
            list.add(created);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Admin creato con successo");
            responseDTO.setAdmins(listDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (EmailAlreadyExistsException e) {
            responseDTO.setResult(AdminResponseDTO.EMAIL_ALREADY_EXISTS);
            responseDTO.setMessage("Email già in uso");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
        }
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@PathVariable String id, @RequestBody AdminDTO adminDto) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        try {
            AdminDTO updated = adminService.updateAdminById(id, adminDto);

            List<AdminDTO> list = new ArrayList<>();
            list.add(updated);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Admin aggiornato con successo");
            responseDTO.setAdmins(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(AdminResponseDTO.ADMIN_NOT_FOUND);
            responseDTO.setMessage("Admin non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}/area",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignArea(@PathVariable String id, @RequestBody String areaId) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        try {
            AdminDTO updated = adminService.assignArea(id, areaId);

            List<AdminDTO> list = new ArrayList<>();
            list.add(updated);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Area assegnata con successo");
            responseDTO.setAdmins(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(AdminResponseDTO.ADMIN_NOT_FOUND);
            responseDTO.setMessage("Admin non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();

        try {
            AdminDTO deleted = adminService.deleteAdminById(id);

            List<AdminDTO> list = new ArrayList<>();
            list.add(deleted);

            AdminsListDTO listDTO = new AdminsListDTO();
            listDTO.setAdminsList(list);

            responseDTO.setResult(AdminResponseDTO.OK);
            responseDTO.setMessage("Admin eliminato con successo");
            responseDTO.setAdmins(listDTO);
        } catch (Exception e) {
            responseDTO.setResult(AdminResponseDTO.ADMIN_NOT_FOUND);
            responseDTO.setMessage("Admin non trovato");
        }

        return ResponseEntity.ok(responseDTO);
    }
}