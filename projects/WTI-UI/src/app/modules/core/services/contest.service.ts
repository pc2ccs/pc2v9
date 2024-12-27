import { Injectable } from '@angular/core';
import { IContestService } from '../abstract-services/i-contest.service';
import { Observable } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { ContestProblem } from '../models/contest-problem';
import { ContestClock } from '../models/contest-clock';
import { ContestTimerService } from './contestTimer.service' ;
import { Clarification } from '../models/clarification';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class ContestService extends IContestService {

  standingsAreCurrent: boolean ;
  cachedStandings: Observable<String> ;

  //the WTI-UI timer service which updates elapsed and remaining time when started
  contestTimer: ContestTimerService = new ContestTimerService() ; 
  
  constructor(private _httpClient: HttpClient) {
<<<<<<< Upstream, based on c4b09354e015df5ecedbdcd004dea9810eecc455
	super();

	if (DEBUG_MODE) {
		console.log ("Executing ContestService constructor; instance ID = ", this.uniqueId) ;
	}
	
	this.standingsAreCurrent = false;
=======
    super();
	if (DEBUG_MODE) {
		console.log ("Executing ContestService constructor; instance ID = ", this.uniqueId) ;
	}
    this.standingsAreCurrent = false;
>>>>>>> ab9b09a i1027: ContestService: make root-injectable; add ContestTimer; also:
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
		console.log ("ContestService.getIsContestRunning(): calling HTTP client get(.../contest.isRunning)") ;
	}
	return this._httpClient.get<boolean>(`${environment.baseUrl}/contest/isRunning`);
  }
  
  /** This method returns an Observable "ContestClock" object -- a WTI-UI model corresponding to the PC2 "ContestTime" class,
   *  which itself encapsulates the "contest clock" on the PC2 server.
   * TODO: should this method return the local copy of the ContestClock?
   */
  getContestClock(): Observable<ContestClock> {
    return this._httpClient.get<ContestClock>(`${environment.baseUrl}/contest/contestclock`);
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
	
	updateContestClock (newContestClock: ContestClock)  {
		//save the new clock
		if (DEBUG_MODE) {
			console.log("ContestService (id ", this.uniqueId, ").updateContestClock(): replacing Contest Clock with:");
			console.log(newContestClock);
		}
		this.contestClock = newContestClock;
		
		//pull the values out of the updated clock
		let timerShouldBeStarted = this.contestClock.isRunning ;
		let elapsedSecs = parseInt(this.contestClock.elapsedSecs);
		let contestLengthSecs = parseInt(this.contestClock.contestLengthSecs);
		let remainingSecs = contestLengthSecs - elapsedSecs;
		
/*		if (DEBUG_MODE) {
			console.log ("ContestService.updateContestClock(): values pulled from newContestClock:");
			console.log ("  timerShouldBeStarted = ", timerShouldBeStarted);
			console.log ("  elapsedSecs = ", elapsedSecs);
			console.log ("  contestLengthSecs = ", contestLengthSecs);
			console.log ("  remainingSecs = ", remainingSecs);
		}
*/		
		//shut off timer if it is running (otherwise we can't update the elapsed/remaining time values)
		if (this.contestTimer.isTimerRunning) {
			if (DEBUG_MODE) {
				console.log("ContestService (id ", this.uniqueId, ").updateContestClock(): stopping timer");
			}
			this.contestTimer.stopTimer();
		}
		
		//store the new contest time values in the Timer
		this.contestTimer.setElapsedSecs(elapsedSecs);
		this.contestTimer.setRemainingSecs(remainingSecs);
		
		//restart the timer if the new contest clock values indicate it should be running
		if (timerShouldBeStarted) {
			if (DEBUG_MODE) {
				console.log("ContestService (id ", this.uniqueId, ").updateContestClock(): starting timer");
			}
			this.contestTimer.startTimer();
		}
	}
	
	enableContestTimerUpdates() {
		if (DEBUG_MODE) {
			console.log("ContestService (id ", this.uniqueId, ").enableContestTimerUpdates(): starting timer");
		}
		this.contestTimer.startTimer();
	}
	
	disableContestTimerUpdates() {
		if (DEBUG_MODE) {
			console.log("ContestService (id ", this.uniqueId, ").disableContestTimerUpdates(): stopping timer");
		}
		this.contestTimer.stopTimer();
	}
	
	getElapsedSecs(): number {
		let elapsedSecs = this.contestTimer.getElapsedSecs();
/*		if (DEBUG_MODE) {
			console.log("ContestService (id ", this.uniqueId, ").getElapsedSecs(): returning elapsed secs from ContestTimer: ", elapsedSecs);
		}
*/		
		return elapsedSecs ;
	}
	
	getRemainingSecs(): number {
		let remainingSecs = this.contestTimer.getRemainingSecs();
/*		if (DEBUG_MODE) {
			console.log("ContestService (id ", this.uniqueId, ").getRemainingSecs(): returning remaining secs from ContestTimer: ", remainingSecs);
		}
*/
		return remainingSecs ;
	}

}
