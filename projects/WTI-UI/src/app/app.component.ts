import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import * as Constants from 'src/constants';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/modules/core/auth/auth.service' ;
import { ContestService } from 'src/app/modules/core/services/contest.service' ;
import { WebsocketService } from 'src/app/modules/core/services/websocket.service' ;
import { DEBUG_MODE } from 'src/constants';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit {
  configLoaded = false;

  constructor(private _httpClient: HttpClient, 
		      public router: Router, 
              private _authService: AuthService, 
              private _contestService: ContestService,
              private _websocketService: WebsocketService) { 
	  if (DEBUG_MODE) {
		  console.log("Executing AppComponent constructor...");
	  }
  	//this.router.events.subscribe(console.log); //shows router tracing on console
  }

  ngOnInit(): void {
  
    if (DEBUG_MODE) {
	    console.log ("Entering AppComponent.ngOnInit()...");
	    console.log ("...sessionStorage.length = " + sessionStorage.length);
	    if (sessionStorage.length > 0) {
	    	console.log ("...sessionStorage values:");
	    	for (let i = 0; i < sessionStorage.length; i++) {
	    		  let key = sessionStorage.key(i);
	    		  let value = sessionStorage.getItem(key);
	    		  console.log("      Key: ", key, " Value: ", value);
	    	}
	    }
    }

    //check if we're loading for the first time
    if (!getCurrentPage()) {
        if (DEBUG_MODE) {
        	console.log ('Starting Single-Page-Application from scratch...');
        }
        //we have no current page so we must be starting from scratch;
        // the following matches the sum total of what ngOnInit() used to do before "F5 handling" was added -- jlc
        this.loadEnvironment();
        
	} else {
	    //there is a current page stored; we must be reloading from (e.g.) an F5 refresh
	    if (DEBUG_MODE) {
	    	console.log ('Restarting Single-Page-Application after refresh navigation...');
	    }
	    
        //restore former environment
        if (DEBUG_MODE) {
        	console.log ("...Loading environment...") ;
        }
        this.loadEnvironment();

		//The following was initially done by the login-page component's onSubmit() method during login;
		// this F5 "restore state" code needs to accomplish the equivalent:
		//this._authService.login(loginCreds)
		//  .pipe(takeUntil(this._unsubscribe))
		//  .subscribe((result: TeamsLoginResponse) => {
        //    this._authService.completeLogin(result.teamId, result.teamName);  //save 'teamId' returned from HTTP login  "token"
        //    this._websocketService.startWebsocket();							//create a websocket connection to the WTI Server
        //    this._contestService.getIsContestRunning()				//get contest isRunning state and save in ContestService
        //      .subscribe((val: boolean) => {
        //        this._contestService.isContestRunning = val;
        //        this._contestService.contestClock.next();
        //      });
        //  }

        //restore the connection token and username into the AuthService from browser sessionStorage
        let token = getCurrentToken();
        let username = getCurrentUserName();
        if (DEBUG_MODE) {
        	if (!!token && !!username) {
        		console.log("...restoring token '" + token + "' and username '" + username + "' into AuthService..." );
        	}
        }

        //check whether we found non-null token and username from sessionStorage
        if (! (!!token && !!username) ) {
        	console.warn ("Attempting to reload after F5 restart but found invalid state: token = ", token, " and username = ", username, " !!") ;
        	//TODO: what should happen if the above occurs??
        } else {
			//put the token and username back into AuthService (accomplishing what was originally done
			// by AuthService.completeLogin(teamId, teamName); teamId is the "token")
        	this._authService.token = token;
        	this._authService.username = username;
        }
        
        //re-create websocket connection to the WTI Server
        if (DEBUG_MODE) {
			console.log ("...calling WebsocketService.startWebsocket()...");
		}
        this._websocketService.startWebsocket();
        
        //update the "is contest running" value in the IContestService object using the value returned from
        // an HTTP call within ContestService.getIsContestRunning() 
         this._contestService.getIsContestRunning()
              .subscribe((val: boolean) => {
					if (DEBUG_MODE) {
						console.log("AppComponent F5 'restore-state' code: ");
						console.log (" Subscription callback from ContestService.getIsContestRunning() returned: ", val);
						console.log (" ContestService object is:")
						console.log (this._contestService);
					}
					this._contestService.isContestRunning = val;
					this._contestService.contestClock.next();
				});

	    // transfer to the (former) "current page".
	    let page = getCurrentPage();
	    if (DEBUG_MODE) {
	    	console.log ('...navigating to previous page: ' + page);
	    }
	    
	    //navigate to the most recently saved page
	    // TODO:  consider whether using history.pushState()/popState() is a better solution for this...
	    this.router.navigate([page])
	      .then(nav => {
	         if (DEBUG_MODE) {
	        	 console.log ("...navigation complete.");
	         }
	      }, err => {
	         console.log("Call to router.navigate([" + page + "]) failed (returned '" + err + "')");
	      });
	    
	}
	
  }

  // Load appconfig.json from assets directory, overwrite environment.ts with these values
  loadEnvironment(): void {
	if (DEBUG_MODE) {
		console.log("...loading environment from 'assets/appconfig.json'...");
	}
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

  //save the current page in sessionStorage so a subsequent F5 (refresh) can return to that page
  export function saveCurrentPage(page:string) {
      sessionStorage.setItem(Constants.CURRENT_PAGE_KEY, page) ;
  }
  
  //return the most recently saved page
  export function getCurrentPage() {
      return (sessionStorage.getItem(Constants.CURRENT_PAGE_KEY));
  }
  
  //clear any record of a "current page"
  export function clearCurrentPage() {
      sessionStorage.removeItem(Constants.CURRENT_PAGE_KEY);
  }
  
  //save the current token in sessionStorage so a subsequent F5 (refresh) can restore it
  export function saveCurrentToken(token:string) {
      sessionStorage.setItem(Constants.CONNECTION_TOKEN_KEY, token) ;
  }
  
  //return the most recently saved token
  export function getCurrentToken() {
      return (sessionStorage.getItem(Constants.CONNECTION_TOKEN_KEY));
  }
  
  //save the current username in sessionStorage so a subsequent F5 (refresh) can restore it
  export function saveCurrentUserName(username:string) {
      sessionStorage.setItem(Constants.CONNECTION_USERNAME_KEY, username) ;
  }
  
  //return the most recently saved username
  export function getCurrentUserName() {
      return (sessionStorage.getItem(Constants.CONNECTION_USERNAME_KEY));
  }
  
  //clear all data in sessionStorage
  export function clearSessionStorage() {
      sessionStorage.clear();
  }
