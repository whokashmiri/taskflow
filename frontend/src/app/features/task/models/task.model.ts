
export type TaskType =
  | 'TASK'
  | 'BUG'
  | 'FEATURE'
  | 'IMPROVEMENT'
  | 'MILESTONE';

export type TaskStatus =
  | 'BACKLOG'
  | 'TODO'
  | 'IN_PROGRESS'
  | 'IN_REVIEW'
  | 'DONE'
  | 'CANCELLED';

export type TaskPriority =
  | 'LOWEST'
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'URGENT';

export interface TaskRequest {
  name: string;
  description?: string | null;
  type?: TaskType | null;
  priority?: TaskPriority | null;
  parentTaskId?: string | null;
  startDate?: string | null;
  dueDate?: string | null;
  estimatedMinutes?: number | null;
  progressPercentage?: number | null;
}

export interface TaskResponse {
  id: string;
  taskKey: string;
  name: string;
  description: string | null;
  projectId: string;
  parentTaskId: string | null;
  subtaskCount: number;
  type: TaskType;
  status: TaskStatus;
  priority: TaskPriority;
  createdBy: string;
  reporterId: string | null;
  startDate: string | null;
  dueDate: string | null;
  completedAt: string | null;
  estimatedMinutes: number | null;
  progressPercentage: number | null;
  position: number | null;
  archived: boolean;
  createdAt: string | null;
  updatedAt: string | null;
  version: number | null;
}
