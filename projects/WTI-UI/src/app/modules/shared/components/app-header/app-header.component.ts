import { Component } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { environment } from 'src/environments/environment';
import { DEBUG_MODE } from 'src/constants'

/**
 * This class defines a component which acts as the "header" for all WTI-UI pages.  Together with the
 * corresponding app-header.component.html file, the component displays header images (such as the PC2 Balloon Logo).
 * If there is currently a team logged in, then the header component also shows
 * links to the various WTI-UI pages (such as Runs, Clarifications, Scoreboard, etc.), the currently logged-in team's ID,
 * and clocks showing the current contest elapsed time and remaining time (whose values it gets from the 
 * injected ContestService).
 * 
 */
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
    const acctId = this._authService.username; 
    const teamId = acctId.substr(4);
    return teamId;
  }
  
  constructor(private _authService: AuthService, private _contestService: IContestService) {
	  if (DEBUG_MODE ) {
		console.log ("Executing AppHeaderComponent constructor...")
        console.log ("...environment:") ;
		const environmentCopy = JSON.parse(JSON.stringify(environment));
		console.log(environmentCopy);
	  }
  }


  getElapsedSecs(): number {
	const secs = this._contestService.getElapsedSecs() ;
    return secs;
  }

  getRemainingSecs(): number {
	const secs = this._contestService.getRemainingSecs() ;
	return secs;
  }
}
