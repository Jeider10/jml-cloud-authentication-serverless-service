package com.cloud.jml.utils.authentication;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.model.authentication.AuthenticationEntity;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.utils.jwt.JwtProperties;
import com.cloud.jml.utils.user.UserFormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class AuthenticationMapper {

    private final JwtProperties jwtProperties;
    private final UserFormatearFecha userFormatearFecha;

    public AuthenticationMapper(JwtProperties jwtProperties, UserFormatearFecha userFormatearFecha) {
        this.jwtProperties = jwtProperties;
        this.userFormatearFecha = userFormatearFecha;
        log.info("🔥 AuthenticationMapper inicializado correctamente.");
    }

    public AuthenticationEntity mapRequestDtoToEntity(AuthenticationRequestDTO authenticationRequestDTO, UserEntity userEntity, String jti) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para autenticacion: usuario={}", authenticationRequestDTO.getUsuario());

        AuthenticationEntity authenticationEntity = new AuthenticationEntity();

        authenticationEntity.setUsuario(authenticationRequestDTO.getUsuario());
        authenticationEntity.setRoleCode(userEntity.getRoleCode());
        authenticationEntity.setRoleName(userEntity.getRoleName());
        authenticationEntity.setIdentificacion(userEntity.getIdentificacion());
        authenticationEntity.setNombres(userEntity.getNombres());
        authenticationEntity.setApellidos(userEntity.getApellidos());
        authenticationEntity.setJti(jti);
        authenticationEntity.setFechaCreacion(LocalDateTime.now());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticacion: usuario={}", authenticationEntity.getUsuario());

        return authenticationEntity;
    }

    public AuthenticationOptionsDTO mapEntityToAuthenticationOptionsDTO(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para autenticacion de usuario: nombre={}", userEntity.getUserName());

        AuthenticationOptionsDTO authenticationOptionsDTO = new AuthenticationOptionsDTO();

        authenticationOptionsDTO.setLogin(userEntity.getUserName());
        authenticationOptionsDTO.setRoleCode(userEntity.getRoleCode());
        authenticationOptionsDTO.setRoleName(userEntity.getRoleName());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticacion de usuario: nombre={}", authenticationOptionsDTO.getLogin());

        return authenticationOptionsDTO;
    }

    public UserResponseDTO buildUserResponseDTO(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando construccion de DTO de respuesta para usuario: {}", userEntity.getUserName());

        // 🔹 Construimos el UserResponseDTO
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setIdentificacion(userEntity.getIdentificacion());
        userResponseDTO.setNombres(userEntity.getNombres());
        userResponseDTO.setApellidos(userEntity.getApellidos());
        userResponseDTO.setUserName(userEntity.getUserName());
        userResponseDTO.setRoleCode(userEntity.getRoleCode());
        userResponseDTO.setRoleName(userEntity.getRoleName());
        userResponseDTO.setTelefono(userEntity.getTelefono());
        userResponseDTO.setEmail(userEntity.getEmail());
        userResponseDTO.setDireccion(userEntity.getDireccion());
        userResponseDTO.setHistorialUltimoActualizado(userEntity.getHistorialUltimoActualizado());

        // 🕓 Formateo de fechas
        userFormatearFecha.asignarFechasFormateadas(userEntity, userResponseDTO);

        log.info("✅ [MAPEO] Mapeo completado de DTO de respuesta para usuario: {}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    public AuthenticationOptionsDTO mapEntityToAuthenticationOptionsDTO(RefreshTokenEntity refreshTokenEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para refresh token de usuario: nombre={}", refreshTokenEntity.getUsuario());

        AuthenticationOptionsDTO authenticationOptionsDTO = new AuthenticationOptionsDTO();

        authenticationOptionsDTO.setLogin(refreshTokenEntity.getUsuario());
        authenticationOptionsDTO.setRoleCode(refreshTokenEntity.getRoleCode());
        authenticationOptionsDTO.setRoleName(refreshTokenEntity.getRoleName());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para refresh token de usuario: nombre={}", authenticationOptionsDTO.getLogin());

        return authenticationOptionsDTO;
    }

    public AuthenticationResponseDTO mapAuthenticationResponseDTO(AuthenticationOptionsDTO options, String refreshToken) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para autenticacion: login={}", options.getLogin());

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();

        authenticationResponseDTO.setOptions(options);
        authenticationResponseDTO.setAccessToken("");
        authenticationResponseDTO.setExpiresIn(jwtProperties.getExpiration());
        authenticationResponseDTO.setTokenType("Bearer");
        authenticationResponseDTO.setRefreshToken(refreshToken.replace("Bearer ", ""));
        authenticationResponseDTO.setAuthorization("");

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticacion: login={}", authenticationResponseDTO.getOptions().getLogin());

        return authenticationResponseDTO;
    }

    public AuthenticationResponseDTO mapAuthenticationResponseDTO(AuthenticationOptionsDTO options, String accessToken, String refreshToken, String tokenAuthorization, boolean isRefreshToken) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para autenticacion con refresh token: login={}", options.getLogin());

        long expiration = isRefreshToken ? jwtProperties.getRefreshExpirationMs() : jwtProperties.getExpiration();

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();

        authenticationResponseDTO.setOptions(options);
        authenticationResponseDTO.setAccessToken(accessToken);
        authenticationResponseDTO.setExpiresIn(expiration);
        authenticationResponseDTO.setTokenType("Bearer");
        authenticationResponseDTO.setRefreshToken(refreshToken);
        authenticationResponseDTO.setAuthorization(tokenAuthorization);

        log.info("✅ [MAPEO] Mapeo completado para usuario: {}", options.getLogin());

        return authenticationResponseDTO;
    }
}
