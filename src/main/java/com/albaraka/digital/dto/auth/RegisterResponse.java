package com.albaraka.digital.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String accountNumber;
}