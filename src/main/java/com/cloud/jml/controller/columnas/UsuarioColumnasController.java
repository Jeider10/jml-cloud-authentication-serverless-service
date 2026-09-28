package com.cloud.jml.controller.columnas;

import com.cloud.jml.dto.columnas.UsuarioColumnasRequestDTO;
import com.cloud.jml.dto.columnas.UsuarioColumnasResponseDTO;
import com.cloud.jml.service.columnas.UsuarioColumnasService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/usuario/columnas")
public class UsuarioColumnasController {

    private final UsuarioColumnasService service;

    public UsuarioColumnasController(UsuarioColumnasService service) {
        this.service = service;
        log.info("🔥 UsuarioColumnasController inicializado correctamente.");
    }

    // ─── Obtener config de una seccion para un usuario ────────────────────────
    @GetMapping
    public ResponseEntity<UsuarioColumnasResponseDTO> obtenerPorUsuarioYSeccion(
            @RequestParam("identificacionUsuario") Long identificacionUsuario,
            @RequestParam("seccion") String seccion) {

        log.info("📥 [SOLICITUD] Obtener columnas ocultas: usuario={} seccion={}", identificacionUsuario, seccion);

        UsuarioColumnasResponseDTO response = service.obtenerPorUsuarioYSeccion(identificacionUsuario, seccion);

        return ResponseEntity.ok(response);
    }

    // ─── Obtener todas las secciones de un usuario (para el login) ────────────
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<String>>> obtenerTodasPorUsuario(
            @RequestParam("identificacionUsuario") Long identificacionUsuario) {

        log.info("📥 [SOLICITUD] Obtener todas las columnas ocultas: usuario={}", identificacionUsuario);

        Map<String, List<String>> response = service.obtenerTodasPorUsuario(identificacionUsuario);

        return ResponseEntity.ok(response);
    }

    // ─── Guardar/actualizar config de una seccion ─────────────────────────────
    @PutMapping
    public ResponseEntity<UsuarioColumnasResponseDTO> guardar(
            @RequestBody @Valid UsuarioColumnasRequestDTO dto) {

        log.info("📥 [SOLICITUD] Guardar columnas ocultas: usuario={} seccion={} columnas={}",
                dto.getIdentificacionUsuario(), dto.getSeccion(), dto.getColumnasOcultas());

        UsuarioColumnasResponseDTO response = service.guardar(dto);

        log.info("📤 [RESPUESTA] Columnas ocultas guardadas para usuario={} seccion={}", dto.getIdentificacionUsuario(), dto.getSeccion());

        return ResponseEntity.ok(response);
    }
}
