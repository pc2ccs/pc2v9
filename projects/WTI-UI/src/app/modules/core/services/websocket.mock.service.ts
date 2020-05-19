import { Injectable, Injector } from '@angular/core';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import { UiHelperService } from './ui-helper.service';
import { IContestService } from '../abstract-services/i-contest.service';
import { ITeamsService } from '../abstract-services/i-teams.service';

@Injectable()
export class WebsocketMockService extends IWebsocketService {
  constructor(private _injector: Injector) {
    // Manually get UiHelperService from angular DI to pass to abstract class
    // This avoids having two references to UiHelperService
    super(_injector.get(UiHelperService), _injector.get(IContestService), _injector.get(ITeamsService));
    console.log('firing construcitor in websocket MOCK');
  }

  startWebsocket(): void {
    // no need for a socket here.... this is a mock
    console.log('[Websocket] Start websocket called!');
  }

  stopWebsocket(): void {
    // no need for a socket here.... this is a mock
    console.log('[Websocket] Stop websocket called!');
  }
}
