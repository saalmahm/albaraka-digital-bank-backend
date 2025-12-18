package com.albaraka.digital.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountMeResponse {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private String ownerEmail;
    private String ownerFullName;
}