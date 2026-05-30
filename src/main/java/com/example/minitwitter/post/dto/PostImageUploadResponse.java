package com.example.minitwitter.post.dto;

import java.util.List;

public record PostImageUploadResponse(
    Long postId,
    List<String> imageUrls
) {
}