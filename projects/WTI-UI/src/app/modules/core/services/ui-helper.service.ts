import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewClarificationAlertComponent } from '../../clarifications/components/new-clarification-alert/new-clarification-alert.component';
import { NewRunAlertComponent } from '../../runs/components/new-run-alert/new-run-alert.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class UiHelperService {
  enableClarificationNotifications = true;
  enableRunsNotifications = true;

  constructor(private _dialogService: MatDialog,
              private _matSnackBar: MatSnackBar) { }

  incomingClarification(id: string): void {
    if (this.enableClarificationNotifications) {
      this._dialogService.open(NewClarificationAlertComponent, {
        data: { id }
      });
    }
  }

  incomingRun(id: string): void {
    if (this.enableRunsNotifications) {
      this._dialogService.open(NewRunAlertComponent, {
        data: { id }
      });
    }
  }

  alert(message: string): void {
    this._matSnackBar.open(message, 'Close', {
      duration: 4000
    });
  }

  indefinitelyDisplayedAlert(message: string): void {
	this._matSnackBar.open(message, 'Close', {
	  duration: undefined
	});
  }
}
