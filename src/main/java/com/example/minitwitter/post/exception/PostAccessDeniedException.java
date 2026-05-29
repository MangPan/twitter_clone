package com.example.minitwitter.post.exception;

public class PostAccessDeniedException extends RuntimeException {
    public PostAccessDeniedException(Long postId, Long userId) {
        super("게시글에 대한 권한이 없습니다. postId=" + postId + ", userId=" + userId);
    }
}
