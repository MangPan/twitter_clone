package com.example.minitwitter.user.domain;

import com.example.minitwitter.global.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "users")
@Getter
public class User extends BaseTimeEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickName;

    @Column(length = 100)
    private String bio;

    @Column(length = 500)
    private String profileImageUrl;

    protected User(){}

    public User(
        String loginId,
        String password,
        String nickName,
        String bio
    ){
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.bio = bio;
    }


    public void updateProfile(String nickname, String bio){
        if(nickname != null)
            this.nickName = nickname;

        if(bio != null)
            this.bio = bio;
    }

    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
