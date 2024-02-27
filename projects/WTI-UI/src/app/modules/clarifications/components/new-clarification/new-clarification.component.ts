import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NewClarification } from 'src/app/modules/core/models/new-clarification';
import { takeUntil } from 'rxjs/operators';
import { MatDialogRef } from '@angular/material/dialog';
import { ITeamsService } from 'src/app/modules/core/abstract-services/i-teams.service';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service';

@Component({
  selector: 'app-new-clarification',
  templateUrl: './new-clarification.component.html',
  styleUrls: ['./new-clarification.component.scss']
})
export class NewClarificationComponent implements OnInit, OnDestroy {
  private _unsubscribe = new Subject<void>();
  newClarificationForm: FormGroup;

  constructor(private _teamService: ITeamsService,
              private _contestService: IContestService,
              private _formBuilder: FormBuilder,
              private _matDialogRef: MatDialogRef<NewClarificationComponent>,
              private _uiHelper: UiHelperService) { }

  ngOnInit() {
    this.buildForm();
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

  submitNewClarification(): void {
    const newClarification = new NewClarification();
    newClarification.message = this.newClarificationForm.get('content').value;
    newClarification.probName = this.newClarificationForm.get('problem').value;

    this._teamService.postClarification(newClarification)
      .pipe(takeUntil(this._unsubscribe))
      .subscribe(_ => {
        this.newClarificationForm.reset();
        this.close();
        this._contestService.clarificationsUpdated.next();
        this._uiHelper.alertOk('Clarification has been submitted successfully!');
      }, (error: any) => {
        console.error('error submitting new clarification');
        console.error(error);
      });
  }

  close(): void {
    this._matDialogRef.close();
  }

  private buildForm(): void {
    this.newClarificationForm = this._formBuilder.group({
      problem: [undefined, []],
      content: [undefined, [Validators.required]]
    });
  }
}
