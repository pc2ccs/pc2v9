import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Clarification } from '../../../core/models/clarification';
import { MatDialog } from '@angular/material/dialog';
import { NewClarificationComponent } from '../new-clarification/new-clarification.component';
import { AuthService } from '../../../core/auth/auth.service';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';

@Component({
  templateUrl: './clarifications-page.component.html',
  styleUrls: ['./clarifications-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ClarificationsPageComponent implements OnInit, OnDestroy {
  private _unsubscribe = new Subject<void>();
  filterForm: FormGroup;
  clarifications: Clarification[] = [];
  filteredClarifications: Clarification[] = [];
  get teamName(): string { return this._authService.username; }

  constructor(private _formBuilder: FormBuilder,
              private _contestService: IContestService,
              private _modalService: MatDialog,
              private _authService: AuthService,
			  private _appTitleService: AppTitleService) { }

  ngOnInit(): void {
	
	this._appTitleService.setTitleWithTeamId("Submit Clar");
	
    this.buildForm();
    this.loadClars();

    this._contestService.clarificationsUpdated
      .pipe(takeUntil(this._unsubscribe))
      .subscribe(_ => {
        this.loadClars();
      });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

  newClarification(): void {
    this._modalService.open(NewClarificationComponent, {
      data: {},
      disableClose: true
    });
  }

  private filterClarifications() {
    const fitlerParams = this.filterForm.value;
    let filtered = this.clarifications;
    if (fitlerParams.receipient === 'all') { filtered = filtered.filter(x => x.recipient === 'All'); }
    if (fitlerParams.receipient === 'team') { filtered = filtered.filter(x => x.recipient !== 'All'); }
    if (fitlerParams.problem) { filtered = filtered.filter(x => x.problem === fitlerParams.problem); }

    this.filteredClarifications = filtered;
  }

  private buildForm(): void {
    this.filterForm = this._formBuilder.group({
      receipient: [''],
      problem: [],
    });

    this.filterForm.valueChanges.subscribe(_ => this.filterClarifications());
  }

  private loadClars(): void {
    this._contestService.getClarifications()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: Clarification[]) => {
        this.clarifications = data.sort((x: Clarification, y: Clarification) => y.time - x.time);
        this.filterClarifications();
      }, (error: any) => {
        console.error('error loading clarifications!');
        console.error(error);
      });
  }

  public reset(): void {
    this.filteredClarifications = this.clarifications;
    this.buildForm();
  }
}
