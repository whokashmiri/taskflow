package com.taskflow.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private UUID userId;
    private String name;
    private String email;
}
