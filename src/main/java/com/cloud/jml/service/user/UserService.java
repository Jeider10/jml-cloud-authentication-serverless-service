package com.cloud.jml.service.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.exception.user.UserDuplicationException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.user.UserRepository;
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
        log.info("🔍 [CONSULTA] Inicio de creacion de usuario: {} con identificacion: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());

        Optional<UserEntity> existingUserAndRole = userRepository.findByUserNameAndRoleCode(userRequestDTO.getUserName(), userRequestDTO.getRoleCode());

        if (existingUserAndRole.isPresent()) {
            log.warn("❌ [ERROR] Usuario: {} ya existe con ese role: {}", userRequestDTO.getUserName(), userRequestDTO.getRoleCode());
            throw new UserDuplicationException(userRequestDTO.getUserName());
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de usuario");
        UserEntity userEntity = mapper.mapRequestDtoToEntity(userRequestDTO);
        log.info("📦 [MAPEO] Usuario mapeado a entidad. nombre: {}, identificacion: {}", userEntity.getUserName(), userEntity.getIdentificacion());

        userUtils.validarUnicoAdministrador(userEntity);

        UserEntity guardarUsuario = userUtils.guardarUsuarioBD(userEntity);
        log.info("💾 [PERSISTENCIA] Usuario guardado exitosamente. nombre: {}, identificacion: {}", guardarUsuario.getUserName(), guardarUsuario.getIdentificacion());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (registrarUsuario)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(guardarUsuario);
        log.info("📦 [MAPEO] Usuario mapeado a DTO. nombre: {} con identificacion: {}",
                guardarUsuario.getUserName(), guardarUsuario.getIdentificacion());

        log.info("✅ [FINALIZADO] Usuario creado correctamente: {} con identificacion {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return userResponseDTO;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorIdentificacion(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con identificacion: {}", userRequestDTO.getIdentificacion());

        Optional<UserEntity> userEntity = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con identificacion: {} no encontrado.", userRequestDTO.getIdentificacion());
            return null;
        }

        log.info("📦 [ENCONTRADO] Usuario encontrado -> con identificacion: {}", userEntity.get().getIdentificacion());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (obtenerUsuarioPorIdentificacion)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(userEntity.get());
        log.info("📦 [MAPEO] Usuario mapeado a DTO. identificacion: {}", userResponseDTO.getIdentificacion());

        log.info("✅ [FINALIZADO] Usuario obtenido correctamente con identificacion: {}", userResponseDTO.getIdentificacion());

        return userResponseDTO;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorUserName(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con userName: {}", userRequestDTO.getUserName());

        List<UserEntity> userEntity = userRepository.findByUserNameContainingIgnoreCase(userRequestDTO.getUserName());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con userName: {} no encontrado.", userRequestDTO.getUserName());
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de usuarios a DTOs (userName: {})", userEntity.size(), userRequestDTO.getUserName());

        // convertir a stream
        Stream<UserEntity> streamUsuarios = userEntity.stream();

        // mapear entidades a DTOs
        Stream<UserResponseDTO> streamDto = streamUsuarios.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<UserResponseDTO> usuariosResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Usuarios encontrados con userName: {}. Total encontrados: {}", userRequestDTO.getUserName(), usuariosResponse.size());

        return usuariosResponse;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorNombres(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con nombres: {}", userRequestDTO.getNombres());

        List<UserEntity> userEntity = userRepository.findByNombresContainingIgnoreCase(userRequestDTO.getNombres());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con nombres: {} no encontrado.", userRequestDTO.getNombres());
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de usuarios a DTOs (nombres: {})", userEntity.size(), userRequestDTO.getNombres());

        // convertir a stream
        Stream<UserEntity> streamUsuarios = userEntity.stream();

        // mapear entidades a DTOs
        Stream<UserResponseDTO> streamDto = streamUsuarios.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<UserResponseDTO> usuariosResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Usuarios encontrados con nombres: {}. Total encontrados: {}", userRequestDTO.getNombres(), usuariosResponse.size());

        return usuariosResponse;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorApellidos(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con apellidos: {}", userRequestDTO.getApellidos());

        List<UserEntity> userEntity = userRepository.findByApellidosContainingIgnoreCase(userRequestDTO.getApellidos());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con apellidos: {} no encontrado.", userRequestDTO.getApellidos());
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de usuarios a DTOs (apellidos: {})", userEntity.size(), userRequestDTO.getApellidos());

        // convertir a stream
        Stream<UserEntity> streamUsuarios = userEntity.stream();

        // mapear entidades a DTOs
        Stream<UserResponseDTO> streamDto = streamUsuarios.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<UserResponseDTO> usuariosResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Usuarios encontrados con apellidos: {}. Total encontrados: {}", userRequestDTO.getApellidos(), usuariosResponse.size());

        return usuariosResponse;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorRoleCode(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con roleCode: {}", userRequestDTO.getRoleCode());

        Optional<UserEntity> userEntity = userRepository.findByRoleCode(userRequestDTO.getRoleCode());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con roleCode: {} no encontrado.", userRequestDTO.getRoleCode());
            return null;
        }

        log.info("📦 [ENCONTRADO] Usuario encontrado -> con roleCode: {}", userEntity.get().getRoleCode());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (obtenerUsuarioPorRoleCode)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(userEntity.get());
        log.info("📦 [MAPEO] Usuario mapeado a DTO. roleCode: {}", userResponseDTO.getRoleCode());

        log.info("✅ [FINALIZADO] Usuario obtenido correctamente: con roleCode: {}", userResponseDTO.getRoleCode());

        return userResponseDTO;
    }

    public List<UserResponseDTO> obtenerUsuarioPorRoleName(String roleName) {
        log.info("🔍 [CONSULTA] Inicio de busqueda de usuario con roleName: {}", roleName);

        List<UserEntity> userEntity = userRepository.findByRoleNameContainingIgnoreCase(roleName);

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con roleName: {} no encontrado.", roleName);
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de usuarios a DTOs (roleName: {})", userEntity.size(), roleName);

        // convertir a stream
        Stream<UserEntity> streamUsuarios = userEntity.stream();

        // mapear entidades a DTOs
        Stream<UserResponseDTO> streamDto = streamUsuarios.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<UserResponseDTO> usuariosResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Usuarios encontrados con roleName: {}. Total encontrados: {}", roleName, usuariosResponse.size());

        return usuariosResponse;
    }

    @Transactional
    public UserResponseDTO actualizarUsuario(UserRequestDTO userRequestDTO, String userLogin) {
        log.info("🔍 [CONSULTA] Inicio de actualizacion de usuario: {}", userRequestDTO.getUserName());

        // Paso 1: Validar existencia
        UserEntity userEntity = userUtils.validarExistenciaUsuario(userRequestDTO);

        // Paso 2: Actualizar datos
        mapper.actualizarDatosUsuario(userRequestDTO, userEntity, userLogin);

        // Paso 3: Guardar cambios en la BD
        UserEntity actualizado = userUtils.guardarUsuarioBD(userEntity);
        log.info("💾 [PERSISTENCIA] Usuario actualizado correctamente: {} con identificacion: {}", actualizado.getUserName(), actualizado.getIdentificacion());

        // Paso 4: Mapear a DTO
        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (actualizarUsuario)");
        UserResponseDTO userResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📦 [MAPEO] Usuario mapeado a DTO. nombre: {}, identificacion: {}, direccion: {}",
                userResponseDTO.getUserName(), userResponseDTO.getIdentificacion(), userResponseDTO.getDireccion());

        log.info("✅ [FINALIZADO] Actualizacion de usuario completada: {} con identificacion: {}", userResponseDTO.getUserName(), userResponseDTO.getIdentificacion());

        return userResponseDTO;
    }

    @Transactional
    public void eliminarUsuario(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de eliminacion de usuario: {} con identificacion: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());

        Optional<UserEntity> usuarioExistente = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (usuarioExistente.isPresent()) {
            UserEntity userEntity = usuarioExistente.get();
            log.info("📦 [ENCONTRADO] Usuario localizado -> {} con identificacion: {}", userEntity.getUserName(), userEntity.getIdentificacion());

            userUtils.eliminarUsuarioBD(userEntity);
            log.info("🗑️ [ELIMINADO] Usuario eliminado correctamente -> {} con identificacion: {}", userEntity.getUserName(), userEntity.getIdentificacion());
        } else {
            log.warn("❌ [NO ENCONTRADO] Usuario: {} no encontrado", userRequestDTO.getUserName());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }
    }
}
