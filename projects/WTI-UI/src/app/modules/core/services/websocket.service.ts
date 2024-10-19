import { Injectable, Injector } from '@angular/core';
import { environment } from 'src/environments/environment';
import { UiHelperService } from './ui-helper.service';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import { AuthService } from '../auth/auth.service';
import { WebsocketMessage } from '../models/websocket-message';
import { IContestService } from '../abstract-services/i-contest.service';
import { ITeamsService } from '../abstract-services/i-teams.service';

@Injectable()
export class WebsocketService extends IWebsocketService {
  socket: WebSocket;

  constructor(private _injector: Injector, _authService: AuthService) {
    // Manually get UiHelperService from angular DI to pass to abstract class
    // This avoids having two references to UiHelperService
    super(_injector.get(UiHelperService), _injector.get(IContestService), _injector.get(ITeamsService), _injector.get(AuthService));
    console.log('firing constructor in WebsocketService');
  }

  startWebsocket(): void {
    console.log('Constructing websocket...');
    this.socket = new WebSocket(`${environment.websocketUrl}/${this._authService.token}`);
    this.socket.addEventListener('message', this.handleIncomingMessage);
    console.log('...websocket URL =' + this.socket.url);
  }

  stopWebsocket(): void {
    console.log('Closing websocket');
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
