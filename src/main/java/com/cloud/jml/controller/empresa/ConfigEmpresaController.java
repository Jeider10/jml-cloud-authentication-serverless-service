package com.cloud.jml.controller.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.service.empresa.ConfigEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/empresa")
public class ConfigEmpresaController {

    private final ConfigEmpresaService configEmpresaService;

    public ConfigEmpresaController(ConfigEmpresaService configEmpresaService) {
        this.configEmpresaService = configEmpresaService;
        log.info("🔥 ConfigEmpresaController inicializado correctamente.");
    }

    @GetMapping
    public ResponseEntity<List<ConfigEmpresaResponseDTO>> obtenerPrimeraEmpresa() {
        log.info("📥 [SOLICITUD] /empresa -> Obtener empresa registrada (primera encontrada).");

        List<ConfigEmpresaResponseDTO> primeraEmpresa = configEmpresaService.obtenerPrimeraEmpresa();

        if (primeraEmpresa == null || primeraEmpresa.isEmpty()) {
            log.warn("⚠️ [RESPUESTA] No se encontró ninguna empresa registrada.");
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Empresa encontrada: {}", primeraEmpresa.size());

        return ResponseEntity.ok(primeraEmpresa);
    }

    @GetMapping("/nit")
    public ResponseEntity<ConfigEmpresaResponseDTO> buscarEmpresaNit(@RequestParam("nit") Long nit) {
        log.info("📥 [SOLICITUD] /empresa/nit -> Buscar empresa con NIT: {}", nit);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNit(nit);

        ConfigEmpresaResponseDTO response = configEmpresaService.buscarEmpresaNit(configEmpresaRequestDTO);

        if (response == null || response.getNit() == null) {
            log.warn("⚠️ [RESPUESTA] No se encontró empresa con NIT: {}", nit);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Empresa encontrada: {} (NIT: {})", response.getNombreEmpresa(), response.getNit());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> registrarDatosEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO configEmpresaRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("📥 [SOLICITUD] /empresa/register -> Crear empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.registrarDatosEmpresa(configEmpresaRequestDTO, file);

        if (configEmpresaResponseDTO == null || configEmpresaResponseDTO.getNit() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo crear la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Empresa creada exitosamente: {} (NIT: {})", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        return ResponseEntity.status(HttpStatus.CREATED).body(configEmpresaResponseDTO);
    }

    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> actualizarEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO configEmpresaRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("📥 [SOLICITUD] /empresa/update -> Actualizar empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = configEmpresaService.actualizarEmpresa(configEmpresaRequestDTO, file);

        if (configEmpresaResponseDTO == null || configEmpresaResponseDTO.getNit() == null) {
            log.warn("⚠️ [RESPUESTA] No se pudo actualizar la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("📤 [RESPUESTA] Empresa actualizada correctamente: {} (NIT: {})", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        return ResponseEntity.ok(configEmpresaResponseDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarEmpresa(@RequestParam("nit") Long nit) {
        log.info("📥 [SOLICITUD] /empresa/delete -> Eliminar empresa con NIT: {}", nit);

        ConfigEmpresaRequestDTO configEmpresaRequestDTO = new ConfigEmpresaRequestDTO();
        configEmpresaRequestDTO.setNit(nit);

        configEmpresaService.eliminarEmpresa(configEmpresaRequestDTO);

        log.info("📤 [RESPUESTA] Empresa eliminada correctamente (NIT: {}).", nit);

        return ResponseEntity.ok().build();
    }
}
