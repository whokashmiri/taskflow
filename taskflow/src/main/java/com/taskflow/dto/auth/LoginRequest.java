package com.taskflow.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8 , max = 20)
    private String password;
}
