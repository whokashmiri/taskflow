
package com.taskflow.model.task;

import com.taskflow.model.project.Project;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(
        name = "tasks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_project_key",
                        columnNames = {"project_id", "task_key"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_task_project_status",
                        columnList = "project_id, status, archived"
                ),
                @Index(
                        name = "idx_task_project_due_date",
                        columnList = "project_id, due_date"
                ),
                @Index(
                        name = "idx_task_parent",
                        columnList = "parent_task_id"
                ),
                @Index(
                        name = "idx_task_created_by",
                        columnList = "created_by"
                )
        }
)
@Check(
        constraints = """
        (progress_percentage IS NULL OR
            (progress_percentage >= 0 AND progress_percentage <= 100))
        AND (estimated_minutes IS NULL OR estimated_minutes > 0)
        AND (position IS NULL OR position >= 0)
        """
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    // ---------- Identity ----------

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_key", nullable = false, length = 50)
    private String taskKey;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;


    // ---------- Project relationship ----------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    // ---------- Task hierarchy ----------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask")
    @Builder.Default
    private List<Task> subtasks = new ArrayList<>();


    // ---------- Classification and workflow ----------

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskType type = TaskType.TASK;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status = TaskStatus.TODO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskPriority priority = TaskPriority.MEDIUM;


    // ---------- Ownership and reporting ----------

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "reporter_id")
    private UUID reporterId;


    // ---------- Scheduling ----------

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;


    // ---------- Effort and progress ----------

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(name = "position")
    private Integer position;


    // ---------- Lifecycle ----------

    @Builder.Default
    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @ManyToMany
    @JoinTable(
            name = "task_label_assignments",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_task_label_assignment",
                    columnNames = {"task_id", "label_id"}
            )
    )
    @Builder.Default
    private Set<TaskLabel> labels = new HashSet<>();


    // ---------- Concurrent update protection ----------

    @Version
    private Long version;


    // ---------- Persistence callbacks ----------

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // ---------- Relationship helpers ----------

    public void addSubtask(Task task) {
        Objects.requireNonNull(task, "Subtask cannot be null");

        if (task == this) {
            throw new IllegalArgumentException(
                    "A task cannot be its own subtask"
            );
        }

        if (task.parentTask != null && task.parentTask != this) {
            throw new IllegalArgumentException(
                    "Task already belongs to another parent"
            );
        }

        if (project != null
                && task.project != null
                && project.getId() != null
                && task.project.getId() != null
                && !project.getId().equals(task.project.getId())) {
            throw new IllegalArgumentException(
                    "Parent and subtask must belong to the same project"
            );
        }

        if (!subtasks.contains(task)) {
            subtasks.add(task);
        }

        task.setParentTask(this);
    }

    public void removeSubtask(Task task) {
        if (task == null) {
            return;
        }

        if (subtasks.remove(task)) {
            task.setParentTask(null);
        }
    }
}
