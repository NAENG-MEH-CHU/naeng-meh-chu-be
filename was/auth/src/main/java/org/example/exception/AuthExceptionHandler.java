package org.example.exception;

import org.example.exception.exceptions.BearerTokenNotFoundException;
import org.example.exception.exceptions.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(BearerTokenNotFoundException.class)
    public ResponseEntity<String> handlerBearerTokenNotFoundException(final BearerTokenNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handlerMemberNotFoundException(final MemberNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
