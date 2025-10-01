package com.cloud.jml.config;

import com.cloud.jml.model.UserEntity;
import com.cloud.jml.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Configuration
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("🔥 CustomUserDetailsService inicializado correctamente.");
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        log.info("📌 Buscando usuario: {}", userName);

        // Buscar el usuario
        Optional<UserEntity> userOpt = userRepository.findByUserName(userName);

        if (userOpt.isEmpty()) {
            log.warn("⚠️ Usuario no encontrado: {}", userName);
            throw new UsernameNotFoundException("⚠️ Usuario no encontrado");
        }

        UserEntity usuario = userOpt.get();

//        UserEntity usuario = userRepository.findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getUserName())
                .password(usuario.getPassword())
                .roles(usuario.getRoleName())
                .build();
    }

    // Para pruebas locales
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Usuario dummy para pruebas
//        UserDetailsService userDetailsService = username -> User.builder()
//                .username("dummy")
//                .password(passwordEncoder.encode("dummy"))
//                .roles("USER")
//                .build();
//
//        log.info("📌 Usuario dummy inicializado en memoria para pruebas.");
//
//        return userDetailsService;
//    }
}
