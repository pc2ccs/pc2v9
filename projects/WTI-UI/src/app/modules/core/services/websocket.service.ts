import { Injectable, Injector } from '@angular/core';
import { environment } from 'src/environments/environment';
import { UiHelperService } from './ui-helper.service';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import { AuthService } from '../auth/auth.service';
import { WebsocketMessage } from '../models/websocket-message';
import { IContestService } from '../abstract-services/i-contest.service';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class WebsocketService extends IWebsocketService {
  socket: WebSocket;

  constructor(private _injector: Injector, _authService: AuthService) {
    // Manually get UiHelperService from angular DI to pass to abstract class
    // This avoids having two references to UiHelperService
    super(_injector.get(UiHelperService), _injector.get(IContestService), _injector.get(ITeamsService), _injector.get(AuthService));
    if (DEBUG_MODE) {
    	console.log('Executing WebsocketService constructor');
    }
  }

  startWebsocket(): void {
    if (DEBUG_MODE) {
    	console.log('Constructing websocket...');
    }
    this.socket = new WebSocket(`${environment.websocketUrl}/${this._authService.token}`);
    this.socket.addEventListener('message', this.handleIncomingMessage);
    if (DEBUG_MODE) {
    	console.log('...websocket URL =' + this.socket.url);
    }
  }

  stopWebsocket(): void {
    if (DEBUG_MODE) {
    	console.log('Closing websocket');
    }
    if (this.socket) {
      this.socket.close();
      this.socket = undefined;
    }
  }

  handleIncomingMessage = (event: MessageEvent) => {
    const parsedData: WebsocketMessage = JSON.parse(event.data);
    this.incomingMessage(parsedData);
  }
}
