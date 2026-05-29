package com.example.minitwitter.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minitwitter.auth.security.CurrentUser;
import com.example.minitwitter.post.dto.PostCreateRequest;
import com.example.minitwitter.post.dto.PostResponse;
import com.example.minitwitter.post.dto.TimelineResponse;
import com.example.minitwitter.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CurrentUser currentUser;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostCreateRequest request) {

        Long authorId = currentUser.getId();

        PostResponse response = postService.createPost(authorId, request);

        URI location = URI.create("/api/posts/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping
    public List<PostResponse> getPosts(
            @RequestParam(required = false) Long authorId) {
        if (authorId != null) {
            return postService.getPostsByAuthor(authorId);
        }

        return postService.getPosts();
    }

    @PostMapping("/{id}/like")
    public PostResponse likePost(@PathVariable Long id) {
        return postService.increaseLikeCount(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id) {
        Long requesterId = currentUser.getId();
        postService.deletePost(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/timeline")
    public TimelineResponse getTimeline(
            @RequestParam Long userId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getTimeline(userId, cursor, size);
    }

    @GetMapping("/feed")
    public TimelineResponse getFeed(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getFeed(cursor, size);
    }
}
