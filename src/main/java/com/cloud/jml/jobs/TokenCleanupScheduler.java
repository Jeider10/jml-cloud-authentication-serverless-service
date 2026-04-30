package com.cloud.jml.jobs;

import com.cloud.jml.model.authentication.AuthenticationEntity;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.repository.authentication.AuthenticationRepository;
import com.cloud.jml.repository.token.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationRepository authenticationRepository;

    public TokenCleanupScheduler(RefreshTokenRepository refreshTokenRepository, AuthenticationRepository authenticationRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationRepository = authenticationRepository;
        log.info("🔥 TokenCleanupScheduler inicializado correctamente.");
    }

    // 🧹 Se ejecuta cada 24 horas (🕑 a las 12 AM)
    // (Minuto: 0 Hora: 0 Dia del mes: * (todos) Mes: * (todos) Dia de la semana: * (todos))
    // FIX: Se agrego @Transactional para que las operaciones de eliminacion batch se ejecuten en una transaccion
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void limpiarRegistrosDeAutenticacionAntiguos() {
        log.info("🧹 Iniciando limpieza incremental de registros de autenticacion antiguos...");

        int batchSize = 1000;
        int totalEliminados = 0;
        Page<AuthenticationEntity> page;

        LocalDateTime cutoff = LocalDateTime.now().minusHours(8);

        do {
            page = authenticationRepository.findByFechaCreacionBefore(cutoff, PageRequest.of(0, batchSize));
            int size = page.getContent().size();
            if (size > 0) {
                authenticationRepository.deleteAll(page.getContent());
                totalEliminados += size;
                log.info("🧹 Eliminados {} registros en este lote...", size);
            }
        } while (!page.isEmpty());

        log.info("✅ Limpieza completada. Total registros eliminados: {}", totalEliminados);
    }

    // 🧹 Se ejecuta cada 24 horas (🕑 a las 2 AM)
    // (Minuto: 0 Hora: 0 Dia del mes: * (todos) Mes: * (todos) Dia de la semana: * (todos))
    // FIX: Se agrego @Transactional para que las operaciones de eliminacion batch se ejecuten en una transaccion
    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void limpiarTokensExpiradosPorLotes() {
        log.info("🧹 Iniciando limpieza incremental de tokens expirados...");

        int batchSize = 1000;
        int totalEliminados = 0;
        Page<RefreshTokenEntity> page;

        do {
            page = refreshTokenRepository.findByExpiryDateBefore(Instant.now(), PageRequest.of(0, batchSize));
            int size = page.getContent().size();
            if (size > 0) {
                refreshTokenRepository.deleteAll(page.getContent());
                totalEliminados += size;
                log.info("🧹 Eliminados {} tokens en este lote...", size);
            }
        } while (!page.isEmpty());

        log.info("✅ Limpieza completada. Total de tokens eliminados: {}", totalEliminados);
    }

    // 🧹 Se ejecuta cada hora
//    @Scheduled(cron = "0 0 * * * *")
//    public void limpiarTokensExpirados() {
//        log.info("🧹 Iniciando limpieza automatica de tokens expirados...");
//
//        int eliminados = refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
//
//        log.info("✅ Limpieza completada. Tokens eliminados: {}", eliminados);
//    }
}
