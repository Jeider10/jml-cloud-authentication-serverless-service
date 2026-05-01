package com.cloud.jml.controller.role;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.service.role.RoleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        log.info("🔥 RoleController inicializado correctamente.");
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<RoleResponseDTO>> listarRoles() {
        log.info("📥 [SOLICITUD] /roles/list/all -> Listar todos los roles.");

        List<RoleResponseDTO> roles = roleService.listarRoles();

        if (roles == null || roles.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron roles registrados.");
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se retornan {} roles.", roles.size());

        return ResponseEntity.ok(roles);
    }

    @PostMapping("/register")
    public ResponseEntity<RoleResponseDTO> registrarRole(@RequestBody @Valid RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] /roles/register -> Crear rol: {} (codigo: {}).", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO response = roleService.registrarRole(roleRequestDTO);

        if (response == null || response.getRoleCode() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo registrar el rol: {}", roleRequestDTO.getRoleName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Rol creado exitosamente: {} (codigo: {}).", response.getRoleName(), response.getRoleCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/roleCode")
    public ResponseEntity<RoleResponseDTO> buscarRolePorCodigo(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] /roles/roleCode -> Buscar rol con codigo: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        RoleResponseDTO roleResponseDTO = roleService.buscarRolePorCodigo(roleRequestDTO);

        if (roleResponseDTO == null) {
            log.warn("⚠️ [RESPUESTA] Rol no encontrado con codigo: {}", roleCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("📤 [RESPUESTA] Rol encontrado: {} (codigo: {}).", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @GetMapping("/roleName")
    public ResponseEntity<List<RoleResponseDTO>> buscarRolePorNombre(@RequestParam("roleName") String roleName) {
        log.info("📥 [SOLICITUD] /roles/roleName -> Buscar rol con nombre: {}", roleName);

        RoleRequestDTO request = new RoleRequestDTO();
        request.setRoleName(roleName);

        List<RoleResponseDTO> roles = roleService.buscarRolePorNombre(request);

        if (roles == null || roles.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron roles con nombre: {}", roleName);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se encontraron {} roles con nombre: {}", roles.size(), roleName);

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/fechaCreacion")
    public ResponseEntity<List<RoleResponseDTO>> obtenerRolePorFechaCreacion(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {

        log.info("📥 [SOLICITUD] Buscar roles por rango de fecha de creacion: {} - {}", fechaInicio, fechaFin);

        List<RoleResponseDTO> roleFecha = roleService.obtenerRolePorFechaCreacion(fechaInicio, fechaFin);

        if (roleFecha == null || roleFecha.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontraron roles en el rango de fechas.");
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Se retornan {} roles en el rango de fechas.", roleFecha.size());

        return ResponseEntity.ok(roleFecha);
    }

    @PutMapping("/update")
    public ResponseEntity<RoleResponseDTO> actualizarRole(@RequestBody @Valid RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] /roles/update -> Actualizar rol: {} (codigo: {}).", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.actualizarRole(roleRequestDTO);

        if (roleResponseDTO == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo actualizar el rol: {}.", roleRequestDTO.getRoleName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Rol actualizado correctamente: {} (codigo: {}).", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarRole(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] Eliminar rol con codigo: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        roleService.eliminarRole(roleRequestDTO);

        log.info("📤 [RESPUESTA] Rol eliminado correctamente con codigo: {}", roleCode);

        return ResponseEntity.ok().build();
    }
}
