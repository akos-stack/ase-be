package com.bloxico.userservice.web.model;

import com.bloxico.userservice.exceptions.ErrorUnit;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String errorCode;
    private List<ErrorUnit> errorUnits;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String errorCode) {
        this();
        this.status = status;
        this.errorCode = errorCode;
    }

    public ApiError(HttpStatus status, String errorCode, List<ErrorUnit> errorUnits) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.errorUnits = errorUnits;
    }

    public ApiError(HttpStatus status, String errorCode, Throwable ex) {
        this();
        this.status = status;
        this.errorCode = errorCode;
    }
}