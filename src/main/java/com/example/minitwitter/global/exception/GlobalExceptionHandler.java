package com.example.minitwitter.global.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.minitwitter.user.exception.*;
import com.example.minitwitter.post.exception.*;
import com.example.minitwitter.auth.exception.InvalidLoginException;
import com.example.minitwitter.follow.exception.*;
import com.example.minitwitter.like.exception.DuplicatePostLikeException;
import com.example.minitwitter.like.exception.PostLikeNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ErrorResponse handleValidation(MethodArgumentNotValidException exception) {
                List<FieldErrorResponse> errors = exception.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> new FieldErrorResponse(
                                                error.getField(),
                                                error.getDefaultMessage()))
                                .toList();

                return ErrorResponse.of(
                                "VALIDATION_FAILED",
                                "요청 값이 올바르지 않습니다.",
                                errors);
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(UserNotFoundException.class)
        public ErrorResponse handleUserNotFound(UserNotFoundException exception) {
                return ErrorResponse.of(
                                "USER_NOT_FOUND",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(DuplicateNicknameException.class)
        public ErrorResponse handleDuplicateNickname(DuplicateNicknameException exception) {
                return ErrorResponse.of(
                                "DUPLICATE_NICKNAME",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(PostNotFoundException.class)
        public ErrorResponse handlePostNotFound(PostNotFoundException exception) {
                return ErrorResponse.of(
                                "POST_NOT_FOUND",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.FORBIDDEN)
        @ExceptionHandler(PostAccessDeniedException.class)
        public ErrorResponse handlePostAccessDenied(PostAccessDeniedException exception) {
                return ErrorResponse.of(
                                "POST_ACCESS_DENIED",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(SelfFollowException.class)
        public ErrorResponse handleSelfFollow(SelfFollowException exception) {
                return ErrorResponse.of(
                                "SELF_FOLLOW_NOT_ALLOWED",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(DuplicateFollowException.class)
        public ErrorResponse handleDuplicateFollow(DuplicateFollowException exception) {
                return ErrorResponse.of(
                                "DUPLICATE_FOLLOW",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(FollowNotFoundException.class)
        public ErrorResponse handleFollowNotFound(FollowNotFoundException exception) {
                return ErrorResponse.of(
                                "FOLLOW_NOT_FOUND",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(InvalidTimelineSizeException.class)
        public ErrorResponse handleInvalidTimelineSize(InvalidTimelineSizeException exception) {
                return ErrorResponse.of(
                                "INVALID_TIMELINE_SIZE",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(DuplicateLoginIdException.class)
        public ErrorResponse handleDuplicateLoginId(DuplicateFollowException exception) {
                return ErrorResponse.of(
                                "DUPLICATE_LOGIN_ID",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        @ExceptionHandler(InvalidLoginException.class)
        public ErrorResponse handleInvalidLogin(InvalidLoginException exception) {
                return ErrorResponse.of(
                                "INVALID_LOGIN",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(DuplicatePostLikeException.class)
        public ErrorResponse handleDuplicatePostLike(DuplicatePostLikeException exception) {
                return ErrorResponse.of(
                                "DUPLICATE_POST_LIKE",
                                exception.getMessage());
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(PostLikeNotFoundException.class)
        public ErrorResponse handlePostLikeNotFound(PostLikeNotFoundException exception) {
                return ErrorResponse.of(
                                "POST_LIKE_NOT_FOUND",
                                exception.getMessage());
        }
}
