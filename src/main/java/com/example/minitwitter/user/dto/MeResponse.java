package com.example.minitwitter.user.dto;

public record MeResponse(
    Long id,
    String loginId,
    String nickName,
    String bio,
    String profileImageUrl
) {
}