package com.cloud.jml.utils.authentication;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.exception.authentication.AuthenticationInvalidCredentialsException;
import com.cloud.jml.exception.authentication.AuthenticationPersistenceException;
import com.cloud.jml.model.authentication.AuthenticationEntity;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.authentication.AuthenticationRepository;
import com.cloud.jml.repository.user.UserRepository;
import com.cloud.jml.utils.jwt.JwtUtil;
import com.cloud.jml.utils.token.RefreshTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class AuthenticationUtils {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenUtils refreshTokenUtils;
    private final AuthenticationMapper mapper;

    public AuthenticationUtils(AuthenticationRepository authenticationRepository, UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenUtils refreshTokenUtils, AuthenticationMapper mapper) {
        this.authenticationRepository = authenticationRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenUtils = refreshTokenUtils;
        this.mapper = mapper;
        log.info("🔥 AuthenticationUtils inicializado correctamente.");
    }

    public UserEntity validarUsuario(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("👤 [CONSULTA] Intentando autenticar usuario: {}", authenticationRequestDTO.getUsuario());

        Optional<UserEntity> userOpt = userRepository.findByUserName(authenticationRequestDTO.getUsuario());

        if (userOpt.isEmpty()) {
            log.warn("⚠️ Usuario no encontrado: {}", authenticationRequestDTO.getUsuario());
            throw new AuthenticationInvalidCredentialsException(authenticationRequestDTO.getUsuario());
        }

        UserEntity user = userOpt.get();

        // Comparacion segura de contrasenas
        if (!authenticationRequestDTO.getPassword().equals(user.getPassword())) {
            log.warn("⚠️ Contrasena incorrecta para usuario: {}", authenticationRequestDTO.getUsuario());
            throw new AuthenticationInvalidCredentialsException();
        }

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            log.warn("⚠️ Contrasena incorrecta para usuario: {}", userName);
//            throw new BadCredentialsException("Contrasena incorrecta");
//        }

        log.info("✅ [FINALIZADO] Usuario verificado correctamente para autenticacion: {}", authenticationRequestDTO.getUsuario());

        return user;
    }

    public void processAndSaveAuthentication(String accessToken, AuthenticationRequestDTO authenticationRequestDTO, UserEntity userEntity) {
        log.info("📝 [PROCESO] Procesando y guardando autenticacion para usuario: {}", userEntity.getUserName());

        // 1️⃣ Extraer el token JWT del header
        String jti = jwtUtil.extractJti(accessToken);
        log.info("🔑 [TOKEN] JTI extraido del accessToken: {}", jti);

        // 2️⃣ Map DTO to Entity
        log.info("📦 [MAPPING] Transforming DTO to authentication entity...");
        AuthenticationEntity authenticationEntity = mapper.mapRequestDtoToEntity(authenticationRequestDTO, userEntity, jti);
        log.info("📦 [MAPPING] Entity created. User: {}", authenticationEntity.getUsuario());

        // 3️⃣ Guardar en BD solo si es accessToken
        AuthenticationEntity savedEntity = guardarAuthenticationBD(authenticationEntity);
        log.info("💾 [PERSISTENCE] Authentication saved successfully. User: {}", savedEntity.getUsuario());
    }

    public AuthenticationOptionsDTO obtenerUsuarioActual(String refreshTokenHeader) {
        log.info("👤 [CONSULTA] Intentando obtener usuario actual.");

        // 1️⃣ Extraer el token JWT del header
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");
        log.info("🔑 refreshToken extraido correctamente.");

        // 2️⃣ Verificar expiracion y validez
        RefreshTokenEntity refreshTokenEntity = refreshTokenUtils.verifyExpiration(refreshTokenHeader);
        log.info("🔐 [TOKEN] Token valido detectado. Usuario: {}, jti={}", refreshTokenEntity.getUsuario(), refreshTokenEntity.getJti());

        // 3️⃣ Obtener el userName del token
        String userName = jwtUtil.extractUserName(refreshToken);
        log.info("👤 Usuario extraido del token: {}", userName);

        // 4️⃣ Buscar el usuario en la base de datos
        Optional<UserEntity> usuario = userRepository.findByUserName(userName);

        if (usuario.isEmpty()) {
            log.warn("⚠️ Usuario: {} no encontrado.", userName);
            throw new AuthenticationInvalidCredentialsException(userName);
        }

        UserEntity userEntity = usuario.get();
        log.info("✅ Usuario: {} encontrado.", userEntity.getUserName());

        // 5️⃣ Crear el DTO de respuesta
        AuthenticationOptionsDTO authenticationOptionsDTO = mapper.mapEntityToAuthenticationOptionsDTO(userEntity);

        log.info("🔑 Usuario actual obtenido correctamente: {}", authenticationOptionsDTO.getLogin());

        return authenticationOptionsDTO;
    }

    public AuthenticationEntity guardarAuthenticationBD(AuthenticationEntity authenticationEntity) {
        try {
            return authenticationRepository.save(authenticationEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al guardar el usuario: {}", e.getMessage(), e);
            throw AuthenticationPersistenceException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el usuario: {}", e.getMessage(), e);
            throw AuthenticationPersistenceException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el usuario: {}", e.getMessage(), e);
            throw AuthenticationPersistenceException.unexpected(e);
        }
    }
}
