package com.example.minitwitter.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String nickName,
    String bio,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}