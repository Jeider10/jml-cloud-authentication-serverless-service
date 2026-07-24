package com.cloud.jml.exception;

import com.cloud.jml.exception.authentication.AuthenticationRuntimeException;
import com.cloud.jml.exception.empresa.ConfigEmpresaRuntimeException;
import com.cloud.jml.exception.role.RoleRuntimeException;
import com.cloud.jml.exception.user.UserRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores de validacion de DTOs (@Valid) @NotBlank, @NotNull, @Email, @Size, etc. y retorna un 400 con los detalles
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "📋 [VALIDACION] Error de validacion en los datos enviados",
                errores);
    }

    // 🛡️ Errores de roles
    @ExceptionHandler(RoleRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRoleErrors(RoleRuntimeException ex) {
        return buildErrorResponse(
                ex.getStatus(),
                "🛡️ Error en roles",
                ex.getMessage());
    }

    // 👤 Errores de usuario
    @ExceptionHandler(UserRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleUserErrors(UserRuntimeException ex) {
        return buildErrorResponse(
                ex.getStatus(),
                "👤 Error en usuario",
                ex.getMessage());
    }

    // 🔑 Errores de autenticacion
    @ExceptionHandler(AuthenticationRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationErrors(AuthenticationRuntimeException ex) {
        return buildErrorResponse(
                ex.getStatus(),
                "🔑 Error en autenticacion",
                ex.getMessage());
    }

    // 🏢 Errores de empresa
    @ExceptionHandler(ConfigEmpresaRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleEmpresaErrors(ConfigEmpresaRuntimeException ex) {
        return buildErrorResponse(
                ex.getStatus(),
                " 🏢 Error en empresa",
                ex.getMessage());
    }

    // 🔥 Errores generales no controlados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "🔥 [GENERAL] Error interno del servidor",
                ex.getMessage());
    }

    // 🧱 Metodo comun de respuesta
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
