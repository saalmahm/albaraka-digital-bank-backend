package com.albaraka.digital.controller;

import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentUiController {

    private final OperationService operationService;

    @GetMapping("/home")
    public String agentHome(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) Long selectedId,
                            Model model) {

        Page<Operation> pendingPage = operationService.listPendingOperations(PageRequest.of(page, size));
        model.addAttribute("pendingPage", pendingPage);

        Operation selected = null;
        if (selectedId != null) {
            selected = pendingPage.stream()
                    .filter(op -> op.getId().equals(selectedId))
                    .findFirst()
                    .orElse(null);
        }
        model.addAttribute("selectedOperation", selected);

        return "agent-home";
    }

    @PostMapping("/operations/{id}/approve")
    public String approveOperation(@PathVariable Long id) {
        operationService.approveOperation(id);
        return "redirect:/agent/home";
    }

    @PostMapping("/operations/{id}/reject")
    public String rejectOperation(@PathVariable Long id) {
        operationService.rejectOperation(id);
        return "redirect:/agent/home";
    }
}