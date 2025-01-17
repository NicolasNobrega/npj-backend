package com.uniprojecao.fabrica.gprojuridico.controllers.handlers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.uniprojecao.fabrica.gprojuridico.services.exceptions.ReflectionOperationException;
import com.uniprojecao.fabrica.gprojuridico.services.exceptions.ResourceNotFoundException;
import com.uniprojecao.fabrica.gprojuridico.dto.ErrorResponse;
import com.uniprojecao.fabrica.gprojuridico.services.exceptions.UserAlreadyCreatedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InstantiationException.class)
    public ResponseEntity<ErrorResponse> handleInstantiationException(InstantiationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(status.value(), "Objeto(s) de classe especificado não pode(m) ser instanciado(s)", request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ReflectionOperationException.class)
    public ResponseEntity<ErrorResponse> handleReflectionOperationException(ReflectionOperationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> tokenExpiredException(TokenExpiredException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse error = new ErrorResponse(status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UserAlreadyCreatedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyCreatedException(UserAlreadyCreatedException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(status.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
