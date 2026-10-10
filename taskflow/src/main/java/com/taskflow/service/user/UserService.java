package com.taskflow.service.user;

import com.taskflow.dto.auth.LoginRequest;
import com.taskflow.dto.auth.LoginResponse;
import com.taskflow.dto.auth.RegisterRequest;
import com.taskflow.dto.auth.RegisterResponse;
import com.taskflow.exception.auth.EmailAlreadyExistException;
import com.taskflow.exception.auth.InvalidCredentialsException;
import com.taskflow.model.user.User;
import com.taskflow.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistException("User already registered");
        }

        User user = new User();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        user.setPassword(hashedPassword);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        User saved = userRepository.save(user);


        return new RegisterResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getCreatedAt());
    }

    public User login(LoginRequest loginRequest) {

        Optional<User> user =
                userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        User existingUser = user.get();

        String rawPassword = loginRequest.getPassword();

        if (!passwordEncoder.matches(
                rawPassword,
                existingUser.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        return existingUser;
    }



}