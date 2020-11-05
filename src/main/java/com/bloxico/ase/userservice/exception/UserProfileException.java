package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserProfileException extends AseRuntimeException {

    public UserProfileException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
