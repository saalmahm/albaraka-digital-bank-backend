package com.albaraka.digital.controller;

import com.albaraka.digital.dto.operation.OperationRequest;
import com.albaraka.digital.dto.operation.OperationResponse;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.service.OperationService;
import com.albaraka.digital.service.OperationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import com.albaraka.digital.service.DocumentService;
import com.albaraka.digital.model.entity.Document;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/client/operations")
@RequiredArgsConstructor
public class ClientOperationController {

    private final OperationService operationService;
    private final OperationQueryService operationQueryService;
    private final DocumentService documentService;

    @PostMapping
    public OperationResponse createOperation(@Valid @RequestBody OperationRequest request) {
        Operation op = operationService.createClientOperation(request);
        return new OperationResponse(
                op.getId(),
                op.getType(),
                op.getAmount(),
                op.getStatus(),
                op.getCreatedAt(),
                op.getExecutedAt(),
                op.getAccountSource().getAccountNumber(),
                op.getAccountDestination() != null ? op.getAccountDestination().getAccountNumber() : null
        );
    }

    @GetMapping
    public Page<OperationResponse> getOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return operationQueryService.getCurrentClientOperations(page, size);
    }

    @PostMapping("/{id}/document")
    public String uploadJustificatif(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Document doc = documentService.uploadJustificatif(id, file);
        return "Justificatif uploadé avec succès (id=" + doc.getId() + ")";
    }
}