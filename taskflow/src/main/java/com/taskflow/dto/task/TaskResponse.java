package com.taskflow.dto.task;



import com.taskflow.model.task.TaskPriority;
import com.taskflow.model.task.TaskStatus;
import com.taskflow.model.task.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private UUID id;

    private String taskKey;

    private String name;

    private String description;

    private UUID projectId;

    private UUID parentTaskId;

    private int subtaskCount;

    private TaskType type;

    private TaskStatus status;

    private TaskPriority priority;

    private UUID createdBy;

    private UUID reporterId;

    private LocalDateTime startDate;

    private LocalDateTime dueDate;

    private LocalDateTime completedAt;

    private Integer estimatedMinutes;

    private Integer progressPercentage;

    private Integer position;

    private boolean archived;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
