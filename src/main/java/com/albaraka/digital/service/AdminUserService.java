package com.albaraka.digital.service;

import com.albaraka.digital.dto.admin.AdminCreateUserRequest;
import com.albaraka.digital.dto.admin.AdminCreateUserResponse;
import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.User;
import com.albaraka.digital.model.enums.UserRole;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final com.albaraka.digital.service.AccountNumberGeneratorService accountNumberGeneratorService;
    private final PasswordEncoder passwordEncoder;

    public AdminCreateUserResponse createUser(AdminCreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        UserRole role = request.getRole();
        if (role == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .active(Boolean.TRUE.equals(request.getActive()))
                .build();

        User savedUser = userRepository.save(user);

        String accountNumber = null;

        if (role == UserRole.CLIENT) {
            Account account = Account.builder()
                    .accountNumber(accountNumberGeneratorService.generateUniqueAccountNumber())
                    .balance(BigDecimal.ZERO)
                    .owner(savedUser)
                    .build();

            Account savedAccount = accountRepository.save(account);
            accountNumber = savedAccount.getAccountNumber();
        }

        return new AdminCreateUserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                savedUser.isActive(),
                accountNumber
        );
    }
}