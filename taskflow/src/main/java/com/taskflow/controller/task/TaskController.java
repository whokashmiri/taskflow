package com.taskflow.controller;



import com.taskflow.dto.task.TaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.model.task.TaskStatus;
import com.taskflow.service.task.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create a task inside a project.
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequest request
    ) {
        TaskResponse response =
                taskService.createTask(projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all active tasks in a project.
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status
    ) {
        List<TaskResponse> tasks =
                taskService.getTasksByProject(projectId, status);

        return ResponseEntity.ok(tasks);
    }

    // Get one active task.
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(
                taskService.getTask(taskId)
        );
    }

    // Get active subtasks of a task.
    @GetMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<List<TaskResponse>> getSubtasks(
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(
                taskService.getSubtasks(taskId)
        );
    }

    // Update editable task details.
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest request
    ) {
        return ResponseEntity.ok(
                taskService.updateTask(taskId, request)
        );
    }

    // Archive the task instead of permanently deleting it.
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> archiveTask(
            @PathVariable UUID taskId
    ) {
        taskService.archiveTask(taskId);

        return ResponseEntity.noContent().build();
    }
}
