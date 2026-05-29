package com.example.minitwitter.follow.exception;

public class SelfFollowException extends RuntimeException{
    public SelfFollowException(Long userId) {
        super("자기 자신은 팔로우할 수 없습니다. userId=" + userId);
    }
}
