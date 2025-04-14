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
import { RESYNC_INTERVAL_IN_MINUTES } from 'src/constants';

/**
 * This class provides a variety of "contest-related" services for clients.
 * It provides methods for initiating HTTP calls to the WTI Server to obtain contest
 * information (such as languages, problems, clarifications, standings, and the current
 * contest clock value); each of these services returns an "Observable" to which the invoking
 * client can "subscribe" to receive a callback when the actual data is available from the HTTP call.
 * 
 * The class also provides localized services such as:
 * 
 *  - keeping track of whether the current WTI-UI understanding of the contest standings (scoreboard) 
 *  are current (it maintains a cache of standings and keeps track of events which could cause the cache to be out-of-date);
 *  - a set of methods for providing clients with the current contest elapsed and remaining times, together with methods
 *  to force the local copy of the contest clock to be updated from the server and methods to load a new value into the
 *  local contest clock; 
 *  - a local timer which forces the local (on-screen) contest clock to be resynced with the server periodically.
 */
@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class ContestService extends IContestService {

  standingsAreCurrent: boolean ;
  cachedStandings: Observable<String> ;

  //the WTI-UI timer service which updates on-screen elapsed and remaining time when started (enabled)
  contestTimer: ContestTimerService = new ContestTimerService(this) ; 
  
  constructor(private _httpClient: HttpClient) {
	super();

	this.standingsAreCurrent = false;
    
    //set a timer to auto-refresh the contest clock displays, at a rate defined in src/constants
    //TODO: what happens to the timing of this update when the browser is minimized (because setInterval() runs slower when
    // minimized)?  Need to track "most recent update"??
    setInterval(
            //execute this function at the following-specified interval:
            () => {
              this.updateLocalContestClockFromServer();
            }, 
            RESYNC_INTERVAL_IN_MINUTES * 60 * 1000	// timer interval in msec: minutes * (secs-per-min) * (msec-per-sec)
          ); 
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
  
  /** This method returns an Observable "ContestClock" object -- a WTI-UI model corresponding to the PC2 "ContestTime" class,
   *  which itself encapsulates the "contest clock" on the PC2 server.
   */
  getContestClock(): Observable<ContestClock> {
    return this._httpClient.get<ContestClock>(`${environment.baseUrl}/contest/contestclock`);
  }
  
  getStandings(): Observable<String> {
	if (!this.standingsAreCurrent) {
		this.cachedStandings = this._httpClient.get<String>(`${environment.baseUrl}/contest/scoreboard`);
		this.standingsAreCurrent = true ;
	} 
	return this.cachedStandings ;
  }

	markStandingsOutOfDate() : void {
		this.standingsAreCurrent = false ;
	}
	
	getStandingsAreCurrentFlag() : boolean {
		return this.standingsAreCurrent ;
	}
	
	/** This method invokes the local getContestClock() method, which makes an HTTP call to the WTI-API to get the current
	 *  PC2 Server clock (aka "ContestTime").  It subscribes to the Observable returned by the HTTP call, 
	 *  and when the subscription callback occurs it uses the received ContestClock data (an instance of 
	 *  WTI=UI models/ContestClock) to update the WTI-UI contest clock (including the onscreen displays).  
	 */
	updateLocalContestClockFromServer ()  {
		
		//get the actual contest clock info from the PC2 server via the Contest Service (which gets it via the WTI Server and its PC2 API)
		this.getContestClock()
			.subscribe(
				(contestClock: ContestClock) => {
        			if (!contestClock) { 
						console.error ("ContestService.updateLocalContestClockFromServer() getContestClock() subscription callback: unable to get ContestClock from PC2 API via ContestService!");
					} else {
						//install the received contest clock data into the ContestService's ContestClock
						this.installNewContestClock(contestClock);					
					}
      			}, 
				(error: unknown) => {
        			console.error("ContestService.updateLocalContestClockFromServer(): getContestClock() subscription callback error: ");
					console.error (error);
      			}
			);	
	}
	
	/** This method receives a WTI-UI ContestClock model containing new values which should be used to update the WTI-UI contest clock,
	 *  including the onscreen displays.  It constructs a new ContestClock object containing the received data and installs that
	 *  object as the current WTI-UI clock.  It then updates the separate "ContestTimer" object with the specified values, and
	 *  if the received data indicates the clock should be running it starts the ContestTimer (which then genrates a "clock tick"
	 *  once per second to update the clock displays).
	 */
	installNewContestClock(data: ContestClock) {
		
		//copy the data fields (received from the PC2 Server via the WTI-API) into a new ContestService ContestClock object
		let newContestClock = new ContestClock();
		newContestClock.running = data.running ;
		newContestClock.contestLengthSecs = data.contestLengthSecs ;
		newContestClock.elapsedSecs = data.elapsedSecs ;
		newContestClock.wallClockStartTime = data.wallClockStartTime ;

		//save the new clock
		this.contestClock = newContestClock;
		
		//pull the values out of the updated clock
		const timerShouldBeStarted = this.contestClock.running ;
		const elapsedSecs = parseInt(this.contestClock.elapsedSecs);
		const contestLengthSecs = parseInt(this.contestClock.contestLengthSecs);
		const remainingSecs = contestLengthSecs - elapsedSecs;
	
		//shut off timer if it is running (otherwise we can't update the elapsed/remaining time values)
		if (this.contestTimer.isTimerRunning) {
			this.contestTimer.stopTimer();
		}
		
		//store the new contest time values in the Timer
		this.contestTimer.setElapsedSecs(elapsedSecs);
		this.contestTimer.setRemainingSecs(remainingSecs);
		
		//restart the timer if the new contest clock values indicate it should be running
		if (timerShouldBeStarted) {
			this.contestTimer.startTimer();
		}
	}
	
	/** Returns the number of seconds which have elapsed so far in the contest, which it obtains from the separate
	 *  ContestTimer object.
	 */
	getElapsedSecs(): number {
		const elapsedSecs = this.contestTimer.getElapsedSecs();	
		return elapsedSecs ;
	}
	
	/** Returns the number of seconds remaining in the contest, which it obtains from the separate
	 *  ContestTimer object.
	 */
	getRemainingSecs(): number {
		const remainingSecs = this.contestTimer.getRemainingSecs();
		return remainingSecs ;
	}
}
