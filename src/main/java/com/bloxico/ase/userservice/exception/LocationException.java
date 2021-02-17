package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class LocationException extends AseRuntimeException {

    public LocationException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
