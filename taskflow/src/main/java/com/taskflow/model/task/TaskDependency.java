package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_dependencies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_dependency",
                        columnNames = {
                                "predecessor_task_id",
                                "successor_task_id",
                                "dependency_type"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Task that must happen first
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predecessor_task_id", nullable = false)
    private Task predecessorTask;

    /**
     * Task that depends on the predecessor
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "successor_task_id", nullable = false)
    private Task successorTask;

    /**
     * Dependency relationship type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "dependency_type", nullable = false)
    private TaskDependencyType type;

    /**
     * User who created the dependency
     */
    @Column(nullable = false, updatable = false)
    private UUID createdBy;

    /**
     * Creation timestamp
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