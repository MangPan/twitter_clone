package com.example.minitwitter.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minitwitter.post.domain.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long>{

    List<PostImage> findByPostIdOrderByDisplayOrderAsc(Long postId);

    long countByPostId(Long postId);
    
}