package com.example.minitwitter.like.domain;

import com.example.minitwitter.post.domain.Post;
import com.example.minitwitter.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Getter
@Table(
    name = "post_likes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_post_like_post_user",
            columnNames = {"post_id", "user_id"}
        )
    }
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요가 눌린 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 좋아요를 누른 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected PostLike(){}
    public PostLike(Post post, User user){
        this.post = post;
        this.user = user;
    }
}
