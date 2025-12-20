package com.albaraka.digital.controller;

import com.albaraka.digital.dto.admin.AdminCreateUserRequest;
import com.albaraka.digital.dto.admin.AdminCreateUserResponse;
import com.albaraka.digital.dto.admin.AdminUpdateUserStatusRequest;
import com.albaraka.digital.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.albaraka.digital.dto.admin.AdminUserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public AdminCreateUserResponse createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}/status")
    public AdminCreateUserResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserStatusRequest request
    ) {
        return adminUserService.updateUserStatus(id, request);
    }

    @GetMapping
    public Page<AdminUserSummary> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminUserService.listUsers(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public AdminUserSummary getUser(@PathVariable Long id) {
        return adminUserService.getUserById(id);
    }
}