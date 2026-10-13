
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { TaskService } from '../../../core/services/task.service';

import { ProjectResponse } from '../../project/models/project.model';
import {
  TaskPriority,
  TaskRequest,
  TaskResponse,
  TaskStatus,
  TaskType,
} from '../models/task.model';

interface TaskTreeRow {
  task: TaskResponse;
  depth: number;
  parent: TaskResponse | null;
}

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task.html',
})
export class Tasks implements OnInit {
  private readonly projectService = inject(ProjectService);
  private readonly taskService = inject(TaskService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  projects: ProjectResponse[] = [];
  tasks: TaskResponse[] = [];
  subtasks: TaskResponse[] = [];

  selectedProjectId = '';
  selectedTask: TaskResponse | null = null;
  selectedStatus: TaskStatus | '' = '';

  loadingProjects = false;
  loadingTasks = false;
  loadingSubtasks = false;
  submitting = false;

  showCreateModal = false;
  parentTaskId: string | null = null;

  errorMessage = '';
  successMessage = '';

  taskName = '';
  taskDescription = '';
  taskType: TaskType = 'TASK';
  taskPriority: TaskPriority = 'MEDIUM';
  startDate = '';
  dueDate = '';
  estimatedMinutes: number | null = null;
  progressPercentage = 0;

  readonly taskTypes: TaskType[] = [
    'TASK',
    'BUG',
    'FEATURE',
    'IMPROVEMENT',
    'MILESTONE',
  ];

  readonly taskPriorities: TaskPriority[] = [
    'LOWEST',
    'LOW',
    'MEDIUM',
    'HIGH',
    'URGENT',
  ];

  readonly taskStatuses: TaskStatus[] = [
    'BACKLOG',
    'TODO',
    'IN_PROGRESS',
    'IN_REVIEW',
    'DONE',
    'CANCELLED',
  ];

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const projectId = params.get('projectId');

      if (
        projectId &&
        this.projects.some((project) => project.id === projectId) &&
        projectId !== this.selectedProjectId
      ) {
        this.selectedProjectId = projectId;
        this.onProjectChange();
      }
    });

    this.loadProjects();
  }

  get selectedProject(): ProjectResponse | undefined {
    return this.projects.find(
      (project) => project.id === this.selectedProjectId,
    );
  }

  get visibleTasks(): TaskTreeRow[] {
    const tasksById = new Map(this.tasks.map((task) => [task.id, task]));
    const childrenByParent = new Map<string, TaskResponse[]>();
    const rootTasks: TaskResponse[] = [];

    for (const task of this.tasks) {
      if (task.parentTaskId && tasksById.has(task.parentTaskId)) {
        const children = childrenByParent.get(task.parentTaskId) ?? [];
        children.push(task);
        childrenByParent.set(task.parentTaskId, children);
      } else {
        rootTasks.push(task);
      }
    }

    const rows: TaskTreeRow[] = [];
    const visited = new Set<string>();

    const appendTask = (task: TaskResponse, depth: number): void => {
      if (visited.has(task.id)) {
        return;
      }

      visited.add(task.id);
      rows.push({
        task,
        depth,
        parent: task.parentTaskId
          ? tasksById.get(task.parentTaskId) ?? null
          : null,
      });

      for (const child of childrenByParent.get(task.id) ?? []) {
        appendTask(child, depth + 1);
      }
    };

    for (const task of rootTasks) {
      appendTask(task, 0);
    }

    for (const task of this.tasks) {
      appendTask(task, 0);
    }

    return rows;
  }

  backToProjects(): void {
    const organisationId = this.selectedProject?.organisationId;

    void this.router.navigate(['/dashboard'], {
      queryParams: organisationId ? { organisationId } : {},
    });
  }

  loadProjects(): void {
    this.loadingProjects = true;
    this.errorMessage = '';

    this.projectService.getMyProjects().subscribe({
      next: (projects) => {
        this.projects = projects;

        if (projects.length > 0) {
          const routeProjectId = this.route.snapshot.paramMap.get('projectId');
          this.selectedProjectId = projects.some(
            (project) => project.id === routeProjectId,
          )
            ? routeProjectId!
            : projects[0].id;
          this.loadTasks();
        } else {
          this.tasks = [];
        }
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.getErrorMessage(
          error,
          'Could not load your projects.',
        );
        this.loadingProjects = false;
      },
      complete: () => {
        this.loadingProjects = false;
      },
    });
  }

  onProjectChange(): void {
    this.selectedTask = null;
    this.subtasks = [];
    this.successMessage = '';
    this.errorMessage = '';
    this.loadTasks();
  }

  onStatusChange(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    if (!this.selectedProjectId) {
      this.tasks = [];
      return;
    }

    this.loadingTasks = true;
    this.errorMessage = '';

    const status = this.selectedStatus || undefined;

    this.taskService
      .getTasksByProject(this.selectedProjectId, status)
      .subscribe({
        next: (tasks) => {
          this.tasks = tasks;
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.getErrorMessage(
            error,
            'Could not load tasks for this project.',
          );
          this.loadingTasks = false;
        },
        complete: () => {
          this.loadingTasks = false;
        },
      });
  }

  openCreateModal(parentTask?: TaskResponse): void {
    this.resetForm();

    this.parentTaskId = parentTask?.id ?? null;
    this.showCreateModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeCreateModal(): void {
    if (this.submitting) {
      return;
    }

    this.showCreateModal = false;
    this.resetForm();
  }

  createTask(): void {
    if (!this.selectedProjectId || !this.taskName.trim()) {
      this.errorMessage = 'Enter a task name and select a project.';
      return;
    }

    if (
      this.startDate &&
      this.dueDate &&
      this.dueDate < this.startDate
    ) {
      this.errorMessage = 'Due date cannot be before start date.';
      return;
    }

    if (
      this.estimatedMinutes !== null &&
      this.estimatedMinutes <= 0
    ) {
      this.errorMessage = 'Estimated minutes must be greater than zero.';
      return;
    }

    if (
      this.progressPercentage < 0 ||
      this.progressPercentage > 100
    ) {
      this.errorMessage = 'Progress must be between 0 and 100.';
      return;
    }

    const request: TaskRequest = {
      name: this.taskName.trim(),
      description: this.taskDescription.trim() || null,
      type: this.taskType,
      priority: this.taskPriority,
      parentTaskId: this.parentTaskId,
      startDate: this.toLocalDateTime(this.startDate),
      dueDate: this.toLocalDateTime(this.dueDate),
      estimatedMinutes: this.estimatedMinutes,
      progressPercentage: this.progressPercentage,
    };

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.taskService
      .createTask(this.selectedProjectId, request)
      .subscribe({
        next: (createdTask) => {
          this.showCreateModal = false;
          this.resetForm();

          this.successMessage = this.parentTaskId
            ? 'Subtask created successfully.'
            : 'Task created successfully.';

          if (createdTask.parentTaskId) {
            this.openTaskDetails(createdTask);
          }

          this.loadTasks();
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.getErrorMessage(
            error,
            'Could not create the task.',
          );
          this.submitting = false;
        },
        complete: () => {
          this.submitting = false;
        },
      });
  }

  openTaskDetails(task: TaskResponse): void {
    this.selectedTask = task;
    this.subtasks = [];
    this.loadingSubtasks = true;
    this.errorMessage = '';

    this.taskService.getSubtasks(task.id).subscribe({
      next: (subtasks) => {
        this.subtasks = subtasks;
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.getErrorMessage(
          error,
          'Could not load subtasks.',
        );
        this.loadingSubtasks = false;
      },
      complete: () => {
        this.loadingSubtasks = false;
      },
    });
  }

  closeTaskDetails(): void {
    this.selectedTask = null;
    this.subtasks = [];
  }

  archiveTask(task: TaskResponse): void {
    const confirmed = window.confirm(
      `Archive "${task.name}"? This will hide it from active task lists.`,
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.taskService.archiveTask(task.id).subscribe({
      next: () => {
        if (this.selectedTask?.id === task.id) {
          this.closeTaskDetails();
        }

        this.successMessage = 'Task archived successfully.';
        this.loadTasks();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.getErrorMessage(
          error,
          'Could not archive the task.',
        );
      },
    });
  }

  formatStatus(status: TaskStatus): string {
    return status.replaceAll('_', ' ');
  }

  formatPriority(priority: TaskPriority): string {
    return priority.charAt(0) + priority.slice(1).toLowerCase();
  }

  statusClasses(status: TaskStatus): string {
    const classes: Record<TaskStatus, string> = {
      BACKLOG: 'bg-slate-700/60 text-slate-300',
      TODO: 'bg-blue-500/15 text-blue-300',
      IN_PROGRESS: 'bg-amber-500/15 text-amber-300',
      IN_REVIEW: 'bg-violet-500/15 text-violet-300',
      DONE: 'bg-emerald-500/15 text-emerald-300',
      CANCELLED: 'bg-rose-500/15 text-rose-300',
    };

    return classes[status];
  }

  priorityClasses(priority: TaskPriority): string {
    const classes: Record<TaskPriority, string> = {
      LOWEST: 'text-slate-400',
      LOW: 'text-sky-300',
      MEDIUM: 'text-emerald-300',
      HIGH: 'text-amber-300',
      URGENT: 'text-rose-300',
    };

    return classes[priority];
  }

  private toLocalDateTime(value: string): string | null {
    if (!value) {
      return null;
    }

    // datetime-local returns YYYY-MM-DDTHH:mm.
    // Add seconds for the Java LocalDateTime format.
    return value.length === 16 ? `${value}:00` : value;
  }

  private resetForm(): void {
    this.taskName = '';
    this.taskDescription = '';
    this.taskType = 'TASK';
    this.taskPriority = 'MEDIUM';
    this.startDate = '';
    this.dueDate = '';
    this.estimatedMinutes = null;
    this.progressPercentage = 0;
    this.parentTaskId = null;
  }

  private getErrorMessage(
    error: HttpErrorResponse,
    fallback: string,
  ): string {
    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    if (error.error?.message) {
      return error.error.message;
    }

    if (error.status === 401) {
      return 'Your session may have expired. Please sign in again.';
    }

    if (error.status === 403) {
      return 'You do not have permission to perform this action in this project.';
    }

    return fallback;
  }
}
