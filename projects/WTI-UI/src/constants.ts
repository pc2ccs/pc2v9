//App-wide constants for the PC2 WTI-UI project

/**
 * Global flag indicating whether to display debug messages on the browser console.
 * Set to null to disable debugging output; any non-null value causes debugging output.
 */
export const DEBUG_MODE = 'true'
 
/**
 * The key under which the currently-active WTI page is stored in sessionStorage.
 */
export const CURRENT_PAGE_KEY = 'curPageKey';

/**
 * The key for the RUNS page.
 */
export const RUNS_PAGE_KEY = 'runs';

/**
 * The key for the OPTIONS page.
 */
export const OPTIONS_PAGE_KEY = 'options';

/**
 * The key for the CLARIFICATIONS page.
 */
export const CLARIFICATIONS_PAGE_KEY = 'clarifications';

/**
 * The key for the SCOREBOARD page.
 */
export const SCOREBOARD_PAGE_KEY = 'scoreboard';

/**
 * The key under which the "connection token" for websocket messages is stored in sessionStorage.
 */
export const CONNECTION_TOKEN_KEY = 'token';

/**
 * The key under which the user name for the current websocket connection is stored in sessionStorage.
 */
export const CONNECTION_USERNAME_KEY = 'username';