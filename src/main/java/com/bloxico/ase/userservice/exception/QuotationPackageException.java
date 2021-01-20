package com.bloxico.ase.userservice.exception;

import org.springframework.http.HttpStatus;

public class QuotationPackageException extends  AseRuntimeException{

    public QuotationPackageException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

}
