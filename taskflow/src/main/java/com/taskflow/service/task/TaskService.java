package com.taskflow.service.task;



import com.taskflow.dto.task.TaskRequest;
import com.taskflow.dto.task.TaskResponse;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.model.project.MembershipStatus;
import com.taskflow.model.project.Project;
import com.taskflow.model.project.ProjectMember;
import com.taskflow.model.project.ProjectRole;
import com.taskflow.model.task.Task;
import com.taskflow.model.task.TaskPriority;
import com.taskflow.model.task.TaskStatus;
import com.taskflow.model.task.TaskType;
import com.taskflow.repository.project.ProjectMemberRepository;
import com.taskflow.repository.project.ProjectRepository;
import com.taskflow.repository.task.TaskRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    // --------------------------------------------------
    // CREATE TASK
    // --------------------------------------------------

    public TaskResponse createTask(UUID projectId, TaskRequest request) {

        UUID currentUserId = getCurrentUserId();

        Project project = getProject(projectId);

        ProjectMember membership =
                requireProjectMembership(projectId, currentUserId);

        requireCanModifyTasks(membership);

        Task parentTask = null;

        if (request.getParentTaskId() != null) {
            parentTask = getActiveTask(request.getParentTaskId());

            if (!parentTask.getProject().getId().equals(projectId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Parent task must belong to the same project"
                );
            }
        }

        Task task = new Task();

        task.setTaskKey(generateTaskKey(projectId));
        task.setName(request.getName().trim());
        task.setDescription(request.getDescription());
        task.setProject(project);
        task.setParentTask(parentTask);

        task.setType(
                request.getType() != null
                        ? request.getType()
                        : TaskType.TASK
        );

        task.setStatus(TaskStatus.TODO);

        task.setPriority(
                request.getPriority() != null
                        ? request.getPriority()
                        : TaskPriority.MEDIUM
        );

        task.setCreatedBy(currentUserId);
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setEstimatedMinutes(request.getEstimatedMinutes());
        task.setProgressPercentage(request.getProgressPercentage());
        task.setPosition(0);
        task.setArchived(false);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    // --------------------------------------------------
    // GET TASKS BY PROJECT
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(
            UUID projectId,
            TaskStatus status
    ) {
        UUID currentUserId = getCurrentUserId();

        getProject(projectId);
        requireProjectMembership(projectId, currentUserId);

        List<Task> tasks;

        if (status == null) {
            tasks = taskRepository
                    .findAllByProject_IdAndArchivedFalseOrderByPositionAscCreatedAtDesc(
                            projectId
                    );
        } else {
            tasks = taskRepository
                    .findAllByProject_IdAndStatusAndArchivedFalseOrderByPositionAsc(
                            projectId,
                            status
                    );
        }

        return tasks.stream()
                .filter(task -> !hasArchivedAncestor(task))
                .map(this::toResponse)
                .toList();
    }

    // --------------------------------------------------
    // GET ONE TASK
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID taskId) {

        UUID currentUserId = getCurrentUserId();

        Task task = getActiveTask(taskId);

        requireProjectMembership(
                task.getProject().getId(),
                currentUserId
        );

        if (hasArchivedAncestor(task)) {
            throw new EntityNotFoundException("Task not found");
        }

        return toResponse(task);
    }

    // --------------------------------------------------
    // GET DIRECT SUBTASKS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<TaskResponse> getSubtasks(UUID taskId) {

        UUID currentUserId = getCurrentUserId();

        Task parent = getActiveTask(taskId);

        requireProjectMembership(
                parent.getProject().getId(),
                currentUserId
        );

        if (hasArchivedAncestor(parent)) {
            throw new EntityNotFoundException("Task not found");
        }

        return taskRepository
                .findAllByParentTask_IdAndArchivedFalseOrderByPositionAsc(
                        taskId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --------------------------------------------------
    // UPDATE TASK
    // --------------------------------------------------

    public TaskResponse updateTask(
            UUID taskId,
            TaskRequest request
    ) {
        UUID currentUserId = getCurrentUserId();

        Task task = getActiveTask(taskId);

        UUID projectId = task.getProject().getId();

        ProjectMember membership =
                requireProjectMembership(projectId, currentUserId);

        requireCanModifyTasks(membership);

        if (hasArchivedAncestor(task)) {
            throw new EntityNotFoundException("Task not found");
        }

        Task newParent = null;

        if (request.getParentTaskId() != null) {
            newParent = getActiveTask(request.getParentTaskId());

            if (!newParent.getProject().getId().equals(projectId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Parent task must belong to the same project"
                );
            }

            if (newParent.getId().equals(taskId)
                    || isDescendantOf(newParent, taskId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A task cannot be its own ancestor"
                );
            }
        }

        task.setName(request.getName().trim());
        task.setDescription(request.getDescription());

        if (request.getType() != null) {
            task.setType(request.getType());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        task.setParentTask(newParent);
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setEstimatedMinutes(request.getEstimatedMinutes());
        task.setProgressPercentage(request.getProgressPercentage());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    // --------------------------------------------------
    // ARCHIVE TASK AND ITS DESCENDANTS
    // --------------------------------------------------

    public void archiveTask(UUID taskId) {

        UUID currentUserId = getCurrentUserId();

        Task task = getActiveTask(taskId);

        ProjectMember membership =
                requireProjectMembership(
                        task.getProject().getId(),
                        currentUserId
                );

        requireCanModifyTasks(membership);

        if (hasArchivedAncestor(task)) {
            throw new EntityNotFoundException("Task not found");
        }

        archiveRecursively(task);
    }

    private void archiveRecursively(Task task) {

        if (task.isArchived()) {
            return;
        }

        List<Task> subtasks =
                taskRepository
                        .findAllByParentTask_IdAndArchivedFalseOrderByPositionAsc(
                                task.getId()
                        );

        for (Task subtask : subtasks) {
            archiveRecursively(subtask);
        }

        task.setArchived(true);
        taskRepository.save(task);
    }

    // --------------------------------------------------
    // AUTHENTICATION AND AUTHORISATION
    // --------------------------------------------------

    private UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            throw new UserNotAuthenticatedException(
                    "User not authenticated"
            );
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new UserNotAuthenticatedException(
                    "Invalid authenticated user"
            );
        }
    }

    private Project getProject(UUID projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Project not found")
                );
    }

    private Task getActiveTask(UUID taskId) {

        return taskRepository.findByIdAndArchivedFalse(taskId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Task not found")
                );
    }


    private ProjectMember requireProjectMembership(
            UUID projectId,
            UUID userId
    ) {
        ProjectMember membership = projectMemberRepository
                .findByProject_IdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Active project membership required"
                        )
                );

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Active project membership required"
            );
        }

        return membership;
    }


    private void requireCanModifyTasks(ProjectMember membership) {

        if (membership.getRole() == ProjectRole.VIEWER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Viewers cannot modify tasks"
            );
        }
    }

    // --------------------------------------------------
    // TASK KEY
    // --------------------------------------------------

    private String generateTaskKey(UUID projectId) {

        String projectPrefix = projectId
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        for (int attempt = 0; attempt < 5; attempt++) {

            String randomSuffix = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();

            String taskKey = projectPrefix + "-" + randomSuffix;

            if (!taskRepository.existsByProject_IdAndTaskKey(
                    projectId,
                    taskKey
            )) {
                return taskKey;
            }
        }

        throw new IllegalStateException(
                "Could not generate a unique task key"
        );
    }

    // --------------------------------------------------
    // HIERARCHY VALIDATION
    // --------------------------------------------------

    private boolean isDescendantOf(Task candidate, UUID taskId) {

        Task current = candidate;

        while (current != null) {

            if (current.getId().equals(taskId)) {
                return true;
            }

            current = current.getParentTask();
        }

        return false;
    }

    private boolean hasArchivedAncestor(Task task) {

        Task parent = task.getParentTask();

        while (parent != null) {

            if (parent.isArchived()) {
                return true;
            }

            parent = parent.getParentTask();
        }

        return false;
    }

    // --------------------------------------------------
    // ENTITY -> RESPONSE DTO
    // --------------------------------------------------

    private TaskResponse toResponse(Task task) {

        int subtaskCount = Math.toIntExact(
                taskRepository.countByParentTask_IdAndArchivedFalse(
                        task.getId()
                )
        );

        return TaskResponse.builder()
                .id(task.getId())
                .taskKey(task.getTaskKey())
                .name(task.getName())
                .description(task.getDescription())
                .projectId(task.getProject().getId())
                .parentTaskId(
                        task.getParentTask() != null
                                ? task.getParentTask().getId()
                                : null
                )
                .subtaskCount(subtaskCount)
                .type(task.getType())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdBy(task.getCreatedBy())
                .reporterId(task.getReporterId())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .estimatedMinutes(task.getEstimatedMinutes())
                .progressPercentage(task.getProgressPercentage())
                .position(task.getPosition())
                .archived(task.isArchived())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .version(task.getVersion())
                .build();
    }
}
