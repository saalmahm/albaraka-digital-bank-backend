package com.albaraka.digital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String loginPage() {
        // renvoie src/main/resources/templates/login.html
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        // renvoie src/main/resources/templates/access-denied.html
        return "access-denied";
    }
}