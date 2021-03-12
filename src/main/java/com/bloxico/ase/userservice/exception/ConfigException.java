package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class ConfigException extends AseRuntimeException {

    public ConfigException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
