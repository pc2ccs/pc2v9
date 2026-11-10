import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { environment } from 'src/environments/environment';

/**
 * Blocks the scoreboard route when pc2v9.ini hideWTIScoreboard=true.
 * Logged-in users are sent to Runs. The Scoreboard tab is also omitted from the header.
 */
@Injectable({
	providedIn: 'root'
})
export class ScoreboardAccessGuard implements CanActivate {
	constructor(private _router: Router) { }

	canActivate(): boolean {
		if (environment.hideWTIScoreboard) {
			this._router.navigateByUrl('/runs');
			return false;
		}
		return true;
	}
}
