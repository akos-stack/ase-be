package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class DocumentException extends AseRuntimeException {

    public DocumentException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
