package org.example.exception

import org.example.exception.exceptions.RecipeNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RecipeExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException::class)
    fun handlerRecipeNotFoundException(exception: RecipeNotFoundException): ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.NOT_FOUND)
    }
}