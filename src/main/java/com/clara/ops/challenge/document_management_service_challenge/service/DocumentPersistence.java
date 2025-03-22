package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.FilterRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentPersistence {

    Document createDocument(Document document);

    List<Document> getFilteredDocuments(FilterRequest filter);

    Optional<Document> getDocument(String documentId);

}
