package com.example.minitwitter.like.exception;

public class DuplicatePostLikeException extends RuntimeException{
    public DuplicatePostLikeException(Long postId, Long userId){
        super("이미 좋아요를 누른 게시글입니다. postid=" + postId + ", userId=" + userId);
    }
}
