import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from '../../features/auth/models/auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly authUrl = `${environment.apiUrl}/auth`;

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(
      `${this.authUrl}/register`,
      request,
    );
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    /*
     * Read the response as text so this client can handle either:
     * 1. A plain JWT string
     * 2. A JSON object containing a token
     *
     * We can simplify this after confirming the exact backend response.
     */
    return this.http
      .post(`${this.authUrl}/login`, request, {
        responseType: 'text',
      })
      .pipe(
        map((rawResponse) => {
          let data: unknown = rawResponse;

          try {
            data = JSON.parse(rawResponse);
          } catch {
            // A plain JWT is also a valid response format.
          }

          if (typeof data === 'string') {
            return { token: data };
          }

          if (
            typeof data === 'object' &&
            data !== null &&
            'token' in data &&
            typeof data.token === 'string'
          ) {
            return data as LoginResponse;
          }

          throw new Error('The login response did not contain a valid token.');
        }),
        map((response) => {
          if (!response.token.trim()) {
            throw new Error('The server returned an empty token.');
          }

          sessionStorage.setItem('taskflow_token', response.token);

          return response;
        }),
      );
  }

  getToken(): string | null {
    return sessionStorage.getItem('taskflow_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    sessionStorage.removeItem('taskflow_token');
  }
}