package com.example.minitwitter.user.dto;

public record UserProfileResponse(
    Long id,
    String nickName,
    String bio,
    long postCount,
    long followerCount,
    long followingCount
) {
}