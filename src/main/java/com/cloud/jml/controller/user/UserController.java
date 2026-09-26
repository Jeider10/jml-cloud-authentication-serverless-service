package com.cloud.jml.controller.user;

import com.cloud.jml.dto.user.UserPapeleraResponseDTO;
import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.service.user.UserService;
import jakarta.validation.Valid;
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

    // ─── Listar activos ───────────────────────────────────────────────────────
    @GetMapping("/list/all")
    public ResponseEntity<List<UserResponseDTO>> listarUsuarios() {
        log.info("📥 [SOLICITUD] Listar todos los usuarios activos");

        List<UserResponseDTO> usuarios = userService.listarUsuarios();

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios", usuarios.size());

        return ResponseEntity.ok(usuarios);
    }

    // ─── Registrar ────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrarUsuario(
            @RequestBody @Valid UserRequestDTO userRequestDTO,
            @RequestParam("creadoPor") String creadoPor) {
        log.info("📥 [SOLICITUD] Crear usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO response = userService.registrarUsuario(userRequestDTO, creadoPor);

        log.info("📤 [RESPUESTA] Usuario creado: {} (identificacion: {})", response.getUserName(), response.getIdentificacion());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── Buscar por identificacion ────────────────────────────────────────────
    @GetMapping("/identificacion")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorIdentificacion(@RequestParam("identificacion") Long identificacion) {
        log.info("📥 [SOLICITUD] Obtener usuario con identificacion: {}", identificacion);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setIdentificacion(identificacion);

        UserResponseDTO response = userService.obtenerUsuarioPorIdentificacion(userRequestDTO);

        if (response == null || response.getIdentificacion() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por userName ──────────────────────────────────────────────────
    @GetMapping("/userName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorUserName(@RequestParam("userName") String userName) {
        log.info("📥 [SOLICITUD] /usuario/userName -> Buscar usuario con userName: {}.", userName);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserName(userName);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorUserName(userRequestDTO);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por nombres ───────────────────────────────────────────────────
    @GetMapping("/nombres")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorNombres(@RequestParam("nombres") String nombres) {
        log.info("📥 [SOLICITUD] /usuario/nombres -> Buscar usuario con nombres: {}.", nombres);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNombres(nombres);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorNombres(userRequestDTO);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por apellidos ─────────────────────────────────────────────────
    @GetMapping("/apellidos")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorApellidos(@RequestParam("apellidos") String apellidos) {
        log.info("📥 [SOLICITUD] /usuario/apellidos -> Buscar usuario con apellidos: {}.", apellidos);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setApellidos(apellidos);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorApellidos(userRequestDTO);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por roleCode ──────────────────────────────────────────────────
    @GetMapping("/roleCode")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorRoleCode(@RequestParam("roleCode") Integer roleCode) {
        log.info("📥 [SOLICITUD] /usuario/roleCode -> Buscar usuario con roleCode: {}.", roleCode);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setRoleCode(roleCode);

        UserResponseDTO response = userService.obtenerUsuarioPorRoleCode(userRequestDTO);

        if (response == null || response.getRoleCode() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por roleName ──────────────────────────────────────────────────
    @GetMapping("/roleName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorRoleName(@RequestParam("roleName") String roleName) {
        log.info("📥 [SOLICITUD] /usuario/roleName -> Buscar usuarios con roleName: {}.", roleName);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorRoleName(roleName);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por fecha de creacion ─────────────────────────────────────────
    @GetMapping("/fechaCreacion")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorFechaCreacion(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {

        log.info("📥 [SOLICITUD] Buscar usuarios por rango de fecha de creacion: {} - {}", fechaInicio, fechaFin);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorFechaCreacion(fechaInicio, fechaFin);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por fecha de actualizacion ───────────────────────────────────
    @GetMapping("/fechaActualizacion")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorFechaActualizacion(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {

        log.info("📥 [SOLICITUD] Buscar usuarios por rango de fecha de actualizacion: {} - {}", fechaInicio, fechaFin);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorFechaActualizacion(fechaInicio, fechaFin);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(
            @RequestBody @Valid UserRequestDTO userRequestDTO,
            @RequestParam("userLogin") String userLogin) {
        log.info("📥 [SOLICITUD] Actualizar usuario: {}", userRequestDTO.getUserName());

        UserResponseDTO response = userService.actualizarUsuario(userRequestDTO, userLogin);

        log.info("📤 [RESPUESTA] Usuario actualizado: {}", response.getIdentificacion());

        return ResponseEntity.ok(response);
    }

    // ─── Resetear password ────────────────────────────────────────────────────
    @PutMapping("/forgot-password")
    public ResponseEntity<Void> resetearPassword(@RequestBody java.util.Map<String, String> body) {

        String userName = body.get("userName");
        String password = body.get("password");

        log.info("📥 [SOLICITUD] /usuario/forgot-password -> Resetear password para usuario: {}", userName);

        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            log.warn("⚠️ [RESPUESTA] userName o password vacios.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        userService.resetearPassword(userName, password);

        log.info("📤 [RESPUESTA] Password reseteado correctamente para usuario: {}", userName);

        return ResponseEntity.ok().build();
    }

    // ─── Soft delete (enviar a papelera) ──────────────────────────────────────
    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarUsuario(
            @RequestParam("identificacion") Long identificacion,
            @RequestParam("eliminadoPorId") String eliminadoPorId,
            @RequestParam("eliminadoPorNombre") String eliminadoPorNombre) {

        log.info("📥 [SOLICITUD] Enviar a papelera usuario con identificacion: {}", identificacion);

        userService.eliminarUsuario(identificacion, eliminadoPorId, eliminadoPorNombre);

        log.info("📤 [RESPUESTA] Usuario {} enviado a papelera por: {}", identificacion, eliminadoPorNombre);

        return ResponseEntity.ok().build();
    }

    // ─── Listar papelera ──────────────────────────────────────────────────────
    @GetMapping("/trash")
    public ResponseEntity<List<UserPapeleraResponseDTO>> listarPapelera() {

        log.info("📥 [SOLICITUD] Listar usuarios en papelera");

        List<UserPapeleraResponseDTO> papelera = userService.listarPapelera();

        if (papelera.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios en papelera", papelera.size());

        return ResponseEntity.ok(papelera);
    }

    // ─── Restaurar desde papelera ─────────────────────────────────────────────
    @PutMapping("/restore")
    public ResponseEntity<UserResponseDTO> restaurarUsuario(@RequestParam("identificacion") Long identificacion) {

        log.info("📥 [SOLICITUD] Restaurar usuario con identificacion: {}", identificacion);

        UserResponseDTO response = userService.restaurarUsuario(identificacion);

        log.info("📤 [RESPUESTA] Usuario restaurado: {}", identificacion);

        return ResponseEntity.ok(response);
    }

    // ─── Eliminar definitivamente ─────────────────────────────────────────────
    @DeleteMapping("/permanent-delete")
    public ResponseEntity<Void> eliminarDefinitivo(@RequestParam("identificacion") Long identificacion) {

        log.info("📥 [SOLICITUD] Eliminar definitivamente usuario con identificacion: {}", identificacion);

        userService.eliminarDefinitivo(identificacion);

        log.info("📤 [RESPUESTA] Usuario {} eliminado definitivamente", identificacion);

        return ResponseEntity.ok().build();
    }

    // ─── Filtrar papelera por fecha de eliminacion ────────────────────────────
    @GetMapping("/trash/fecha")
    public ResponseEntity<List<UserPapeleraResponseDTO>> listarPapeleraPorFecha(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {

        List<UserPapeleraResponseDTO> resultado = userService.listarPapeleraPorFecha(fechaInicio, fechaFin);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    // ─── Filtrar papelera por quien elimino ───────────────────────────────────
    @GetMapping("/trash/eliminadoPor")
    public ResponseEntity<List<UserPapeleraResponseDTO>> listarPapeleraPorEliminadoPor(
            @RequestParam("eliminadoPorId") String eliminadoPorId) {

        List<UserPapeleraResponseDTO> resultado = userService.listarPapeleraPorEliminadoPor(eliminadoPorId);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }
}
