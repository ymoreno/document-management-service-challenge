package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.FilterRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class DocumentPersistenceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentPersistenceImpl documentPersistence;

    private Document document;

    @BeforeEach
    void setUp() {
        document = Document.builder()
                .id(UUID.randomUUID())
                .user("testUser")
                .documentName("testDoc.pdf")
                .build();
    }

    @Test
    void createDocument_ShouldSaveAndReturnDocument() {
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document savedDocument = documentPersistence.createDocument(document);

        assertNotNull(savedDocument);
        assertEquals("testUser", savedDocument.getUser());
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void getDocument_ShouldReturnDocument_WhenExists() {
        when(documentRepository.findById(any(UUID.class))).thenReturn(Optional.of(document));

        Optional<Document> result = documentPersistence.getDocument(document.getId().toString());

        assertTrue(result.isPresent());
        assertEquals("testDoc.pdf", result.get().getDocumentName());
    }

    @Test
    void getDocument_ShouldReturnEmpty_WhenNotExists() {
        when(documentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Document> result = documentPersistence.getDocument(UUID.randomUUID().toString());

        assertFalse(result.isPresent());
    }

    @Test
    void getFilteredDocuments_ShouldReturnFilteredResults() {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setUser("testUser");
        when(documentRepository.findAll(any(Specification.class))).thenReturn(List.of(document));

        List<Document> results = documentPersistence.getFilteredDocuments(filterRequest);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("testUser", results.get(0).getUser());
    }
}
