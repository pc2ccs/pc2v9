//options-page.component.ts

import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordComponent } from '../change-password/change-password.component';
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service';
import { environment } from 'src/environments/environment';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { saveCurrentPage } from 'src/app/app.component';
import * as Constants from 'src/constants';

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
	this._appTitleService.setTitleWithTeamId("Options");

	//indicate that this Options page is the most recently accessed page
	saveCurrentPage(Constants.OPTIONS_PAGE_KEY);
  }

  showWebsocketDebug(): boolean {
    return !!environment.useMock;
  }

  openChangePW(): void {
    this._dialogSvc.open(ChangePasswordComponent);
  }
}
