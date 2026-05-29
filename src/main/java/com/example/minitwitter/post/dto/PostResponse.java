package com.example.minitwitter.post.dto;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    Long authorId,
    String authorNickName,
    String content,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}