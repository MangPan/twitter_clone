package com.example.minitwitter.follow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minitwitter.follow.domain.Follow;
import com.example.minitwitter.follow.dto.FollowResponse;
import com.example.minitwitter.follow.dto.FollowStatusResponse;
import com.example.minitwitter.follow.dto.FollowUserResponse;
import com.example.minitwitter.follow.exception.DuplicateFollowException;
import com.example.minitwitter.follow.exception.FollowNotFoundException;
import com.example.minitwitter.follow.exception.SelfFollowException;
import com.example.minitwitter.follow.repository.FollowRepository;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponse follow(Long userId, Long targetUserId){
        if(userId.equals(targetUserId)){
            throw new SelfFollowException(userId);
        }

        User follower = getUserOrThrow(userId);
        User following = getUserOrThrow(targetUserId);

        if(followRepository.existsByFollowerAndFollowing(follower, following)){
            throw new DuplicateFollowException(userId, targetUserId);
        }

        Follow follow = new Follow(follower, following);
        Follow savedFollow = followRepository.save(follow);

        return toResponse(savedFollow);
    }

    @Transactional
    public void unfollow(Long userId, Long targetUserId){
        User follower = getUserOrThrow(userId);
        User following = getUserOrThrow(targetUserId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
            .orElseThrow(() -> new FollowNotFoundException(userId, targetUserId));
        
        followRepository.delete(follow);
    }

    // user가 팔로우한 목록
    public List<FollowUserResponse> getFollowings(Long userId){
        User Follower = getUserOrThrow(userId);

        return followRepository.findFollowingsByFollower(Follower)
            .stream()
            .map(follow -> toUserResponse(follow.getFollowing()))
            .toList();
    }

    // user를 팔로우한 사람들 목록
    public List<FollowUserResponse> getFollowers(Long userId){
        User following = getUserOrThrow(userId);

        return followRepository.findFollowersByFollowing(following)
            .stream()
            .map(follow -> toUserResponse(follow.getFollower()))
            .toList();
    }

    private User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private FollowResponse toResponse(Follow follow){
        return new FollowResponse(
            follow.getId(),

            follow.getFollower().getId(),
            follow.getFollower().getNickName(),

            follow.getFollowing().getId(),
            follow.getFollowing().getNickName(),
            follow.getCreatedAt()
        );
    }

    private FollowUserResponse toUserResponse(User user){
        return new FollowUserResponse(
            user.getId(),
            user.getNickName(),
            user.getBio()
        );
    }

    public FollowStatusResponse getFollowStatus(Long userId, Long targetUserId){
        User follower = getUserOrThrow(userId);
        User following = getUserOrThrow(targetUserId);

        boolean followingStatus = followRepository.existsByFollowerAndFollowing(follower, following);

        return new FollowStatusResponse(followingStatus);
    }
}
