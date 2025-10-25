package com.cloud.jml.controller;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.service.ConfigEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{nit}")
    public ResponseEntity<ConfigEmpresaResponseDTO> buscarEmpresaNit(@PathVariable Long nit) {
        log.info("📥 [SOLICITUD] Buscar empresa con NIT: {}", nit);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNit(nit);

        ConfigEmpresaResponseDTO response = configEmpresaService.buscarEmpresaNit(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa encontrada: {} con nit: {}", response.getNombreEmpresa(), response.getNit());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> registrarDatosEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO configEmpresaRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("📥 [SOLICITUD] Crear empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.registrarDatosEmpresa(configEmpresaRequestDTO, file);

        log.info("📤 [RESPUESTA] Empresa creada: {} con nit: {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        return ResponseEntity.ok(configEmpresaResponseDTO);
    }

    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> actualizarEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO configEmpresaRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("📥 [SOLICITUD] Actualizar empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.actualizarEmpresa(configEmpresaRequestDTO, file);

        log.info("📤 [RESPUESTA] Empresa actualizada correctamente: {}", configEmpresaRequestDTO.getNombreEmpresa());

        return ResponseEntity.ok(configEmpresaResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarEmpresa(@RequestParam("nit") Long nit) {
        log.info("📥 [SOLICITUD] Eliminar empresa con nit: {}", nit);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNit(nit);

        configEmpresaService.eliminarEmpresa(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa eliminada correctamente con nit: {}", nit);

        return ResponseEntity.ok().build();
    }
}
