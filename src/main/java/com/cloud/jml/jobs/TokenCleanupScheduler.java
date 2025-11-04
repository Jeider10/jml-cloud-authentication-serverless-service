package com.cloud.jml.jobs;

import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.repository.token.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenCleanupScheduler(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        log.info("🔥 TokenCleanupScheduler inicializado correctamente.");
    }

    // 🧹 Se ejecuta cada 24 horas (medianoche)
    @Scheduled(cron = "0 0 0 * * *")
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
//        log.info("🧹 Iniciando limpieza automática de tokens expirados...");
//
//        int eliminados = refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
//
//        log.info("✅ Limpieza completada. Tokens eliminados: {}", eliminados);
//    }
}
