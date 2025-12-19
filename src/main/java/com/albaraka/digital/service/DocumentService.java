package com.albaraka.digital.service;

import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.Document;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.model.enums.OperationStatus;
import com.albaraka.digital.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final OperationRepository operationRepository;
    private final AccountService accountService;
    private final DocumentStorageService storageService;

    public Document uploadJustificatif(Long operationId, MultipartFile file) throws IOException {
        Operation op = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Opération introuvable"));

        // Vérifier que l'opération appartient au compte du client courant
        Account currentAccount = accountService.getCurrentUserAccount();
        if (!op.getAccountSource().getId().equals(currentAccount.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas propriétaire de cette opération");
        }

        // Vérifier que l'opération est PENDING
        if (op.getStatus() != OperationStatus.PENDING) {
            throw new IllegalArgumentException("Le justificatif ne peut être ajouté que pour une opération PENDING");
        }

        return storageService.storeDocumentForOperation(op, file);
    }
}