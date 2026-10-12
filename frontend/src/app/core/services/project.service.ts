
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

import {
  ProjectRequest,
  ProjectResponse,
} from '../../features/project/models/project.model';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl = `${environment.apiUrl}/projects`;
  private readonly organisationUrl = `${environment.apiUrl}/organisations`;

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();

    return token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
  }

  createProject(request: ProjectRequest): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(
      this.apiUrl,
      request,
      { headers: this.getAuthHeaders() },
    );
  }

  getMyProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(
      `${this.apiUrl}/me`,
      { headers: this.getAuthHeaders() },
    );
  }

  getProjectsByOrganisation(
    organisationId: string,
  ): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(
      `${this.organisationUrl}/${organisationId}/projects`,
      { headers: this.getAuthHeaders() },
    );
  }
}
