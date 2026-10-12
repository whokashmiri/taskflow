
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import {
  ProjectRequest,
  ProjectResponse,
} from '../models/project.model';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/projects`;
  private readonly organisationUrl = `${environment.apiUrl}/organisations`;

  createProject(request: ProjectRequest): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(this.apiUrl, request);
  }

  getMyProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/me`);
  }

  getProjectsByOrganisation(
    organisationId: string,
  ): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(
      `${this.organisationUrl}/${organisationId}/projects`,
    );
  }
}
