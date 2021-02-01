package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.ase.userservice.web.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request)
    {
        var apiError = ApiError.builder()
                .status(BAD_REQUEST)
                .errorCode("Method argument is not valid.")
                .details(returnBindingResultErrors(ex))
                .build();
        log.info("Returning error response: " + apiError);
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    private static Map<String, Object> returnBindingResultErrors(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, Object>();
        for (FieldError error : ex.getBindingResult().getFieldErrors())
            errors.put(error.getField(), error.getDefaultMessage());
        return errors;
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleRestOfExceptions(Exception ex, WebRequest __) {
        var apiError = ApiError.builder()
                .status(INTERNAL_SERVER_ERROR)
                .errorCode("Unexpected error: " + ex.getMessage())
                .build();
        log.error("Unexpected error occurred:", ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest __) {
        var apiError = ApiError.builder()
                .status(FORBIDDEN)
                .errorCode(ex.getMessage())
                .build();
        log.info("Returning AccessDeniedException error response: " + apiError);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AseRuntimeException.class})
    protected ResponseEntity<Object> handleAseRuntimeException(Exception exception, WebRequest __) {
        var aseError = (AseRuntimeException) exception;
        var apiError = aseError.toApiError();
        log.info("Returning AseRuntimeException error response: " + apiError);
        if (aseError.getCause() != null)
            log.error("Error cause:", aseError.getCause());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}