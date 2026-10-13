package com.taskflow.model.task;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_checklist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Parent task containing the checklist item
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * Checklist item title
     */
    @Column(nullable = false, length = 500)
    private String name;

    /**
     * Optional details
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Completion state
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * User who completed the item
     */
    private UUID completedBy;

    /**
     * Completion timestamp
     */
    private LocalDateTime completedAt;

    /**
     * Display order
     */
    private Integer position;

    /**
     * Creator
     */
    @Column(nullable = false, updatable = false)
    private UUID createdBy;

    /**
     * Creation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking
     */
    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted(UUID userId) {
        this.completed = true;
        this.completedBy = userId;
        this.completedAt = LocalDateTime.now();
    }

    public void markIncomplete() {
        this.completed = false;
        this.completedBy = null;
        this.completedAt = null;
    }
}