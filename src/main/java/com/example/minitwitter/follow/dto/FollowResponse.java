package com.example.minitwitter.follow.dto;

import java.time.LocalDateTime;

public record FollowResponse(
    Long id,
    Long followerId,
    String followerNickName,
    Long followingId,
    String followingNickName,
    LocalDateTime createdAt
) {
}