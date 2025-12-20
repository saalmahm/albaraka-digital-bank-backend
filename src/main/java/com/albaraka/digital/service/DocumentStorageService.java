package com.albaraka.digital.service;

import com.albaraka.digital.model.entity.Document;
import com.albaraka.digital.model.entity.Operation;
import com.albaraka.digital.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentStorageService {

    private final DocumentRepository documentRepository;

    @Value("${storage.documents-dir}")
    private String documentsDir;

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    public Document storeDocumentForOperation(Operation operation, MultipartFile file) throws IOException {
        validateFile(file);

        Files.createDirectories(Paths.get(documentsDir));

        String ext = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path target = Paths.get(documentsDir).resolve(storedName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
                .fileName(storedName)
                .fileType(file.getContentType())
                .storagePath(target.toString())
                .uploadedAt(LocalDateTime.now())
                .operation(operation)
                .build();

        return documentRepository.save(document);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier ne peut pas être vide");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale de 5MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé (PDF/JPG/PNG uniquement)");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        return (idx != -1 && idx < filename.length() - 1) ? filename.substring(idx + 1) : null;
    }

    public Resource loadAsResource(String storagePath) {
        try {
            Path file = Paths.get(storagePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalArgumentException("Fichier introuvable ou illisible: " + storagePath);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Chemin de fichier invalide: " + storagePath, e);
        }
    }
}