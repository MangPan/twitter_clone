package com.example.minitwitter.like.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minitwitter.like.domain.PostLike;
import com.example.minitwitter.like.dto.LikeUserResponse;
import com.example.minitwitter.like.exception.DuplicatePostLikeException;
import com.example.minitwitter.like.exception.PostLikeNotFoundException;
import com.example.minitwitter.like.repository.PostLikeRepository;
import com.example.minitwitter.post.domain.Post;
import com.example.minitwitter.post.dto.PostResponse;
import com.example.minitwitter.post.exception.PostAccessDeniedException;
import com.example.minitwitter.post.exception.PostNotFoundException;
import com.example.minitwitter.post.repository.PostRepository;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 좋아요 누름
    @Transactional
    public PostResponse likePost(Long postId, Long userId) {
        Post post = getPostByIdWithAuthorOrThrow(postId);
        User user = getUserOrThrow(userId);

        if (this.postLikeRepository.existsByPostAndUser(post, user)) {
            throw new DuplicatePostLikeException(postId, userId);
        }

        PostLike postLike = new PostLike(post, user);
        postLikeRepository.save(postLike);

        post.increaseLikeCount();

        return toPostResponse(post);
    }

    // 좋아요 취소
    @Transactional
    public PostResponse unlikePost(Long postId, Long userId) {
        Post post = getPostByIdWithAuthorOrThrow(postId);
        User user = getUserOrThrow(userId);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
            .orElseThrow(() -> new PostLikeNotFoundException(postId, userId));


        postLikeRepository.delete(postLike);
        post.decreaseLikeCount();

        return toPostResponse(post);
    }

    // userId가 좋아요 표시한 게시물 목록
    public List<PostResponse> getMyLikedPost(Long userId){
        getUserOrThrow(userId);

        return postLikeRepository.findByUserIdWithPostAndAuthor(userId)
            .stream()
            .map(postLike -> toPostResponse(postLike.getPost()))
            .toList();
    }

    // 내 게시물을 좋아요 한 user 목록 < 본인만 확인 가능
    public List<LikeUserResponse> getLikeUsers(Long postId, Long currentUserId){
        Post post = getPostByIdWithAuthorOrThrow(postId);

        if(!post.isAuthor(currentUserId)){
            throw new PostAccessDeniedException(postId, currentUserId);
        }

        return postLikeRepository.findByPostIdWithUser(postId)
            .stream()
            .map(postLike -> toLikeUserResponse(postLike.getUser()))
            .toList();
    }


    private Post getPostByIdWithAuthorOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private PostResponse toPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getNickName(),
                post.getContent(),
                post.getLikeCount(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    private LikeUserResponse toLikeUserResponse(User user) {
        return new LikeUserResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickName(),
                user.getProfileImageUrl());
    }
}
