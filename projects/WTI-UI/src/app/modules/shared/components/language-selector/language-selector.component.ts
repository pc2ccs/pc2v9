import { Component, OnInit, OnDestroy, forwardRef } from '@angular/core';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { ContestLanguage } from 'src/app/modules/core/models/contest-language';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LanguageSelectorComponent),
      multi: true
    }
  ]
})
export class LanguageSelectorComponent implements OnInit, OnDestroy, ControlValueAccessor {
  private _unsubscribe = new Subject<void>();
  languages: ContestLanguage[] = [];
  value: ContestLanguage;
  onChange = (event: any) => { };
  onTouched = (event: any) => { };

  constructor(private _contestService: IContestService) { }

  ngOnInit(): void {
    this.loadLanguages();
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

  writeValue(value: ContestLanguage) {
    this.value = (value === null) ? undefined : value;
  }

  private loadLanguages(): void {
    this._contestService.getLanguages()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: ContestLanguage[]) => {
        this.languages = data;
      }, (error: any) => {
        this.languages = [];
      });
  }
}
