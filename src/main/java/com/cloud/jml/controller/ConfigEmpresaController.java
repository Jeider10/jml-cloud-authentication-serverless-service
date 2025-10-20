package com.cloud.jml.controller;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.service.ConfigEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/empresa")
@CrossOrigin(origins = "http://localhost:8080")
public class ConfigEmpresaController {

    private final ConfigEmpresaService configEmpresaService;

    public ConfigEmpresaController(ConfigEmpresaService configEmpresaService) {
        this.configEmpresaService = configEmpresaService;
        log.info("🔥 ConfigEmpresaController inicializado correctamente.");
    }

    @GetMapping
    public ResponseEntity<List<ConfigEmpresaResponseDTO>> obtenerPrimeraEmpresa() {
        log.info("📥 [SOLICITUD] Obtener empresa registrada (primera encontrada).");

        List<ConfigEmpresaResponseDTO> primeraEmpresa = configEmpresaService.obtenerPrimeraEmpresa();

        log.info("📤 [RESPUESTA] Empresa encontrada: {}", primeraEmpresa.size());

        return ResponseEntity.ok(primeraEmpresa);
    }

    @GetMapping("/{nic}")
    public ResponseEntity<ConfigEmpresaResponseDTO> buscarEmpresaNic(@PathVariable Long nic) {
        log.info("📥 [SOLICITUD] Buscar empresa con NIC: {}", nic);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNic(nic);

        ConfigEmpresaResponseDTO response = configEmpresaService.buscarEmpresaNic(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa encontrada: {} con nic: {}", response.getNombreEmpresa(), response.getNic());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ConfigEmpresaResponseDTO> registrarDatosEmpresa(@RequestBody ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📥 [SOLICITUD] Crear empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.registrarDatosEmpresa(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa creada: {} con nic: {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNic());

        return ResponseEntity.ok(configEmpresaResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<ConfigEmpresaResponseDTO> actualizarEmpresa(@RequestBody ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📥 [SOLICITUD] Actualizar empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.actualizarEmpresa(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa actualizada correctamente: {}", configEmpresaRequestDTO.getNombreEmpresa());

        return ResponseEntity.ok(configEmpresaResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarEmpresa(@RequestParam("nic") Long nic) {
        log.info("📥 [SOLICITUD] Eliminar empresa con nic: {}", nic);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNic(nic);

        configEmpresaService.eliminarEmpresa(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa eliminada correctamente con nic: {}", nic);

        return ResponseEntity.ok().build();
    }
}
