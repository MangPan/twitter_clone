package com.example.minitwitter.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String loginId,
    String nickName,
    String bio,
    String profileImageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}