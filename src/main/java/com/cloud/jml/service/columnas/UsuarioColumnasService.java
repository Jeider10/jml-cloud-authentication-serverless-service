package com.cloud.jml.service.columnas;

import com.cloud.jml.dto.columnas.UsuarioColumnasRequestDTO;
import com.cloud.jml.dto.columnas.UsuarioColumnasResponseDTO;
import com.cloud.jml.model.columnas.UsuarioColumnasEntity;
import com.cloud.jml.repository.columnas.UsuarioColumnasRepository;
import com.cloud.jml.repository.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioColumnasService {

    private final UsuarioColumnasRepository repository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UsuarioColumnasService(UsuarioColumnasRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        log.info("🔥 UsuarioColumnasService inicializado correctamente.");
    }

    // ─── Obtener config de una seccion para un usuario ────────────────────────
    @Transactional(readOnly = true)
    public UsuarioColumnasResponseDTO obtenerPorUsuarioYSeccion(Long identificacionUsuario, String seccion) {
        log.info("🔍 Obteniendo columnas ocultas para usuario {} seccion {}", identificacionUsuario, seccion);

        Optional<UsuarioColumnasEntity> opt = repository.findByIdentificacionUsuarioAndSeccion(identificacionUsuario, seccion);

        if (opt.isPresent()) {
            return mapToDTO(opt.get());
        }

        // Si no existe configuracion, devolver lista vacia (ninguna columna oculta)
        return new UsuarioColumnasResponseDTO(identificacionUsuario, seccion, List.of());
    }

    // ─── Obtener todas las secciones de un usuario ────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, List<String>> obtenerTodasPorUsuario(Long identificacionUsuario) {
        log.info("🔍 Obteniendo todas las columnas ocultas para usuario {}", identificacionUsuario);

        List<UsuarioColumnasEntity> entidades = repository.findAllByIdentificacionUsuario(identificacionUsuario);

        return entidades.stream()
                .collect(Collectors.toMap(
                        UsuarioColumnasEntity::getSeccion,
                        e -> deserializarColumnas(e.getColumnasOcultas())
                ));
    }

    // ─── Guardar/actualizar config de una seccion ─────────────────────────────
    @Transactional
    public UsuarioColumnasResponseDTO guardar(UsuarioColumnasRequestDTO dto) {
        log.info("💾 Guardando columnas ocultas para usuario {} seccion {}", dto.getIdentificacionUsuario(), dto.getSeccion());

        Optional<UsuarioColumnasEntity> opt = repository.findByIdentificacionUsuarioAndSeccion(
                dto.getIdentificacionUsuario(), dto.getSeccion());

        UsuarioColumnasEntity entidad = opt.orElseGet(() -> {
            UsuarioColumnasEntity nueva = new UsuarioColumnasEntity();
            nueva.setIdentificacionUsuario(dto.getIdentificacionUsuario());
            nueva.setSeccion(dto.getSeccion());
            return nueva;
        });

        List<String> columnas = dto.getColumnasOcultas() != null ? dto.getColumnasOcultas() : List.of();
        entidad.setColumnasOcultas(serializarColumnas(columnas));

        UsuarioColumnasEntity guardado = repository.save(entidad);

        // Actualizar fechaActualizacion del usuario — cualquier cambio de configuracion
        // debe reflejarse en la fecha de modificacion del registro de usuario
        userRepository.findByIdentificacionAndEliminadoFalse(dto.getIdentificacionUsuario())
                .ifPresent(u -> {
                    u.setFechaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                    userRepository.save(u);
                    log.info("🕒 [COLUMNAS] fechaActualizacion actualizada para usuario={}", dto.getIdentificacionUsuario());
                });

        log.info("✅ Columnas ocultas guardadas: usuario={} seccion={} columnas={}", dto.getIdentificacionUsuario(), dto.getSeccion(), columnas);

        return mapToDTO(guardado);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private UsuarioColumnasResponseDTO mapToDTO(UsuarioColumnasEntity entidad) {
        return new UsuarioColumnasResponseDTO(
                entidad.getIdentificacionUsuario(),
                entidad.getSeccion(),
                deserializarColumnas(entidad.getColumnasOcultas())
        );
    }

    private String serializarColumnas(List<String> columnas) {
        try {
            return objectMapper.writeValueAsString(columnas);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando columnas: {}", e.getMessage());
            return "[]";
        }
    }

    private List<String> deserializarColumnas(String json) {
        try {
            if (json == null || json.isBlank()) return new ArrayList<>();
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("❌ Error deserializando columnas: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
