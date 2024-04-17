package org.example.exception.exceptions;

public class IngredientNotInException extends RuntimeException{
    public IngredientNotInException() {
        super("냉장고에 재료가 존재하지 않습니다");
    }
}
