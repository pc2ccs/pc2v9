//App-wide constants for the PC2 WTI-UI project

/**
 * Global flag indicating whether to display debug messages on the browser console.
 * Set to null to disable debugging output; any non-null value causes debugging output.
 * NOTE: this flag should probably be set to null when creating a production release.
 */
export let DEBUG_MODE = true ;
 
/**
 * The key under which the currently-active WTI page is stored in sessionStorage.
 */
export const CURRENT_PAGE_KEY = 'curPageKey';

/**
 * The storageSession value indicating the RUNS page is "current".
 */
export const RUNS_PAGE = 'runs';

/**
 * The storageSession value indicating the OPTIONS page is "current".
 */
export const OPTIONS_PAGE = 'options';

/**
 * The key under which the OPTIONS page details (that is, current option values) are stored in sessionStorage.
 */
export const OPTIONS_DETAILS_KEY = 'optionsDetails';

/**
 * The optionsDetails key for the clarifications-notifications-enabled option
 */
export const CLARS_ENABLED_OPTIONS_KEY = 'clarsNotificationsEnabled';
		 
/**
 * The optionsDetails key for the runs-notifications-enabled option
 */
export const RUNS_ENABLED_OPTIONS_KEY = 'runsNotificationsEnabled';
		 		 
/**
 * The storageSession value indicating the CLARIFICATIONS page is "current".
 */
export const CLARIFICATIONS_PAGE = 'clarifications';

/**
  * The storageSession value indicating the SCOREBOARD page is "current".
 */
export const SCOREBOARD_PAGE = 'scoreboard';

/**
 * The key under which the "connection token" for websocket messages is stored in sessionStorage.
 */
export const CONNECTION_TOKEN_KEY = 'token';

/**
 * The key under which the user name for the current websocket connection is stored in sessionStorage.
 */
export const CONNECTION_USERNAME_KEY = 'username';