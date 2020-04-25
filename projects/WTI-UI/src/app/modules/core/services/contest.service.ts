import { Injectable } from '@angular/core';
import { IContestService } from '../abstract-services/i-contest.service';
import { Observable } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { ContestProblem } from '../models/contest-problem';
import { Clarification } from '../models/clarification';

@Injectable()
export class ContestService extends IContestService {
  constructor(private _httpClient: HttpClient) {
    super();
  }

  getLanguages(): Observable<ContestLanguage[]> {
    return this._httpClient.get<ContestLanguage[]>(`${environment.baseUrl}/contest/languages`);
  }

  getProblems(): Observable<ContestProblem[]> {
    return this._httpClient.get<ContestProblem[]>(`${environment.baseUrl}/contest/problems`);
  }

  getJudgements(): Observable<string[]> {
    return this._httpClient.get<string[]>(`${environment.baseUrl}/contest/judgements`);
  }

  getClarifications(): Observable<Clarification[]> {
    return this._httpClient.get<Clarification[]>(`${environment.baseUrl}/contest/clarifications`);
  }

  getIsContestRunning(): Observable<boolean> {
    return this._httpClient.get<boolean>(`${environment.baseUrl}/contest/isRunning`);
  }
}
