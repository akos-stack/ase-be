package com.bloxico.userservice.exceptions;

public class CoinUserException extends RuntimeException {

//    public static final String USER_EXISTS_ERROR_CODE = "1";
//    public static final String USER_DOES_NOT_EXIST_ERROR_CODE = "v0.1.1";
//    public static final String OLD_PASSWORD_DOES_NOT_MATCH_ERROR_CODE = "3";
//    public static final String MATCH_REGISTRATION_PASSWORD_ERROR_CODE = "4";
//
//    public static final String INVALID_USERNAME_OR_PASSWORD_ERROR_CODE = "5";
//    public static final String USER_PROFILE_DOES_NOT_EXIST_ERROR_CODE = "6";

    public CoinUserException(String message) {
        super(message);
    }
}
