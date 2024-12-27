import { DEBUG_MODE } from 'src/constants'

export class ContestTimerService {

  elapsedSecs = 0 ; 		//how many seconds the contest has been running (doesn't include any 'paused' time)
  remainingSecs = 18000 ;   //how many seconds remain in the contest (default initial value: 5 hours = 18,000 secs)
  intervalId: ReturnType<typeof setInterval> ;  //the id of the JavaScript "interval" used to generate timer ticks
  isTimerRunning: boolean = false;
  
  constructor() {
	if (DEBUG_MODE) {
		console.log ("Executing ContestTimerService constructor");
	}
  }

  getElapsedSecs(): number {
    if (DEBUG_MODE) {
	  console.log("ContestTimerService.getElapsedSecs(): returning ", this.elapsedSecs);
    }
    return this.elapsedSecs;
  }

  getRemainingSecs(): number {
    if (DEBUG_MODE) {
	  console.log("ContestTimerService.getRemainingSecs(): returning ", this.remainingSecs);
    }
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
          //bump the elapsed & remaining counters since 1000msec (one sec) has passed
          this.elapsedSecs += 1 ;
		  this.remainingSecs -= 1
          if (DEBUG_MODE) {
            console.log(`Elapsed secs: ${this.elapsedSecs}; remaining secs: ${this.remainingSecs}`);
          }
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
