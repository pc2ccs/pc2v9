import { Injectable, Injector } from '@angular/core';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import { UiHelperService } from './ui-helper.service';
import { IContestService } from '../abstract-services/i-contest.service';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { AuthService } from '../auth/auth.service';
import { DEBUG_MODE } from 'src/constants';

@Injectable()
export class WebsocketMockService extends IWebsocketService {
	
  constructor(private _injector: Injector) {
    // Manually get UiHelperService from angular DI to pass to abstract class
    // This avoids having two references to UiHelperService
    super(_injector.get(UiHelperService), _injector.get(IContestService), _injector.get(ITeamsService), _injector.get(AuthService));
    if (DEBUG_MODE) {
    	console.log('Executing WebsocketMockService constructor');
    }
  }

  startWebsocket(): void {
    // no need for a socket here.... this is a mock
    if (DEBUG_MODE) {
    	console.log('[Websocket] Start websocket called!');
    }
  }

  stopWebsocket(): void {
    // no need for a socket here.... this is a mock
    if (DEBUG_MODE) {
    	console.log('[Websocket] Stop websocket called!');
    }
  }
}
