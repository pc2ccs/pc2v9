import { UiHelperService } from '../services/ui-helper.service';
import { WebsocketMessage } from '../models/websocket-message';
import { IContestService } from './i-contest.service';
import { ITeamsService } from './i-teams.service';

export abstract class IWebsocketService {
  constructor(private _uiHelperService: UiHelperService,
              private _contestService: IContestService,
              private _teamsService: ITeamsService) { }

  abstract startWebsocket(): void;

  abstract stopWebsocket(): void;

  incomingMessage(message: WebsocketMessage) {
    switch (message.type) {
      case 'test':
      case 'judged': {
        this._uiHelperService.incomingRun(message.id);
        this._teamsService.runsUpdated.next();
        break;
      }
      case 'clarification': {
        this._uiHelperService.incomingClarification(message.id);
        this._contestService.clarificationsUpdated.next();
        break;
      }
      case 'contest_clock': {
        this._contestService.getIsContestRunning()
          .subscribe((val: any) => {
            this._contestService.isContestRunning = val;
            this._contestService.contestClock.next();
          });
        break;
      }
      case 'standings': {
        console.log("got a Standings websocket message; marking standings out of date");
		this._contestService.markStandingsOutOfDate();
        break;
      }
      default:
        console.warn('unrecognized message on websocket');
        console.warn(message);
    }
  }
}
