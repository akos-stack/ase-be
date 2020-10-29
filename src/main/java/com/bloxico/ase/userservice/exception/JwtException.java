package com.bloxico.ase.userservice.exception;

public class JwtException extends RuntimeException {

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

}
