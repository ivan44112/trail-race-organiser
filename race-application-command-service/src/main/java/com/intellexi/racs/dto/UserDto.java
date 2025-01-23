package com.intellexi.racs.dto;

import com.intellexi.racs.utils.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private Role role;
}
