//App-wide constants for the PC2 WTI-UI project

/**
 * Global flag indicating whether to display debug messages on the browser console.
 * Set to null to disable debugging output; any non-null value causes debugging output.
 * NOTE: this flag should probably be set to null when creating a production release.
 */
export const DEBUG_MODE = null ;
 
/**
 * Interval in minutes at which the WTI-UI will resync its clock displays with PC2
 */
 export const RESYNC_INTERVAL_IN_MINUTES = 5 ;
 
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
 * The storageSession key indicating what filter settings (recipient and problems) have been selected on the CLARIFICATIONS page filter.
 */
export const CLARS_PAGE_FILTER_KEY = 'clarificationsFilterForm';

/**
 * The key under which the "connection token" for websocket messages is stored in sessionStorage.
 */
export const CONNECTION_TOKEN_KEY = 'token';

/**
 * The key under which the user name for the current websocket connection is stored in sessionStorage.
 */
export const CONNECTION_USERNAME_KEY = 'username';

/**
 * The key under which the base URL for the server is stored in sessionStorage.
 */
export const BASE_URL_KEY = 'baseURL';

/**
 * The key under which the base URL for the server is stored in sessionStorage.
 */
export const WEBSOCKET_URL_KEY = 'websocketURL';

/**
 * The key under which the contest scoreboard type (e.g. pass-fail or point-scoring) is stored in sessionStorage.
 */
export const SCOREBOARD_TYPE_KEY = 'scoreboardType';

/**
 * Interface defining the structure of UI options, for robustness/generality in other modules.
 */
export interface UiOptions {
  clarsNotificationsEnabled: boolean;
  runsNotificationsEnabled: boolean;
}

export const DEFAULT_SHOW_RUNS_POPUP = true;
export const DEFAULT_SHOW_CLARS_POPUP = true;

export const SCOREBOARD_TYPE = {
  PASS_FAIL: "pass-fail",
  POINT_SCORING: "point-scoring",
} as const;

export type ScoreboardType =
  typeof SCOREBOARD_TYPE[keyof typeof SCOREBOARD_TYPE];

