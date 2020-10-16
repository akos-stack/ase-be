package com.bloxico.userservice.exceptions;

public class TokenException extends RuntimeException {

//    public static final String TOKEN_EXPIRED_ERROR_CODE = "10";
//    public static final String TOKEN_NOT_FOUND_ERROR_CODE = "11";

    public TokenException(String message) {
        super(message);
    }
}
