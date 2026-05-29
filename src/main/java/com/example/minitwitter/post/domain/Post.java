package com.example.minitwitter.post.domain;

import com.example.minitwitter.global.BaseTimeEntity;
import com.example.minitwitter.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;;

@Entity
@Table(name = "posts")
public class Post extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false)
    private Long likeCount;

    protected Post(){}

    public Post(User author, String content){
        this.author = author;
        this.content = content;
        this.likeCount = 0L;
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        if(this.likeCount > 0)
            this.likeCount--;
    }

    public boolean isAuthor(Long userId){
        return this.author.getId().equals(userId);
    }
    
    public Long getId(){
        return this.id;
    }

    public User getAuthor(){
        return this.author;
    }

    public String getContent(){
        return this.content;
    }

    public Long getLikeCount(){
        return this.likeCount;
    }
}
