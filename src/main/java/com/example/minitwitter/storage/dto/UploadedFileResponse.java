package com.example.minitwitter.storage.dto;

public record UploadedFileResponse(
    String objectKey,
    String url,
    String contentType,
    long size
) {
}
