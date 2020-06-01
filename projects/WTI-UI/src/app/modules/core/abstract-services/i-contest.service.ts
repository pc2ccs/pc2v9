import { Observable, Subject } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { ContestProblem } from '../models/contest-problem';
import { Clarification } from '../models/clarification';

export abstract class IContestService {
  clarificationsUpdated = new Subject<void>();
  contestClock = new Subject<void>();
  standingsUpdated = new Subject<void>();
  isContestRunning = false;

  abstract getLanguages(): Observable<ContestLanguage[]>;

  abstract getProblems(): Observable<ContestProblem[]>;

  abstract getJudgements(): Observable<string[]>;

  abstract getClarifications(): Observable<Clarification[]>;

  abstract getIsContestRunning(): Observable<boolean>;
  
  abstract getStandings(): Observable<String>;
}
