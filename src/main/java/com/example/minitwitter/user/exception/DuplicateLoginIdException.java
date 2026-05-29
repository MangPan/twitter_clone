package com.example.minitwitter.user.exception;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException(String loginId){
        super("이미 사용중인 아이디입니다. loginId=" + loginId);
    }
}
