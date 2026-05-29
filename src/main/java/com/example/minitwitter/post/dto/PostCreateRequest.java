package com.example.minitwitter.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
    @NotNull(message = "작성자 id는 필수입니다.")
    Long authorId,

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 280, message = "내용은 280자 이하로 입력해주세요.")
    String content
) {
}