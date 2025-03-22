package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MinioDocumentManagerTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private MinioDocumentManager minioDocumentManager;

    private final String bucketName = "test-bucket";
    private final String user = "test-user";
    private final String fileName = "test-file.txt";

    @BeforeEach
    void setUp() {
        minioDocumentManager = new MinioDocumentManager(minioClient, bucketName);
    }

    @Test
    void uploadFile_Success() throws Exception {
        InputStream mockInputStream = mock(InputStream.class);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(mockInputStream);
        when(file.getSize()).thenReturn(1024L);

        CompletableFuture<String> result = minioDocumentManager.uploadFile(file, user);
        assertEquals(user + "/" + fileName, result.get());
    }

    @Test
    void uploadFile_ThrowsIOException() throws IOException {
        when(file.getInputStream()).thenThrow(new IOException("Failed to read file"));

        Exception exception = assertThrows(RuntimeException.class, () -> minioDocumentManager.uploadFile(file, user).join());
        assertTrue(exception.getMessage().contains("Error getting file InputStream"));
    }

    @Test
    void checkIfExist_BucketExists() throws Exception {
        when(minioClient.bucketExists(any())).thenReturn(true);
        minioDocumentManager.checkIfExist();
        verify(minioClient, never()).makeBucket(any());
    }

    @Test
    void checkIfExist_BucketDoesNotExist() throws Exception {
        when(minioClient.bucketExists(any())).thenReturn(false);
        minioDocumentManager.checkIfExist();
        verify(minioClient, times(1)).makeBucket(any());
    }

    @Test
    void generateDownloadUrl_Success() throws Exception {
        when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://example.com/download");
        String url = minioDocumentManager.generateDownloadUrl("doc123", 10);
        assertEquals("http://example.com/download", url);
    }

    @Test
    void generateDownloadUrl_Exception() throws Exception {
        when(minioClient.getPresignedObjectUrl(any())).thenThrow(new RuntimeException("MinIO error"));
        Exception exception = assertThrows(RuntimeException.class, () -> minioDocumentManager.generateDownloadUrl("doc123", 10));
        assertTrue(exception.getMessage().contains("Error generating temporary URL"));
    }
}
