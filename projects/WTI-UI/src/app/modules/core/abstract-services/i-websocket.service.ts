import { Injectable } from '@angular/core';
import { UiHelperService } from '../services/ui-helper.service';
import { WebsocketMessage } from '../models/websocket-message';
import { IContestService } from './i-contest.service';
import { ITeamsService } from './i-teams.service';
import { AuthService } from '../auth/auth.service';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export abstract class IWebsocketService {
  constructor(private _uiHelperService: UiHelperService,
              private _contestService: IContestService,
              private _teamsService: ITeamsService,
              public _authService: AuthService) { 
		  if (DEBUG_MODE) {
			  console.log("Executing IWebsocketService constructor...");
		  }
	}

  abstract startWebsocket(): void;

  abstract stopWebsocket(): void;

  incomingMessage(message: WebsocketMessage) {
    switch (message.type) {
      case 'test':
      case 'judged': {
    	  if (DEBUG_MODE) {
    		  console.log ("Got '", message.type, "' websocket message in IWebsocketService.incomingMessage()");
    	  }
        this._uiHelperService.incomingRun(message.id);
        this._teamsService.runsUpdated.next();
        break;
      }
      case 'refresh_runs_list': {
        this._teamsService.runsUpdated.next();
        break;
      }
      case 'clarification': {
    	  if (DEBUG_MODE) {
    		  console.log ("Got '", message.type, "' websocket message in IWebsocketService.incomingMessage()");
    	  }
        this._uiHelperService.incomingClarification(message.id);
        this._contestService.clarificationsUpdated.next();
        break;
      }
      case 'refresh_clarification_list': {
        this._contestService.clarificationsUpdated.next();
        break;
      }
      case 'announcement': {
        this._uiHelperService.incomingAnnouncement(message.id);
        this._contestService.clarificationsUpdated.next();
        break;
      }
      case 'contest_clock': {
    	  /* This case is invoked when the WTI Server gets a "Contest_Clock" message from the PC2 Server
    	   * (via the PC2 API "ConfigurationItemUpdated()" listener in the WTI class "ConfigurationService").
    	   * Such a message from the PC2 Server causes the WTI Server to send a "contest_clock" message through
    	   * the websocket to this client. 
    	   */
    	  if (DEBUG_MODE) {
    		  console.log ("IWebsocketService.incomingMessage(): got websocket message '", message.type);
              console.log ("IWebsocketService.incomingMessage(): invoking ContestService.getIsContestRunning() and");
              console.log ("  subscribing to callback from HTTP GET");
    	  }
        this._contestService.getIsContestRunning()
          .subscribe((val: any) => {
        	  if (DEBUG_MODE) {
        		  console.log ("IWebsocketService.incomingMessage(): callback from ContestService.getIsContestRunning() returned '", val, "'");
        		  console.log ("Setting ContestService.isContestRunning to '", val, "',") ;
        		  console.log ("  invoking ContestService.contestClockEvent.next()") ;
                  console.log ("  and invoking ContestService.updateLocalContestClockFromServer()");
        	  }

            this._contestService.isContestRunning = val;
            this._contestService.contestClockEvent.next();
			this._contestService.updateLocalContestClockFromServer();
          });
        break;
      }
      case 'standings': {
        if (DEBUG_MODE) {
        	console.log("Got a 'Standings' websocket message; marking standings out of date");
        }
		this._contestService.markStandingsOutOfDate();
        break;
      }
      case 'connection_dropped': {
    	  if (DEBUG_MODE) {
    	      console.log("Got a connection_dropped websocket message:");
    	      console.log(message);
    	      console.log ("Invoking UIHelperService.indefinitelyDisplayedAlert('connection lost'") ;
    	      console.log ("  and invoking AuthService.logout() then AuthService.completeLogout()") ;
    	  }
          this._uiHelperService.indefinitelyDisplayedAlert("PC2 Server connection lost");
          this._authService.logout();  			//invokes teamsService.logout();
          this._authService.completeLogout();		//navigates to login page
          break;
      }
      default:
        console.warn('unrecognized message on websocket:');
        console.warn(message);
    }
  }
}
