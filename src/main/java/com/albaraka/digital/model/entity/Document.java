package com.albaraka.digital.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;      // type (application/pdf, image/png, ...)

    @Column(nullable = false)
    private String storagePath;   // chemin absolu/relatif sur le disque

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "operation_id")
    private Operation operation;
}