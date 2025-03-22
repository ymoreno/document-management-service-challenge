package com.clara.ops.challenge.document_management_service_challenge.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface DocumentManager {

    CompletableFuture<String> uploadFile(MultipartFile file, String user);

    String generateDownloadUrl(String documentId, int duration);
}
