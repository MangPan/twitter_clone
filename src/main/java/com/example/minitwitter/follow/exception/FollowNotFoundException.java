package com.example.minitwitter.follow.exception;

public class FollowNotFoundException extends RuntimeException {
    public FollowNotFoundException(Long followerId, Long followingId) {
        super("팔로우 관계를 찾을 수 없습니다. followerId=" + followerId + ", followingId=" + followingId);
    }
}
