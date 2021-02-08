package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class S3Exception extends AseRuntimeException {

    public S3Exception(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
