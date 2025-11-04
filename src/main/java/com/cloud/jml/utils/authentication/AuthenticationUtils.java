package com.cloud.jml.utils.authentication;

import com.cloud.jml.utils.jwt.JwtUtil;
import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.exception.authentication.AuthenticationInvalidCredentialsException;
import com.cloud.jml.exception.authentication.AuthenticationPersistenceException;
import com.cloud.jml.model.authentication.AuthenticationEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.authentication.AuthenticationRepository;
import com.cloud.jml.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class AuthenticationUtils {

    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationUtils(AuthenticationRepository authenticationRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.authenticationRepository = authenticationRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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

        // Comparación segura de contraseñas
        if (!authenticationRequestDTO.getPassword().equals(user.getPassword())) {
            log.warn("⚠️ Contraseña incorrecta para usuario: {}", authenticationRequestDTO.getUsuario());
            throw new AuthenticationInvalidCredentialsException();
        }

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            log.warn("⚠️ Contraseña incorrecta para usuario: {}", userName);
//            throw new BadCredentialsException("Contraseña incorrecta");
//        }

        log.info("✅ [FINALIZADO] Usuario verificado correctamente para autenticación: {}", authenticationRequestDTO.getUsuario());

        return user;
    }

    public AuthenticationOptionsDTO obtenerUsuarioActual(String authorizationHeader) {
        log.info("👤 [CONSULTA] Intentando obtener usuario actual: {}", authorizationHeader);

        // 1️⃣ Extraer el token JWT del header
        String token = authorizationHeader.replace("Bearer ", "");
        log.info("🔑 Token extraído: {}", token);

        // 2️⃣ Obtener el userName (o email) del token
        String userName = jwtUtil.extractUserName(token);
        log.info("👤 Usuario extraído del token: {}", userName);

        // 3️⃣ Buscar el usuario en la base de datos
        Optional<UserEntity> usuario = userRepository.findByUserName(userName);

        if (usuario.isEmpty()) {
            log.warn("⚠️ Usuario: {} no encontrado.", userName);
            throw new AuthenticationInvalidCredentialsException(userName);
        }

        log.info("✅ Usuario: {} encontrado.", userName);

        // 4️⃣ Crear el DTO de respuesta
        AuthenticationOptionsDTO options = new AuthenticationOptionsDTO();

        options.setLogin(usuario.get().getUserName());
        options.setRoleCode(usuario.get().getRoleCode());
        options.setRoleName(usuario.get().getRoleName());

        log.info("🔑 Usuario actual obtenido correctamente: {}", options.getLogin());

        return options;
    }

    /**
     * 💾 Guarda la orden en BD con manejo de excepciones.
     */
    public AuthenticationEntity guardarAuthenticationBD(AuthenticationEntity authenticationEntity) {
        try {
            return authenticationRepository.save(authenticationEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar el usuario: {}", e.getMessage(), e);
            throw new AuthenticationPersistenceException("Error de integridad en base de datos al guardar el usuario", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el usuario: {}", e.getMessage(), e);
            throw new AuthenticationPersistenceException("Error al guardar el usuario en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el usuario: {}", e.getMessage(), e);
            throw new AuthenticationPersistenceException("Error inesperado al registrar el usuario", e);
        }
    }
}
