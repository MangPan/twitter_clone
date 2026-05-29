package com.example.minitwitter.follow.dto;

public record FollowUserResponse(
    Long userId,
    String nickName,
    String bio
) {
}