package com.example.minitwitter.follow.domain;

import com.example.minitwitter.global.BaseTimeEntity;
import com.example.minitwitter.user.domain.User;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "follows",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_follower_following",
            columnNames = {"follower_id", "following_id"}
        )
    }
)
public class Follow extends BaseTimeEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    public Follow(User follower, User following){
        this.follower = follower;
        this.following = following;
    }

    protected Follow(){}
}
