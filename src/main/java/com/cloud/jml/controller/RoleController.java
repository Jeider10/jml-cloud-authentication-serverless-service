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
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        log.info("🔥 RoleController inicializado correctamente.");
    }

    @PostMapping("/register")
    public ResponseEntity<RoleResponseDTO> registrarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📌 Recibiendo solicitud para registrar role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.registrarRole(roleRequestDTO);

        log.info("✅ Finaliza petición para registrar role: {}", roleRequestDTO.getRoleName());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<RoleResponseDTO> actualizarRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        log.info("📌 Recibiendo solicitud para actualizar role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        RoleResponseDTO roleResponseDTO = roleService.actualizarRole(roleRequestDTO);

        log.info("✅ Finaliza petición para actualizar role: {}", roleRequestDTO.getRoleName());

        return ResponseEntity.ok(roleResponseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleResponseDTO>> listarRoles() {
        log.info("📌 Recibiendo solicitud para obtener todos los roles.");

        List<RoleResponseDTO> roles = roleService.listarRoles();

        log.info("✅ Finaliza petición para obtener todos los roles.");

        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarRole(@RequestParam("roleCode") int roleCode) {
        log.info("📌 Recibiendo solicitud para eliminar role con código: {}", roleCode);

        RoleRequestDTO roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setRoleCode(roleCode);

        roleService.eliminarRole(roleRequestDTO);

        log.info("✅ Finaliza petición para eliminar role con código: {}", roleCode);

        return ResponseEntity.ok().build();
    }
}
