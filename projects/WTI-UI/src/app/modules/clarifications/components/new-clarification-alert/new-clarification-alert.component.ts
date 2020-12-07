import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { Subject } from 'rxjs';
import { Clarification } from '../../../core/models/clarification';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-new-clarification-alert',
  templateUrl: './new-clarification-alert.component.html',
  styleUrls: ['./new-clarification-alert.component.scss']
})
export class NewClarificationAlertComponent implements OnInit {
  private _unsubscribe = new Subject<void>();
  problem: string;
  question: string;
  answer: string;

  constructor(private _matDialogRef: MatDialogRef<NewClarificationAlertComponent>,
              private _router: Router,
              private _contestService: IContestService,
              @Inject(MAT_DIALOG_DATA) private _data: any) { }

  ngOnInit() {
    this.loadClarification(this._data.id);
  }

  goToClarifications(): void {
    this._router.navigateByUrl('/clarifications');
    this.close();
  }

  close(): void {
    this._matDialogRef.close();
  }

  private loadClarification(clarId: string): void {
    this._contestService.getClarifications()
      .pipe(takeUntil(this._unsubscribe))
      .subscribe((clars: Clarification[]) => {
        if (clars && clars.length > 0) {
          const clar = clars.find(x => x.id === clarId);
          if (!clar) {
            console.error('clarification not found! invalid ID passed via websocket');
            return;
          }
          this.problem = clar.problem;
          this.question = clar.question;
          this.answer = clar.answer;
        }
      });
  }
}
