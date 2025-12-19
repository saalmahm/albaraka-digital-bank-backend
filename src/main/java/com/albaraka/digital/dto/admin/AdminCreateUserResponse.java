package com.albaraka.digital.dto.admin;

import com.albaraka.digital.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminCreateUserResponse {

    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private boolean active;
    private String accountNumber;
}