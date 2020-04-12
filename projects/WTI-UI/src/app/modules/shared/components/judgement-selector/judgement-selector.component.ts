import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';

@Component({
  selector: 'app-judgement-selector',
  templateUrl: './judgement-selector.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => JudgementSelectorComponent),
      multi: true
    }
  ]
})
export class JudgementSelectorComponent implements OnInit, OnDestroy, ControlValueAccessor {
  private _unsubscribe = new Subject<void>();
  judgements: string[] = [];
  value: string;
  onChange = (event: any) => { };
  onTouched = (event: any) => { };

  constructor(private _contestService: IContestService) { }

  ngOnInit(): void {
    this.loadJudgements();
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

  private loadJudgements(): void {
    this._contestService.getJudgements()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: string[]) => {
        this.judgements = data;
      }, (error: any) => {
        this.judgements = [];
        console.error('Error loading judgements!');
        console.error(error);
      });
  }
}
