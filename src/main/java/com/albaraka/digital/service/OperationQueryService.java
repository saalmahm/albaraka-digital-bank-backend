package com.albaraka.digital.service;

import com.albaraka.digital.dto.operation.OperationResponse;
import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationQueryService {

    private final AccountService accountService;
    private final OperationRepository operationRepository;

    public Page<OperationResponse> getCurrentClientOperations(int page, int size) {
        Account account = accountService.getCurrentUserAccount();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Operation> opsPage =
                operationRepository.findByAccountSourceOrAccountDestination(
                        account, account, pageable);

        return opsPage.map(op -> new OperationResponse(
                op.getId(),
                op.getType(),
                op.getAmount(),
                op.getStatus(),
                op.getCreatedAt(),
                op.getExecutedAt(),
                op.getAccountSource() != null ? op.getAccountSource().getAccountNumber() : null,
                op.getAccountDestination() != null ? op.getAccountDestination().getAccountNumber() : null
        ));
    }
}