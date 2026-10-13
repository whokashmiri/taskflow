package com.taskflow.model.task;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_assignees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_assignee",
                        columnNames = {"task_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Assigned task
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * Assigned user
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * User who made the assignment
     */
    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;

    /**
     * Assignment timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

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
        this.assignedAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}