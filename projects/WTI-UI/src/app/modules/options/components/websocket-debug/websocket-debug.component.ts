import { Component, OnInit } from '@angular/core';
import { IWebsocketService } from 'src/app/modules/core/abstract-services/i-websocket.service';
import { WebsocketMessage } from '../../../core/models/websocket-message';

@Component({
  selector: 'app-websocket-debug',
  templateUrl: './websocket-debug.component.html',
  styleUrls: ['./websocket-debug.component.scss']
})
export class WebsocketDebugComponent implements OnInit {

  constructor(private _websocketService: IWebsocketService) { }

  ngOnInit() {
  }

  newClarification() {
    const mockedClar: WebsocketMessage =  { type: 'clarification', id: '1-3' };
    this._websocketService.incomingMessage(mockedClar);
  }

  newJudgedRun() {
    const mockedRun: WebsocketMessage =  { type: 'judged', id: '1-1' };
    this._websocketService.incomingMessage(mockedRun);
  }

  newTestRun() {
    const mockedRun: WebsocketMessage =  { type: 'judged', id: '3-1' };
    this._websocketService.incomingMessage(mockedRun);
  }

}
