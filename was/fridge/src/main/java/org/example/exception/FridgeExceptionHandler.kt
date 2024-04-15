package org.example.exception

import org.example.exception.exceptions.FridgeIngredientNotFoundException
import org.example.exception.exceptions.IngredientNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
open class FridgeExceptionHandler {

    @ExceptionHandler(IngredientNotFoundException::class)
    open fun handlerIngredientNotFoundException(exception: IngredientNotFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FridgeIngredientNotFoundException::class)
    open fun handlerFridgeIngredientNotFoundException(exception: FridgeIngredientNotFoundException) : ResponseEntity<String> {
        return ResponseEntity<String>(exception.message, HttpStatus.NOT_FOUND)
    }
}