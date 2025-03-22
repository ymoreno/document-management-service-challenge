package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.FilterRequest;
import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManager;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentPersistence;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/document-management")
public class DocumentController {

    @Autowired
    private DocumentManager documentManager;
    @Autowired
    private DocumentPersistence documentPersistence;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ObjectMapper objectMapper;

    public DocumentController(DocumentManager documentManager, DocumentPersistence documentPersistence, DocumentService documentService, ObjectMapper objectMapper) {
        this.documentManager = documentManager;
        this.documentPersistence = documentPersistence;
        this.documentService = documentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public CompletableFuture<ResponseEntity<Document>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson) {

        try {
            UploadRequest request = objectMapper.readValue(metadataJson, UploadRequest.class);

            return documentService.processCall(file, request)
                    .thenApply(document -> ResponseEntity.ok(document))
                    .exceptionally(e -> {
                        log.error("error uploading file: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new Document());
                    });
        } catch (Exception e) {
            log.error("General error in execution {}", e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null));
        }
    }

    @GetMapping
    public ResponseEntity<List<Document>> getFilteredFiles(@RequestBody FilterRequest filter) {
        return ResponseEntity.ok(documentPersistence.getFilteredDocuments(filter));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<String> downloadDocument(@PathVariable String documentId) {
        Optional<Document> document = documentPersistence.getDocument(documentId);
        if (document.isPresent()) {
            String downloadUrl = documentManager.generateDownloadUrl(document.get().getDocumentName(), 5);
            return ResponseEntity.ok(downloadUrl);
        } else {
            return null;
        }
    }
}
