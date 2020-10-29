package com.bloxico.ase.userservice.web.error;

import com.bloxico.ase.userservice.exception.JwtException;
import com.bloxico.ase.userservice.exception.UserProfileException;
import lombok.Getter;

public interface ErrorCodes {

    RuntimeException newException(Throwable cause);

    default RuntimeException newException() {
        return newException(null);
    }

    @Getter
    enum User implements ErrorCodes {

        USER_DOES_NOT_EXIST(
                "1",
                "For various situations, when user passed by parameter (email) does not exist.");

        private final String code, description;

        User(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public RuntimeException newException(Throwable cause) {
            return new UserProfileException(code, cause);
        }

    }

    @Getter
    enum Jwt implements ErrorCodes {

        INVALID_TOKEN(
                "10",
                "Token is not valid (anymore). E.g. it's fake, expired or blacklisted.");

        private final String code, description;

        Jwt(String code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public RuntimeException newException(Throwable cause) {
            return new JwtException(code, cause);
        }

    }

}
