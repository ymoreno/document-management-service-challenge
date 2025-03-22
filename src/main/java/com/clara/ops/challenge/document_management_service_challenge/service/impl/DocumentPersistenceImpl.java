package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.FilterRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.model.DocumentSpecifications;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentPersistenceImpl implements DocumentPersistence {

    private DocumentRepository documentRepository;

    @Autowired
    public DocumentPersistenceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public List<Document> getFilteredDocuments(FilterRequest filter) {
        if (filter == null) {
            return documentRepository.findAll();
        } else {
            Specification<Document> spec = Specification.where(null);
            if (filter.getUser() != null) {
                spec = spec.and(DocumentSpecifications.hasUser(filter.getUser()));
            }
            if (filter.getDocumentName() != null) {
                spec = spec.and(DocumentSpecifications.hasDocumentName(filter.getDocumentName()));
            }
            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                spec = spec.and(DocumentSpecifications.hasTags(filter.getTags()));
            }
            return documentRepository.findAll(spec);
        }
    }

    @Override
    public Optional<Document> getDocument(String documentId) {
        return documentRepository.findById(UUID.fromString(documentId));
    }
}
