package com.albaraka.digital.service;

import com.albaraka.digital.dto.operation.OperationRequest;
import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.model.enums.OperationStatus;
import com.albaraka.digital.model.enums.OperationType;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationService {

    private static final BigDecimal THRESHOLD = BigDecimal.valueOf(10_000);

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    @Transactional
    public Operation createClientOperation(OperationRequest request) {

        Account source = accountService.getCurrentUserAccount();

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }

        OperationType type = request.getType();
        if (type == null) {
            throw new IllegalArgumentException("Le type d'opération est obligatoire");
        }

        // Gestion du compte destinataire pour les virements
        Account destination = null;
        if (type == OperationType.TRANSFER) {
            if (request.getDestinationAccountNumber() == null
                    || request.getDestinationAccountNumber().isBlank()) {
                throw new IllegalArgumentException("Le numéro de compte destinataire est obligatoire pour un virement");
            }

            destination = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Compte destinataire introuvable"));

            if (destination.getId().equals(source.getId())) {
                throw new IllegalArgumentException("Le compte source et le compte destinataire doivent être différents");
            }
        }

        // Vérifier solde suffisant pour retraits et virements
        if (type == OperationType.WITHDRAWAL || type == OperationType.TRANSFER) {
            if (source.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Solde insuffisant pour effectuer cette opération");
            }
        }

        // CAS A : montant ≤ 10 000 DH -> VALIDATED + mise à jour immédiate des soldes
        if (amount.compareTo(THRESHOLD) <= 0) {

            Operation operation = Operation.builder()
                    .type(type)
                    .amount(amount)
                    .status(OperationStatus.VALIDATED)
                    .accountSource(source)
                    .accountDestination(destination)
                    .createdAt(LocalDateTime.now())
                    .executedAt(LocalDateTime.now())
                    .build();

            // Mise à jour des soldes
            switch (type) {
                case DEPOSIT -> source.setBalance(source.getBalance().add(amount));
                case WITHDRAWAL -> source.setBalance(source.getBalance().subtract(amount));
                case TRANSFER -> {
                    source.setBalance(source.getBalance().subtract(amount));
                    destination.setBalance(destination.getBalance().add(amount));
                    accountRepository.save(destination);
                }
            }

            accountRepository.save(source);
            return operationRepository.save(operation);
        }

        // CAS B : montant > 10 000 DH -> PENDING, pas de modification de solde
        Operation operation = Operation.builder()
                .type(type)
                .amount(amount)
                .status(OperationStatus.PENDING)
                .accountSource(source)
                .accountDestination(destination)
                .createdAt(LocalDateTime.now())
                .build();

        // NE PAS toucher aux soldes ici
        return operationRepository.save(operation);
    }

        // =========================
    //      PARTIE AGENT
    // =========================
    public Page<Operation> listPendingOperations(Pageable pageable) {
      return operationRepository.findByStatus(OperationStatus.PENDING, pageable);
    }
    @Transactional
    public Operation approveOperation(Long operationId) {
        Operation op = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Opération introuvable"));
        if (op.getStatus() != OperationStatus.PENDING) {
            throw new IllegalArgumentException("Seules les opérations PENDING peuvent être approuvées");
        }
        Account source = op.getAccountSource();
        Account destination = op.getAccountDestination();
        BigDecimal amount = op.getAmount();
        OperationType type = op.getType();
        // Mise à jour des soldes comme pour les opérations <= 10 000
        switch (type) {
            case DEPOSIT -> source.setBalance(source.getBalance().add(amount));
            case WITHDRAWAL -> source.setBalance(source.getBalance().subtract(amount));
            case TRANSFER -> {
                source.setBalance(source.getBalance().subtract(amount));
                if (destination == null) {
                    throw new IllegalStateException("Compte destinataire manquant pour un virement");
                }
                destination.setBalance(destination.getBalance().add(amount));
                accountRepository.save(destination);
            }
        }
        accountRepository.save(source);
        op.setStatus(OperationStatus.VALIDATED);
        LocalDateTime now = LocalDateTime.now();
        if (op.getExecutedAt() == null) {
            op.setExecutedAt(now);
        }
        op.setValidatedAt(now);
        return operationRepository.save(op);
    }
    @Transactional
    public Operation rejectOperation(Long operationId) {
        Operation op = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Opération introuvable"));
        if (op.getStatus() != OperationStatus.PENDING) {
            throw new IllegalArgumentException("Seules les opérations PENDING peuvent être rejetées");
        }
        op.setStatus(OperationStatus.REJECTED);
        // Option : historiser aussi la date de décision
        op.setValidatedAt(LocalDateTime.now());
        // Pas de modification de solde
        return operationRepository.save(op);
    }
}