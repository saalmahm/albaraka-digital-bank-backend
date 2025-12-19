package com.albaraka.digital.repository;

import com.albaraka.digital.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}