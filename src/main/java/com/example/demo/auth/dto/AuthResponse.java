package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private final String tokenType;   // "Bearer"
    private final String accessToken;
    private final long   expiresIn;   // seconds
    private final UserInfo user;

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private final String userId;
        private final String userNm;
        private final List<String> roles;
    }
}
