package com.taskflow.repository.task;


import com.taskflow.model.task.Task;
import com.taskflow.model.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Find an active task by its ID.
    Optional<Task> findByIdAndArchivedFalse(UUID id);

    // Check task-key uniqueness within a project.
    boolean existsByProject_IdAndTaskKey(
            UUID projectId,
            String taskKey
    );

    // List active tasks belonging to a project.
    List<Task> findAllByProject_IdAndArchivedFalseOrderByPositionAscCreatedAtDesc(
            UUID projectId
    );

    // List active tasks in a particular status column.
    List<Task> findAllByProject_IdAndStatusAndArchivedFalseOrderByPositionAsc(
            UUID projectId,
            TaskStatus status
    );

    // Find active subtasks of a parent task.
    List<Task> findAllByParentTask_IdAndArchivedFalseOrderByPositionAsc(
            UUID parentTaskId
    );

    // Count active direct subtasks.
    long countByParentTask_IdAndArchivedFalse(UUID parentTaskId);

    // Find active tasks whose deadlines have passed.
    @Query("""
        SELECT t
        FROM Task t
        WHERE t.project.id = :projectId
          AND t.archived = false
          AND t.dueDate < :now
          AND t.status NOT IN :finishedStatuses
        ORDER BY t.dueDate ASC
        """)
    List<Task> findOverdueTasks(
            @Param("projectId") UUID projectId,
            @Param("now") LocalDateTime now,
            @Param("finishedStatuses") List<TaskStatus> finishedStatuses
    );
}
