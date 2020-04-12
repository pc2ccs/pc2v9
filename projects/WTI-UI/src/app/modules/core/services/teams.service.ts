import { Injectable } from '@angular/core';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { LoginCredentials } from '../models/login-credentials';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { Submission } from '../models/submission';
import { Run } from '../models/run';
import { NewClarification } from '../models/new-clarification';

@Injectable()
export class TeamsService extends ITeamsService {
  constructor(private _httpClient: HttpClient) {
    super();
  }

  login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse> {
    return this._httpClient.post<TeamsLoginResponse>(`${environment.baseUrl}/teams/login`, loginCredentials);
  }

  logout(): Observable<any> {
    return this._httpClient.delete<any>(`${environment.baseUrl}/teams/logout`);
  }

  submitRun(submission: Submission): Observable<any> {
    return this._httpClient.post<Submission>(`${environment.baseUrl}/teams/run`, submission);
  }

  getRuns(): Observable<Run[]> {
    return this._httpClient.get<Run[]>(`${environment.baseUrl}/teams/run`);
  }

  postClarification(clarification: NewClarification): Observable<any> {
    return this._httpClient.post<NewClarification>(`${environment.baseUrl}/teams/clarification`, clarification);
  }
}
