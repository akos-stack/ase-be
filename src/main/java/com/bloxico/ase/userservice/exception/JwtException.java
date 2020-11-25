package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class JwtException extends AseRuntimeException {

    public JwtException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
