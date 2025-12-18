package com.albaraka.digital.controller;

import com.albaraka.digital.dto.auth.LoginRequest;
import com.albaraka.digital.dto.auth.LoginResponse;
import com.albaraka.digital.dto.auth.RegisterRequest;
import com.albaraka.digital.dto.auth.RegisterResponse;
import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.User;
import com.albaraka.digital.model.enums.UserRole;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.UserRepository;
import com.albaraka.digital.security.jwt.JwtService;
import com.albaraka.digital.service.AccountNumberGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountNumberGeneratorService accountNumberGeneratorService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.CLIENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        Account account = Account.builder()
                .accountNumber(accountNumberGeneratorService.generateUniqueAccountNumber())
                .balance(BigDecimal.ZERO)
                .owner(savedUser)
                .build();

        Account savedAccount = accountRepository.save(account);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedAccount.getAccountNumber()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }
}