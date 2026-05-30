package com.example.minitwitter.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
    Long id,
    Long authorId,
    String authorNickName,
    String content,
    List<String> imageUrls,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}