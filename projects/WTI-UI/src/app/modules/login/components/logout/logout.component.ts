import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { IWebsocketService } from 'src/app/modules/core/abstract-services/i-websocket.service';
import { DEBUG_MODE } from 'src/constants';

@Component({
  templateUrl: './logout.component.html'
})
export class LogoutComponent implements OnInit {
  constructor(private _authService: AuthService,
              private _websocketService: IWebsocketService) { }

  ngOnInit(): void {
    if (DEBUG_MODE) {
	    console.log("Executing logout.component.ngOnInit()");
	    console.log ("Calling AuthService.logout()");
    }
    this._authService.logout()
      .subscribe(_ => {
    	if (DEBUG_MODE) {
    		console.log ("Received 'subscribe()' callback from AuthService.logout(); calling WebsocketService.stopWebsocket()...");
    	}
        this._websocketService.stopWebsocket();
        if (DEBUG_MODE) {
        	console.log ("Calling AuthService.completeLogout()");
        }
        this._authService.completeLogout();
      }, (error: any) => {
    	  if (DEBUG_MODE) {
    		  console.log ("AuthService.logout().subscribe() callback returned error; calling AuthService.completeLogout()");
    	  }
          this._authService.completeLogout();
      });
  }
}
