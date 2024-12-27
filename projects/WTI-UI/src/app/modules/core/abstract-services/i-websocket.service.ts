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
    		  console.log ("IWebsocketService.incomingMessage(): got websocket message '", message.type);
              console.log ("IWebsocketService.incomingMessage(): invoking ContestService.getIsContestRunning() and");
              console.log ("  subscribing to callback from HTTP GET");
    	  }
        this._contestService.getIsContestRunning()
          .subscribe((val: any) => {
        	  if (DEBUG_MODE) {
        		  console.log ("IWebsocketService.incomingMessage(): callback from ContestService.getIsContestRunning() returned '", val, "'");
        		  console.log ("Setting ContestService.isContestRunning to '", val, "'") ;
<<<<<<< Upstream, based on c4b09354e015df5ecedbdcd004dea9810eecc455
        		  console.log ("  and invoking ContestService.contestClock.next()") ;
=======
        		  console.log ("  and invoking ContestService.contestClockEvent.next()") ;
>>>>>>> 5291696 i1027: IWebSocketService: make root-injectable; add debug output.
        	  }

            this._contestService.isContestRunning = val;
            //TODO: need to force the ContestService to update the contest clock and set the ContestTimer as appropriate here.
            //  Possibly: subscribe somewhere to the ContestClockEvent Subject and implement a subscription callback that does this updating? 
            this._contestService.contestClockEvent.next();
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
