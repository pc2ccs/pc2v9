import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewClarificationAlertComponent } from '../../clarifications/components/new-clarification-alert/new-clarification-alert.component';
import { NewRunAlertComponent } from '../../runs/components/new-run-alert/new-run-alert.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class UiHelperService {
  enableClarificationNotifications = true;
  enableRunsNotifications = true;

  constructor(private _dialogService: MatDialog,
              private _matSnackBar: MatSnackBar) { 
	  if (DEBUG_MODE) {
		  console.log ("Executing UiHelperService constructor.") ;
	  }
  }

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

  alertOk(message: string): void {
    this._matSnackBar.open(message, 'Close', {
      duration: undefined,   //no automatic dismissal; user must close
	  panelClass: 'green-snackbar'
    });
  }

  alertError(message: string): void {
    this._matSnackBar.open(message, 'Close', {
      duration: undefined,   //no automatic dismissal; user must close
	  panelClass: 'red-snackbar'
    });
  }

  indefinitelyDisplayedAlert(message: string): void {
	this._matSnackBar.open(message, 'Close', {
	  duration: undefined
	});
  }
}
