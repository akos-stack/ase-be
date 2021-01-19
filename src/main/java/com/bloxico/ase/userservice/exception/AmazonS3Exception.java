package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class AmazonS3Exception extends AseRuntimeException {

    public AmazonS3Exception(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
