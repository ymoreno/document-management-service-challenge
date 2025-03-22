package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import com.clara.ops.challenge.document_management_service_challenge.dto.request.UploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManager;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class DocumentServiceImplTest {

    @Mock
    private DocumentManager documentManager;

    @Mock
    private DocumentPersistence documentPersistence;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private UploadRequest request;

    @BeforeEach
    void setUp() {
        request = new UploadRequest();
        request.setUser("testUser");
        request.setDocumentName("testDoc.pdf");
    }

    @Test
    void processCall_ShouldReturnDocument_WhenUploadSuccessful() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("application/pdf");
        when(documentManager.uploadFile(any(MultipartFile.class), any(String.class)))
                .thenReturn(CompletableFuture.completedFuture("path/to/file"));
        when(documentPersistence.createDocument(any(Document.class)))
                .thenReturn(Document.builder().documentName("testDoc.pdf").build());

        CompletableFuture<Document> result = documentService.processCall(file, request);

        assertThat(result.get().getDocumentName()).isEqualTo("testDoc.pdf");
    }

    @Test
    void processCall_ShouldThrowException_WhenFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () ->
                documentService.processCall(file, request).join()
        );

        assertThat(exception.getMessage()).contains("File cannot be empty");
    }

    @Test
    void processCall_ShouldThrowException_WhenUserIsEmpty() {
        request.setUser("");
        when(file.isEmpty()).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                documentService.processCall(file, request).join()
        );

        assertThat(exception.getMessage()).contains("user cannot be empty");
    }
}
