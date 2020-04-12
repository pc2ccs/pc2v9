import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { IWebsocketService } from 'src/app/modules/core/abstract-services/i-websocket.service';

@Component({
  templateUrl: './logout.component.html'
})
export class LogoutComponent implements OnInit {
  constructor(private _authService: AuthService,
              private _websocketService: IWebsocketService) { }

  ngOnInit(): void {
    this._authService.logout()
      .subscribe(_ => {
        this._websocketService.stopWebsocket();
        this._authService.completeLogout();
      }, (error: any) => {
        this._authService.completeLogout();
      });
  }
}
