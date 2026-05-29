package com.example.minitwitter.like.dto;

public record LikeUserResponse(
    Long id,
    String loginId,
    String nickName,
    String profileImageUrl
) {
}