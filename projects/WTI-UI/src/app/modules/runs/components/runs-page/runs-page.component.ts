import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { takeUntil, filter } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Run } from 'src/app/modules/core/models/run';
import { MatDialog } from '@angular/material/dialog';
import { NewRunComponent } from '../new-run/new-run.component';
import { TestRunDetailComponent } from '../test-run-detail/test-run-detail.component';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { saveCurrentPage } from 'src/app/app.component';
import * as Constants from 'src/constants';

@Component({
  templateUrl: './runs-page.component.html',
  styleUrls: ['./runs-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class RunsPageComponent implements OnInit, OnDestroy {
  private _unsubscribe = new Subject<void>();
  filterForm: FormGroup;
  runs: Run[] = [];
  filteredRuns: Run[] = [];

  constructor(private _formBuilder: FormBuilder,
              private _teamService: ITeamsService,
              private _matDialog: MatDialog,
			  private _appTitleService: AppTitleService) { }

  ngOnInit(): void {
	
	this._appTitleService.setTitleWithTeamId("Runs");
	
    this.buildForm();
    this.loadRuns();
    
    //indicate that this Runs page is the most recently accessed page
    saveCurrentPage(Constants.RUNS_PAGE);

    this.filteredRuns = this.runs;

    // when runs are updated, trigger a reload
    this._teamService.runsUpdated
      .pipe(takeUntil(this._unsubscribe))
      .subscribe(_ => {
        this.loadRuns();
      });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

  openNewJudged(): void {
    this._matDialog.open(NewRunComponent, {
      data: { submitType: 'judged' },
      disableClose: true
    });
  }

  openNewTest(): void {
    this._matDialog.open(NewRunComponent, {
      data: { submitType: 'test' },
      disableClose: true
    });
  }

  viewResults(problem: string, results: string): void {
    this._matDialog.open(TestRunDetailComponent, {
      data: { problem, results }
    });
  }

  filterData(): void {
    const fitlerParams = this.filterForm.value;
    let filtered = this.runs;
    if (fitlerParams.runType === 'test') { filtered = filtered.filter(x => x.isTestRun); }
    else if (fitlerParams.runType === 'judged') { filtered = filtered.filter(x => !x.isTestRun); }
    if (fitlerParams.language) { filtered = filtered.filter(x => fitlerParams.language === x.language); }
    if (fitlerParams.problem) { filtered = filtered.filter(x => fitlerParams.problem === x.problem); }
    if (fitlerParams.judgement) { filtered = filtered.filter(x => fitlerParams.judgement === x.judgement); }
    this.filteredRuns = filtered;
  }

  private buildForm(): void {
    this.filterForm = this._formBuilder.group({
      runType: ['both'],
      language: [],
      problem: [],
      judgement: []
    });

    this.filterForm.valueChanges.subscribe(_ => this.filterData());
  }

  private loadRuns(): void {
    this._teamService.getRuns()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: Run[]) => {
        this.runs = data.sort((x: Run, y: Run) => y.time - x.time);
        this.filterData();
      });
  }

  public reset(): void {
    this.filteredRuns = this.runs;
    this.buildForm();
  }
}
