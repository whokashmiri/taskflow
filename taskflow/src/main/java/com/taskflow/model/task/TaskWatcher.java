package com.taskflow.model.task;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "task_watchers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_watcher",
                        columnNames = {"task_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskWatcher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Watched task
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * User watching the task
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * When watching began
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
