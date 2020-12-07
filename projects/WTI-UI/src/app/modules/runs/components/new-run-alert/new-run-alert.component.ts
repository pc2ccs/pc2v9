import { Component, OnInit, Inject } from '@angular/core';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Run } from 'src/app/modules/core/models/run';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-new-run-alert',
  templateUrl: './new-run-alert.component.html',
  styleUrls: ['./new-run-alert.component.scss']
})
export class NewRunAlertComponent implements OnInit {
  private _unsubscribe = new Subject<void>();
  type: string;
  problem: string;
  judgement: string;
  isPreliminary: boolean;

  constructor(private _matDialogRef: MatDialogRef<NewRunAlertComponent>,
              private _router: Router,
              private _teamsService: ITeamsService,
              @Inject(MAT_DIALOG_DATA) private _data: any) { }

  ngOnInit() {
    this.loadRun(this._data.id);
  }

  goToRuns(): void {
    this._router.navigateByUrl('/runs');
    this.close();
  }

  close(): void {
    this._matDialogRef.close();
  }

  private loadRun(runId: string): void {
    this._teamsService.getRuns()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((runs: Run[]) => {
        if (runs && runs.length > 0) {
          const latest = runs.find(x => x.id === runId);
          if (!latest) {
            console.error('run not found! invalid ID passed via websocket!')
            return;
          }
          this.type = latest.isTestRun ? 'Test' : 'Judged';
          this.problem = latest.problem;
          this.judgement = latest.judgement;
          this.isPreliminary = latest.isPreliminary;
        }
      });
  }
}
