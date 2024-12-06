import { Injectable } from '@angular/core';
import { IContestService } from '../abstract-services/i-contest.service';
import { Observable } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { ContestProblem } from '../models/contest-problem';
import { Clarification } from '../models/clarification';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class ContestService extends IContestService {

  standingsAreCurrent: boolean ;
  cachedStandings: Observable<String> ;

  constructor(private _httpClient: HttpClient) {
	super();

	if (DEBUG_MODE) {
		console.log ("Executing ContestService constructor; instance ID = ", this.uniqueId) ;
	}
	
	this.standingsAreCurrent = false;
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
	if (DEBUG_MODE) {
		console.log ("Executing ContestService.getIsContestRunning(): calling HTTP client get(.../contest.isRunning)") ;
	}
	return this._httpClient.get<boolean>(`${environment.baseUrl}/contest/isRunning`);
  }
  
  getStandings(): Observable<String> {
	if (DEBUG_MODE) {
		console.log("ContestService.getStandings():")
	}
	if (!this.standingsAreCurrent) {
		if (DEBUG_MODE) {
			console.log ("Standings are out of date; fetching new standings");
		}
		this.cachedStandings = this._httpClient.get<String>(`${environment.baseUrl}/contest/scoreboard`);
		this.standingsAreCurrent = true ;
	} else {
		 if (DEBUG_MODE) {
			 console.log("Returning cached standings");
		 }
	}
	return this.cachedStandings ;
  }

	markStandingsOutOfDate() : void {
		this.standingsAreCurrent = false ;
	}
	
	getStandingsAreCurrentFlag() : boolean {
		return this.standingsAreCurrent ;
	}

}
