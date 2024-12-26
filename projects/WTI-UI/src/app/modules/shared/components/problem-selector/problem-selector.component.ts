import { Component, forwardRef, OnInit, OnDestroy, Input } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { ContestProblem } from 'src/app/modules/core/models/contest-problem';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DEBUG_MODE } from 'src/constants';

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
  value: string;
  onChange = (event: any) => { };
  onTouched = (event: any) => { };

  constructor(private _contestService: IContestService) { 
	if (DEBUG_MODE) {
		console.log ("Executing ProblemSelectorComponent constructor; IContestService id = ", this._contestService.uniqueId) ;
	}
  }

  ngOnInit(): void {
	  if (DEBUG_MODE) {
		  console.log ("Executing ProblemSelectorComponent.ngOnInit()") ;
	  }
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

  private loadProblems(): void {
	  if (DEBUG_MODE) {
		  console.log ("Executing ProblemSelectorComponent.loadProblems()") ;
	  }
    if (this._contestService.isContestRunning) {
    	if (DEBUG_MODE) {
    		console.log ("ProblemSelectorComponent.loadProblems(): ContestService.isContestRunning() returned positive Truthy value") ;
    	}
      this._contestService.getProblems()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: ContestProblem[]) => {
    	  if (DEBUG_MODE) {
    		  console.log ("ProblemSelectorComponent.loadProblems(): subscription callback from ContestService.getProblems() returned data:");
    		  console.log (data) ;
    	  }
        this.problems = data;
      }, (error: any) => {
    	  if (DEBUG_MODE) {
    		  console.log ("ProblemSelectorComponent.loadProblems(): subscription callback from ContestService.getProblems() returned error");
    		  console.log ("  Setting contest problem list to empty array." ) ;
    	  }
        this.problems = [];
      });
    } else {
    	if (DEBUG_MODE) {
    		console.log ("ProblemSelectorComponent.loadProblems(): ContestService.isContestRunning() returned negative Truthy value") ;
			console.log ("  Setting contest problem list to empty array." ) ;

    	}
      this.problems = [];
    }
  }
}
