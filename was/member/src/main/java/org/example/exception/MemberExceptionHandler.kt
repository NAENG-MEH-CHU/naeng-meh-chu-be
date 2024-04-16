package org.example.exception

import org.example.exception.exceptions.AgeNotValidException
import org.example.exception.exceptions.GenderNotValidException
import org.example.exception.exceptions.MemberReasonNotFoundException
import org.example.exception.exceptions.UsingReasonUnableException
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

    @ExceptionHandler(AgeNotValidException::class)
    open fun handlerAgeNotValidException(exception: AgeNotValidException): ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UsingReasonUnableException::class)
    open fun handlerUsingReasonUnableException(exception: UsingReasonUnableException): ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MemberReasonNotFoundException::class)
    fun handlerMemberReasonNotFoundException(exception: MemberReasonNotFoundException): ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.NOT_FOUND)
    }
}