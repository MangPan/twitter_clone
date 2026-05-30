package com.example.minitwitter.storage.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.minitwitter.storage.config.S3Properties;
import com.example.minitwitter.storage.dto.UploadedFileResponse;
import com.example.minitwitter.storage.exception.StorageException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class StorageService {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private final S3Client s3Client;
    private final S3Properties properties;

    @PostConstruct
    public void initBucket() {
        String bucket = properties.bucket();

        try {
            boolean exists = s3Client.listBuckets()
                    .buckets()
                    .stream()
                    .anyMatch(existingBucket -> existingBucket.name().equals(bucket));

            if (!exists) {
                s3Client.createBucket(
                        CreateBucketRequest.builder()
                                .bucket(bucket)
                                .build());
            }
        } catch (S3Exception exception) {
            throw new StorageException("스토리지 버킷 초기화 실패", exception);
        }
    }

    public UploadedFileResponse uploadImage(
            MultipartFile file,
            String directory,
            long maxSizeBytes) {

        validateDirectory(directory);
        validateImage(file, maxSizeBytes);

        String contentType = file.getContentType();
        String extension = getExtension(contentType);
        String objectKey = directory + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes()));

            return new UploadedFileResponse(
                    objectKey,
                    properties.publicBaseUrl() + "/" + objectKey,
                    contentType,
                    file.getSize());
        } catch (IOException exception) {
            throw new StorageException("파일을 읽는 중 오류가 발생했습니다.", exception);
        } catch (S3Exception exception) {
            throw new StorageException("파일 업로드에 실패했습니다.", exception);
        }
    }

    private void validateDirectory(String directory) {
        if (directory == null || directory.isBlank()) {
            throw new StorageException("스토리지 디렉터리는 필수입니다.");
        }

        if (directory.startsWith("/") || directory.endsWith("/")) {
            throw new StorageException("스토리지 디렉터리 형식이 올바르지 않습니다. directory=" + directory);
        }

        if (directory.contains("..")) {
            throw new StorageException("스토리지 디렉터리 형식이 올바르지 않습니다. directory=" + directory);
        }
    }

    private void validateImage(MultipartFile file, long maxSizeBytes) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("업로드할 파일이 없습니다.");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new StorageException("파일 크기가 너무 큽니다 maxSizeBytes=" + maxSizeBytes);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new StorageException("지원하지 않는 이미지 형식입니다. contentType=" + contentType);
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new StorageException("지원하지 않는 이미지 형식입니다. contentType=" + contentType);
        };
    }

}
