package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserException extends AseRuntimeException {

    public UserException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
