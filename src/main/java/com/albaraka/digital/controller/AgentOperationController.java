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

    @GetMapping("/pending")
    public Page<Operation> listPending(
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