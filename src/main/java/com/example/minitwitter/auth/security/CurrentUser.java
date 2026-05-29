package com.example.minitwitter.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public Long getId(){
        Authentication authentication = 
            SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if(!(principal instanceof Long userId)){
            throw new IllegalStateException("인증 사용자 정보가 올바르지 않습니다.");
        }

        return userId;

    }
}
