package com.example.minitwitter.auth.dto;

public record LoginResponse(
    String accessToken,
    String tokenType,
    Long userId,
    String loginId,
    String nickName,
    String profileImageUrl
) {
}