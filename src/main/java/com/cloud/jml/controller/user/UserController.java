package com.cloud.jml.controller.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        log.info("📥 [SOLICITUD] /usuario/list/all -> Listar todos los usuarios.");

        List<UserResponseDTO> usuarios = userService.listarUsuarios();

        if (usuarios == null || usuarios.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron usuarios registrados.");
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios.", usuarios.size());

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrarUsuario(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("📥 [SOLICITUD] /usuario/register -> Crear usuario: {}.", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.registrarUsuario(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.getIdentificacion() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo registrar el usuario: {}.", userRequestDTO.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Usuario creado exitosamente: {} (identificación: {}).", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @GetMapping("/identificacion")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorIdentificacion(@RequestParam("identificacion") Long identificacion) {
        log.info("📥 [SOLICITUD] /usuario/identificacion -> Obtener usuario con identificación: {}.", identificacion);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setIdentificacion(identificacion);

        UserResponseDTO userResponseDTO = userService.obtenerUsuarioPorIdentificacion(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.getIdentificacion() == null) {
            log.warn("⚠️ [RESPUESTA] Usuario no encontrado con identificación: {}.", identificacion);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Usuario obtenido correctamente: {} (identificación: {}).", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/userName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorUserName(@RequestParam("userName") String userName) {
        log.info("📥 [SOLICITUD] /usuario/userName -> Buscar usuario con userName: {}.", userName);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserName(userName);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorUserName(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron usuarios con userName: {}.", userName);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se encontraron {} usuarios con userName: {}.", userResponseDTO.size(), userName);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/nombres")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorNombres(@RequestParam("nombres") String nombres) {
        log.info("📥 [SOLICITUD] /usuario/nombres -> Buscar usuario con nombres: {}.", nombres);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNombres(nombres);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorNombres(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron usuarios con nombres: {}.", nombres);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se encontraron {} usuarios con nombres: {}.", userResponseDTO.size(), nombres);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/apellidos")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorApellidos(@RequestParam("apellidos") String apellidos) {
        log.info("📥 [SOLICITUD] /usuario/apellidos -> Buscar usuario con apellidos: {}.", apellidos);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setApellidos(apellidos);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorApellidos(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron usuarios con apellidos: {}.", apellidos);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se encontraron {} usuarios con apellidos: {}.", userResponseDTO.size(), apellidos);

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/roleCode")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorRoleCode(@RequestParam("roleCode") Integer roleCode) {
        log.info("📥 [SOLICITUD] /usuario/roleCode -> Buscar usuario con roleCode: {}.", roleCode);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setRoleCode(roleCode);

        UserResponseDTO userResponseDTO = userService.obtenerUsuarioPorRoleCode(userRequestDTO);

        if (userResponseDTO == null || userResponseDTO.getRoleCode() == null) {
            log.warn("⚠️ [RESPUESTA] Usuario no encontrado con roleCode: {}.", roleCode);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Usuario obtenido correctamente con roleCode: {}.", userResponseDTO.getRoleCode());

        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/roleName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorRoleName(@RequestParam("roleName") String roleName) {
        log.info("📥 [SOLICITUD] /usuario/roleName -> Buscar usuarios con roleName: {}.", roleName);

        List<UserResponseDTO> userResponseDTO = userService.obtenerUsuarioPorRoleName(roleName);

        if (userResponseDTO == null || userResponseDTO.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron usuarios con roleName: {}.", roleName);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se encontraron {} usuarios con roleName: {}.", userResponseDTO.size(), roleName);

        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(
            @RequestBody UserRequestDTO userRequestDTO,
            @RequestParam("userLogin") String userLogin) {

        log.info("📥 [SOLICITUD] /usuario/update -> Actualizar usuario: {}.", userRequestDTO.getUserName());

        UserResponseDTO userResponseDTO = userService.actualizarUsuario(userRequestDTO, userLogin);

        if (userResponseDTO == null || userResponseDTO.getIdentificacion() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo actualizar el usuario: {}.", userRequestDTO.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Usuario actualizado correctamente: {} (identificación: {}).", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

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
