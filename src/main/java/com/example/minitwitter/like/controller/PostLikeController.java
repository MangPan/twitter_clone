package com.example.minitwitter.like.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minitwitter.auth.security.CurrentUser;
import com.example.minitwitter.like.dto.LikeUserResponse;
import com.example.minitwitter.like.service.PostLikeService;
import com.example.minitwitter.post.dto.PostResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final CurrentUser currentUser; 

    // postId 게시물 좋아요 누름
    @PostMapping("/api/posts/{postId}/likes")
    public PostResponse likePost(@PathVariable Long postId){
        Long currentUserId = currentUser.getId();
        return postLikeService.likePost(postId, currentUserId);
    }

    // postId 게시물의 좋아요 취소
    @DeleteMapping("/api/posts/{postId}/likes")
    public PostResponse unlikePost(@PathVariable Long postId){
        Long currentUserId = currentUser.getId();
        return postLikeService.unlikePost(postId, currentUserId);
    }

    // 내가 좋아요 한 목록
    @GetMapping("/api/me/likes")
    public List<PostResponse> getMyLikedPosts(){
        Long currentUserId = currentUser.getId();
        return postLikeService.getMyLikedPost(currentUserId);
    }

    // postId 게시물에 좋아요 한 유저 목록 < 게시물 작성자 본인만 확인 가능
    @GetMapping("/api/posts/{postId}/likes/users")
    public List<LikeUserResponse> getLikeUsers(@PathVariable Long postId){
        Long currentUserId = currentUser.getId();

        return postLikeService.getLikeUsers(postId, currentUserId);
    }

    
}
