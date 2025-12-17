// session-storage.utils.ts
import * as Constants from 'src/constants';
import { environment } from 'src/environments/environment';

/** ------------------------ Current Page ------------------------ */

export function saveCurrentPage(page: string) {
    sessionStorage.setItem(Constants.CURRENT_PAGE_KEY, page);
}

export function getCurrentPage(): string | null {
    return sessionStorage.getItem(Constants.CURRENT_PAGE_KEY);
}

export function clearCurrentPage() {
    sessionStorage.removeItem(Constants.CURRENT_PAGE_KEY);
}

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

export function saveOptions(options: Object) {
    sessionStorage.setItem(Constants.OPTIONS_DETAILS_KEY, JSON.stringify(options));
}

export function getOptions(): any | null {
    const value = sessionStorage.getItem(Constants.OPTIONS_DETAILS_KEY);
    return value ? JSON.parse(value) : null;
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
