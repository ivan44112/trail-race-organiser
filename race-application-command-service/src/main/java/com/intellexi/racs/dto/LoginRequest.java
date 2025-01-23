package com.intellexi.racs.dto;

import com.intellexi.racs.utils.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {
    private String email;
    private Role role;
}
