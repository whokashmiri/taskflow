package com.taskflow.dto.task;



import com.taskflow.model.task.TaskPriority;
import com.taskflow.model.task.TaskType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task name is required")
    @Size(max = 255, message = "Task name cannot exceed 255 characters")
    private String name;

    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;

    private TaskType type;

    private TaskPriority priority;

    private UUID parentTaskId;

    private LocalDateTime startDate;

    private LocalDateTime dueDate;

    @Positive(message = "Estimated minutes must be greater than zero")
    private Integer estimatedMinutes;

    @Min(value = 0, message = "Progress cannot be less than 0")
    @Max(value = 100, message = "Progress cannot exceed 100")
    private Integer progressPercentage;

    @AssertTrue(message = "Due date cannot be before start date")
    public boolean isDateRangeValid() {
        return startDate == null
                || dueDate == null
                || !dueDate.isBefore(startDate);
    }
}
