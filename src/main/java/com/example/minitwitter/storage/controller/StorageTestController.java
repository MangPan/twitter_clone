package com.example.minitwitter.storage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.minitwitter.storage.dto.UploadedFileResponse;
import com.example.minitwitter.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/storage-test")
@RequiredArgsConstructor
public class StorageTestController {

    private static final long MAX_TEST_IMAGE_SIZE = 5 * 1024 * 1024;

    private final StorageService storageService;

    @PostMapping("/images")
    public UploadedFileResponse uploadImage(@RequestParam MultipartFile file) {
        return storageService.uploadImage(
                file,
                "test",
                MAX_TEST_IMAGE_SIZE);
    }
}
