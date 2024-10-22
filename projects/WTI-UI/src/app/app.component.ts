import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import * as Constants from 'src/constants';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/modules/core/auth/auth.service' ;
import { ContestService } from 'src/app/modules/core/services/contest.service' ;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit {
  configLoaded = false;

  constructor(private _httpClient: HttpClient, public router: Router, 
    private _authService: AuthService, private _contestService: ContestService) { 
  	//this.router.events.subscribe(console.log); //shows router tracing on console
  }

  ngOnInit(): void {
  
    //debugging
    console.log ("Entering AppComponent.ngOnInit()...");
    console.log ("...localStorage.length = " + localStorage.length);

    //check if we're loading for the first time
    if (!getCurrentPage()) {
        //we have no current page so we must be starting from scratch
        console.log ('Starting Single-Page-Application from scratch...');
        this.loadEnvironment();
        
	} else {
	    //there is a current page stored; we must be reloading from (e.g.) an F5 refresh
	    console.log ('Restarting Single-Page-Application after refresh navigation');
	    
        //restore former environment
        this.loadEnvironment();

        //restore the connection token and username into the AuthService from browser localStorage
        let token = localStorage.getItem(Constants.CONNECTION_TOKEN_KEY);
        let username = localStorage.getItem(Constants.CONNECTION_USERNAME_KEY);
        console.log("Calling completeLogin() on AuthService to set token '" + token + "' and username '" + username + "'" );
        this._authService.completeLogin(token,username);

	    // transfer to the (former) "current page".
	    let page = getCurrentPage();
	    console.log ('Transferring to previous page: ' + page);
	    
	    //navigate to the most recently saved page
	    // TODO:  consider whether using history.pushState()/popState() is a better solution for this...
	    this.router.navigate([page])
	      .then(nav => {
	        
	      }, err => {
	         console.log("Call to router.navigate([" + page + "]) failed (returned '" + err + "')");
	      });
	    
	}
	
  }

  // Load appconfig.json from assets directory, overwrite environment.ts with these values
  loadEnvironment(): void {
	console.log("Loading environment from 'assets/appconfig.json'");
	this._httpClient.get('assets/appconfig.json')
	    .subscribe((data: any) => {
	        this.configLoaded = true;
	        if (!data) { return; }
	        Object.keys(data).forEach((key: string) => environment[key] = data[key]);
	    }, (error: any) => {
	        console.log('Could not find appconfig.json in assets directory. using default values!');
	        this.configLoaded = true;
	    });	
  }//end function loadEnvironment()

}//end class AppComponent

  //save the current page in localStorage so a subsequent F5 (refresh) can return to that page
  export function saveCurrentPage(page:string) {
      localStorage.setItem(Constants.CURRENT_PAGE_KEY, page) ;
  }
  
  //return the most recently saved page
  export function getCurrentPage() {
      return (localStorage.getItem(Constants.CURRENT_PAGE_KEY));
  }
  
  //clear any record of a "current page"
  export function clearCurrentPage() {
      localStorage.removeItem(Constants.CURRENT_PAGE_KEY);
  }
  
  //clear all data in localStorage
  export function clearLocalStorage() {
      localStorage.clear();
  }
