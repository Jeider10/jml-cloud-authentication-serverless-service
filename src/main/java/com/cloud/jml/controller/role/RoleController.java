package com.cloud.jml.controller.role;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.service.role.RoleService;
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
    public ResponseEntity<RoleResponseDTO> registrarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] /roles/register -> Crear rol: {} (código: {}).", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO response = roleService.registrarRole(roleRequestDTO);

        if (response == null || response.getRoleCode() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo registrar el rol: {}", roleRequestDTO.getRoleName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Rol creado exitosamente: {} (código: {}).", response.getRoleName(), response.getRoleCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/roleCode")
    public ResponseEntity<RoleResponseDTO> buscarRolePorCodigo(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] /roles/roleCode -> Buscar rol con código: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        RoleResponseDTO roleResponseDTO = roleService.buscarRolePorCodigo(roleRequestDTO);

        if (roleResponseDTO == null) {
            log.warn("⚠️ [RESPUESTA] Rol no encontrado con código: {}", roleCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("📤 [RESPUESTA] Rol encontrado: {} (código: {}).", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

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

    @PutMapping("/update")
    public ResponseEntity<RoleResponseDTO> actualizarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] /roles/update -> Actualizar rol: {} (código: {}).", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.actualizarRole(roleRequestDTO);

        if (roleResponseDTO == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo actualizar el rol: {}.", roleRequestDTO.getRoleName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Rol actualizado correctamente: {} (código: {}).", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarRole(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] Eliminar rol con código: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        roleService.eliminarRole(roleRequestDTO);

        log.info("📤 [RESPUESTA] Rol eliminado correctamente con código: {}", roleCode);

        return ResponseEntity.ok().build();
    }
}
