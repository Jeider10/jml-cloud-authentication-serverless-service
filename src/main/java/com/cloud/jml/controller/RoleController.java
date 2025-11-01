package com.cloud.jml.controller;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "http://localhost:8080")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        log.info("🔥 RoleController inicializado correctamente.");
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<RoleResponseDTO>> listarRoles() {
        log.info("📥 [SOLICITUD] Listar todos los roles.");

        List<RoleResponseDTO> roles = roleService.listarRoles();

        log.info("📤 [RESPUESTA] Se retornan {} roles", roles.size());

        return ResponseEntity.ok(roles);
    }

    @PostMapping("/register")
    public ResponseEntity<RoleResponseDTO> registrarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] Crear role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.registrarRole(roleRequestDTO);

        log.info("📤 [RESPUESTA] Role creado: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @GetMapping("/roleCode")
    public ResponseEntity<RoleResponseDTO> buscarRolePorCodigo(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] Buscar role con código: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        RoleResponseDTO roleResponseDTO = roleService.buscarRolePorCodigo(roleRequestDTO);

        log.info("📤 [RESPUESTA] Role encontrado: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<RoleResponseDTO> actualizarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📥 [SOLICITUD] Actualizar role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.actualizarRole(roleRequestDTO);

        log.info("📤 [RESPUESTA] Role actualizado correctamente: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarRole(@RequestParam("roleCode") int roleCode) {
        log.info("📥 [SOLICITUD] Eliminar role con código: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        roleService.eliminarRole(roleRequestDTO);

        log.info("📤 [RESPUESTA] Role eliminado correctamente con código: {}", roleCode);

        return ResponseEntity.ok().build();
    }
}
