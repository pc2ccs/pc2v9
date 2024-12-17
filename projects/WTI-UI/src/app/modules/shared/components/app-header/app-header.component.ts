import { Component } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { ElapsedTimePipe } from 'src/app/modules/core/services/elapsedTimePipe.service';

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
  
  elapsedSecs = 0 ; 
  
  getElapsedTimeAsDate(): Date {
	  let newDate = new Date(this.elapsedSecs * 1000);
	  console.log(newDate.toString());
	  return newDate ;
  }


  constructor(private _authService: AuthService) {
	  
	  setInterval(
			//execute this function at the specified interval:
			() => {
				//add 1 second to the counter since 1000msec have passed
	    		this.elapsedSecs += 1 ;
	    		console.log(`Seconds: ${this.elapsedSecs}`);
	  		}, 
			1000	// 1000 milliseconds = 1 second interval
		); 
	  
  }
}
