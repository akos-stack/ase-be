package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class EvaluationException extends AseRuntimeException {

    public EvaluationException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}