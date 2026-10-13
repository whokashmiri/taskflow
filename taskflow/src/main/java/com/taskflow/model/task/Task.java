package com.taskflow.model.task;

import com.taskflow.model.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String taskKey;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Owning project
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Parent task (nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    /**
     * Child tasks
     */
    @OneToMany(
            mappedBy = "parentTask",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Task> subtasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(nullable = false)
    private UUID createdBy;

    private UUID reporterId;

    private LocalDateTime startDate;

    private LocalDateTime dueDate;

    private LocalDateTime completedAt;

    private Integer estimatedMinutes;

    /**
     * Optional manual progress (0-100)
     */
    private Integer progressPercentage;

    /**
     * Used for ordering in task lists / boards
     */
    private Integer position;

    @Builder.Default
    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    public void addSubtask(Task task) {
        subtasks.add(task);
        task.setParentTask(this);
    }

    public void removeSubtask(Task task) {
        subtasks.remove(task);
        task.setParentTask(null);
    }
}