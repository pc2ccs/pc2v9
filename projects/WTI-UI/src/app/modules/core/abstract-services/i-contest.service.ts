import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { ContestProblem } from '../models/contest-problem';
import { Clarification } from '../models/clarification';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export abstract class IContestService {
  clarificationsUpdated = new Subject<void>();
  contestClock = new Subject<void>();
  standingsUpdated = new Subject<void>();
  isContestRunning = false;

  //give each instance of IContestService a unique ID for debugging purposes
  private static nextId: number = 1;
  public uniqueId: number;

  
  constructor () {
	  this.uniqueId = IContestService.nextId++;
	  if (DEBUG_MODE) {
		  console.log ("Executing IContestService constructor for unique instance ", this.uniqueId) ;
	  }
  }

  abstract getLanguages(): Observable<ContestLanguage[]>;

  abstract getProblems(): Observable<ContestProblem[]>;

  abstract getJudgements(): Observable<string[]>;

  abstract getClarifications(): Observable<Clarification[]>;

  abstract getIsContestRunning(): Observable<boolean>;
  
  abstract getStandings(): Observable<String>;

  abstract markStandingsOutOfDate(): void;

  abstract getStandingsAreCurrentFlag() : boolean;
}
