package com.example.minitwitter.user.domain;

import com.example.minitwitter.global.BaseTimeEntity;

import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User extends BaseTimeEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nickName;

    @Column(length = 100)
    private String bio;

    protected User(){}

    public User(String nickname, String bio){
        this.nickName = nickname;
        this.bio = bio;
    }

    public void update(String nickname, String bio){
        if(nickname != null)
            this.nickName = nickname;

        if(bio != null)
            this.bio = bio;
    }

    public Long getId(){return this.id;}

    public String getNickName(){return this.nickName;}

    public String getBio(){return this.bio;}

}
