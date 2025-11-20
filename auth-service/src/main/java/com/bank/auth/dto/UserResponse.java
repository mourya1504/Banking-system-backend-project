package com.bank.auth.dto;

// File: auth-service/src/main/java/com/bank/auth/dto/UserResponse.java

import java.time.LocalDateTime;
import java.util.Set;

import com.bank.auth.entity.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String phoneNumber;
    
    private UserStatus status;
    
    private Set<String> roles;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLoginAt;
}