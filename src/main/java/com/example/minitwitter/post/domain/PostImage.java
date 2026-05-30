package com.example.minitwitter.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "post_iamges")
public class PostImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String objectKey;

    @Column(nullable = false)
    private Integer displayOrder;

    protected PostImage(){}
    public PostImage(Post post, String imageUrl, String objectKey, Integer displayOrder){
        this.post = post;
        this.imageUrl = imageUrl;
        this.objectKey = objectKey;
        this.displayOrder = displayOrder;
    }
}
