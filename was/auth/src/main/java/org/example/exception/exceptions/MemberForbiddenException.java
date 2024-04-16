package org.example.exception.exceptions;

public class MemberForbiddenException extends RuntimeException{

    public MemberForbiddenException() {
        super("권한이 없습니다.");
    }
}
