package com.bloxico.userservice.exceptions;

public class OAuthTokenServiceException extends RuntimeException {
    public OAuthTokenServiceException(String message) {
        super(message);
    }
}