package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_time_entries",
        indexes = {
                @Index(name = "idx_time_entry_task", columnList = "task_id"),
                @Index(name = "idx_time_entry_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Task worked on
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * User who performed the work
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Work performed
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Optional start time
     */
    private LocalDateTime startedAt;

    /**
     * Optional end time
     */
    private LocalDateTime endedAt;

    /**
     * Recorded duration in minutes
     */
    @Column(nullable = false)
    private Integer durationMinutes;

    /**
     * Whether work is billable
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean billable = false;

    /**
     * Work log creation time
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        // Auto-calculate duration if start/end provided
        if (startedAt != null &&
                endedAt != null &&
                durationMinutes == null) {

            this.durationMinutes =
                    Math.toIntExact(
                            Duration.between(startedAt, endedAt).toMinutes()
                    );
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (startedAt != null &&
                endedAt != null) {

            this.durationMinutes =
                    Math.toIntExact(
                            Duration.between(startedAt, endedAt).toMinutes()
                    );
        }
    }
}
