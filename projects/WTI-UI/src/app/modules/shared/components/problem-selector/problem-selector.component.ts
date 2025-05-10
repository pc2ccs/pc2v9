import { Component, forwardRef, OnInit, OnDestroy, Input } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { ContestProblem } from 'src/app/modules/core/models/contest-problem';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { ContestClock } from 'src/app/modules/core/models/contest-clock';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/**
 * This class defines a component which displays a drop-down list containing the current contest problems.
 * Each time the component is initialized (that is, on each rendering during which its "ngOnInit(" method is invoked) 
 * it invokes its "loadProblems()" method, which examines the current contest clock to determine 
 * if the contest has been started.  If so, it fetches the current problem list from the server 
 * and displays it in the dropdown list; if the contest has not been started it sets the dropdown list to be empty.
 * Method loadProblems() is also invoked any time there is a change in the contest clock state (such as stopping
 * or restarting the contest clock), and the same process happens (the list is repopulated if the contest has been
 * started, otherwise it is set to empty).
 */
@Component({
selector: 'app-problem-selector',
templateUrl: './problem-selector.component.html',
providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProblemSelectorComponent),
      multi: true
    }
  ]
})
export class ProblemSelectorComponent implements OnInit, OnDestroy, ControlValueAccessor {
	private _unsubscribe = new Subject<void>();
	@Input() allowGeneral = false;
	problems: ContestProblem[] = [];
	
	//fields required to implement "ControlValueAccessor"
	value: string;
	onChange = (event: any) => { };
	onTouched = (event: any) => { };

	constructor(private _contestService: IContestService) { 
	}

	ngOnInit(): void {
		this.loadProblems();
		if (this.allowGeneral) {
			this.writeValue('general');
		}

    //Listen for (subscribe to) contest start/stop events to show/hide contest problems.
  	//The contestClockEvent "Subject" (defined in IContestService) is used as a toggle to indicate when 
	//contest clock-related events have occured. Subscribing to it means that this component will get a 
	//callback whenever the contestClockEvent's "next()" method is invoked. 
	//Currently the contestClockEvent's "next()" method is invoked in exactly one
	//place:  IWebsocketService.incomingMessage(), when a message of type "contest_clock" is received by the WTI-UI from the WTI-API
	//(which in turn happens when the WTI-API receives a "contest clock configuration update" notice via the PC2 API).
	//The effect of all this is that this Component gets notified when "some change" has occurred in the state of the
	//PC2 contest clock.  This causes the Component to execute its "loadProblems()" method; that method in turn checks
	//whether the contest is currently RUNNING; if so, it loads the problem names; if not, it blanks out problem names.

		this._contestService.contestClockEvent
		.pipe(takeUntil(this._unsubscribe))
		.subscribe(_ => this.loadProblems());
	}

	ngOnDestroy(): void {
		this._unsubscribe.next();
		this._unsubscribe.complete();
	}

	registerOnChange(fn: (event: any) => void) {
		this.onChange = fn;
	}

	registerOnTouched(fn: (event: any) => void) {
		this.onTouched = fn;
	}

	writeValue(value: string) {
		this.value = (value === null) ? undefined : value;
	}

	/**
	 * This method determines whether the contest problems should be displayed in a "ProblemSelectorComponent".
	 * Problems are only allowed to be displayed when the contest has been started, which is determined by looking
	 * at the "wallClockStartTime" value in the current ContestClock.  The method fetches the current clock from the WTI-API server,
	 * and if wallClockStartTime is greater than zero it fetches the current problem list from the server and displays it in the
	 * component; otherwise it sets the list of displayed problems to an empty list (array).
	 */
	private loadProblems(): void {
		//get the contest clock to see whether problems should be loaded/displayed
		this._contestService.getContestClock()
			.subscribe(
				(contestClock: ContestClock) => {
					if (!contestClock) { 
						console.error ("ProblemSelectorComponent.loadProblems(): ContestService.getContestClock() subscription callback: unable to get ContestClock from PC2 API via ContestService!");
					} else {
						//we got back a contest clock; check if the contest has been started
						if (this.contestHasBeenStarted(contestClock)) {	
							//yes, contest has been started; get the problems and display them
							this._contestService.getProblems()
								.subscribe(
									(data: ContestProblem[]) => {
										this.problems = data;
									}, 
									(error: unknown) => {
										console.warn("ProblemSelectorComponent.loadProblems(): error getting contest problems:");
										console.warn(error);
										this.problems = [];
									}
								);
						} else {
							this.problems = [];
						}					
							
					}
				}, 
				(error: unknown) => {
    				console.error("ProblemSelectorComponent.loadProblems(): ContestService.getContestClock() subscription callback error: ");
					console.error (error);
  				}
			);	
	} //end loadProblems()
	
	/**
	 * Returns true if the wallClockStartTime in the specified ContestClock is greater than zero (in other words,
	 * if the contest has been started); false otherwise.
	 * (Note that wallClockStartTime is the Unix Epoch time at which the contest was first started (if it has been started);
	 * otherwise (i.e. if the contest has never been started), wallClockStartTime is null in PC2 ContestTime objects 
	 * and such a null value is converted to zero by the WTI-API "/contestclock" endpoint.)
	 * 
	 */
	private contestHasBeenStarted (contestClock: ContestClock) : boolean {
		if (parseInt(contestClock.wallClockStartTime) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
