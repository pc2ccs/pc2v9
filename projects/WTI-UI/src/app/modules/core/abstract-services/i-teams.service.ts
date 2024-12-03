import { LoginCredentials } from '../models/login-credentials';
import { Observable, Subject } from 'rxjs';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { Submission } from '../models/submission';
import { Run } from '../models/run';
import { NewClarification } from '../models/new-clarification';
import { DEBUG_MODE } from 'src/constants';

export abstract class ITeamsService {
  runsUpdated = new Subject<void>();
  
  constructor () {
	  if (DEBUG_MODE) {
		  console.log ("Executing ITeamsService constructor") ;
	  }
  }

  abstract login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse>;

  abstract logout(): Observable<any>;

  abstract submitRun(submission: Submission): Observable<any>;

  abstract getRuns(): Observable<Run[]>;

  abstract postClarification(clarification: NewClarification): Observable<any>;
}
