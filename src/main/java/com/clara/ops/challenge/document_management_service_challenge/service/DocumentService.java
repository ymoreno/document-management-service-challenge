package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface DocumentService {

    CompletableFuture<Document> processCall(MultipartFile file, UploadRequest request);
}
