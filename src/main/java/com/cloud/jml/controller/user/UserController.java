package com.cloud.jml.controller.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuario")
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

        if (userResponseDTO != null) {
            log.info("📤 [RESPUESTA] Usuario obtenido con identificación: {}", userResponseDTO.getIdentificacion());
        }

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/userName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorUserName(@RequestParam("userName") String userName) {
        log.info("📥 [SOLICITUD] Obtener usuario con userName: {}", userName);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserName(userName);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorUserName(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuarios obtenidos: {} con userName: {}", userResponseDTO.size(), userName);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/nombres")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorNombres(@RequestParam("nombres") String nombres) {
        log.info("📥 [SOLICITUD] Obtener usuario con nombre: {}", nombres);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNombres(nombres);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorNombres(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuarios obtenidos: {} con nombre: {}", userResponseDTO.size(), nombres);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/apellidos")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorApellidos(@RequestParam("apellidos") String apellidos) {
        log.info("📥 [SOLICITUD] Obtener usuario con apellido: {}", apellidos);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setApellidos(apellidos);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorApellidos(userRequestDTO);

        log.info("📤 [RESPUESTA] Usuarios obtenidos: {} con apellido: {}", userResponseDTO.size(), apellidos);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/roleCode")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorRoleCode(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] Obtener usuario con roleCode: {}", roleCode);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setRoleCode(roleCode);

        UserResponseDTO userResponseDTO = userService.obtenerUsuarioPorRoleCode(userRequestDTO);

        if (userResponseDTO != null) {
            log.info("📤 [RESPUESTA] Usuario obtenido con roleCode: {}", userResponseDTO.getRoleCode());
        }

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/roleName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorRoleName(@RequestParam("roleName") String roleName) {
        log.info("📥 [SOLICITUD] Obtener usuario con roleName: {}", roleName);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorRoleName(roleName);

        log.info("📤 [RESPUESTA] Usuarios obtenidos: {} con roleName: {}", userResponseDTO.size(), roleName);

        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(
            @RequestBody UserRequestDTO userRequestDTO,
            @RequestParam("userLogin") String userLogin) {

        log.info("📥 [SOLICITUD] Actualizar usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.actualizarUsuario(userRequestDTO, userLogin);

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
