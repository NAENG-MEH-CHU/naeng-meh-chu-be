package org.example.exception.exceptions;

public class BearerTokenNotFoundException extends RuntimeException{

    public BearerTokenNotFoundException() {
        super("로그인 후 이용해주세요");
    }
}
