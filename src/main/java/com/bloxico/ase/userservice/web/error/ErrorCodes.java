package com.bloxico.ase.userservice.web.error;

import com.bloxico.ase.userservice.exception.*;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public interface ErrorCodes {

    AseRuntimeException newException(Throwable cause);

    default AseRuntimeException newException() {
        return newException(null);
    }

    HttpStatus getHttpStatus();

    String getCode();

    String getDescription();

    default Map<String, String> asMap() {
        return Map.of(
                "code", getCode(),
                "description", getDescription(),
                "http_status", getHttpStatus().toString());
    }

    @Getter
    enum User implements ErrorCodes {

        USER_EXISTS(
                HttpStatus.CONFLICT,
                "User_01",
                "Upon registration, when given email already exists."),

        USER_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "User_02",
                "For various situations, when user passed by parameter (email) does not exist."),

        OLD_PASSWORD_DOES_NOT_MATCH(
                HttpStatus.BAD_REQUEST,
                "User_03",
                "When updating known password, if provided old password does not match with (current) password."),

        MATCH_REGISTRATION_PASSWORD_ERROR(
                HttpStatus.BAD_REQUEST,
                "User_04",
                "When registering user, if password and matchPassword values are not valid."),

        ROLE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "User_05",
                "When filtering users, if role parameter is non existing role."),

        RESUME_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "User_06",
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
                "Token_01",
                "Token is not valid (anymore). E.g. it's fake, expired or blacklisted."),

        TOKEN_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Token_02",
                "Token cannot be found in the database. It may be deleted due to expiry."),

        TOKEN_EXISTS(
                HttpStatus.CONFLICT,
                "Token_03",
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
                "AmazonS3_01",
                "File type is not supported."),

        FILE_SIZE_EXCEEDED(
                HttpStatus.BAD_REQUEST,
                "AmazonS3_02",
                "File size exceeded limit."),

        FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY(
                HttpStatus.BAD_REQUEST,
                "AmazonS3_03",
                "File type is not supported for the category."),

        FILE_UPLOAD_FAILED(
                HttpStatus.BAD_REQUEST,
                "AmazonS3_04",
                "File upload failed."),

        FILE_DOWNLOAD_FAILED(
                HttpStatus.BAD_REQUEST,
                "AmazonS3_05",
                "File download failed."),

        FILE_DELETE_FAILED(
                HttpStatus.BAD_REQUEST,
                "AmazonS3_06",
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
                "Location_01",
                "Region already exists in the database."),

        REGION_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Location_02",
                "Region with specified name was not found."),

        REGION_DELETE_OPERATION_NOT_SUPPORTED(
                HttpStatus.CONFLICT,
                "Location_03",
                "One or more countries are tied down to the region. Region cannot be deleted."),

        COUNTRY_EXISTS(
                HttpStatus.CONFLICT,
                "Location_04",
                "Country already exists in the database."),

        COUNTRY_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Location_05",
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
    enum Artwork implements ErrorCodes {

        ARTWORK_METADATA_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Artworks_01",
                "Artwork metadata not found."),

        ARTWORK_METADATA_TYPE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Artworks_02",
                "Artwork metadata type not found."),

        ARTWORK_MISSING_RESUME(
                HttpStatus.BAD_REQUEST,
                "Artworks_03",
                "Art Owner resume missing."),

        ARTWORK_MISSING_CERTIFICATE(
                HttpStatus.BAD_REQUEST,
                "Artworks_04",
                "Artwork certificate not uploaded."),

        ARTWORK_GROUP_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Artworks_05",
                "Artwork group not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Artwork(HttpStatus httpStatus, String code, String description) {
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
                "Evaluation_01",
                "Evaluation details already exists for specified country."),

        COUNTRY_EVALUATION_DETAILS_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Evaluation_02",
                "Specified evaluation details not found."),

        COUNTRY_EVALUATION_DETAILS_DELETE_OPERATION_NOT_SUPPORTED(
                HttpStatus.CONFLICT,
                "Evaluation_03",
                "There are evaluators from country to which evaluation details belong."),

        QUOTATION_PACKAGE_EXISTS(
                HttpStatus.CONFLICT,
                "Evaluation_04",
                "Quotation package already exists for specified artwork."),

        QUOTATION_PACKAGE_COUNTRY_EXISTS(
                HttpStatus.CONFLICT,
                "Evaluation_05",
                "Quotation package country already exists for specified country.");

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
    enum Document implements ErrorCodes {

        DOCUMENT_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Documents_01",
                "Document not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Document(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        }

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new DocumentException(httpStatus, code, cause);
        }

    }

    @Getter
    enum Config implements ErrorCodes {
        CONFIG_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "Config_01",
                "Config not found.");

        private final HttpStatus httpStatus;
        private final String code, description;

        Config(HttpStatus httpStatus, String code, String description) {
            this.httpStatus = httpStatus;
            this.code = code;
            this.description = description;
        };

        @Override
        public AseRuntimeException newException(Throwable cause) {
            return new ConfigException(httpStatus, code, cause);
        }

    }

}
