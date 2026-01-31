// session-storage.utils.ts -- provides functions to access the "sessionStorage" 
// variable which is declared within the browser as part of the HTML5 Web Storage API.
//(sessionStorage exists for the lifetime of the browser tab and survives F5.)

//SessionStorage is used to cache values such as the current user's login name and
// token, the current environment (webserver and websocket URLs), the current
// settings on the WTI-UI "Options" page, and the current scoreboard type
// (e.g. pass-fail or point-scoring.
import * as Constants from 'src/constants';
import { UiOptions } from 'src/constants';
import { environment } from 'src/environments/environment';
import { isScoreboardType } from 'src/app/utils/scoreboard-type.utils';
import { ScoreboardType } from 'src/constants';

/** ------------------------ Connection Token ------------------------ */

export function saveCurrentToken(token: string) {
	sessionStorage.setItem(Constants.CONNECTION_TOKEN_KEY, token);
}

export function getCurrentToken(): string | null {
	return sessionStorage.getItem(Constants.CONNECTION_TOKEN_KEY);
}

/** ------------------------ Username ------------------------ */

export function saveCurrentUserName(username: string) {
	sessionStorage.setItem(Constants.CONNECTION_USERNAME_KEY, username);
}

export function getCurrentUserName(): string | null {
	return sessionStorage.getItem(Constants.CONNECTION_USERNAME_KEY);
}

/** ------------------ Scoreboard Type (e.g. pass-fail vs. point-scoring) */

//Save the scoreboard type to sessionStorage
export function saveStoredScoreboardType(type: ScoreboardType): void {
  sessionStorage.setItem(Constants.SCOREBOARD_TYPE_KEY, type);
}

//Retrieve the scoreboard type from sessionStorage (if present and valid)
export function getStoredScoreboardType(): ScoreboardType | null {
  const stored = sessionStorage.getItem(Constants.SCOREBOARD_TYPE_KEY);
  if (stored && isScoreboardType(stored)) {
    return stored;
  }
  return null;
}

//Remove scoreboard type from sessionStorage
export function clearStoredScoreboardType(): void {
  sessionStorage.removeItem(Constants.SCOREBOARD_TYPE_KEY);
}

/** ------------------------ Options ------------------------ */

//save the specified UI Options (e.g. "don't create clar notification popups") in sessionStorage
export function saveOptions(options: UiOptions): void {
	try {
		sessionStorage.setItem(
			Constants.OPTIONS_DETAILS_KEY,
			JSON.stringify(options)
		);
	} catch (err) {
		console.warn('[sessionStorage] Failed to save UI options', err);
	}
}


//Return the Options stored in sessionStorage, or null if none.
export function loadOptions(): any | null {
	const raw = sessionStorage.getItem(Constants.OPTIONS_DETAILS_KEY);
	if (!raw) {
		return null;
	}
	try {
		return JSON.parse(raw);
	} catch {
		return null;
	}
}


/** ------------------------ Environment ------------------------ */

export function saveCurrentEnvironment() {
	sessionStorage.setItem(Constants.BASE_URL_KEY, environment.baseUrl);
	sessionStorage.setItem(Constants.WEBSOCKET_URL_KEY, environment.websocketUrl);
}

export function restoreEnvironment() {
	const baseUrl = sessionStorage.getItem(Constants.BASE_URL_KEY);
	const websocketUrl = sessionStorage.getItem(Constants.WEBSOCKET_URL_KEY);

	if (baseUrl) environment.baseUrl = baseUrl;
	if (websocketUrl) environment.websocketUrl = websocketUrl;
}

/** ------------------------ Utility ------------------------ */

export function clearSessionStorage() {
	sessionStorage.clear();
}