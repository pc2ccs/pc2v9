import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { ContestProblem } from '../models/contest-problem';
import { ContestClock } from '../models/contest-clock';
import { Clarification } from '../models/clarification';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export abstract class IContestService {

  //need to document how the "clarificationsUpdated" Subject works (see the description of contestClockEvent, below; 
  //this operates similarly although it is used in more places) 
  clarificationsUpdated = new Subject<void>();

  //The contestClockEvent "Subject" is used as a toggle to indicate when contest clock-related events have occured.
  //It is "subscribed to" by the "ProblemSelectorComponent.ngOnInit()" method (meaning, that component will get a callback
  //whenever the contestClockEvent's "next()" method is invoked). The contestClockEvent's "next()" method is invoked in exactly one
  //place:  IWebsocketService.incomingMessage(), when a message of type "contest_clock" is received by the WTI-UI from the WTI-API
  //(which in turn happens when the WTI-API receives a "contest clock configuration update" notice via the PC2 API).
  //The effect of all this is that the ProblemSelectorComponent gets notified when "some change" has occurred in the state of the
  //PC2 contest clock.  This causes the ProblemSelectorComponent to execute its "loadProblems()" method; that method in turn checks
  //whether the contest is currently RUNNING; if so, it loads the problem names; if not, it blanks out problem names.
  //note that contestClockEvent is a WTI-UI-specific object used to coordinate with Websocket "clock" messages; 
  //it is NOT a "ContestClock" object in the sense of classes defined in core/models, nor an "Event" it PC2 terms.
  contestClockEvent = new Subject<void>();  

  standingsUpdated = new Subject<void>();
  isContestRunning = false;
  
  //the WTI-UI representation of the PC2 Server's ContestClock (PC2 class ContestTime)
  contestClock: ContestClock = new ContestClock();  

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

  abstract getContestClock(): Observable<ContestClock>;		//note this refers to a "ContestClock" model object, not the "contestClockEvent" Subject above
  
  abstract updateContestClock (): void;		//update the WTI-UI representation of the PC2 contest clock

  abstract getElapsedSecs(): number;

  abstract getRemainingSecs(): number;

  abstract getStandings(): Observable<String>;

  abstract markStandingsOutOfDate(): void;

  abstract getStandingsAreCurrentFlag() : boolean;
}
