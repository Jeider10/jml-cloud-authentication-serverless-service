package com.cloud.jml.service;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.exception.user.UserDuplicationException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.model.UserEntity;
import com.cloud.jml.repository.UserRepository;
import com.cloud.jml.utils.user.UserMapper;
import com.cloud.jml.utils.user.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final UserUtils userUtils;

    public UserService(UserRepository userRepository, UserMapper mapper, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userUtils = userUtils;
        log.info("🔥 UserService inicializado correctamente.");
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarUsuarios() {
        log.info("🔍 [CONSULTA] Recuperando todos los usuarios desde la base de datos");

        List<UserEntity> userEntity = userRepository.findAll();

        if (userEntity.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No se encontraron usuarios registrados en la base de datos");
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de usuarios a DTOs", userEntity.size());

        // convertir a stream
        Stream<UserEntity> entityStream = userEntity.stream();

        // mapear entidades a DTOs
        Stream<UserResponseDTO> streamDto = entityStream.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<UserResponseDTO> userResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Total de usuarios mapeados y retornados: {}", userResponse.size());

        return userResponse;
    }

    @Transactional
    public UserResponseDTO registrarUsuario(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de creación de usuario: {} con identificación: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());

        Optional<UserEntity> existingUserAndRole = userRepository.findByUserNameAndRoleCode(userRequestDTO.getUserName(), userRequestDTO.getRoleCode());

        if (existingUserAndRole.isPresent()) {
            log.warn("❌ [ERROR] Usuario: {} ya existe con ese role: {}", userRequestDTO.getUserName(), userRequestDTO.getRoleCode());
            throw new UserDuplicationException(userRequestDTO.getUserName());
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de usuario");
        UserEntity userEntity = mapper.mapRequestDtoToEntity(userRequestDTO);
        log.info("📦 [MAPEO] Usuario mapeado a entidad. nombre: {}, identificación: {}", userEntity.getUserName(), userEntity.getIdentificacion());

        UserEntity guardarUsuario = userUtils.guardarUsuarioBD(userEntity);
        log.info("💾 [PERSISTENCIA] Usuario guardado exitosamente. nombre: {}, identificación: {}", guardarUsuario.getUserName(), guardarUsuario.getIdentificacion());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (registrarUsuario)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(guardarUsuario);
        log.info("📦 [MAPEO] Usuario mapeado a DTO. nombre: {} con identificación: {}",
                guardarUsuario.getUserName(), guardarUsuario.getIdentificacion());

        log.info("✅ [FINALIZADO] Usuario creado correctamente: {} con identificación {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return userResponseDTO;
    }

    @Transactional
    public UserResponseDTO actualizarUsuario(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de actualización de usuario: {}", userRequestDTO.getUserName());

        // Paso 1: Validar existencia
        UserEntity userEntity = userUtils.validarExistenciaUsuario(userRequestDTO);

        // Paso 2: Actualizar datos
        userUtils.actualizarDatosUsuario(userRequestDTO, userEntity);

        // Paso 3: Guardar cambios en la BD
        UserEntity actualizado = userUtils.guardarUsuarioBD(userEntity);
        log.info("💾 [PERSISTENCIA] Usuario actualizado correctamente: {} con identificación: {}", actualizado.getUserName(), actualizado.getIdentificacion());

        // Paso 4: Mapear a DTO
        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (actualizarUsuario)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📦 [MAPEO] Usuario mapeado a DTO. nombre: {}, identificación: {}, dirección: {}",
                userResponseDTO.getUserName(), userResponseDTO.getIdentificacion(), userResponseDTO.getDireccion());

        log.info("✅ [FINALIZADO] Actualización de usuario completada: {} con identificación: {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return userResponseDTO;
    }

    @Transactional
    public void eliminarUsuario(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de eliminación de usuario: {} con identificación: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());

        Optional<UserEntity> usuarioExistente = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (usuarioExistente.isPresent()) {
            UserEntity userEntity = usuarioExistente.get();
            log.info("📦 [ENCONTRADO] Usuario localizado -> {} con identificación: {}", userEntity.getUserName(), userEntity.getIdentificacion());

            userUtils.eliminarUsuarioBD(userEntity);
            log.info("🗑️ [ELIMINADO] Usuario eliminado correctamente -> {} con identificación: {}", userEntity.getUserName(), userEntity.getIdentificacion());
        } else {
            log.warn("❌ [NO ENCONTRADO] Usuario: {} no encontrado", userRequestDTO.getUserName());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }
    }
}
