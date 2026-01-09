// session-storage.utils.ts -- provides functions to access the "sessionStorage" 
// variable which is declared within the browser as part of the HTML5 Web Storage API.
//(sessionStorage exists for the lifetime of the browser tab and survives F5.)
import * as Constants from 'src/constants';
import { UiOptions } from 'src/constants';
import { environment } from 'src/environments/environment';

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
