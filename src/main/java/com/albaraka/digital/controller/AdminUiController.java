package com.albaraka.digital.controller;

import com.albaraka.digital.dto.admin.AdminCreateUserRequest;
import com.albaraka.digital.dto.admin.AdminUpdateUserStatusRequest;
import com.albaraka.digital.dto.admin.AdminUserSummary;
import com.albaraka.digital.model.enums.UserRole;
import com.albaraka.digital.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUiController {

    private final AdminUserService adminUserService;

    @GetMapping("/home")
    public String adminHome(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {

        Page<AdminUserSummary> usersPage = adminUserService.listUsers(PageRequest.of(page, size));
        model.addAttribute("usersPage", usersPage);
        return "admin-home";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String email,
                             @RequestParam String fullName,
                             @RequestParam UserRole role,
                             @RequestParam String password) {

        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setEmail(email);
        req.setFullName(fullName);
        req.setRole(role);
        req.setActive(true);
        req.setPassword(password);

        adminUserService.createUser(req);
        return "redirect:/admin/home";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id) {
        AdminUserSummary user = adminUserService.getUserById(id);

        if (user.isActive()) {
            adminUserService.deactivateUser(id);
        } else {
            AdminUpdateUserStatusRequest req = new AdminUpdateUserStatusRequest();
            req.setActive(true);
            adminUserService.updateUserStatus(id, req);
        }

        return "redirect:/admin/home";
    }
}