package com.cloud.jml.config.init;

import com.cloud.jml.model.role.RoleEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.role.RoleRepository;
import com.cloud.jml.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Inicializa datos por defecto al arrancar la aplicacion.
 * <p>
 * Crea:
 * - Rol ADMIN   (roleCode=1) si no existe.
 * - Rol CAJERO  (roleCode=2) si no existe.
 * - Rol USUARIO (roleCode=3) si no existe.
 * - Usuario administrador por defecto si no existe ningun usuario con userName="admin".
 * <p>
 * Si el usuario admin ya existe pero su contraseña NO esta encodeada con BCrypt,
 * la actualiza automaticamente con el hash correcto.
 * <p>
 * Credenciales por defecto:
 * usuario  : admin
 * password : admin
 * email    : admin@admin.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("🔧 [DataInitializer] Verificando datos por defecto...");

        crearRolSiNoExiste(1, "ADMIN", "Administrador del sistema");
        crearRolSiNoExiste(2, "CAJERO", "Cajero / vendedor");
        crearRolSiNoExiste(3, "USUARIO", "Usuario estandar");

        crearORepararAdmin();

        log.info("✅ [DataInitializer] Inicializacion completada.");
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void crearRolSiNoExiste(int roleCode, String roleName, String descripcion) {
        if (roleRepository.findByRoleCode(roleCode).isEmpty()) {
            RoleEntity rol = new RoleEntity();
            rol.setRoleCode(roleCode);
            rol.setRoleName(roleName);
            rol.setDescripcion(descripcion);
            rol.setFechaCreacion(LocalDateTime.now());
            rol.setFechaActualizacion(LocalDateTime.now());
            roleRepository.save(rol);
            log.info("✅ [DataInitializer] Rol creado → roleCode={} roleName={}", roleCode, roleName);
        } else {
            log.info("ℹ️  [DataInitializer] Rol ya existe → roleCode={} roleName={}", roleCode, roleName);
        }
    }

    private void crearORepararAdmin() {
        Optional<UserEntity> existente = userRepository.findByUserName("admin");

        if (existente.isPresent()) {
            UserEntity admin = existente.get();
            String pwd = admin.getPassword();

            // Si la contraseña NO esta encodeada con BCrypt, la corregimos
            if (pwd == null || (!pwd.startsWith("$2a$") && !pwd.startsWith("$2b$"))) {
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setFechaActualizacion(LocalDateTime.now());
                admin.setHistorialUltimoActualizado("Contraseña re-encodeada con BCrypt por DataInitializer");
                userRepository.save(admin);
                log.info("🔑 [DataInitializer] Contraseña del admin corregida con BCrypt.");
            } else {
                log.info("ℹ️  [DataInitializer] Usuario 'admin' ya existe con contraseña BCrypt valida.");
            }
            return;
        }

        // No existe — crearlo desde cero
        UserEntity admin = new UserEntity();
        admin.setIdentificacion(1L);
        admin.setNombres("Administrador");
        admin.setApellidos("Sistema");
        admin.setUserName("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoleCode(1);
        admin.setRoleName("ADMIN");
        admin.setEmail("admin@admin.com");
        admin.setTelefono("3000000000");
        admin.setDireccion("Centro");
        admin.setFechaCreacion(LocalDateTime.now());
        admin.setFechaActualizacion(LocalDateTime.now());
        admin.setHistorialUltimoActualizado("Creado por DataInitializer");

        userRepository.save(admin);
        log.info("✅ [DataInitializer] Usuario admin creado → userName=admin | email=admin@admin.com");
        log.warn("⚠️  [DataInitializer] Recuerda cambiar la contraseña del admin en produccion.");
    }
}
