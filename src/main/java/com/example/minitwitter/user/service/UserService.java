package com.example.minitwitter.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.dto.UserCreateRequest;
import com.example.minitwitter.user.dto.UserProfileResponse;
import com.example.minitwitter.user.dto.UserResponse;
import com.example.minitwitter.user.exception.DuplicateLoginIdException;
import com.example.minitwitter.user.exception.DuplicateNicknameException;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /*
    계정 생성 
    */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByNickName(request.nickName())) {
            throw new DuplicateNicknameException(request.nickName());
        }

        if(userRepository.existsByLoginId(request.loginId())){
            throw new DuplicateLoginIdException(request.loginId());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
            request.loginId(),
            encodedPassword,
            request.nickName(),
            request.bio()
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }



    public List<UserResponse> getUsers(){
        return userRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }



    public UserResponse getUser(Long id){
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return toResponse(user);
    }




    public UserResponse getUserByNickName(String nickName){
        User user = userRepository.findByNickName(nickName)
            .orElseThrow(() -> new UserNotFoundException(nickName));

        return toResponse(user);
    }




    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getBio(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
