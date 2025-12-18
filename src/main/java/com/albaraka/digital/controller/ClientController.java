package com.albaraka.digital.controller;

import com.albaraka.digital.dto.client.AccountMeResponse;
import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.User;
import com.albaraka.digital.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final AccountService accountService;

    @GetMapping("/account/me")
    public AccountMeResponse getMyAccount() {
        Account account = accountService.getCurrentUserAccount();
        User owner = account.getOwner();

        return new AccountMeResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                owner.getEmail(),
                owner.getFullName()
        );
    }
}