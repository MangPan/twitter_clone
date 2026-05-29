package com.example.minitwitter.follow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minitwitter.follow.dto.FollowResponse;
import com.example.minitwitter.follow.dto.FollowStatusResponse;
import com.example.minitwitter.follow.dto.FollowUserResponse;
import com.example.minitwitter.follow.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;


    @PostMapping("/{userId}/followings/{targetUserId}")
    public FollowResponse follow(
        @PathVariable Long userId,
        @PathVariable Long targetUserId
    ){
        return followService.follow(userId, targetUserId);
    } 

    @DeleteMapping("/{userId}/followings/{targetUserId}")
    public ResponseEntity<Void> unfollow(
        @PathVariable Long userId,
        @PathVariable Long targetUserId 
    ){
        followService.unfollow(userId, targetUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followings")
    public List<FollowUserResponse> getFollowing(@PathVariable Long userId){
        return followService.getFollowings(userId);
    }

    @GetMapping("/{userId}/followers")
    public List<FollowUserResponse> getFollowers(@PathVariable Long userId){
        return followService.getFollowers(userId);
    }

    @GetMapping("/{userId}/followings/{targetUserId}/status")
    public FollowStatusResponse getFollowStatus(
        @PathVariable Long userId,
        @PathVariable Long targetUserId
    ){
        return followService.getFollowStatus(userId, targetUserId);
    }
}
