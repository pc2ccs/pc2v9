import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { IContestService } from '../abstract-services/i-contest.service';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import { UiHelperService } from '../services/ui-helper.service';
import * as Constants from 'src/constants';
import { getCurrentToken, getCurrentUserName, loadOptions } from './session-storage.utils';
import { DEFAULT_SHOW_CLARS_POPUP, DEFAULT_SHOW_RUNS_POPUP } from 'src/constants';

/**
This service class houses initialization for the WTI-UI application.  
Specifically, when the Angular bootstrap process invokes this class's constructor, 
the constructor parameters cause TypeScript to automatically construct
local property variables (objects) of type HttpClient, AuthService, IContestService, and IWebsocketService.
Constructing the AuthService object in turn causes creation of an ITeamsService object.

The AuthService, IContestService, ITeamsService, and IWebsocketService classes are all listed in the "providers" array
of class CoreModule (core.module.ts), which means that CoreModule is responsible for providing those service classes. 
All four service classes are marked as "injectable", which means they can be injected into other classes.  All four classes
are marked as "providedIn: 'root'" in their "@Injectable" decorator, which means they are all defined as singletons provided
by (injected by) the "root injector"

The latter three classes (IContestService, ITeamsService, and IWebsocketService) are listed in CoreModule with "provide" properties
indicating that they are to be "provided" by corresponding "factory methods" (also in CoreModule).  These factory methods choose 
between "real" and "mock" service providers depending on the value of an "environment flag" named "useMock" (see the files 
under "environments").

Subsequently, the Angular bootstrap process invokes AppModule (in app.module.ts) which in turn invokes this class's 
initializeApp() method.  Angular arranges that initializeApp() runs to completion prior to loading the main "AppComponent"
class (defined in app.component.ts).  This sequence happens regardless of whether the application is being run for the
first time or being re-run due to a browser "F5" (Refresh) operation.  This is what is known as the Angular
APP_INITIALIZER process, and is what guarantees that the WTI-UI application is fully and correctly initialized
before the actual rendered components (AppComponent and its children) are rendered.

initializeApp() first makes a request to the WTI Server for "environment data" (specifically, the server's 
URL and WebsocketURL information).  It waits until this data is received, then saves it in the global
"environment" variable.  It then checks to see if sessionStorage contains user login information (a token
and a username).  If so, it is an indication that the application must be restarting following an F5 (Refresh);
in that case it restores the login information into the AuthService class, starts a new WebSocket for 
communication with the WTI Server (the old one having been destroyed when the F5 occurred), 
checks the ContestService "isContestRunning" value (using it to trigger updates to things like 
display of problem names). initializeApp() also checks SessionStorage for any previously-stored
WTI options; if present in SessionStorage then those settings are restored; if not present then 
Options are set to their default values.  Lastly, initalizeApp() checks if there is a logged-in 
user and if so it starts (or restarts) the contest countup/down clocks.

Subsequently, as part of the AppModule bootstrap process, module "AppRoutingModule" sets up a list of "available routes"
(that is, pages which the SPA knows how to transfer to), with the first (default) route being the LoginPageComponent.
This initially causes the SPA to display the LoginPageComponent, which builds a "submission form" for the user to enter 
team name and password.  When this form's "Submit" button is clicked, 
the LoginPageComponent.onSubmit() method invokes the AuthService's "login" method, which connects to
the WTI server and logs the user into the PC2 server, then (on successful login) uses the Router's 
path list to transfer to the "/runs" page.  On the other hand, if the SPA is restarting due to a previous
F5, the router will transfer to whatever URL appears in the browser's address bar -- thus automatically
returning to whatever page was active before the F5 occurred.

(Note that an older version of the application, which did not use the Angular APP_INITIALIZER mechansim,
instead relied on keeping track of the "current page" by storing it in sessionStorage; using the
APP_INITIALIZER structure eliminated the need to do this.  Some old cruft code doing that probably
still remains but is no longer used...)

*/

@Injectable({
	providedIn: 'root'
})
export class AppInitService {
	constructor(
		private http: HttpClient,
		private authService: AuthService,
		private contestService: IContestService,
		private websocketService: IWebsocketService,
		private uiHelperService: UiHelperService
	) { }

	async initializeApp(): Promise<void> {
		
		//load the initial environment (e.g. the webserver URL and the websocketURL) from 
		// the assets given in the remote appconfig.json file
		console.log('[AppInit] Loading environment...');
		try {
			const data: any = await firstValueFrom(this.http.get('assets/appconfig.json'));
			if (data) {
				Object.keys(data).forEach(key => environment[key] = data[key]);
			}
			console.log('[AppInit] Environment loaded:', environment);
		} catch (err) {
			console.warn('[AppInit] Failed to load environment, using defaults.');
		}

		//initialize the scoreboard type (e.g. pass-fail or point-scoring) by invoking the "ContestService",
		// which will in turn obtain it from the webserver if it doesn't already have it.
		console.log('[AppInit] Initializing ScoreboardType...');
		try {
			await this.contestService.initializeScoreboardType();
			console.log('[AppInit] ScoreboardType initialized to: ', this.contestService.getScoreboardType());
		} catch (err) {
			console.error('[AppInit] Failed to initialize ScoreboardType', err);
			throw err; // stop bootstrap if required
		}


		// Restore login state if present in sessionStorage (sessionStorage is a variable declared 
		// by the browser and accessed via functions declared in session-storage.utils)

		const token = getCurrentToken();
		const username = getCurrentUserName();

		if (token && username) {
			this.authService.token = token;
			this.authService.username = username;
			console.log(`[AppInit] Auth restored for ${username}`);

			this.websocketService.startWebsocket();

			// Wait for contest state before letting components render
			try {
				const isRunning = await firstValueFrom(this.contestService.getIsContestRunning());
				this.contestService.isContestRunning = isRunning;
				this.contestService.contestClockEvent.next();
				console.log('[AppInit] Contest state initialized');
			} catch (err) {
				console.warn('[AppInit] Failed to initialize contest state', err);
			}
		}

		//force clocks to start running if user is logged in
		if (this.authService.isLoggedIn) {
			this.contestService.updateLocalContestClockFromServer();
			console.log('[AppInit] Contest clock restore triggered');
		}

		//restore Options from sessionStorage if present; otherwise, set to defaults.
		const storedOptions = loadOptions();

		if (storedOptions) {
			if (typeof storedOptions.clarsNotificationsEnabled === 'boolean') {
				this.uiHelperService.enableClarificationNotifications =
					storedOptions.clarsNotificationsEnabled;
			}

			if (typeof storedOptions.runsNotificationsEnabled === 'boolean') {
				this.uiHelperService.enableRunsNotifications =
					storedOptions.runsNotificationsEnabled;
			}

			console.log('[AppInit] UI options restored from sessionStorage:', storedOptions);
		} else {
			// Explicitly apply defaults
			this.uiHelperService.enableClarificationNotifications = DEFAULT_SHOW_CLARS_POPUP;
			this.uiHelperService.enableRunsNotifications = DEFAULT_SHOW_RUNS_POPUP;

			console.log('[AppInit] No stored UI options found; defaults applied');
		}
		
	}//end async initializeApp()
}
