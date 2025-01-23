package com.intellexi.racs.controller;

import com.intellexi.racs.domain.UserEntity;
import com.intellexi.racs.dto.LoginRequest;
import com.intellexi.racs.dto.LoginResponse;
import com.intellexi.racs.jwt.JwtUtil;
import com.intellexi.racs.service.user.UserEntityService;
import com.intellexi.racs.utils.Role;
import com.intellexi.racs.utils.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTests {

    @Mock
    private UserEntityService userEntityService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        String email = "marieann@gmail.com";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpbC5jb20iLCJyb2xlIjoiQVBQTElDQU5UIiwiaWF0IjoxNzM3NTgyOTQ5LCJleHAiOjE3Mzc1ODY1NDl9.7RbryxFVfbArWNLUPehfi2rllLXnHWEoxt-udmgrreQ";
        LoginRequest request = new LoginRequest(email, Role.ADMINISTRATOR);

        UserEntity userEntity = UserEntity.builder()
                .firstName("Marie")
                .lastName("Ann")
                .email(email)
                .role(Role.ADMINISTRATOR)
                .build();

        when(userEntityService.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(jwtUtil.generateToken(email, String.valueOf(Role.ADMINISTRATOR))).thenReturn(token);

        ResponseEntity<?> response = authController.login(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof LoginResponse);

        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        assertEquals(token, loginResponse.getToken());

        verify(userEntityService, times(1)).findByEmail(email);
        verify(jwtUtil, times(1)).generateToken(email, String.valueOf(Role.ADMINISTRATOR));
    }

    @Test
    void testLogin_UserNotFound() {
        String email = "test@example.com";
        LoginRequest request = new LoginRequest(email, Role.APPLICANT);

        when(userEntityService.findByEmail(email)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> authController.login(request));

        assertEquals("Unable to find user with email: " + email, exception.getMessage());

        verify(userEntityService, times(1)).findByEmail(email);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
