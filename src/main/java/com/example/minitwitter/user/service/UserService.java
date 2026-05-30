package com.example.minitwitter.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.minitwitter.storage.dto.UploadedFileResponse;
import com.example.minitwitter.storage.service.StorageService;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.dto.MeResponse;
import com.example.minitwitter.user.dto.ProfileImageResponse;
import com.example.minitwitter.user.dto.UserCreateRequest;
import com.example.minitwitter.user.dto.UserResponse;
import com.example.minitwitter.user.exception.DuplicateLoginIdException;
import com.example.minitwitter.user.exception.DuplicateNicknameException;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;


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

    /*
    프로필 사진 업데이트
    */
    @Transactional
    public ProfileImageResponse updateProfileImageResponse(Long currentUserId, MultipartFile file){
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new UserNotFoundException(currentUserId));
        
        UploadedFileResponse uploadedFile = storageService.uploadImage(
            file, 
            "profile/" + currentUserId, 
            2 * 1024 * 1024);
        
        user.updateProfileImageUrl(uploadedFile.url());

        return new ProfileImageResponse(uploadedFile.url());
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

    public MeResponse getMe(Long currentUserId){
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new UserNotFoundException(currentUserId));

        return new MeResponse(
            user.getId(),
            user.getLoginId(),
            user.getNickName(),
            user.getBio(),
            user.getProfileImageUrl()
        );
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
