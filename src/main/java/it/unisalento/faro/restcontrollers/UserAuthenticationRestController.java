package it.unisalento.faro.restcontrollers;

import it.unisalento.faro.dto.main.LoginDTO;
import it.unisalento.faro.dto.responseDTO.AuthenticationResponseDTO;
import it.unisalento.faro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/authenticate")
public class UserAuthenticationRestController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDTO loginDTO) {
        Optional<String> jwt = userService.authenticate(loginDTO);

        if (jwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt.get()));
    }
}