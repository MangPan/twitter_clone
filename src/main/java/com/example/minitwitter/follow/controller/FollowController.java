package com.example.minitwitter.follow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.minitwitter.auth.security.CurrentUser;
import com.example.minitwitter.follow.dto.FollowResponse;
import com.example.minitwitter.follow.dto.FollowStatusResponse;
import com.example.minitwitter.follow.dto.FollowUserResponse;
import com.example.minitwitter.follow.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final CurrentUser currentUser;

    // 팔로우
    @PostMapping("/api/users/{targetUserId}/follow")
    public FollowResponse follow(@PathVariable Long targetUserId){
        Long currentUserId = currentUser.getId();
        return followService.follow(currentUserId, targetUserId);
    }
    // 언팔로우
    @DeleteMapping("/api/users/{targetUserId}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable Long targetUserId){
        Long currentUserId = currentUser.getId();
        followService.unfollow(currentUserId, targetUserId);

        return ResponseEntity.noContent().build();
    }

    // curUser가 targetUser를 팔로우 했는지 여부 boolean
    @GetMapping("/api/users/{targetUserId}/follow/status")
    public FollowStatusResponse getFollowStatus(@PathVariable Long targetUserId){
        Long currentUserId = currentUser.getId();

        return followService.getFollowStatus(currentUserId, targetUserId);
    }

    // curUser가 팔로우하는 user 목록 
    @GetMapping("/api/me/followings")
    public List<FollowUserResponse> getMyFollowing(){
        Long currentUserId = currentUser.getId();
        return followService.getFollowings(currentUserId);
    }

    // curUser를 팔로우하는 user 목록
    @GetMapping("/api/me/followers")
    public List<FollowUserResponse> getMyFollowers(){
        Long currentUserId = currentUser.getId();
        return followService.getFollowers(currentUserId);
    }


    // userID가 팔로우하는 user 목록
    @GetMapping("/api/users/{userId}/followings")
    public List<FollowUserResponse> getFollowings(@PathVariable Long userId){
        return followService.getFollowings(userId);
    }

    // userID를 팔로우하는 user 목록
    @GetMapping("/api/users/{userId}/followers")
    public List<FollowUserResponse> getFollowers(@PathVariable Long userId){
        return followService.getFollowers(userId);
    }











    
}