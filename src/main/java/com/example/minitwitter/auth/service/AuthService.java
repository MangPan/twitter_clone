package com.example.minitwitter.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minitwitter.auth.dto.LoginRequest;
import com.example.minitwitter.auth.dto.LoginResponse;
import com.example.minitwitter.auth.exception.InvalidLoginException;
import com.example.minitwitter.auth.jwt.JwtTokenProvider;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByLoginId(request.loginId())
            .orElseThrow(() -> new InvalidLoginException());

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidLoginException();
        }
        

        String accessToken = jwtTokenProvider.createAccessToken(user);

        return new LoginResponse(
            accessToken,
            "Bearer",
            user.getId(),
            user.getLoginId(),
            user.getNickName(),
            user.getProfileImageUrl()
        );
    }
    
}
