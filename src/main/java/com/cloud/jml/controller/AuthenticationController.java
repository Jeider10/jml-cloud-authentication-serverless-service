package com.cloud.jml.controller;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        log.info("🔥 AuthenticationController inicializado correctamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticationLogin(@RequestBody @Valid AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("📥 [SOLICITUD] Iniciando login para usuario: {}", authenticationRequestDTO.getUsuario());

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticationLogin(authenticationRequestDTO);

        log.info("📤 [RESPUESTA] Login exitoso para usuario: {}", authenticationRequestDTO.getUsuario());

        return ResponseEntity.ok(authenticationResponseDTO);
    }

    @GetMapping("/obtener-usuario-actual")
    public ResponseEntity<AuthenticationResponseDTO> obtenerUsuarioActual(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("📥 [SOLICITUD] Iniciando obtener usuario actual.");

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.obtenerUsuarioActual(authorizationHeader);

        log.info("📤 [RESPUESTA] Usuario actual obtenido correctamente.");

        return ResponseEntity.ok(authenticationResponseDTO);
    }
}
