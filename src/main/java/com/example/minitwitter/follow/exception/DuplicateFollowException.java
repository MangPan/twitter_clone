package com.example.minitwitter.follow.exception;

public class DuplicateFollowException extends RuntimeException {
    public DuplicateFollowException(Long followerId, Long followingId) {
        super("이미 팔로우한 사용자입니다. followerId=" + followerId + ", followingId=" + followingId);
    }
}
