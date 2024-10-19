//auth.service.ts

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginCredentials } from '../models/login-credentials';
import { Router } from '@angular/router';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { getCurrentPage } from 'src/app/app.component';

@Injectable()
export class AuthService {
  private _token: string;
  private _userName: string;
  get isLoggedIn(): boolean { return (!!this.token  ||  !!getCurrentPage()) ; }
  private _defaultRoute = '/runs';
  get defaultRoute(): string { return this._defaultRoute; }
  redirectUrl: string;
  get token(): string { return this._token; }
  get username(): string { return this._userName; }

  constructor(private _teamsService: ITeamsService,
              private _router: Router) { 
  	console.log("Constructing AuthService...");
  }

  completeLogin(tokenValue: string, username: string) {
	console.log ("Running AuthService.completeLogin(); saving token=" + tokenValue + ", username=" + username);
    this._token = tokenValue;
    this._userName = username;
    this._router.navigateByUrl(this.redirectUrl || this.defaultRoute);
  }

  login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse> {
    return this._teamsService.login(loginCredentials);
  }

  logout(): Observable<any> {
    return this._teamsService.logout();
  }

  completeLogout(): void {
	console.log("Running AuthService.completeLogout(): ");
	
	console.log("   ...clearing localStorage");
	localStorage.clear();
	
	console.log("   ...setting 'token' and username to 'undefined'");
    this._token = undefined;
    this._userName = undefined;

	console.log("   ...invoking Router.navigateByUrl('/login')");
    this._router.navigateByUrl('/login');
  }
}
