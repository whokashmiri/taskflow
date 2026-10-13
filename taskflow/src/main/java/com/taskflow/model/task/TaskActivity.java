package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_activities",
        indexes = {
                @Index(name = "idx_task_activity_task", columnList = "task_id"),
                @Index(name = "idx_task_activity_created_at", columnList = "created_at"),
                @Index(name = "idx_task_activity_type", columnList = "activity_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Related task
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * User or system responsible
     */
    @Column(nullable = false)
    private UUID actorId;

    /**
     * Type of activity
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 100)
    private TaskActivityType activityType;

    /**
     * Field that changed
     * Example: status, priority, assignee
     */
    @Column(length = 100)
    private String fieldName;

    /**
     * Previous value
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * New value
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * Additional activity metadata
     * PostgreSQL JSONB
     */
    @Column(columnDefinition = "jsonb")
    private String metadata;

    /**
     * Event timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}