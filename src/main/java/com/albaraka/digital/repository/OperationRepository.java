package com.albaraka.digital.repository;

import com.albaraka.digital.model.entity.Account;
import com.albaraka.digital.model.entity.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    Page<Operation> findByAccountSourceOrAccountDestination(
            Account accountSource,
            Account accountDestination,
            Pageable pageable
    );
}