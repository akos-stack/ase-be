package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends AseRuntimeException {

    public TokenException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
