package com.clara.ops.challenge.document_management_service_challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "document_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 100)
    private String user;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "minio_path", nullable = false, length = 500)
    private String minioPath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
