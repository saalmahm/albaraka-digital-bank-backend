package com.albaraka.digital.controller;

import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent/operations")
@RequiredArgsConstructor
public class AgentOperationController {

    private final OperationService operationService;

    /**
     * Endpoint 1 : PENDING via OAuth2 / Keycloak
     * - Protégé par la SecurityFilterChain OAuth2 (scope SCOPE_operations.read)
     */
    @GetMapping("/pending")
    public Page<Operation> listPendingOAuth2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return operationService.listPendingOperations(PageRequest.of(page, size));
    }

    /**
     * Endpoint 2 : PENDING via JWT interne
     * - Protégé par la SecurityFilterChain JWT (rôle AGENT_BANCAIRE)
     */
    @GetMapping("/jwt/pending")
    public Page<Operation> listPendingJwt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return operationService.listPendingOperations(PageRequest.of(page, size));
    }

    @PutMapping("/{id}/approve")
    public Operation approve(@PathVariable Long id) {
        return operationService.approveOperation(id);
    }

    @PutMapping("/{id}/reject")
    public Operation reject(@PathVariable Long id) {
        return operationService.rejectOperation(id);
    }
}