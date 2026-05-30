package com.example.minitwitter.post.exception;

public class PostImageLimitExceededException extends RuntimeException{
    public PostImageLimitExceededException(Long postId){
        super("게시글 이미지는 최대 4장까지만 업로드할 수 있습니다. postId=" + postId);
    }
}
