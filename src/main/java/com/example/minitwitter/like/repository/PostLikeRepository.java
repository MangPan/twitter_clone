package com.example.minitwitter.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.minitwitter.like.domain.PostLike;
import com.example.minitwitter.post.domain.Post;
import com.example.minitwitter.user.domain.User;

// N:M 관계
public interface PostLikeRepository extends JpaRepository<PostLike, Long>{
    
    boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Query("""
            select pl
            from PostLike pl
            join fetch pl.post p
            join fetch p.author
            where pl.user.id = :userId
            order by pl.id desc
            """)
    List<PostLike> findByUserIdWithPostAndAuthor(Long userId);
    // userId로 postlike를 뒤짐 
    // 즉 특정 유저의 좋아요를 누른 게시물 탐색이 목적

    @Query("""
            select pl
            from PostLike pl
            join fetch pl.user
            where pl.post.id = :postId
            order by pl.id desc
            """)
    List<PostLike> findByPostIdWithUser(Long postId);
    // postId로 postlike를 뒤짐
    // 즉 게시물에 대한 좋아요를 누른 user 탐색이 목적
}
