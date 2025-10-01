package com.cloud.jml.utils.authentication;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.exception.authentication.AuthenticationInvalidCredentialsException;
import com.cloud.jml.model.UserEntity;
import com.cloud.jml.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class AuthenticationUtils {

    private final UserRepository userRepository;

    public AuthenticationUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("🔥 AuthenticationUtils inicializado correctamente.");
    }

    public UserEntity validarUsuario(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("📌 Intentando autenticar usuario: {}", authenticationRequestDTO.getUsuario());

        // Buscar el usuario
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

        log.info("✅ Usuario autenticado correctamente: {}", authenticationRequestDTO.getUsuario());

        return user;
    }
}
