package com.cloud.jml.exception;

import com.cloud.jml.exception.authentication.AuthenticationRuntimeException;
import com.cloud.jml.exception.role.RoleRuntimeException;
import com.cloud.jml.exception.user.UserRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleUserErrors(UserRuntimeException ex) {
        return buildErrorResponse(ex.getStatus(), "👤 Error en usuario", ex.getMessage());
    }

    @ExceptionHandler(RoleRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRoleErrors(RoleRuntimeException ex) {
        return buildErrorResponse(ex.getStatus(), "🛡️ Error en roles", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationErrors(AuthenticationRuntimeException ex) {
        return buildErrorResponse(ex.getStatus(), "🔑 Error en autenticación", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "🔥 Error interno", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
