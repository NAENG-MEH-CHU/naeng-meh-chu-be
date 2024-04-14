package org.example.exception

import org.example.exception.exceptions.GenderNotValidException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
open class MemberExceptionHandler {

    @ExceptionHandler(GenderNotValidException::class)
    open fun handlerGenderNotValidException(exception: GenderNotValidException): ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.BAD_REQUEST)
    }
}