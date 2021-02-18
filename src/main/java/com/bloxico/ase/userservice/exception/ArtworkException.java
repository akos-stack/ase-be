package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class ArtworkException extends AseRuntimeException {

    public ArtworkException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
