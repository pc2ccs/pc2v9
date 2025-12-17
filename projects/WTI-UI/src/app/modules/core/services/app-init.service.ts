import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { IContestService } from '../abstract-services/i-contest.service';
import { IWebsocketService } from '../abstract-services/i-websocket.service';
import * as Constants from 'src/constants';

@Injectable({
  providedIn: 'root'
})
export class AppInitService {
  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private contestService: IContestService,
    private websocketService: IWebsocketService
  ) {}

  async initializeApp(): Promise<void> {
    console.log('[AppInit] Loading environment...');
    try {
      const data: any = await firstValueFrom(this.http.get('assets/appconfig.json'));
      if (data) {
        Object.keys(data).forEach(key => environment[key] = data[key]);
      }
      console.log('[AppInit] Environment loaded:', environment);
    } catch (err) {
      console.warn('[AppInit] Failed to load environment, using defaults.');
    }

    // Restore session state if present
    const token = sessionStorage.getItem(Constants.CONNECTION_TOKEN_KEY);
    const username = sessionStorage.getItem(Constants.CONNECTION_USERNAME_KEY);

    if (token && username) {
      this.authService.token = token;
      this.authService.username = username;
      console.log(`[AppInit] Auth restored for ${username}`);

      this.websocketService.startWebsocket();
      this.contestService.getIsContestRunning().subscribe(val => {
        this.contestService.isContestRunning = val;
        this.contestService.contestClockEvent.next();
      });
    }
  }
}
