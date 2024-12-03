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
		console.log ("Executing ProblemSelectorComponent constructor.") ;
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

    // listen for contest start/stop to show/hide contest problems
    this._contestService.contestClock
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
