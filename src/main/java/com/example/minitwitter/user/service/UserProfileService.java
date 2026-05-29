package com.example.minitwitter.user.service;

import org.springframework.stereotype.Service;

import com.example.minitwitter.follow.repository.FollowRepository;
import com.example.minitwitter.post.repository.PostRepository;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.dto.UserProfileResponse;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        long postCount = postRepository.countByAuthorId(userId);
        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        return new UserProfileResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getBio(),
                user.getProfileImageUrl(),
                postCount,
                followerCount,
                followingCount);
    }
}
