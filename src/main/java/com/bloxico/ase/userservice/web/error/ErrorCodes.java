package com.bloxico.ase.userservice.web.error;

import com.bloxico.ase.userservice.exception.AmazonS3Exception;
import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.ase.userservice.exception.TokenException;
import com.bloxico.ase.userservice.exception.UserProfileException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public interface ErrorCodes {

    AseRuntimeException newException(Throwable cause);

    default AseRuntimeException newException() {
        return newException(null);
    }

    @Getter
    enum User implements ErrorCodes {

        USER_EXISTS(
                HttpStatus.CONFLICT,
                "1",
                "Upon registration, when given email already exists."),

        USER_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "2",
                "For various situations, when user passed by parameter (email) does not exist."),

        OLD_PASSWORD_DOES_NOT_MATCH(
                HttpStatus.BAD_REQUEST,
                "3",
                "When updating known password, if provided old password does not match with (current) password."),

        MATCH_REGISTRATION_PASSWORD_ERROR(
                HttpStatus.BAD_REQUEST,
                "4",
                "When registering user, if password and matchPassword values are not valid."),

        ROLE_NOT_FOUND (
                HttpStatus.BAD_REQUEST,
                "5",
                "When filtering users, if role parameter is non existing role.");

        private final HttpStatus httpStatus;
        private final String code, description;

        User(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        public AseRuntimeException newException(Throwable cause) {
            return new UserProfileException(httpStatus, code, cause);
        }

    }

    @Getter
    enum Token implements ErrorCodes {

        INVALID_TOKEN(
                HttpStatus.FORBIDDEN,
                "10",
                "Token is not valid (anymore). E.g. it's fake, expired or blacklisted."),

        TOKEN_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "11",
                "Token cannot be found in the database. It may be deleted due to expiry."),

        TOKEN_EXISTS(
                HttpStatus.CONFLICT,
                "12",
                "Token already exists for given parameters.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Token(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new TokenException(httpStatus, code, cause);
        }

    }

    @Getter
    enum AmazonS3 implements ErrorCodes {

        FILE_TYPE_NOT_SUPPORTED(
                HttpStatus.BAD_REQUEST,
                "20",
                "File type is not supported.");

        private final HttpStatus httpStatus;
        private final String code, description;

        AmazonS3(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new AmazonS3Exception(httpStatus, code, cause);
        }

    }

}
