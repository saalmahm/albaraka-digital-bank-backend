package com.albaraka.digital.service;

import com.albaraka.digital.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorService {

    private final AccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateRandom10Digits();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateRandom10Digits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}