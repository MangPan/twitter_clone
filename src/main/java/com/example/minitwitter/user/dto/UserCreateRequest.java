package com.example.minitwitter.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "NickName은 필수입니다.")
    @Size(max = 20, message = "NickName은 20자 이하로 입력해주세요.")
    String nickName,

    @Size(max = 100, message = "Bio는 100자 이하로 입력해주세요.")
    String bio
) {
}