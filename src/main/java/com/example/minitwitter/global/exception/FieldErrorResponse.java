package com.example.minitwitter.global.exception;

public record FieldErrorResponse(
    String field,
    String message
) {
}