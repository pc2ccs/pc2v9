/** This class encapsulates the PC2 notion of a "Contest Clock" -- a collection of items which together provide 
 *  information on the state of state of time in the contest -- what time the contest started, how long the contest runs,
 *  whether the contest is running or paused right now, and how much time has elapsed on the contest clock since the
 *  contest started (note that this latter value does NOT include any real time which passed while the contest was "paused").
 *  Essentially, this model class corresponds to the PC2 server class "ContestTime".
 */
export class ContestClock { 
  isRunning: string = '';			//string 'true' or 'false'
  contestLengthSecs: string = '';	//string representing an integer number of seconds
  elapsedSecs: string = '';         //string representing total time in seconds that the contest has been running -- does NOT include time during any "pauses"
  wallClockStartTime: string = '';  //string representing unix timestamp when the contest actually started -- msec since the Epoch.  Does not change due to "pauses"
}