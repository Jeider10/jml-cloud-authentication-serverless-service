package com.cloud.jml.controller.authentication;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.service.authentication.AuthenticationService;
import com.cloud.jml.service.token.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        log.info("🔥 AuthenticationController inicializado correctamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticationLogin(
            @RequestBody @Valid AuthenticationRequestDTO authenticationRequestDTO) {

        log.info("📥 [SOLICITUD] Iniciando login para usuario: {}", authenticationRequestDTO.getUsuario());

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticationLogin(authenticationRequestDTO);

        if (authenticationResponseDTO == null
                || authenticationResponseDTO.getOptions() == null
                || authenticationResponseDTO.getOptions().getLogin() == null
                || authenticationResponseDTO.getOptions().getLogin().isBlank()) {

            log.warn("📤 [RESPUESTA] Login fallido para usuario: {}", authenticationRequestDTO.getUsuario());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("📤 [RESPUESTA] Login exitoso para usuario: {}", authenticationRequestDTO.getUsuario());

        return ResponseEntity.ok(authenticationResponseDTO);
    }

    @GetMapping("/obtener-usuario-actual")
    public ResponseEntity<AuthenticationResponseDTO> obtenerUsuarioActual(@RequestHeader("refreshToken") String refreshTokenHeader) {
        log.info("📥 [SOLICITUD] Iniciando obtener usuario actual.");

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.obtenerUsuarioActual(refreshTokenHeader);

        if (authenticationResponseDTO == null || authenticationResponseDTO.getOptions() == null || authenticationResponseDTO.getOptions().getLogin() == null) {
            log.warn("⚠️ [RESPUESTA] No se encontró información del usuario actual o token inválido.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("📤 [RESPUESTA] Usuario actual obtenido correctamente: {}", authenticationResponseDTO.getOptions().getLogin());
        return ResponseEntity.ok(authenticationResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(@RequestBody Map<String, String> body) {
        log.info("📥 [SOLICITUD] /authentication/refresh -> Iniciando proceso de refresh token.");

        AuthenticationResponseDTO authenticationResponseDTO = refreshTokenService.refreshToken(body);

        if (authenticationResponseDTO == null || authenticationResponseDTO.getOptions() == null || authenticationResponseDTO.getOptions().getLogin() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo renovar el token. Verifica el refresh token recibido.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("📤 [RESPUESTA] /authentication/refresh -> Token renovado exitosamente para el usuario: {}", authenticationResponseDTO.getOptions().getLogin());

        return ResponseEntity.ok(authenticationResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        log.info("📥 [SOLICITUD] /authentication/logout -> Iniciando proceso de logout.");

        refreshTokenService.logout(body);

        log.info("📤 [RESPUESTA] /authentication/logout -> Logout exitoso, refresh token revocado correctamente.");

        return ResponseEntity.ok().build();
    }
}
