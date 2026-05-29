package com.example.minitwitter.post.dto;

import java.util.List;

public record TimelineResponse(
    List<PostResponse> content,
    Long nextCursor,
    boolean hasNext
) {
}