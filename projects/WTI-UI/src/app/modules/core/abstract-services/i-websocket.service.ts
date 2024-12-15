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
      case 'clarification': {
    	  if (DEBUG_MODE) {
    		  console.log ("Got '", message.type, "' websocket message in IWebsocketService.incomingMessage()");
    	  }
        this._uiHelperService.incomingClarification(message.id);
        this._contestService.clarificationsUpdated.next();
        break;
      }
      case 'contest_clock': {
    	  if (DEBUG_MODE) {
    		  console.log ("Got '", message.type, "' websocket message in IWebsocketService.incomingMessage()");
    	  }
        this._contestService.getIsContestRunning()
          .subscribe((val: any) => {
        	  if (DEBUG_MODE) {
        		  console.log ("IWebsocketService.incomingMessage(): callback from ContestService.getIsContestRunning() returned '", val, "'");
        		  console.log ("Setting ContestService.isContestRunning to '", val, "'") ;
        		  console.log ("  and invoking ContestService.contestClock.next()") ;
        	  }

            this._contestService.isContestRunning = val;
            this._contestService.contestClock.next();
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
