
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

import {
  OrganisationRequest,
  OrganisationResponse,
} from '../../features/organisation/organisation-model';

@Injectable({
  providedIn: 'root',
})
export class OrganisationService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl = `${environment.apiUrl}/organisations`;

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();

    return token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
  }

  createOrganisation(
    request: OrganisationRequest,
  ): Observable<OrganisationResponse> {
    return this.http.post<OrganisationResponse>(
      this.apiUrl,
      request,
      { headers: this.getAuthHeaders() },
    );
  }

  getOrganisation(): Observable<OrganisationResponse[]> {
    return this.http.get<OrganisationResponse[]>(
      `${this.apiUrl}/me`,
      { headers: this.getAuthHeaders() },
    );
  }
}
