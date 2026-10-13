package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_attachments",
        indexes = {
                @Index(name = "idx_attachment_task", columnList = "task_id"),
                @Index(name = "idx_attachment_storage_key", columnList = "storage_key")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Associated task
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * Uploading user
     */
    @Column(nullable = false, updatable = false)
    private UUID uploadedBy;

    /**
     * Original filename
     */
    @Column(nullable = false, length = 500)
    private String fileName;

    /**
     * Internal object storage identifier
     * Example:
     * tasks/TF-101/design-v4.pdf
     */
    @Column(name = "storage_key", nullable = false, unique = true, length = 1000)
    private String storageKey;

    /**
     * Storage backend
     * Example:
     * S3, MINIO, AZURE_BLOB, GCS
     */
    @Column(nullable = false, length = 100)
    private String storageProvider;

    /**
     * MIME type
     * Example:
     * application/pdf
     * image/png
     */
    @Column(nullable = false, length = 255)
    private String contentType;

    /**
     * File size in bytes
     */
    @Column(nullable = false)
    private Long fileSizeBytes;

    /**
     * SHA-256 or MD5 checksum
     */
    @Column(length = 255)
    private String checksum;

    /**
     * Upload timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Soft deletion timestamp
     */
    private LocalDateTime deletedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}