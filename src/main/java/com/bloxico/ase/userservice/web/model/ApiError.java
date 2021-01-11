package com.bloxico.ase.userservice.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Value
@Builder
public class ApiError {

    HttpStatus status;

    String errorCode;

    Map<String, Object> details;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    LocalDateTime timestamp = LocalDateTime.now();

}
