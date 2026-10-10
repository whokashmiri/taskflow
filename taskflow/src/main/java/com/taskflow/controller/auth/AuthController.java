package com.taskflow.controller.auth;

import com.taskflow.dto.auth.LoginRequest;
import com.taskflow.dto.auth.RegisterRequest;
import com.taskflow.dto.auth.RegisterResponse;
import com.taskflow.model.user.User;
import com.taskflow.service.JwtService;
import com.taskflow.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService , JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest){
       User user = userService.login(loginRequest);
       String token = jwtService.generateToken(user);
       return ResponseEntity.ok(token);
    }
}