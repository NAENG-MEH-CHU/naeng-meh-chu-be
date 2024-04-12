package org.example.exception.exceptions;

public class NeedToLoginException extends RuntimeException{

    public NeedToLoginException() {
        super("로그인 후 이용해주세요");
    }
}
