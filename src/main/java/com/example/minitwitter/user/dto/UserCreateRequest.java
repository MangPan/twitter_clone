package com.example.minitwitter.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min=4, max=20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    String loginId,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min=8, max=72, message = "비밀번호는 8자 이상 72자 이하로 입력해주세요.")
    String password,

    @NotBlank(message = "NickName은 필수입니다.")
    @Size(max = 20, message = "NickName은 20자 이하로 입력해주세요.")
    String nickName,

    @Size(max = 100, message = "Bio는 100자 이하로 입력해주세요.")
    String bio
) {
}