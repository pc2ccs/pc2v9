import { SCOREBOARD_TYPE, ScoreboardType } from 'src/constants';

/**
 * This file contains declarations to support mapping of server-side scoreboard strings 
 * to client-side ScoreboardType. This allows using "point-scoring" as the client-side name 
 * for that scoreboard type (whereas in the server-side Java enum "ScoreboardType" it is simply 
 * called "score").
 * consolidating scoreboard-type manipulation here also provides support for easy future extensions
 * to other "scoreboard types", as well as client-side error handling if the server passes an 
 * unknown scoreboard type.
 */
const SERVER_TO_CLIENT_SCOREBOARD_MAP: Record<string, ScoreboardType> = {
  'pass-fail': SCOREBOARD_TYPE.PASS_FAIL,
  'score': SCOREBOARD_TYPE.POINT_SCORING,
};

/**
 * Converts a server-side scoreboard string to a client-side ScoreboardType
 */
export function mapServerScoreboardType(serverValue: string): ScoreboardType | null {
  const mapped = SERVER_TO_CLIENT_SCOREBOARD_MAP[serverValue];
  if (!mapped) {
    console.warn('[mapServerScoreboardType] Unknown server value: ', serverValue);
    return null;
  }
  return mapped;
}

/**
 * Type guard for client-side ScoreboardType
 */
export function isScoreboardType(value: string): value is ScoreboardType {
  return Object.values(SCOREBOARD_TYPE).includes(value as ScoreboardType);
}
