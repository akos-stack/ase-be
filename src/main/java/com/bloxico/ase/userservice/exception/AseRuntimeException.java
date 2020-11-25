package com.bloxico.ase.userservice.exception;

import com.bloxico.userservice.web.model.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class AseRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AseRuntimeException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = requireNonNull(httpStatus);
    }

    public ApiError toApiError() {
        return new ApiError(httpStatus, getMessage());
    }

}
