package com.albaraka.digital.repository;

import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findByOwner(User owner);
}