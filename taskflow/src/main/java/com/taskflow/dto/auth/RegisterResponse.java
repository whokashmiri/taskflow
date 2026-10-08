package com.taskflow.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}