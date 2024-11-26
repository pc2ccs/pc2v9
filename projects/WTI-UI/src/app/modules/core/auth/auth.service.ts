import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginCredentials } from '../models/login-credentials';
import { Router } from '@angular/router';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { saveCurrentToken, saveCurrentUserName, clearSessionStorage } from 'src/app/app.component';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == root injector)
})
export class AuthService {
  private _token: string;
  private _userName: string;
  private _defaultRoute = '/runs';
  redirectUrl: string;
  
  get token(): string { return this._token; }
  get username(): string { return this._userName; }
  get isLoggedIn(): boolean { return !!this.token; }
  get defaultRoute(): string { return this._defaultRoute; }
  
  set token(value) { this._token = value; }
  set username(value) { this._userName = value; }
  
  constructor(private _teamsService: ITeamsService,
              private _router: Router) { }

  completeLogin(tokenValue: string, username: string) {
    this._token = tokenValue;
    this._userName = username;
    //save values in sessionStorage to allow for recovery from F5
    saveCurrentToken(token);
    saveCurrentUserName(userName);
    //switch to the main user page
    this._router.navigateByUrl(this.redirectUrl || this.defaultRoute);
  }

  login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse> {
    return this._teamsService.login(loginCredentials);
  }

  logout(): Observable<any> {
    return this._teamsService.logout();
  }

  completeLogout(): void {
    this._token = undefined;
    this._userName = undefined;
    clearSessionStorage();
    this._router.navigateByUrl('/login');
  }
}
