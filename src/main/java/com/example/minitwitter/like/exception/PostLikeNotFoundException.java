package com.example.minitwitter.like.exception;

public class PostLikeNotFoundException extends RuntimeException{
    public PostLikeNotFoundException(Long postId, Long userId){
        super("좋아요를 찾을 수 없습니다. postId=" + postId + ", userId=" + userId);
    }
}
