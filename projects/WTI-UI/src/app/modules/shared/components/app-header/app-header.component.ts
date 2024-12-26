import { Component } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { DEBUG_MODE } from 'src/constants'

@Component({
    selector: 'app-header',
    templateUrl: './app-header.component.html',
    styleUrls: ['./app-header.component.scss']
})
export class AppHeaderComponent {

  //Return a boolean indicating whether or not to show nav-bar links in the header
  get showLinks(): boolean { return this._authService.isLoggedIn; }
  
  //Return a boolean indicating whether or not to show a teamId in the header
  get showTeamId(): boolean { return this._authService.isLoggedIn; }
  
  //Return a boolean indicating whether or not to show the contest clock(s) in the header
  get showClocks(): boolean { return this._authService.isLoggedIn; }
  
  /* Return a string containing the "team id" -- that is, the PC2 team account number with
     the leading "team" removed */
  get teamId(): string { 
    let acctId = this._authService.username; 
    let teamId = acctId.substr(4);
    return teamId;
  }
  
  constructor(private _authService: AuthService, private _contestService: IContestService) {
	  if (DEBUG_MODE ) {
		console.log ("Executing AppHeaderComponent constructor")
	  }
  }


  getElapsedSecs(): number {
	let secs = this._contestService.getElapsedSecs() ;
	return secs;
  }

  getRemainingSecs(): number {
	let secs = this._contestService.getRemainingSecs() ;
	return secs;
  }
}
