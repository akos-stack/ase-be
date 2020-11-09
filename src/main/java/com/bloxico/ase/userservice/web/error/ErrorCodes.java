package com.bloxico.ase.userservice.web.error;

import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.ase.userservice.exception.JwtException;
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

        USER_DOES_NOT_EXIST(
                HttpStatus.NOT_FOUND,
                "1",
                "For various situations, when user passed by parameter (email) does not exist.");

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
    enum Jwt implements ErrorCodes {

        INVALID_TOKEN(
                HttpStatus.FORBIDDEN,
                "10",
                "Token is not valid (anymore). E.g. it's fake, expired or blacklisted.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Jwt(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new JwtException(httpStatus, code, cause);
        }

    }

}
