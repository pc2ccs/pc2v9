import { Observable, Subject } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { ContestProblem } from '../models/contest-problem';
import { ContestClock } from '../models/contest-clock';
import { Clarification } from '../models/clarification';

export abstract class IContestService {
  clarificationsUpdated = new Subject<void>();
  contestClock = new Subject<void>();  //note that this is a WTI-UI-specific object used in conjunction with Websocket "clock" messages; 
									//it is NOT a "ContestClock" object in the sense of classes defined in core/models.
  standingsUpdated = new Subject<void>();
  isContestRunning = false;

  abstract getLanguages(): Observable<ContestLanguage[]>;

  abstract getProblems(): Observable<ContestProblem[]>;

  abstract getJudgements(): Observable<string[]>;

  abstract getClarifications(): Observable<Clarification[]>;

  abstract getIsContestRunning(): Observable<boolean>;

  abstract getContestClock(): Observable<ContestClock>;		//note that this refers to a "ContestClock" model object, not the "contestClock" subject above
  
  abstract getStandings(): Observable<String>;

  abstract markStandingsOutOfDate(): void;

  abstract getStandingsAreCurrentFlag() : boolean;
}
