
import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

import {
  TaskRequest,
  TaskResponse,
  TaskStatus,
} from '../../features/task/models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl = `${environment.apiUrl}/tasks`;
  private readonly projectsUrl = `${environment.apiUrl}/projects`;

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();

    return token
      ? new HttpHeaders({
          Authorization: `Bearer ${token}`,
        })
      : new HttpHeaders();
  }

  getTasksByProject(
    projectId: string,
    status?: TaskStatus,
  ): Observable<TaskResponse[]> {
    let params = new HttpParams();

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<TaskResponse[]>(
      `${this.projectsUrl}/${projectId}/tasks`,
      {
        headers: this.getAuthHeaders(),
        params,
      },
    );
  }

  createTask(
    projectId: string,
    request: TaskRequest,
  ): Observable<TaskResponse> {
    return this.http.post<TaskResponse>(
      `${this.projectsUrl}/${projectId}/tasks`,
      request,
      {
        headers: this.getAuthHeaders(),
      },
    );
  }

  getTask(taskId: string): Observable<TaskResponse> {
    return this.http.get<TaskResponse>(
      `${this.apiUrl}/${taskId}`,
      {
        headers: this.getAuthHeaders(),
      },
    );
  }

  getSubtasks(taskId: string): Observable<TaskResponse[]> {
    return this.http.get<TaskResponse[]>(
      `${this.apiUrl}/${taskId}/subtasks`,
      {
        headers: this.getAuthHeaders(),
      },
    );
  }

  updateTask(
    taskId: string,
    request: TaskRequest,
  ): Observable<TaskResponse> {
    return this.http.put<TaskResponse>(
      `${this.apiUrl}/${taskId}`,
      request,
      {
        headers: this.getAuthHeaders(),
      },
    );
  }

  archiveTask(taskId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${taskId}`,
      {
        headers: this.getAuthHeaders(),
      },
    );
  }
}
