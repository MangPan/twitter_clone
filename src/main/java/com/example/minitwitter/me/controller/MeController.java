package com.example.minitwitter.me.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minitwitter.auth.security.CurrentUser;
import com.example.minitwitter.post.dto.PostResponse;
import com.example.minitwitter.post.dto.TimelineResponse;
import com.example.minitwitter.post.service.PostService;
import com.example.minitwitter.user.dto.MeResponse;
import com.example.minitwitter.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final CurrentUser currentUser;
    private final UserService userService;
    private final PostService postService;
    
    

    @GetMapping
    public MeResponse getMe(){
        Long currentUserId = currentUser.getId();
        return userService.getMe(currentUserId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getMyPosts(){
        Long currentUserId = currentUser.getId();
        return postService.getPostsByAuthor(currentUserId);
    }

    @GetMapping("/timeline")
    public TimelineResponse getMyTimeline(
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "10") int size
    ){
        Long currentUserId = currentUser.getId();
        return postService.getMyTimeline(currentUserId, cursor, size);
    }
}
