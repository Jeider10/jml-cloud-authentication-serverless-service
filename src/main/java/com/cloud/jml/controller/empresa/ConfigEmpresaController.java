package com.cloud.jml.controller.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaPapeleraResponseDTO;
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

    // ─── Obtener empresa activa (objeto unico) ────────────────────────────────
    // Devuelve la empresa activa como objeto unico o null si no hay ninguna.
    // Devuelve 200 en todos los casos (incluso sin empresa) para que el frontend
    // distinga entre "no hay empresa" (body null) y error de red (4xx/5xx).
    // No usar 204 porque el interceptor Axios lo convierte a [] rompiendo el tipo.
    @GetMapping("/current")
    public ResponseEntity<ConfigEmpresaResponseDTO> obtenerEmpresaActual() {
        log.info("📥 [SOLICITUD] Obtener empresa activa (objeto unico)");

        ConfigEmpresaResponseDTO empresa = configEmpresaService.obtenerEmpresaActual();

        log.info("📤 [RESPUESTA] Empresa: {}", empresa != null ? empresa.getNombreEmpresa() : "ninguna registrada");

        return ResponseEntity.ok(empresa);
    }

    // ─── Obtener empresa activa ───────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ConfigEmpresaResponseDTO>> obtenerPrimeraEmpresa() {
        log.info("📥 [SOLICITUD] Obtener empresa registrada");

        List<ConfigEmpresaResponseDTO> empresa = configEmpresaService.obtenerPrimeraEmpresa();

        if (empresa.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay empresa registrada — lista vacia");
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retorna empresa: {}", empresa.getFirst().getNombreEmpresa());

        return ResponseEntity.ok(empresa);
    }

    // ─── Buscar por NIT ───────────────────────────────────────────────────────
    @GetMapping("/nit")
    public ResponseEntity<ConfigEmpresaResponseDTO> buscarEmpresaNit(@RequestParam("nit") Long nit) {
        log.info("📥 [SOLICITUD] Buscar empresa con NIT: {}", nit);

        ConfigEmpresaRequestDTO dto = new ConfigEmpresaRequestDTO();
        dto.setNit(nit);

        ConfigEmpresaResponseDTO response = configEmpresaService.buscarEmpresaNit(dto);

        if (response == null || response.getNit() == null) {
            log.warn("⚠️ [RESPUESTA] No se encontro empresa con NIT: {}", nit);
            return ResponseEntity.noContent().build();
        }

        log.info("📤 [RESPUESTA] Empresa encontrada: {} (NIT: {})", response.getNombreEmpresa(), response.getNit());

        return ResponseEntity.ok(response);
    }

    // ─── Registrar ────────────────────────────────────────────────────────────
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> registrarDatosEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("📥 [SOLICITUD] Crear empresa: {}", dto.getNombreEmpresa());

        ConfigEmpresaResponseDTO response = configEmpresaService.registrarDatosEmpresa(dto, file);

        log.info("📤 [RESPUESTA] Empresa creada: {} (NIT: {})", response.getNombreEmpresa(), response.getNit());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ConfigEmpresaResponseDTO> actualizarEmpresa(
            @RequestPart("empresa") ConfigEmpresaRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("📥 [SOLICITUD] Actualizar empresa: {}", dto.getNombreEmpresa());

        ConfigEmpresaResponseDTO response = configEmpresaService.actualizarEmpresa(dto, file);

        log.info("📤 [RESPUESTA] Empresa actualizada: {} (NIT: {})", response.getNombreEmpresa(), response.getNit());

        return ResponseEntity.ok(response);
    }

    // ─── Soft delete (enviar a papelera) ──────────────────────────────────────
    @DeleteMapping("/delete")
    public ResponseEntity<Void> eliminarEmpresa(
            @RequestParam("nit") Long nit,
            @RequestParam("eliminadoPorId") String eliminadoPorId,
            @RequestParam("eliminadoPorNombre") String eliminadoPorNombre,
            @RequestParam("eliminadoPorRol") String eliminadoPorRol,
            @RequestParam(value = "motivo", required = false) String motivo) {

        log.info("📥 [SOLICITUD] Enviar a papelera empresa con NIT: {}", nit);

        configEmpresaService.eliminarEmpresa(nit, eliminadoPorId, eliminadoPorNombre, eliminadoPorRol, motivo);

        log.info("📤 [RESPUESTA] Empresa {} enviada a papelera por: {}", nit, eliminadoPorNombre);

        return ResponseEntity.ok().build();
    }

    // ─── Listar papelera ──────────────────────────────────────────────────────
    @GetMapping("/trash")
    public ResponseEntity<List<ConfigEmpresaPapeleraResponseDTO>> listarPapelera() {

        log.info("📥 [SOLICITUD] Listar empresas en papelera");

        List<ConfigEmpresaPapeleraResponseDTO> papelera = configEmpresaService.listarPapelera();

        if (papelera.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay empresas en papelera — lista vacia");
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} empresas en papelera", papelera.size());

        return ResponseEntity.ok(papelera);
    }

    // ─── Restaurar desde papelera ─────────────────────────────────────────────
    @PutMapping("/restore")
    public ResponseEntity<ConfigEmpresaResponseDTO> restaurarEmpresa(@RequestParam("nit") Long nit) {

        log.info("📥 [SOLICITUD] Restaurar empresa con NIT: {}", nit);

        ConfigEmpresaResponseDTO response = configEmpresaService.restaurarEmpresa(nit);

        log.info("📤 [RESPUESTA] Empresa restaurada: {}", nit);

        return ResponseEntity.ok(response);
    }

    // ─── Eliminar definitivamente ─────────────────────────────────────────────
    @DeleteMapping("/permanent-delete")
    public ResponseEntity<Void> eliminarDefinitivo(@RequestParam("nit") Long nit) {

        log.info("📥 [SOLICITUD] Eliminar definitivamente empresa con NIT: {}", nit);

        configEmpresaService.eliminarDefinitivo(nit);

        log.info("📤 [RESPUESTA] Empresa {} eliminada definitivamente", nit);

        return ResponseEntity.ok().build();
    }

    // ─── Filtrar papelera por fecha de eliminacion ────────────────────────────
    @GetMapping("/trash/fecha")
    public ResponseEntity<List<ConfigEmpresaPapeleraResponseDTO>> listarPapeleraPorFecha(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {

        log.info("📥 [SOLICITUD] Filtrar papelera de empresas por fecha: {} - {}", fechaInicio, fechaFin);

        List<ConfigEmpresaPapeleraResponseDTO> resultado = configEmpresaService.listarPapeleraPorFecha(fechaInicio, fechaFin);

        if (resultado.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay empresas en papelera en el rango {} - {} — lista vacia", fechaInicio, fechaFin);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} empresas en papelera en el rango de fechas", resultado.size());

        return ResponseEntity.ok(resultado);
    }

    // ─── Filtrar papelera por quien elimino ───────────────────────────────────
    @GetMapping("/trash/eliminadoPor")
    public ResponseEntity<List<ConfigEmpresaPapeleraResponseDTO>> listarPapeleraPorEliminadoPor(
            @RequestParam("eliminadoPorId") String eliminadoPorId) {

        log.info("📥 [SOLICITUD] Filtrar papelera de empresas por eliminadoPorId: {}", eliminadoPorId);

        List<ConfigEmpresaPapeleraResponseDTO> resultado = configEmpresaService.listarPapeleraPorEliminadoPor(eliminadoPorId);

        if (resultado.isEmpty()) {
            log.info("📤 [RESPUESTA] No hay empresas en papelera eliminadas por: {} — lista vacia", eliminadoPorId);
            return ResponseEntity.ok(List.of());
        }

        log.info("📤 [RESPUESTA] Se retornan {} empresas en papelera eliminadas por: {}", resultado.size(), eliminadoPorId);

        return ResponseEntity.ok(resultado);
    }
}
