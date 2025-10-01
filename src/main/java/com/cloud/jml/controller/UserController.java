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

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrarUsuario(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("📌 Iniciando registro de usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.registrarUsuario(userRequestDTO);

        log.info("✅ Usuario registrado exitosamente con nombre: {}", userRequestDTO.getUserName());

        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("📌 Recibiendo solicitud para actualizar usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.actualizarUsuario(userRequestDTO);

        log.info("✅ Usuario actualizado exitosamente con nombre: {}", userRequestDTO.getUserName());

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> listarUsuarios() {
        log.info("📌 Recibiendo solicitud para obtener todos los usuarios.");

        List<UserResponseDTO> usuarios = userService.listarUsuarios();

        log.info("✅ Finaliza petición para obtener todos los usuarios.");

        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarUsuario(@RequestParam("identificacion") Long identificacion) {
        log.info("📌 Recibiendo solicitud para eliminar usuario con identificación: {}", identificacion);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setIdentificacion(identificacion);

        userService.eliminarUsuario(userRequestDTO);
        log.info("✅ Finaliza petición para eliminar usuario con identificación: {}", identificacion);
        return ResponseEntity.ok().build();
    }
}
