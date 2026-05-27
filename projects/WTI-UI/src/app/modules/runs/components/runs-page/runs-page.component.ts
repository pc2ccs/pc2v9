import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Run } from 'src/app/modules/core/models/run';
import { MatDialog } from '@angular/material/dialog';
import { NewRunComponent } from '../new-run/new-run.component';
import { TestRunDetailComponent } from '../test-run-detail/test-run-detail.component';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { SCOREBOARD_TYPE } from 'src/constants';
import type { ScoreboardType } from 'src/constants';
import { DEBUG_MODE } from 'src/constants';
import { ScoreboardModeService } from 'src/app/modules/core/services/scoreboard-mode.service';


@Component({
  templateUrl: './runs-page.component.html',
  styleUrls: ['./runs-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class RunsPageComponent implements OnInit, OnDestroy {
  private _unsubscribe = new Subject<void>();
  filterForm: FormGroup;
  runs: Run[] = [];
  filteredRuns: Run[] = [];

  readonly SCOREBOARD_TYPE = SCOREBOARD_TYPE;
  scoreboardType!: ScoreboardType;

  constructor(private _formBuilder: FormBuilder,
              private _teamService: ITeamsService,
              private _contestService: IContestService,
              private _matDialog: MatDialog,
			  private _appTitleService: AppTitleService,
			  private _scoreboardMode: ScoreboardModeService) { 
				
				if (DEBUG_MODE) {
					console.log('[RunsPageComponent constructor] ContestService instance:', this._contestService);
				}
			}

  ngOnInit(): void {
	
	this._appTitleService.setTitleWithTeamId("Runs");
	
	this.scoreboardType = this._contestService.getScoreboardType();

    this.buildForm();
    this.loadRuns();
    
    this.filteredRuns = this.runs;

    // when runs are updated, trigger a reload
    this._teamService.runsUpdated
      .pipe(
	takeUntil(this._unsubscribe))
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
    const filterParams = this.filterForm.value;
    let filtered = this.runs;
    if (filterParams.runType === 'test') { filtered = filtered.filter(x => x.isTestRun); }
    else if (filterParams.runType === 'judged') { filtered = filtered.filter(x => !x.isTestRun); }
    if (filterParams.language) { filtered = filtered.filter(x => filterParams.language === x.language); }
    if (filterParams.problem) { filtered = filtered.filter(x => filterParams.problem === x.problem); }
    if (filterParams.judgement) { filtered = filtered.filter(x => filterParams.judgement === x.judgement); }
    this.filteredRuns = filtered;
  }

  private buildForm(): void {
    this.filterForm = this._formBuilder.group({
      runType: ['both'],
      language: [],
      problem: [],
      judgement: []
    });

    this.filterForm.valueChanges
		.pipe(takeUntil(this._unsubscribe))
		.subscribe(() => this.filterData());
  }

  private loadRuns(): void {
    this._teamService.getRuns()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((data: Run[]) => {

        this.runs = data.sort((x: Run, y: Run) => 
        {
	      if (y.time !== x.time) {
        	return y.time - x.time;//sort by descending order of time
	      } else {
	        return y.id.localeCompare(x.id); //if times are same sort by the id in descending order
	      }
    });
        this.filterData();
      });
  }

  public reset(): void {
	this.filterForm.reset({
		runtype: 'both',
		language: null,
		problem: null,
		judgement: null
	});
	this.filteredRuns = this.runs;
  }

	get isPassFail(): boolean {
		return this._scoreboardMode.isPassFail();
	}

	get isPointScoring(): boolean {
		return this._scoreboardMode.isPointScoring();
	}

	hasAcceptedJudgement(run: Run): boolean {
    return (run.score ?? 0) > 0 || ['accepted', 'yes'].includes(run.judgement?.toLowerCase() ?? '');	
  }

}
