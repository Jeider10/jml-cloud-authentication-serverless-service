package com.cloud.jml.service.token;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.utils.token.GeneratorTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GeneratorTokenService {

    private final GeneratorTokenUtils generatorTokenUtils;

    public GeneratorTokenService(GeneratorTokenUtils generatorTokenUtils) {
        this.generatorTokenUtils = generatorTokenUtils;
        log.info("🔥 GeneratorTokenService inicializado correctamente.");
    }

    @Transactional
    public String generarToken(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName, String tokenUse) {
        log.info("🔑 [SERVICIO] Generando nuevo token JWT para usuario: {}", authenticationRequestDTO.getUsuario());

        // 1️⃣ Generar token
        String token = generatorTokenUtils.generateToken(authenticationRequestDTO.getUsuario(), roleCode, roleName, tokenUse);
        log.info("🔑 [TOKEN] Token generado correctamente para tipo: {}", tokenUse);

        // 2️⃣ Retornar token
        log.info("📤 [RESPUESTA] Token JWT generado correctamente para el usuario: {}", authenticationRequestDTO.getUsuario());

        return token;
    }
}
