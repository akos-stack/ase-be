package com.bloxico.ase.userservice.web.error;

import com.bloxico.ase.userservice.exception.*;
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

        ROLE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "5",
                "When filtering users, if role parameter is non existing role."),

        RESUME_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "6",
                "When downloading user resume, if resume path is null or empty."
        );

        private final HttpStatus httpStatus;
        private final String code, description;

        User(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        public AseRuntimeException newException(Throwable cause) {
            return new UserException(httpStatus, code, cause);
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
                "File type is not supported."),

        FILE_SIZE_EXCEEDED(
                HttpStatus.BAD_REQUEST,
                "21",
                "File size exceeded limit."),

        FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY(
                HttpStatus.BAD_REQUEST,
                "22",
                "File type is not supported for the category."),

        FILE_UPLOAD_FAILED(
                HttpStatus.BAD_REQUEST,
                "23",
                "File upload failed."),

        FILE_DOWNLOAD_FAILED(
                HttpStatus.BAD_REQUEST,
                "24",
                "File download failed."),

        FILE_DELETE_FAILED(
                HttpStatus.BAD_REQUEST,
                "25",
                "File deletion failed.");

        private final HttpStatus httpStatus;
        private final String code, description;

        AmazonS3(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new S3Exception(httpStatus, code, cause);
        }

    }

    @Getter
    enum Location implements ErrorCodes {

        REGION_EXISTS(
                HttpStatus.CONFLICT,
                "30",
                "Region already exists in the database."),

        REGION_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "31",
                "Region with specified name was not found."),

        COUNTRY_EXISTS(
                HttpStatus.CONFLICT,
                "32",
                "Country already exists in the database."),

        COUNTRY_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "33",
                "Country with specified name was not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Location(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new LocationException(httpStatus, code, cause);
        }

    }

    @Getter
    enum Artworks implements ErrorCodes {

        ARTWORK_METADATA_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "40",
                "Artwork metadata not found."),

        ARTWORK_METADATA_TYPE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "41",
                "Artwork metadata type not found."),

        ARTWORK_ARTIST_NOT_PROVIDED(
                HttpStatus.BAD_REQUEST,
                "42",
                "Artwork artist name not provided."),

        ARTWORK_MISSING_RESUME(
                HttpStatus.BAD_REQUEST,
                "43",
                "Art Owner resume missing."),

        ARTWORK_MISSING_CERTIFICATE(
                HttpStatus.BAD_REQUEST,
                "44",
                "Artwork certificate not uploaded."),

        ARTWORK_GROUP_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "45",
                "Artwork group not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Artworks(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new ArtworkException(httpStatus, code, cause);
        }

    }

    @Getter
    enum Evaluation implements ErrorCodes {

        COUNTRY_EVALUATION_DETAILS_EXISTS(
                HttpStatus.CONFLICT,
                "50",
                "Evaluation details already exists for specified country.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Evaluation(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new EvaluationException(httpStatus, code, cause);
        }

    }

    @Getter
    enum Documents implements ErrorCodes {

        DOCUMENT_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "60",
                "Document not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Documents(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new DocumentException(httpStatus, code, cause);
        }

    }
}
