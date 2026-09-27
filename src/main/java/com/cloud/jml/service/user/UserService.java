package com.cloud.jml.service.user;

import com.cloud.jml.dto.user.UserPapeleraResponseDTO;
import com.cloud.jml.dto.user.UserPermisosRequestDTO;
import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.exception.user.UserDuplicationException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.user.UserRepository;
import com.cloud.jml.utils.user.UserMapper;
import com.cloud.jml.utils.user.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final UserUtils userUtils;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper mapper, UserUtils userUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userUtils = userUtils;
        this.passwordEncoder = passwordEncoder;
        log.info("🔥 UserService inicializado correctamente.");
    }

    // ─── Listar activos ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarUsuarios() {
        log.info("🔍 [CONSULTA] Recuperando todos los usuarios activos");

        List<UserEntity> entidades = userRepository.findAllByEliminadoFalse();

        if (entidades.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No se encontraron usuarios activos");
            return List.of();
        }

        List<UserResponseDTO> respuesta = entidades.stream()
                .map(mapper::mapEntityToResponseDto)
                .toList();

        log.info("✅ [FINALIZADO] Total de usuarios activos retornados: {}", respuesta.size());

        return respuesta;
    }

    // ─── Registrar ────────────────────────────────────────────────────────────
    @Transactional
    public UserResponseDTO registrarUsuario(UserRequestDTO userRequestDTO, String creadoPor) {
        log.info("🔍 [SOLICITUD] Creando usuario: {} con identificacion: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());

        // Validar que la contraseña no venga vacia al crear un usuario nuevo
        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("El campo 'password' es obligatorio al registrar un usuario");
        }

        Optional<UserEntity> existingUserAndRole = userRepository.findByUserNameAndRoleCode(userRequestDTO.getUserName(), userRequestDTO.getRoleCode());

        if (existingUserAndRole.isPresent()) {
            log.warn("❌ [ERROR] Usuario: {} ya existe con ese role: {}", userRequestDTO.getUserName(), userRequestDTO.getRoleCode());
            throw new UserDuplicationException(userRequestDTO.getUserName());
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de usuario");
        UserEntity userEntity = mapper.mapRequestDtoToEntity(userRequestDTO);
        log.info("📦 [MAPEO] Usuario mapeado a entidad. nombre: {}, identificacion: {}", userEntity.getUserName(), userEntity.getIdentificacion());

        userEntity.setCreadoPor(creadoPor);

        userUtils.validarUnicoAdministrador(userEntity);

        UserEntity guardado = userUtils.guardarUsuarioBD(userEntity);

        log.info("💾 [PERSISTENCIA] Usuario guardado: {}", guardado.getIdentificacion());

        return mapper.mapEntityToResponseDto(guardado);
    }

    // ─── Buscar por identificacion ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorIdentificacion(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Buscando usuario con identificacion: {}", userRequestDTO.getIdentificacion());

        Optional<UserEntity> userEntity = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con identificacion: {} no encontrado.", userRequestDTO.getIdentificacion());
            return null;
        }

        return mapper.mapEntityToResponseDto(userEntity.get());
    }

    // ─── Buscar por userName ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorUserName(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Buscando usuario con userName: {}", userRequestDTO.getUserName());

        List<UserEntity> entidades = userRepository.findByUserNameContainingIgnoreCaseAndEliminadoFalse(userRequestDTO.getUserName());

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Buscar por nombres ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorNombres(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Buscando usuario con nombres: {}", userRequestDTO.getNombres());

        List<UserEntity> entidades = userRepository.findByNombresContainingIgnoreCaseAndEliminadoFalse(userRequestDTO.getNombres());

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Buscar por apellidos ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorApellidos(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Buscando usuario con apellidos: {}", userRequestDTO.getApellidos());

        List<UserEntity> entidades = userRepository.findByApellidosContainingIgnoreCaseAndEliminadoFalse(userRequestDTO.getApellidos());

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Buscar por roleCode ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorRoleCode(UserRequestDTO userRequestDTO) {
        log.info("🔍 [CONSULTA] Buscando usuario con roleCode: {}", userRequestDTO.getRoleCode());

        Optional<UserEntity> userEntity = userRepository.findByRoleCode(userRequestDTO.getRoleCode());

        if (userEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario con roleCode: {} no encontrado.", userRequestDTO.getRoleCode());
            return null;
        }

        return mapper.mapEntityToResponseDto(userEntity.get());
    }

    // ─── Buscar por roleName ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorRoleName(String roleName) {
        log.info("🔍 [CONSULTA] Buscando usuario con roleName: {}", roleName);

        List<UserEntity> entidades = userRepository.findByRoleNameContainingIgnoreCaseAndEliminadoFalse(roleName);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Buscar por fecha de creacion ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorFechaCreacion(String fechaInicio, String fechaFin) {

        LocalDateTime inicio = userUtils.parsearFechaInicio(fechaInicio);
        LocalDateTime fin = userUtils.parsearFechaFin(fechaFin);

        List<UserEntity> entidades = userRepository.findByFechaCreacionBetweenAndEliminadoFalse(inicio, fin);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Buscar por fecha de actualizacion ───────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerUsuarioPorFechaActualizacion(String fechaInicio, String fechaFin) {

        LocalDateTime inicio = userUtils.parsearFechaInicio(fechaInicio);
        LocalDateTime fin = userUtils.parsearFechaFin(fechaFin);

        List<UserEntity> entidades = userRepository.findByFechaActualizacionBetweenAndEliminadoFalse(inicio, fin);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToResponseDto).toList();
    }

    // ─── Resetear password ────────────────────────────────────────────────────
    @Transactional
    public void resetearPassword(String userName, String newPassword) {
        log.info("🔑 [RESET] Reseteando password para usuario: {}", userName);

        Optional<UserEntity> optionalUser = userRepository.findByUserName(userName);

        if (optionalUser.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Usuario no encontrado: {}", userName);
            throw new UserNotFoundException(userName);
        }

        UserEntity userEntity = optionalUser.get();

//        userEntity.setPassword(newPassword);
//        userEntity.setFechaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // Encodear siempre con BCrypt
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setFechaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // Encriptar la nueva contraseña
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        userEntity.setPassword(encodedPassword);
//        userEntity.setFechaActualizacion(LocalDateTime.now());

        userRepository.save(userEntity);

        log.info("✅ [RESET] Password reseteado correctamente para usuario: {}", userName);
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public UserResponseDTO actualizarUsuario(UserRequestDTO userRequestDTO, String userLogin) {
        log.info("🔍 [SOLICITUD] Actualizando usuario: {}", userRequestDTO.getUserName());

        UserEntity entidad = userUtils.validarExistenciaUsuario(userRequestDTO);
        mapper.actualizarDatosUsuario(userRequestDTO, entidad, userLogin);
        UserEntity actualizado = userUtils.guardarUsuarioBD(entidad);

        log.info("✅ [FINALIZADO] Usuario actualizado: {}", actualizado.getIdentificacion());

        return mapper.mapEntityToResponseDto(actualizado);
    }

    // ─── Soft delete (a papelera) ─────────────────────────────────────────────
    @Transactional
    public void eliminarUsuario(Long identificacion, String eliminadoPorId, String eliminadoPorNombre) {
        log.info("🔍 [SOLICITUD] Enviando a papelera usuario con identificacion: {}", identificacion);

        UserEntity entidad = userRepository.findByIdentificacionAndEliminadoFalse(identificacion)
                .orElseThrow(() -> new UserNotFoundException(identificacion));

        entidad.setEliminado(true);
        entidad.setFechaEliminacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entidad.setEliminadoPorId(eliminadoPorId);
        entidad.setEliminadoPorNombre(eliminadoPorNombre);

        userUtils.guardarUsuarioBD(entidad);

        log.info("🗑️ [PAPELERA] Usuario {} enviado a papelera por: {}", identificacion, eliminadoPorNombre);
    }

    // ─── Listar papelera ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserPapeleraResponseDTO> listarPapelera() {
        log.info("🔍 [CONSULTA] Listando usuarios en papelera");

        List<UserEntity> entidades = userRepository.findAllByEliminadoTrue();

        if (entidades.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No hay usuarios en papelera");
            return List.of();
        }

        List<UserPapeleraResponseDTO> respuesta = entidades.stream()
                .map(mapper::mapEntityToPapeleraDto)
                .toList();

        log.info("✅ [FINALIZADO] Total de usuarios en papelera: {}", respuesta.size());

        return respuesta;
    }

    // ─── Restaurar desde papelera ─────────────────────────────────────────────
    @Transactional
    public UserResponseDTO restaurarUsuario(Long identificacion) {
        log.info("🔍 [SOLICITUD] Restaurando usuario con identificacion: {}", identificacion);

        UserEntity entidad = userRepository.findByIdentificacionAndEliminadoTrue(identificacion)
                .orElseThrow(() -> {
                    log.warn("❌ [RESULTADO] Usuario no encontrado en papelera: {}", identificacion);
                    return new UserNotFoundException(identificacion);
                });

        entidad.setEliminado(false);
        entidad.setFechaEliminacion(null);
        entidad.setEliminadoPorId(null);
        entidad.setEliminadoPorNombre(null);
        entidad.setFechaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        UserEntity restaurado = userUtils.guardarUsuarioBD(entidad);

        log.info("✅ [FINALIZADO] Usuario restaurado: {}", restaurado.getIdentificacion());

        return mapper.mapEntityToResponseDto(restaurado);
    }

    // ─── Eliminar definitivamente ─────────────────────────────────────────────
    @Transactional
    public void eliminarDefinitivo(Long identificacion) {
        log.info("🔍 [SOLICITUD] Eliminando definitivamente usuario con identificacion: {}", identificacion);

        UserEntity entidad = userRepository.findByIdentificacionAndEliminadoTrue(identificacion)
                .orElseThrow(() -> {
                    log.warn("❌ [RESULTADO] Usuario no encontrado en papelera: {}", identificacion);
                    return new UserNotFoundException(identificacion);
                });

        userUtils.eliminarUsuarioBD(entidad);

        log.info("🗑️ [ELIMINADO] Usuario eliminado definitivamente: {}", identificacion);
    }

    // ─── Actualizar permisos granulares ──────────────────────────────────────
    @Transactional
    public UserResponseDTO actualizarPermisos(UserPermisosRequestDTO dto) {
        log.info("🔍 [SOLICITUD] Actualizando permisos del usuario con identificacion: {}", dto.getIdentificacion());

        UserEntity entidad = userRepository.findByIdentificacionAndEliminadoFalse(dto.getIdentificacion())
                .orElseThrow(() -> new UserNotFoundException(dto.getIdentificacion()));

        entidad.setPermisoNuevaVenta(dto.isPermisoNuevaVenta());
        entidad.setPermisoClientes(dto.isPermisoClientes());
        entidad.setPermisoProveedores(dto.isPermisoProveedores());
        entidad.setPermisoProductos(dto.isPermisoProductos());
        entidad.setPermisoHistorial(dto.isPermisoHistorial());
        entidad.setPermisoPapelera(dto.isPermisoPapelera());
        entidad.setFechaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        UserEntity guardado = userUtils.guardarUsuarioBD(entidad);

        log.info("✅ [FINALIZADO] Permisos actualizados para usuario: {}", guardado.getIdentificacion());

        return mapper.mapEntityToResponseDto(guardado);
    }

    // ─── Filtrar papelera por fecha de eliminacion ────────────────────────────
    @Transactional(readOnly = true)
    public List<UserPapeleraResponseDTO> listarPapeleraPorFecha(String fechaInicio, String fechaFin) {

        LocalDateTime inicio = userUtils.parsearFechaInicio(fechaInicio);
        LocalDateTime fin = userUtils.parsearFechaFin(fechaFin);

        List<UserEntity> entidades = userRepository.findByFechaEliminacionBetweenAndEliminadoTrue(inicio, fin);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToPapeleraDto).toList();
    }

    // ─── Filtrar papelera por quien elimino ───────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserPapeleraResponseDTO> listarPapeleraPorEliminadoPor(String eliminadoPorId) {

        List<UserEntity> entidades = userRepository.findByEliminadoPorIdContainingIgnoreCaseAndEliminadoTrue(eliminadoPorId);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToPapeleraDto).toList();
    }
}
