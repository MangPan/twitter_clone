package com.example.minitwitter.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minitwitter.user.dto.UserCreateRequest;
import com.example.minitwitter.user.dto.UserProfileResponse;
import com.example.minitwitter.user.dto.UserResponse;
import com.example.minitwitter.user.service.UserProfileService;
import com.example.minitwitter.user.service.UserService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);

        URI location = URI.create("/api/users" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/by-nickname/{nickName}")
    public UserResponse getUserbyNickName(@PathVariable String nickName) {
        return userService.getUserByNickName(nickName);
    }

    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        return userProfileService.getUserProfile(userId);
    }
    

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
