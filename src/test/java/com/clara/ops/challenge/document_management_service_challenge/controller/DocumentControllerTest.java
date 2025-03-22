package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.FilterRequest;
import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManager;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentPersistence;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class DocumentControllerTest {

    @Mock
    private DocumentManager documentManager;

    @Mock
    private DocumentPersistence documentPersistence;

    @Mock
    private DocumentService documentService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DocumentController documentController;

    private MultipartFile file;
    private UploadRequest request;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "Dummy content".getBytes());
        request = new UploadRequest();
        request.setUser("testUser");
        request.setDocumentName("testDoc.pdf");
    }

    @Test
    void uploadFile_ShouldReturnDocument_WhenUploadSuccessful() throws Exception {
        when(objectMapper.readValue(anyString(), eq(UploadRequest.class))).thenReturn(request);
        when(documentService.processCall(any(MultipartFile.class), any(UploadRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(Document.builder().documentName("testDoc.pdf").build()));

        CompletableFuture<ResponseEntity<Document>> response = documentController.uploadFile(file, "{}");

        assertNotNull(response);
        assertEquals("testDoc.pdf", response.get().getBody().getDocumentName());
    }

    @Test
    void uploadFile_ShouldReturnBadRequest_WhenExceptionOccurs() throws Exception {
        when(objectMapper.readValue(anyString(), eq(UploadRequest.class))).thenThrow(new RuntimeException("Invalid JSON"));

        CompletableFuture<ResponseEntity<Document>> response = documentController.uploadFile(file, "{}");

        assertEquals(400, response.get().getStatusCodeValue());
    }

    @Test
    void getFilteredFiles_ShouldReturnDocumentList() {
        when(documentPersistence.getFilteredDocuments(any(FilterRequest.class))).thenReturn(List.of(new Document()));

        ResponseEntity<List<Document>> response = documentController.getFilteredFiles(new FilterRequest());

        assertNotNull(response);
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void downloadDocument_ShouldReturnUrl_WhenDocumentExists() {
        Document document = Document.builder().documentName("testDoc.pdf").build();
        when(documentPersistence.getDocument(anyString())).thenReturn(Optional.of(document));
        when(documentManager.generateDownloadUrl(anyString(), anyInt())).thenReturn("http://download-url");

        ResponseEntity<String> response = documentController.downloadDocument("docId");

        assertNotNull(response);
        assertEquals("http://download-url", response.getBody());
    }

    @Test
    void downloadDocument_ShouldReturnNull_WhenDocumentNotFound() {
        when(documentPersistence.getDocument(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = documentController.downloadDocument("docId");

        assertNull(response);
    }
}
