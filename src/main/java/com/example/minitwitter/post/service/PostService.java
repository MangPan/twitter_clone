package com.example.minitwitter.post.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.minitwitter.post.domain.Post;
import com.example.minitwitter.post.domain.PostImage;
import com.example.minitwitter.post.dto.PostCreateRequest;
import com.example.minitwitter.post.dto.PostImageUploadResponse;
import com.example.minitwitter.post.dto.PostResponse;
import com.example.minitwitter.post.dto.TimelineResponse;
import com.example.minitwitter.post.exception.InvalidTimelineSizeException;
import com.example.minitwitter.post.exception.PostAccessDeniedException;
import com.example.minitwitter.post.exception.PostImageLimitExceededException;
import com.example.minitwitter.post.exception.PostNotFoundException;
import com.example.minitwitter.post.repository.PostImageRepository;
import com.example.minitwitter.post.repository.PostRepository;
import com.example.minitwitter.storage.dto.UploadedFileResponse;
import com.example.minitwitter.storage.service.StorageService;
import com.example.minitwitter.user.domain.User;
import com.example.minitwitter.user.exception.UserNotFoundException;
import com.example.minitwitter.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public PostResponse createPost(Long authorId, PostCreateRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        Post post = new Post(author, request.content());
        Post savedPost = postRepository.save(post);

        return toResponse(savedPost);
    }

    @Transactional
    public PostImageUploadResponse uploadPostImages(
        Long postId,
        Long currentUserId,
        List<MultipartFile> files
    ){
        Post post = getPostByIdWithAuthorOrThrow(postId);

        if(!post.isAuthor(currentUserId)){
            throw new PostAccessDeniedException(postId, currentUserId);
        }

        if(files == null || files.isEmpty()){
            return new PostImageUploadResponse(
                postId,
                postImageRepository.findByPostIdOrderByDisplayOrderAsc(postId)
                    .stream()
                    .map(PostImage::getImageUrl)
                    .toList()
            );
        }

        long currentCount = postImageRepository.countByPostId(postId);

        if(currentCount + files.size() > 4){
            throw new PostImageLimitExceededException(postId);
        }

        List<PostImage> savedImages = new ArrayList<>();
        int displayOrder = (int)currentCount;

        for(MultipartFile file : files){
            UploadedFileResponse uploadedFile = storageService.uploadImage(
                file,
                "post/" + postId, 
                5 * 1024 * 1024);
            
            PostImage postImage = new PostImage(
                post,
                uploadedFile.url(),
                uploadedFile.objectKey(),
                displayOrder
            );
            
            savedImages.add(postImageRepository.save(postImage));
            displayOrder++;
        }

        List<String> imageUrls = postImageRepository.findByPostIdOrderByDisplayOrderAsc(postId)
            .stream()
            .map(PostImage::getImageUrl)
            .toList();

        return new PostImageUploadResponse(postId, imageUrls);
    }

    public List<PostResponse> getPosts() {
        return postRepository.findAllWithAuthorOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PostResponse getPost(Long id) {
        Post post = getPostByIdWithAuthorOrThrow(id);
        return toResponse(post);
    }

    @Transactional
    public void deletePost(Long id, Long requesterId) {
        Post post = getPostByIdWithAuthorOrThrow(id);

        if (!post.isAuthor(requesterId)) {
            throw new PostAccessDeniedException(id, requesterId);
        }

        postRepository.delete(post);
    }

    public TimelineResponse getTimeline(Long userId, Long cursor, int size) {
        if (size < 1 || size > 50) {
            throw new InvalidTimelineSizeException(size);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int querySize = size + 1; // 요청보다 1개 더 가져와봄
        List<Post> posts;

        // PageRequest.of(pageNumber, pageSize)로 가져오면 pageSize가 요소의 최대 개수.
        // 요청보다 적을 수도 있음!
        if (cursor == null) { // 첫 타임라인 조회인가?
            posts = postRepository.findTimeLineFirstPage(
                    userId,
                    PageRequest.of(0, querySize));
        } else { // 첫 타임라인 조회가 아님 -> cursor가 있음
            posts = postRepository.findTimelineByCursor(
                    userId,
                    cursor,
                    PageRequest.of(0, querySize));
        }

        boolean hasNext = posts.size() > size; // 하나 더 있다면 hasNext는 true

        List<Post> slicedPosts = posts.stream() // 요청만큼의 수만 전달하기 위해 자름
                .limit(size)
                .toList();

        Long nextCursor = null;

        if (!slicedPosts.isEmpty()) { // slicedPosts의 요소가 존재한다면 slicedPosts의 마지막 요소의 ID를 cursor로 설정
            nextCursor = slicedPosts.get(slicedPosts.size() - 1).getId();
        }

        return new TimelineResponse(
                slicedPosts.stream()
                        .map(this::toResponse)
                        .toList(),
                nextCursor,
                hasNext);
    }

    public TimelineResponse getFeed(Long cursor, int size) {
        if (size < 1 || size > 50) {
            throw new InvalidTimelineSizeException(size);
        }

        int querySize = size + 1;

        List<Post> posts = cursor == null
                ? postRepository.findFeedFirstPage(PageRequest.of(0, querySize))
                : postRepository.findFeedByCursor(cursor, PageRequest.of(0, querySize));
        
        boolean hasNext = posts.size() > size;

        List<Post> slicedPosts = posts.stream()
            .limit(size)
            .toList();

        Long nextCursor = null;
        if(!slicedPosts.isEmpty()){
            nextCursor = slicedPosts.get(slicedPosts.size()-1).getId();
        }

        return new TimelineResponse(
            slicedPosts.stream().map(this::toResponse).toList(),
            nextCursor,
            hasNext
        );
    }

    // 자신의 timeline 메서드
    public TimelineResponse getMyTimeline(Long currentUserId, Long cursor, int size){
        return getTimeline(currentUserId, cursor, size);
    }

    public List<PostResponse> getPostsByAuthor(Long authorId) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        return postRepository.findByAuthorIdWithAuthorOrderByIdDesc(authorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Post getPostByIdWithAuthorOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    private PostResponse toResponse(Post post) {
        List<String> imageUrls = postImageRepository.findByPostIdOrderByDisplayOrderAsc(post.getId())
            .stream()
            .map(PostImage::getImageUrl)
            .toList();


        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getNickName(),
                post.getContent(),
                imageUrls,
                post.getLikeCount(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
