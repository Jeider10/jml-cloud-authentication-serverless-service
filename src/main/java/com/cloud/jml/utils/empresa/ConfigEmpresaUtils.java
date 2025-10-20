package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.exception.empresa.ConfigEmpresaPersistenceException;
import com.cloud.jml.model.ConfigEmpresaEntity;
import com.cloud.jml.repository.ConfigEmpresaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class ConfigEmpresaUtils {

    private final ConfigEmpresaRepository configEmpresaRepository;

    public ConfigEmpresaUtils(ConfigEmpresaRepository configEmpresaRepository) {
        this.configEmpresaRepository = configEmpresaRepository;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    /**
     * 💾 Guarda la orden en BD con manejo de excepciones.
     */
    public ConfigEmpresaEntity guardarEmpresaBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            return configEmpresaRepository.save(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error de integridad en base de datos al guardar la empresa", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error al guardar la empresa en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error inesperado al registrar la empresa", e);
        }
    }

    /**
     * 🗑️ Elimina la orden de BD con manejo de excepciones.
     */
    public void eliminarUsuarioBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            configEmpresaRepository.delete(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error de integridad en base de datos al eliminar la empresa", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error al eliminar la empresa en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error inesperado al eliminar la empresa", e);
        }
    }

    public ConfigEmpresaEntity validarExistenciaEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📌 Inicia validación de existencia de la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        Optional<ConfigEmpresaEntity> configEmpresaExistencia = configEmpresaRepository.findByNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());

        if (configEmpresaExistencia.isEmpty()) {
            log.warn("⚠️ Empresa na encontrada: {}", configEmpresaRequestDTO.getNombreEmpresa());
            throw new ConfigEmpresaNotFoundException(configEmpresaRequestDTO.getNombreEmpresa());
        }

        ConfigEmpresaEntity configEmpresaEntity = configEmpresaExistencia.get();
        log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nic: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNic());

        log.info("✅ [FINALIZADO] Empresa verificada correctamente para actualización: {}", configEmpresaEntity.getNombreEmpresa());

        return configEmpresaEntity;
    }

    public void actualizarDatosEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO, ConfigEmpresaEntity configEmpresaEntity) {
        log.info("📌 Actualizando datos de la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        // Actualizamos solo los campos permitidos
        configEmpresaEntity.setNic(configEmpresaRequestDTO.getNic());
        configEmpresaEntity.setNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());
        configEmpresaEntity.setDireccion(configEmpresaRequestDTO.getDireccion());
        configEmpresaEntity.setTelefono(configEmpresaRequestDTO.getTelefono());
        configEmpresaEntity.setMensaje(configEmpresaRequestDTO.getMensaje());
        configEmpresaEntity.setLogo(configEmpresaRequestDTO.getLogo());

        log.info("✅ Datos de la empresa actualizados correctamente: {}", configEmpresaEntity.getNombreEmpresa());
    }
}
