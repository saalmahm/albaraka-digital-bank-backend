package com.albaraka.digital.service;

import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.User;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public Account getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

        return accountRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalStateException("Compte non trouvé pour cet utilisateur"));
    }
}