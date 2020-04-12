import { Injectable } from '@angular/core';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { LoginCredentials } from '../models/login-credentials';
import { Observable, of } from 'rxjs';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { Submission } from '../models/submission';
import { Run } from '../models/run';
import { NewClarification } from '../models/new-clarification';

@Injectable()
export class TeamsMockService extends ITeamsService {
  login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse> {
    return of<TeamsLoginResponse>({
      teamName: loginCredentials.teamName,
      teamId: 'hv83h-jsh38rbdi-dhdiuhih43r'
    });
  }

  logout(): Observable<any> {
    return of({});
  }

  submitRun(submission: Submission): Observable<any> {
    console.log(submission);
    return of({});
  }

  getRuns(): Observable<Run[]> {
    return of<Run[]>([
      {
        id: '1-1',
        isTestRun: false,
        language: 'C#',
        problem: 'Dogzilla',
        judgement: 'Wrong Answer',
        time: 1555537910,
        results: undefined,
        isPreliminary: true
      }, {
        id: '2-1',
        isTestRun: false,
        language: 'C#',
        problem: 'Dogzilla',
        judgement: 'Output Format Error',
        time: 1555537914,
        results: undefined,
        isPreliminary: false
      }, {
        id: '3-1',
        isTestRun: true,
        language: 'C#',
        problem: 'Dogzilla',
        judgement: 'Correct',
        time: 1555537917,
        results: 'sample output would go here...\nanother line of stuff here\nmaybe a stack trace!!!',
        isPreliminary: false
      }, {
        id: '4-1',
        isTestRun: false,
        language: 'Java',
        problem: 'Dogzilla',
        judgement: 'Correct',
        time: 1555537917,
        results: undefined,
        isPreliminary: false
      }, {
        id: '5-1',
        isTestRun: false,
        language: 'Java',
        problem: 'Matrix',
        judgement: 'Correct',
        time: 1555537918,
        results: undefined,
        isPreliminary: false
      }, {
        id: '6-1',
        isTestRun: false,
        language: 'C#',
        problem: 'Ducks',
        judgement: 'Output Format Error',
        time: 1555537925,
        results: undefined,
        isPreliminary: false
      }, {
        id: '7-1',
        isTestRun: false,
        language: 'C#',
        problem: 'Magic Numbers',
        judgement: 'Correct',
        time: 1555537933,
        results: undefined,
        isPreliminary: true
      }
    ]);
  }

  postClarification(clarification: NewClarification): Observable<any> {
    console.log(clarification);
    return of({});
  }
}
