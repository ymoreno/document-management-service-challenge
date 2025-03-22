package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManager;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentPersistence;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentManager documentManager;
    private DocumentPersistence documentPersistence;

    @Autowired
    public DocumentServiceImpl(DocumentManager documentManager, DocumentPersistence documentPersistence) {
        this.documentManager = documentManager;
        this.documentPersistence = documentPersistence;
    }

    @Async
    @Override
    public CompletableFuture<Document> processCall(MultipartFile file, UploadRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("File cannot be empty");
                }
                if (request.getUser() == null || request.getUser().trim().isEmpty()) {
                    throw new IllegalArgumentException("user cannot be empty");
                }

                CompletableFuture<String> fileName = documentManager.uploadFile(file, request.getUser());

                Document document = Document.builder()
                        .user(request.getUser())
                        .documentName(request.getDocumentName())
                        .minioPath(fileName.get())
                        .fileSize(file.getSize())
                        .fileType(file.getContentType())
                        .createdAt(LocalDateTime.now())
                        .tags(request.getTags())
                        .build();

                return documentPersistence.createDocument(document);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
            }
        });
    }


}
