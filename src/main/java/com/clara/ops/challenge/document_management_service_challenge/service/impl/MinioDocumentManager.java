package com.clara.ops.challenge.document_management_service_challenge.service.impl;

import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManager;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioDocumentManager implements DocumentManager {

    private MinioClient minioClient;
    private String bucketName;

    @Autowired
    public MinioDocumentManager(MinioClient minioClient, @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Async
    @Override
    public CompletableFuture<String> uploadFile(MultipartFile file, String user) {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = user + "/" + file.getOriginalFilename();
            try (InputStream inputStream = file.getInputStream()) {
                checkIfExist();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .build()
                );
                return fileName;
            } catch (IOException e) {
                throw new RuntimeException("Error getting file InputStream", e);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file to MinIO", e);
            }
        });
    }


    public void checkIfExist() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket created: {}", bucketName);
            } else {
                log.info("Bucket already created: {}", bucketName );
            }
        } catch (ErrorResponseException e) {
            log.error("MinIO Error: {}", e.getMessage());
        } catch (InsufficientDataException e) {
            log.error("Insufficient Data: {}", e.getMessage());
        } catch (InternalException e) {
            log.error("Internal error: {}", e.getMessage());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("I/O Error or security: {}", e.getMessage());
        } catch (ServerException | InvalidResponseException | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateDownloadUrl(String documentId, int duration) {
        try {
            return minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(documentId)
                            .expiry(duration, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating temporary URL : " + e.getMessage(), e);
        }
    }
}
