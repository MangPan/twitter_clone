package com.example.minitwitter.post.exception;

public class InvalidTimelineSizeException extends RuntimeException{
    public InvalidTimelineSizeException(int size){
        super("타임라인 조회는 1 이상 50 이하만 가능합니다. size=" + size);
    }
}