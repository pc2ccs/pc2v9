import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { DEBUG_MODE } from 'src/constants'

/**
 * This class defines a Timer which updates at a (nominal) rate of once per second.  The Timer maintains
 * two values: elapsed time and remaining time; these values default to initial values of zero but their
 * values are intended to be initialized via external calls to methods setElapsedSec() and setRemainingSecs().
 * Each time the Timer "ticks" these values are updated (incremented, for elapsed time, and decremented,
 * for remaining time).
 * The Timer uses a JavaScript "setInterval()" to generate update events at the nominal once-per-second
 * rate.  Since setInterval() is documented to not be guaranteed to operate at its specified rate when
 * the browser has lost focus or is minimized, each update event checks to see if there has been a recent 
 * update; if not, it resets the clocks by invoking the ContestService updateLocalContestClockFromServer() method, which
 * resynchronizes the Timer's elapsed and remaining time from the server.
 * 
 * The Timer is started by calling method startTimer(); it can be stopped by calling method stopTimer();
 * these methods are intended to be invoked by external code which detects when the PC2 Admin starts/stops
 * the PC2 contest clock.
 * 
 * When stopped and then restarted the Timer picks up counting where it left off. 
 */
export class ContestTimerService {

  elapsedSecs = 0 ; 	//how many seconds the contest has been running (doesn't include any 'paused' time)
  remainingSecs = 0 ;   //how many seconds remain in the contest (default default=0 until specified otherwise)
  intervalId: ReturnType<typeof setInterval> ;  //the id of the JavaScript "interval" used to generate timer ticks
  isTimerRunning: boolean = false;
  
  mostRecentTimerUpdate: Date = null;
  
  constructor(private _contestService: IContestService) {
	if (DEBUG_MODE) {
		console.log ("Executing ContestTimerService constructor using IContestService with id ", _contestService.uniqueId);
	}
  }

  getElapsedSecs(): number {
    return this.elapsedSecs;
  }

  getRemainingSecs(): number {
	return this.remainingSecs;
  }
  
  setElapsedSecs(newElapsedSecs: number) {
    if (this.intervalId) {
	  console.error ("ContestTimerService.setElapsedSecs(): cannot set elapsed seconds while Timer is running; stop the timer first.")
      return;
	} else {
      if (DEBUG_MODE) {
        console.log ("ContestTimerService.setElapsedSecs(): setting elapsed seconds to ", newElapsedSecs);
      }
      this.elapsedSecs = newElapsedSecs;
	}
  }
 
  setRemainingSecs(newRemainingSecs: number) {
    if (this.intervalId) {
	  console.error ("ContestTimerService.setRemainingSecs(): cannot set remaining seconds while Timer is running; stop the timer first.")
      return;
	} else {
      if (DEBUG_MODE) {
        console.log ("ContestTimerService.setRemainingTime(): setting remaining seconds to ", newRemainingSecs);
      }
      if (newRemainingSecs>=0) {
        this.remainingSecs = newRemainingSecs;
      } else {
        console.error("ContestTimerService.setRemainingSecs(): attempt to set Remaining Time to less than zero not allowed; setting to zero instead.");
        this.remainingSecs = 0;
      }
	}
  }

  /**
   * This method starts a Timer which fires once per second, updating the "elapsedSecs" and "remainingSecs" variables each second.
   * The method uses the JavaScript "setInterval()" method to detect passage of each second (1000 msec).
   * Note that setInterval() is not guaranteed to continue operating at the correct rate if the browser is minimized;
   * the method compensates for this by saving the current time at each update and then checking to be sure the
   * current update is happening less than two seconds since the last update; if not, it invokes the 
   * ContestService updateLocalContestClockFromServer() method to resync the local contest clock with the server.
   */
  startTimer() {
	if (this.intervalId) {
      console.error("ContestTimerService.startTimer():  call to startTimer() when timer is already running");
      return;
	} else {
      if (DEBUG_MODE) {
        console.log ("ContestTimerService.startTimer():  starting 1-second interval timer");
	  }
      //Start a 1-second interval timer going, save the interval timer Id
      this.intervalId = setInterval(
        //execute this function at the specified interval:
        () => {
        	
        	//update the tracking of when the last timer update happened
        	let now: Date = new Date();
			if (this.mostRecentTimerUpdate == null) {
				//we've never updated the timer; save current update time
				this.mostRecentTimerUpdate = now ;
			}
       	
			//check how long it's been since a timer update has happened (it could be much longer than the 1-second implied by the setInterval()
			// rate because that rate can be significantly slowed if the browser has been minimized)
			let timeSpan = now.getTime() - this.mostRecentTimerUpdate.getTime();	//epoch times, in milliseconds
			if (timeSpan > 4999) {
				//last update was more than 5 seconds ago; update the contest clock from the server (which also updates the timer)
				this._contestService.updateLocalContestClockFromServer();
			} else {
				//we've seen an update within the last five seconds; just update by one second
				this.elapsedSecs += 1 ;
				//we don't want to show a remaining time that is negative
				if (this.remainingSecs > 0) {
					this.remainingSecs -= 1
				}
			}
			
			//record that we've now updated the contest clock display
			this.mostRecentTimerUpdate = now ;
			
        }, 
        1000	// 1000 milliseconds = 1 second interval
      ); 
      this.isTimerRunning = true;
	}
  }

  stopTimer() {	
    if (!this.intervalId) {
      console.error("ContestTimerService.stopTimer():  call to stopTimer() when timer is not running");
      return;
    } else {
      if (DEBUG_MODE) {
        console.log ("ContestTimerService.stopTimer():  stopping timer");
	  }
      //dispose of the existing interval timer (thus stopping it)
      clearInterval(this.intervalId);
      this.intervalId = undefined;
      this.isTimerRunning = false;
    }
  }
 
}
