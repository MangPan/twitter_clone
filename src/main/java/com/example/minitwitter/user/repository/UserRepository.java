package com.example.minitwitter.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minitwitter.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByNickName(String nickName);

    boolean existsByNickName(String nickname);
} 

