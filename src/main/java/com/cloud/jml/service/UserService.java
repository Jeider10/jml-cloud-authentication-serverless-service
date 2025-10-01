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

    @Transactional
    public UserResponseDTO registrarUsuario(UserRequestDTO userRequestDTO) {
        log.info("📌 Intentando registrar usuario: {}", userRequestDTO.getUserName());

        // Validar si el usuario ya existe con el mismo rol
        Optional<UserEntity> existingUserAndRole = userRepository.findByUserNameAndRoleCode(userRequestDTO.getUserName(), userRequestDTO.getRoleCode());

        if (existingUserAndRole.isPresent()) {
            log.warn("⚠️ El usuario ya existe con ese role.");
            throw new UserDuplicationException(userRequestDTO.getUserName());
        }

        // Mapeo de DTO a Entity
        UserEntity userEntity = mapper.mapRequestDtoToEntity(userRequestDTO);

        // Guardamos en la base de datos
        UserEntity guardado = userRepository.save(userEntity);
        log.info("✅ Usuario: {} registrado exitosamente.", guardado.getUserName());

        // Mapeo de Entity DTO
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(guardado);
        log.info("📌 Finaliza creación de Usuario: {}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    @Transactional
    public UserResponseDTO actualizarUsuario(UserRequestDTO userRequestDTO) {
        log.info("📌 Intentando actualizar el usuario: {}", userRequestDTO.getUserName());

        // Paso 1: Validar existencia
        UserEntity userEntity = userUtils.validarExistenciaUsuario(userRequestDTO);

        // Paso 2: Actualizar datos
        userUtils.actualizarDatosUsuario(userRequestDTO, userEntity);

        // Paso 3: Guardar cambios en la BD
        UserEntity actualizado = userRepository.save(userEntity);
        log.info("✅ Usuario actualizado exitosamente: {}", actualizado.getUserName());

        // Paso 4: Mapear a DTO
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📌 Finaliza actualización de Usuario: {}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarUsuarios() {
        log.info("📌 Obteniendo todos los usuarios existentes.");

        // Paso 1: Obtener entidades desde la BD
        List<UserEntity> userEntity = userRepository.findAll();

        // Paso 2: Convertir a Stream
        Stream<UserEntity> entityStream = userEntity.stream();

        // Paso 3: Mapear cada entidad a DTO
        Stream<UserResponseDTO> streamDto = entityStream.map(mapper::mapEntityToResponseDto);

        // Paso 4: Convertir a lista final
        List<UserResponseDTO> userResponseDTOList = streamDto.toList();

        log.info("✅ Finaliza petición para obtener todos los usuarios: {}", userResponseDTOList.size());

        return userResponseDTOList;
    }

    @Transactional
    public void eliminarUsuario(UserRequestDTO userRequestDTO) {
        log.info("📌 Intentando eliminar usuario con userName: {}", userRequestDTO.getIdentificacion());

        Optional<UserEntity> userOptional = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            log.info("📌 Usuario encontrado con userName: {}", userRequestDTO.getUserName());
            userRepository.delete(userEntity);
            log.info("✅ Usuario eliminado exitosamente: {}", userRequestDTO.getUserName());
        } else {
            log.warn("⚠️ Usuario no encontrado con userName: {}", userRequestDTO.getUserName());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }
    }
}
