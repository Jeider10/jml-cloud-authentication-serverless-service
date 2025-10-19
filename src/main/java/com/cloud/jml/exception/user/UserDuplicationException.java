package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserDuplicationException extends UserRuntimeException {

    public UserDuplicationException(String userName) {
        super(
                HttpStatus.CONFLICT,
                "⚠️ [DUPLICADO] Usuario duplicado: " + userName);
    }
}
