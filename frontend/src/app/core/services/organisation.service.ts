import { Injectable , inject } from "@angular/core";
import { HttpClient , HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";
import { AuthService } from "./auth.service";

import { OrganisationRequest , OrganisationResponse } from "../../features/organisation/models/organisation-model";

@Injectable({
    providedIn: 'root'
})

export class OrganisationService{
    private readonly http = inject(HttpClient);
    private readonly authService = inject(AuthService);
      private readonly authUrl = `${environment.apiUrl}/organisations`;


    createOrganisation(
        request:OrganisationRequest
    ):Observable<OrganisationResponse>{
      const token = this.authService.getToken();


      const headers = token ? new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
      : new HttpHeaders();

      return this.http.post<OrganisationResponse>(
        `${this.authUrl}`,
        request,
        {headers}
      )
    }

    getOrganisation(): Observable<OrganisationResponse[]>{
      const token = this.authService.getToken();

      const headers = token
      ? new HttpHeaders({Authorization: `Bearer ${token}`})
      : new HttpHeaders();

      return this.http.get<OrganisationResponse[]>(
        `${this.authUrl}/me`,
        { headers }
      );
    }
}