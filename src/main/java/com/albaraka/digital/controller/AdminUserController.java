package com.albaraka.digital.controller;

import com.albaraka.digital.dto.admin.AdminCreateUserRequest;
import com.albaraka.digital.dto.admin.AdminCreateUserResponse;
import com.albaraka.digital.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public AdminCreateUserResponse createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return adminUserService.createUser(request);
    }
}