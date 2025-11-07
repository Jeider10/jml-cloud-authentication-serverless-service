package com.cloud.jml.utils.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Getter
@Setter
@Component
public class ConnectionUtils {

    private final ConnectionPropertiesUtils connectionPropertiesUtils;

    public ConnectionUtils(ConnectionPropertiesUtils connectionPropertiesUtils) {
        this.connectionPropertiesUtils = connectionPropertiesUtils;
        log.info("🔥 ConnectionUtils inicializado correctamente.");
    }

    /**
     * 🔧 Construye un HikariDataSource configurado con los parámetros indicados.
     */
    public DataSource buildDataSource(String url, String username, String password) {
        log.debug("🧱 Creando HikariDataSource con los parámetros proporcionados...");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(connectionPropertiesUtils.getDriverClassName());

        // ⚙️ Pool de conexiones
        // 🔸 Define el número máximo de conexiones simultáneas que puede abrir el pool.
        dataSource.setMaximumPoolSize(10);
        // 🔸 Número mínimo de conexiones que el pool mantiene abiertas incluso si no hay tráfico.
        dataSource.setMinimumIdle(2);
        // 🔸 Tiempo (en milisegundos) que una conexión inactiva puede permanecer abierta antes de cerrarse.
        dataSource.setIdleTimeout(30000);
        // 🔸 Nombre personalizado del pool de conexiones (solo para logs y monitoreo).
        dataSource.setPoolName("JML-HikariPool");

        log.debug("✅ HikariDataSource creado exitosamente.");

        return dataSource;
    }
}
