package com.cloud.jml.controller.user;

import com.cloud.jml.dto.user.UserPapeleraResponseDTO;
import com.cloud.jml.dto.user.UserPermisosRequestDTO;
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
            log.info("📤 [RESPUESTA] No hay usuarios activos — lista vacia");
            return ResponseEntity.ok(List.of());
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
            log.warn("⚠️ [RESPUESTA] No se encontro usuario con identificacion: {}", identificacion);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Usuario encontrado: {} (identificacion: {})", response.getUserName(), identificacion);

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
            log.info("📤 [RESPUESTA] No se encontraron usuarios con userName: {} — lista vacia", userName);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios con userName: {}", response.size(), userName);

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
            log.info("📤 [RESPUESTA] No se encontraron usuarios con nombres: {} — lista vacia", nombres);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios con nombres: {}", response.size(), nombres);

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
            log.info("📤 [RESPUESTA] No se encontraron usuarios con apellidos: {} — lista vacia", apellidos);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios con apellidos: {}", response.size(), apellidos);

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
            log.warn("⚠️ [RESPUESTA] No se encontro usuario con roleCode: {}", roleCode);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Usuario encontrado con roleCode: {}", roleCode);

        return ResponseEntity.ok(response);
    }

    // ─── Buscar por roleName ──────────────────────────────────────────────────
    @GetMapping("/roleName")
    public ResponseEntity<List<UserResponseDTO>> obtenerUsuarioPorRoleName(@RequestParam("roleName") String roleName) {
        log.info("📥 [SOLICITUD] /usuario/roleName -> Buscar usuarios con roleName: {}.", roleName);

        List<UserResponseDTO> response = userService.obtenerUsuarioPorRoleName(roleName);

        if (response.isEmpty()) {
            log.info("📤 [RESPUESTA] No se encontraron usuarios con roleName: {} — lista vacia", roleName);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios con roleName: {}", response.size(), roleName);

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
            log.info("📤 [RESPUESTA] No se encontraron usuarios en el rango {} - {} — lista vacia", fechaInicio, fechaFin);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios en el rango de fechas", response.size());

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
            log.info("📤 [RESPUESTA] No se encontraron usuarios en el rango {} - {} — lista vacia", fechaInicio, fechaFin);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios en el rango de fechas de actualizacion", response.size());

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

    // ─── Actualizar permisos granulares ──────────────────────────────────────
    @PutMapping("/permisos")
    public ResponseEntity<UserResponseDTO> actualizarPermisos(
            @RequestBody @Valid UserPermisosRequestDTO dto) {

        log.info("📥 [SOLICITUD] Actualizar permisos del usuario con identificacion: {}", dto.getIdentificacion());

        UserResponseDTO response = userService.actualizarPermisos(dto);

        log.info("📤 [RESPUESTA] Permisos actualizados para usuario: {}", response.getIdentificacion());

        return ResponseEntity.ok(response);
    }

    // ─── Actualizar foto de perfil ────────────────────────────────────────────
    // Recibe la imagen como base64 en el body del request (Content-Type: text/plain)
    @PutMapping("/foto")
    public ResponseEntity<UserResponseDTO> actualizarFoto(
            @RequestParam("identificacion") Long identificacion,
            @RequestBody String fotoBase64) {

        log.info("📥 [SOLICITUD] Actualizar foto de perfil del usuario con identificacion: {}", identificacion);

        UserResponseDTO response = userService.actualizarFoto(identificacion, fotoBase64);

        log.info("📤 [RESPUESTA] Foto actualizada para usuario: {}", identificacion);

        return ResponseEntity.ok(response);
    }

    // ─── Eliminar foto de perfil ──────────────────────────────────────────────
    @DeleteMapping("/foto")
    public ResponseEntity<UserResponseDTO> eliminarFoto(
            @RequestParam("identificacion") Long identificacion) {

        log.info("📥 [SOLICITUD] Eliminar foto de perfil del usuario con identificacion: {}", identificacion);

        UserResponseDTO response = userService.eliminarFoto(identificacion);

        log.info("📤 [RESPUESTA] Foto eliminada para usuario: {}", identificacion);

        return ResponseEntity.ok(response);
    }

    // ─── Soft delete (enviar a papelera) ──────────────────────────────────────
    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarUsuario(
            @RequestParam("identificacion") Long identificacion,
            @RequestParam("eliminadoPorId") String eliminadoPorId,
            @RequestParam("eliminadoPorNombre") String eliminadoPorNombre,
            @RequestParam("eliminadoPorRol") String eliminadoPorRol,
            @RequestParam(value = "motivo", required = false) String motivo) {

        log.info("📥 [SOLICITUD] Enviar a papelera usuario con identificacion: {}", identificacion);

        userService.eliminarUsuario(identificacion, eliminadoPorId, eliminadoPorNombre, eliminadoPorRol, motivo);

        log.info("📤 [RESPUESTA] Usuario {} enviado a papelera por: {}", identificacion, eliminadoPorNombre);

        return ResponseEntity.ok().build();
    }

    // ─── Listar papelera ──────────────────────────────────────────────────────
    @GetMapping("/trash")
    public ResponseEntity<List<UserPapeleraResponseDTO>> listarPapelera() {

        log.info("📥 [SOLICITUD] Listar usuarios en papelera");

        List<UserPapeleraResponseDTO> papelera = userService.listarPapelera();

        if (papelera.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay usuarios en papelera — lista vacia");
            return ResponseEntity.ok(List.of());
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

        log.info("📥 [SOLICITUD] Filtrar papelera de usuarios por fecha: {} - {}", fechaInicio, fechaFin);

        List<UserPapeleraResponseDTO> resultado = userService.listarPapeleraPorFecha(fechaInicio, fechaFin);

        if (resultado.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay usuarios en papelera en el rango {} - {} — lista vacia", fechaInicio, fechaFin);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios en papelera en el rango de fechas", resultado.size());

        return ResponseEntity.ok(resultado);
    }

    // ─── Filtrar papelera por quien elimino ───────────────────────────────────
    @GetMapping("/trash/eliminadoPor")
    public ResponseEntity<List<UserPapeleraResponseDTO>> listarPapeleraPorEliminadoPor(
            @RequestParam("eliminadoPorId") String eliminadoPorId) {

        log.info("📥 [SOLICITUD] Filtrar papelera de usuarios por eliminadoPorId: {}", eliminadoPorId);

        List<UserPapeleraResponseDTO> resultado = userService.listarPapeleraPorEliminadoPor(eliminadoPorId);

        if (resultado.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay usuarios en papelera eliminados por: {} — lista vacia", eliminadoPorId);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} usuarios en papelera eliminados por: {}", resultado.size(), eliminadoPorId);

        return ResponseEntity.ok(resultado);
    }
}
