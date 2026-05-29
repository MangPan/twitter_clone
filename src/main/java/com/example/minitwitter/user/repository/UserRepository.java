package com.example.minitwitter.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minitwitter.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByNickName(String nickname);

    boolean existsByLoginId(String loginId);

    Optional<User> findByNickName(String nickName);

    Optional<User> findByLoginId(String loginId);
} 

