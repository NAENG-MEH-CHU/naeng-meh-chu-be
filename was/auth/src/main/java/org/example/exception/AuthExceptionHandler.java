package org.example.exception;

import org.example.exception.exceptions.NeedToLoginException;
import org.example.exception.exceptions.MemberNotFoundException;
import org.example.exception.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(NeedToLoginException.class)
    public ResponseEntity<String> handlerBearerTokenNotFoundException(final NeedToLoginException exception) {
        return getUnauthorizedResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handlerMemberNotFoundException(final MemberNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handlerTokenExpiredException(final TokenExpiredException exception) {
        return getGoneResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    private ResponseEntity<String> getGoneResponse(final String message) {
        return ResponseEntity.status(HttpStatus.GONE).body(message);
    }

    private ResponseEntity<String> getUnauthorizedResponse(final String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
