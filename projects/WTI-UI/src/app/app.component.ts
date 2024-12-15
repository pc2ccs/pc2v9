import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import * as Constants from 'src/constants';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/modules/core/auth/auth.service' ;
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service' ;
import { IWebsocketService } from 'src/app/modules/core/abstract-services/i-websocket.service' ;
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service'
import { DEBUG_MODE } from 'src/constants';

/*
This AppComponent class is the main starting point for the WTI-UI Angular Single-Page-Application (SPA).  
(The overall SPA starts in main.ts, which invokes app.module.ts, which in turn bootstraps this AppComponent class.)

When app.module.ts invokes this class's constructor, the constructor parameters cause TypeScript to automatically construct
local property variables (objects) of type HttpClient, Router, AuthService, IContestService, and IWebsocketService.
Construcing the AuthService object in turn causes creation of an ITeamsService object.

The AuthService, IContestService, ITeamsService, and IWebsocketService classes are all listed in the "providers" array
of class CoreModule (core.module.ts), which means that CoreModule is responsible for providing those service classes. 
All four service classes are marked as "injectable", which means they can be injected into other classes.  All four classes
are marked as "providedIn: 'root'" in their "@Injectable" decorator, which means they are all defined as singletons provided
by (injected by) the "root injector"

The latter three classes (IContestService, ITeamsService, and IWebsocketService) are listed in CoreModule with "provide" properties
indicating that they are to be "provided" by corresponding "factory methods" (also in CoreModule).  These factory methods choose 
between "real" and "mock" service providers depending on the value of an "environment flag" named "useMock" (see the files 
under "environments").

Once construction is complete, this AppComponent then starts running at its "ngOnInit()" method.  
ngOnInit() checks the browser's "sessionStorage" to see if there is a "current page" recorded there.  
If not, AppComponent invokes "loadEnvironment()" to load the SPA configuration from file "assets/appconfig.json".  

If an existing "current page" IS found in "sessionStorage", AppComponent interprets it as indicating that a "browser refresh"
(F5) has occurred.  In this case, AppComponent performs the same "loadEnvironment() operations, then loads additional state 
information (userName and token value) from "sessionStorage" and restores it into the AuthService class.  
It then startsa new WebSocket for communication with the WTI Server, checks the ContestService "isContestRunning" value
(using it to trigger updates to things like display of problem names), and finally uses the Router to navigate to the
previous SPA page.

Meanwhile, as part of the AppModule bootstrap process, module "AppRoutingModule" sets up a list of "available routes"
(that is, pages which the SPA knows how to transfer to), with the first (default) route being the LoginPageComponent.
This causes the SPA to display the LoginPageComponent, which builds a "submission form" for the user to enter 
team name and password.  When this form's "Submit" button is clicked, 
the LoginPageComponent.onSubmit() method invokes the AuthService's "login" method, which connects to
the WTI server and logs the user into the PC2 server, then (on successful login) uses the Router's 
path list to transfer to the "/runs" page.

*/

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
              private _contestService: IContestService,
              private _websocketService: IWebsocketService,
              private _uiHelperService: UiHelperService) { 
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
						console.log (" Subscription callback from ContestService.getIsContestRunning() returned: ", val);
						console.log (" ContestService object has uniqueId", this._contestService.uniqueId);
						//JSON.stringify() can't handle recursive objects like Angular Subjects
						//console.log (JSON.stringify(this._contestService,null,2)); //obj, replacerFunction, indent
						console.log ("   and contents:");
						console.log (this._contestService);
					}
					this._contestService.isContestRunning = val;
					this._contestService.contestClock.next();
				});

        //get the most recently saved Option values from sessionStorage
        let options = getOptions();
        
        //if options were obtained, store them back into the OptionsPageComponent radio buttons
        if (!!options) {
        	if (DEBUG_MODE) {
        		console.log ("Calling 'restoreOptions' to restore Options Page settings; values retrieved from sessionStorage are:") ;
        		console.log ("  ClarsNotificationsEnabled option = ", options.clarsNotificationsEnabled);
        		console.log ("  RunsNotificationsEnabled option = ", options.runsNotificationsEnabled);
        	}
        	this.restoreOptions (options);
        	
        } else {
        	if (DEBUG_MODE) {
        		console.log ("Got null options from sessionStorage; nothing to restore for Options page.") 
        	}
        }
        
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

  /**
   * Accept the received Options object and install the values therein into the OptionsPage Clarifications and Runs radio button options.
   */
  restoreOptions(options: any) {
	  if (DEBUG_MODE) {
		  console.log ("Executing AppComponent.restoreOptions()...");
		  console.log ("...received option values are: 'clarsNoficationsEnabled='", options.clarsNotificationsEnabled);
		  console.log ("...and 'runsNotificationsEnabled='", options.runsNotificationsEnabled);
	  }
	  this._uiHelperService.enableClarificationNotifications = options.clarsNotificationsEnabled;
	  this._uiHelperService.enableRunsNotifications = options.runsNotificationsEnabled;

  }
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
  
  //save the current options in sessionStorage so a subsequent F5 (refresh) can restore it
  export function saveOptions(options: Object) {
      return (sessionStorage.setItem(Constants.OPTIONS_DETAILS_KEY, JSON.stringify(options)));
  }
  
  //return the most recently saved option values from sessionStorage
  export function getOptions() {
      const value = sessionStorage.getItem(Constants.OPTIONS_DETAILS_KEY);
      return value ? JSON.parse(value) : null;
  }
  
  //clear all data in sessionStorage
  export function clearSessionStorage() {
      sessionStorage.clear();
  }
