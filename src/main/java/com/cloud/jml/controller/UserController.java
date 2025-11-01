package com.cloud.jml.controller;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        log.info("🔥 UserController inicializado correctamente.");
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<UserResponseDTO>> listarUsuarios() {
        log.info("📥 [SOLICITUD] Listar todos los usuarios.");

        List<UserResponseDTO> usuarios = userService.listarUsuarios();

        log.info("📤 [RESPUESTA] Se retornan {} usuarios", usuarios.size());

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrarUsuario(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("📥 [SOLICITUD] Crear usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.registrarUsuario(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuario creado: {} con identificación: {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/identificacion")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorIdentificacion(@RequestParam("identificacion") Long identificacion) {
        log.info("📥 [SOLICITUD] Obtener usuario con identificación: {}", identificacion);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setIdentificacion(identificacion);

        UserResponseDTO userResponseDTO = userService.obtenerUsuarioPorIdentificacion(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuario obtenido: {} con identificación: {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("📥 [SOLICITUD] Actualizar usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.actualizarUsuario(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuario actualizado correctamente: {}", userRequestDTO.getUserName());

        return ResponseEntity.ok(userResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarUsuario(@RequestParam("identificacion") Long identificacion) {
        log.info("📥 [SOLICITUD] Eliminar usuario con identificación: {}", identificacion);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setIdentificacion(identificacion);

        userService.eliminarUsuario(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuario eliminado correctamente con identificación: {}", identificacion);

        return ResponseEntity.ok().build();
    }
}
