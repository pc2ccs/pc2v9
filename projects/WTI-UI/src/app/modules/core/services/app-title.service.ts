import { Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AuthService } from 'src/app/modules/core/auth/auth.service';

@Injectable()
export class AppTitleService {

    constructor(private _titleService: Title, private _authService: AuthService) { }

	//return the current browser tab title
    getTitle() {
        this._titleService.getTitle();
    }

	//set a new title in the browser tab
    setTitle(newTitle: string) {
        this._titleService.setTitle(newTitle);
    }

	//set a new title in the browser tab, including the logged-in teamId in the title
	setTitleWithTeamId(newTitle: string) {
	    let acctId = this._authService.username; 
		let teamId = "";
		//make sure we got a reasonable string back from AuthService
    	if (!(acctId==null) && (typeof acctId === "string" && acctId.length>4)) {
			//pull the team number out of the PC2 account (e.g. pull "22" out of account "team22")
     		teamId = acctId.substr(4);
		}
    	this.setTitle("PC2 Team " + teamId + " " + newTitle);
	}
}