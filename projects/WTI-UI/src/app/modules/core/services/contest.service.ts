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
import { RESYNC_INTERVAL_IN_MINUTES } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class ContestService extends IContestService {

  standingsAreCurrent: boolean ;
  cachedStandings: Observable<String> ;

  //the WTI-UI timer service which updates on-screen elapsed and remaining time when started (enabled)
  contestTimer: ContestTimerService = new ContestTimerService(this) ; 
  
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
<<<<<<< Upstream, based on c4b09354e015df5ecedbdcd004dea9810eecc455
>>>>>>> ab9b09a i1027: ContestService: make root-injectable; add ContestTimer; also:
=======
    
    //set a timer to auto-refresh the contest clock displays, at a rate defined in src/constants
    //TODO: what happens to the timing of this update when the browser is minimized (because setInterval() runs slower when
    // minimized)?  Need to track "most recent update"??
    setInterval(
            //execute this function at the following-specified interval:
            () => {
              if (DEBUG_MODE) {
                console.log("ContestService: resyncing clocks with PC2 Server");
              }
              this.updateContestClock();
            }, 
            RESYNC_INTERVAL_IN_MINUTES * 60 * 1000	// timer interval in msec: minutes * (secs-per-min) * (msec-per-sec)
          ); 
>>>>>>> b069333 i1027: add code to resync UI clocks with PC2 periodically.
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
		console.log ("ContestService.getIsContestRunning(): invoking HTTP get(.../contest.isRunning) WTI-API endpoint") ;
	}
	return this._httpClient.get<boolean>(`${environment.baseUrl}/contest/isRunning`);
  }
  
  /** This method returns an Observable "ContestClock" object -- a WTI-UI model corresponding to the PC2 "ContestTime" class,
   *  which itself encapsulates the "contest clock" on the PC2 server.
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
	
	/** This method invokes the local getContestClock() method, which makes an HTTP call to the WTI-API to get the current
	 *  PC2 Server clock (aka "ContestTime").  It subscribes to the Observable returned by the HTTP call, 
	 *  and when the subscription callback occurs it uses the received ContestClock data (an instance of 
	 *  WTI=UI models/ContestClock) to update the WTI-UI contest clock (including the onscreen displays).  
	 */
	updateContestClock ()  {
		
		//get the actual contest clock info from the PC2 server via the Contest Service (which gets it via the WTI Server and its PC2 API)
		if (DEBUG_MODE) {
			console.log("  Invoking ContestService.getContestClock(), subscribing for HTTP callback result")
		}
		this.getContestClock()
			.subscribe(
				(data: any) => {
        			if (!data) { 
						console.error ("ContestService.updateContestClock() getContestClock() subscription callback: unable to get ContestClock from PC2 API via ContestService!");
					} else {
						if (DEBUG_MODE) {
							console.log("ContestService.updateContestClock(): got callback from getContestClock() subscription; callback data =");
							console.log(data);
						}
						//install the received contest clock data into the ContestService's ContestClock
						this.installNewContestClock(data);					
					}
      			}, 
				(error: any) => {
        			console.error("ContestService.updateContestClock(): getContestClock() subscription callback error: ");
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
	installNewContestClock(data: any) {
		
		//copy the data fields (received from the PC2 Server via the WTI-API) into a new ContestService ContestClock object
		let newContestClock = new ContestClock();
		newContestClock.isRunning = data.running ;
		newContestClock.contestLengthSecs = data.contestLengthInSecs ;
		newContestClock.elapsedSecs = data.elapsedSecs ;
		newContestClock.wallClockStartTime = data.wallClockStartTime ;

		//save the new clock
		if (DEBUG_MODE) {
			console.log("ContestService (id", this.uniqueId, ").installNewContestClock(): replacing Contest Clock with:");
			console.log(newContestClock);
		}
		this.contestClock = newContestClock;
		
		//pull the values out of the updated clock
		let timerShouldBeStarted = this.contestClock.isRunning ;
		let elapsedSecs = parseInt(this.contestClock.elapsedSecs);
		let contestLengthSecs = parseInt(this.contestClock.contestLengthSecs);
		let remainingSecs = contestLengthSecs - elapsedSecs;
		
		if (DEBUG_MODE) {
			console.log ("ContestService.installNewContestClock(): values pulled from newContestClock:");
			console.log ("  timerShouldBeStarted = ", timerShouldBeStarted);
			console.log ("  elapsedSecs = ", elapsedSecs);
			console.log ("  contestLengthSecs = ", contestLengthSecs);
			console.log ("  remainingSecs = ", remainingSecs);
		}
	
		//shut off timer if it is running (otherwise we can't update the elapsed/remaining time values)
		if (this.contestTimer.isTimerRunning) {
			if (DEBUG_MODE) {
				console.log("ContestService (id", this.uniqueId, ").installNewContestClock(): stopping timer");
			}
			this.contestTimer.stopTimer();
		}
		
		//store the new contest time values in the Timer
		this.contestTimer.setElapsedSecs(elapsedSecs);
		this.contestTimer.setRemainingSecs(remainingSecs);
		
		//restart the timer if the new contest clock values indicate it should be running
		if (timerShouldBeStarted) {
			if (DEBUG_MODE) {
				console.log("ContestService (id", this.uniqueId, ").installNewContestClock(): starting timer");
			}
			this.contestTimer.startTimer();
		}
	}
	
	/** Returns the number of seconds which have elapsed so far in the contest, which it obtains from the separate
	 *  ContestTimer object.
	 */
	getElapsedSecs(): number {
		let elapsedSecs = this.contestTimer.getElapsedSecs();	
		return elapsedSecs ;
	}
	
	/** Returns the number of seconds remaining in the contest, which it obtains from the separate
	 *  ContestTimer object.
	 */
	getRemainingSecs(): number {
		let remainingSecs = this.contestTimer.getRemainingSecs();
		return remainingSecs ;
	}
}
