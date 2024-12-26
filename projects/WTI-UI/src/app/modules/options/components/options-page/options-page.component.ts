//options-page.component.ts

import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordComponent } from '../change-password/change-password.component';
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service';
import { environment } from 'src/environments/environment';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { saveCurrentPage } from 'src/app/app.component';
import { saveOptions } from 'src/app/app.component';
import * as Constants from 'src/constants';
import { DEBUG_MODE } from 'src/constants';

@Component({
  templateUrl: './options-page.component.html',
  styleUrls: ['./options-page.component.scss']
})
export class OptionsPageComponent implements OnInit {
  get clarsNotificationsEnabled(): boolean { return this._uiHelperService.enableClarificationNotifications; }
  set clarsNotificationsEnabled(newval: boolean) { this._uiHelperService.enableClarificationNotifications = newval; }
  get runsNotificationsEnabled(): boolean { return this._uiHelperService.enableRunsNotifications; }
  set runsNotificationsEnabled(newval: boolean) { this._uiHelperService.enableRunsNotifications = newval; }

  constructor(private _dialogSvc: MatDialog,
              private _uiHelperService: UiHelperService,
			  private _appTitleService: AppTitleService) { }

  ngOnInit(): void {
	  if (DEBUG_MODE) {
		  console.log ("Executing OptionsPageComponent.ngOnInit()...");
	  }
	this._appTitleService.setTitleWithTeamId("Options");

	//indicate that this Options page is the most recently accessed page
	if (DEBUG_MODE) {
		console.log ("...saving OPTIONS_PAGE as 'current page'");
	}
	saveCurrentPage(Constants.OPTIONS_PAGE);
	
	//save the current option values in sessionStorage so they can be restored on F5 refresh
	let options = {
		//TODO: the following field names (on the left) should use the names defined in constants.ts
		clarsNotificationsEnabled : this.clarsNotificationsEnabled, 
		runsNotificationsEnabled: this.runsNotificationsEnabled
	}
	if (DEBUG_MODE) {
		console.log ("...saving options in sessionStorage: clarsNotificationsEnabled = ", options.clarsNotificationsEnabled, 
				"; runsNotificationsEnabled = ", options.runsNotificationsEnabled );
	}
	saveOptions(options) ;
	
	if (DEBUG_MODE) {
		console.log ("OptionsPageComponent.ngOnInit() finished.")
	}
  }

  showWebsocketDebug(): boolean {
    return !!environment.useMock;
  }

  openChangePW(): void {
    this._dialogSvc.open(ChangePasswordComponent);
  }
  
  onRadioButtonChange(event: any) {
	  if (DEBUG_MODE) {
		  console.log ("Executing OptionsPageComponent.onRadioButtonClick()...");
	  }
	  const name = event.target.name;
	  const value = event.target.value;
	  if (DEBUG_MODE) {
		  console.log ("...button name = '", name, "', button value = ", value) ;
	  }
		let options = {
			//TODO: the following field names (on the left) should use the names defined in constants.ts
			clarsNotificationsEnabled : this.clarsNotificationsEnabled, 
			runsNotificationsEnabled: this.runsNotificationsEnabled
		}
		if (DEBUG_MODE) {
			console.log ("Saving options to sessionStorage:");
			console.log ("...Clar popups enabled = ", options.clarsNotificationsEnabled);
			console.log ("...Runs popups enabled = ", options.runsNotificationsEnabled);
		}
		saveOptions(options);

  }
}
