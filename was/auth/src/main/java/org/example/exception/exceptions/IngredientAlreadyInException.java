package org.example.exception.exceptions;

public class IngredientAlreadyInException extends RuntimeException {

    public IngredientAlreadyInException() {
        super("냉장고에 재료가 이미 존재합니다.");
    }
}
