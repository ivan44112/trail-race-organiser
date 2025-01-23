package com.intellexi.racs.controller;

import com.intellexi.racs.aspect.LogRequests;
import com.intellexi.racs.dto.LoginRequest;
import com.intellexi.racs.dto.LoginResponse;
import com.intellexi.racs.dto.UserDto;
import com.intellexi.racs.jwt.JwtUtil;
import com.intellexi.racs.service.user.UserEntityService;
import com.intellexi.racs.utils.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@LogRequests
@Slf4j
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserEntityService userEntityService;

    public AuthController(JwtUtil jwtUtil, UserEntityService userEntityService) {
        this.jwtUtil = jwtUtil;
        this.userEntityService = userEntityService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserDto user = userEntityService.findByEmail(request.getEmail())
                .map(u -> UserDto.builder()
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .dob(u.getDob())
                        .role(u.getRole())
                        .build())
                .orElseThrow(() -> new ValidationException("Unable to find user with email: " + request.getEmail()));

        log.info("User successfully found by email: " + request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), String.valueOf(user.getRole()));
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

