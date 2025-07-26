import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject, delay  } from 'rxjs';
import { Clarification } from '../../../core/models/clarification';
import { MatDialog } from '@angular/material/dialog';
import { NewClarificationComponent } from '../new-clarification/new-clarification.component';
import { AuthService } from '../../../core/auth/auth.service';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { saveCurrentPage } from 'src/app/app.component';
import * as Constants from 'src/constants';

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
	
	this._appTitleService.setTitleWithTeamId("Clarifications");

    //indicate that this Clarifications page is the most recently accessed page
    saveCurrentPage(Constants.CLARIFICATIONS_PAGE);

    this.buildForm();
    this.loadClars();

    this._contestService.clarificationsUpdated
      .pipe(
	takeUntil(this._unsubscribe))
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
    const filterParams = this.filterForm.value;
    let filtered = this.clarifications;

    if (filterParams.recipient === 'all') { filtered = filtered.filter(x => (x.recipient === 'All' || x.recipient === "No Answer Yet")); }
    if (filterParams.recipient === 'some') { filtered = filtered.filter(x => (x.recipient === 'Some' || x.recipient === "No Answer Yet")); }
    if (filterParams.recipient === 'team') { filtered = filtered.filter(x => (x.recipient === 'My Team' || x.recipient === "No Answer Yet")); }
    if (filterParams.problem) { filtered = filtered.filter(x => x.problem === filterParams.problem); }

    this.filteredClarifications = filtered;
  }

  private buildForm(): void {
    this.filterForm = this._formBuilder.group({
      recipient: [''],
      problem: [],
    });

    this.filterForm.valueChanges.subscribe(_ => this.filterClarifications());
  }

  private loadClars(): void {
    this._contestService.getClarifications()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: Clarification[]) => {
        this.clarifications = data.sort((x: Clarification, y: Clarification) => 
        {
	      if (y.time !== x.time) {
        	return y.time - x.time;//sort by descending order of time
	      } else {
	        return y.id.localeCompare(x.id); //if times are same sort by the id in descending order
	      }
    });
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
