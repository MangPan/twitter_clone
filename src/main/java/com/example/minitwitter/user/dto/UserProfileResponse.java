package com.example.minitwitter.user.dto;

public record UserProfileResponse(
    Long id,
    String loginId,
    String nickName,
    String bio,
    String profileImageUrl,
    long postCount,
    long followerCount,
    long followingCount
) {
}