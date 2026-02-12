import { Injectable } from '@angular/core';
import { ITeamsService } from '../abstract-services/i-teams.service';
import { LoginCredentials } from '../models/login-credentials';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { TeamsLoginResponse } from '../models/teams-login-response';
import { Submission } from '../models/submission';
import { Run } from '../models/run';
import { NewClarification } from '../models/new-clarification';
import { DEBUG_MODE } from 'src/constants';

@Injectable({
	providedIn: 'root'   //forces the service to be a singleton across all app components ('root' == "root injector")
})
export class TeamsService extends ITeamsService {

	constructor(private _httpClient: HttpClient) {
		super();
		if (DEBUG_MODE) {
			console.log("Executing TeamsService constructor...");
			const environmentCopy = JSON.parse(JSON.stringify(environment));
			console.log('...Environment Object (Deep Copy):', environmentCopy);
		}
	}

	login(loginCredentials: LoginCredentials): Observable<TeamsLoginResponse> {
		if (DEBUG_MODE) {
			console.log("Executing TeamsService.login()...");
			console.log("...environment is:");
			const environmentCopy = JSON.parse(JSON.stringify(environment));
			console.log(environmentCopy);
		}

		if (DEBUG_MODE) {
			console.log(`Invoking HttpClient.post() with URL ${environment.baseUrl}/teams/login`);
		}
		let resp = this._httpClient.post<TeamsLoginResponse>(`${environment.baseUrl}/teams/login`, loginCredentials);
		if (DEBUG_MODE) {
			console.log("Response from HttClient:");
			console.log(resp);
			console.log("Environment following HTTPClient response is:");
			const environmentCopy = JSON.parse(JSON.stringify(environment));
			console.log(environmentCopy);
		}
		return resp;
	}

	logout(): Observable<any> {
		return this._httpClient.delete<any>(`${environment.baseUrl}/teams/logout`);
	}

	submitRun(submission: Submission): Observable<any> {
		return this._httpClient.post<Submission>(`${environment.baseUrl}/teams/run`, submission);
	}

	getRuns(): Observable<Run[]> {
		return this._httpClient.get<Run[]>(`${environment.baseUrl}/teams/run`);
	}

	postClarification(clarification: NewClarification): Observable<any> {
		return this._httpClient.post<NewClarification>(`${environment.baseUrl}/teams/clarification`, clarification);
	}
}
