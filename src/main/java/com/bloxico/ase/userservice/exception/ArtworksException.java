package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class ArtworksException extends AseRuntimeException {

    public ArtworksException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }
}
