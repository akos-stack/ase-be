package com.bloxico.userservice.web.error;

import com.bloxico.ase.userservice.exception.AseRuntimeException;
import com.bloxico.userservice.exceptions.*;
import com.bloxico.userservice.web.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, String> bindingResultErrors = returnBindingResultErrors(ex);
        ErrorUnit errorUnit = new ErrorUnit(bindingResultErrors);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Method argument is not valid.", Collections.singletonList(errorUnit));
        log.info("Returning error response: {}", apiError.toString());

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    private Map<String, String> returnBindingResultErrors(MethodArgumentNotValidException e) {

        Map<String, String> errorMessages = new HashMap<>();

        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        for (FieldError error : errors) {
            errorMessages.put(error.getField(), error.getDefaultMessage());
        }

        return errorMessages;
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleRestOfExceptions(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR, "Unexpected error", ex);
        log.error("Unexpected error occurred!", ex);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(Exception ex, WebRequest request) {

        EntityNotFoundException e = (EntityNotFoundException) ex;

        ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR, e.getMessage());
        log.info("Returning error response: {}", apiError.toString());

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {CoinUserException.class})
    protected ResponseEntity<Object> handleCoinUserException(Exception ex, WebRequest request) {

        CoinUserException e = (CoinUserException) ex;
        ApiError apiError = new ApiError(CONFLICT, e.getMessage());

        log.info("Returning error response: {}", apiError.toString());

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {TokenException.class})
    protected ResponseEntity<Object> handleTokenException(Exception ex, WebRequest request) {

        TokenException e = (TokenException) ex;
        ApiError apiError = new ApiError(NOT_FOUND, e.getMessage());
        log.info("Returning error response: {}", apiError.toString());

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {

        AccessDeniedException e = (AccessDeniedException) ex;
        ApiError apiError = new ApiError(FORBIDDEN, e.getMessage());

        log.info("Returning error response: {}", apiError.toString());

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(value = {AseRuntimeException.class})
    protected ResponseEntity<Object> handleAseRuntimeException(Exception exception, WebRequest __) {
        var aseError = (AseRuntimeException) exception;
        var apiError = aseError.toApiError();
        log.info("Returning error response: {}", apiError.toString());
        if (aseError.getCause() != null)
            log.error("Error cause: ", aseError.getCause());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}