import { Injectable } from '@angular/core';
import { IContestService } from '../abstract-services/i-contest.service';
import { Observable, of } from 'rxjs';
import { ContestLanguage } from '../models/contest-language';
import { ContestProblem } from '../models/contest-problem';
import { Clarification } from '../models/clarification';

@Injectable()
export class ContestMockService extends IContestService {

  getLanguages(): Observable<ContestLanguage[]> {
    return of<ContestLanguage[]>([
      { id: '1', name: 'Java' },
      { id: '2', name: 'C#' },
      { id: '3', name: 'TypeScript' },
      { id: '4', name: 'Ruby' },
      { id: '5', name: 'Python' }
    ]);
  }

  getProblems(): Observable<ContestProblem[]> {
    return of<ContestProblem[]>([
      { name: 'Dogzilla', shortName: 'A' },
      { name: 'Matrix', shortName: 'B' },
      { name: 'Two Towers', shortName: 'C' },
      { name: 'Ducks', shortName: 'D' },
      { name: 'Magic Numbers', shortName: 'E' }
    ]);
  }

  getJudgements(): Observable<string[]> {
    return of<string[]>([
      'Wrong Answer',
      'Output Format Error',
      'Correct'
    ]);
  }

  getClarifications(): Observable<Clarification[]> {
    return of<Clarification[]>([
      {
        id: '1-1',
        recipient: 'team1',
        problem: 'Dogzilla',
        question: 'I don\'t know how to solve this problem. Help plz!',
        answer: 'Lol nope!',
        time: 1556141234,
        isAnswered: true
      }, {
        id: '1-2',
        recipient: 'team1',
        problem: 'Matrix',
        question: 'I think you misspelled a word on this problem. Can you fix it?',
        answer: 'Why you gotta be so petty like that?',
        time: 1556141249,
        isAnswered: true
      }, {
        id: '1-3',
        recipient: 'All',
        problem: 'Two Towers',
        question: 'Can I pop my team\'s balloons?',
        answer: 'Ummm, sure?',
        time: 1556141235,
        isAnswered: true
      }, {
        id: '1-5',
        recipient: 'team1',
        problem: 'Two Towers',
        question: 'What if there are three towers? Should we test for that edge case?',
        answer: '',
        time: 1556141335,
        isAnswered: false
      }
    ]);
  }

  getIsContestRunning(): Observable<boolean> {
    return of<boolean>(true);
  }
  
  getStandings(): Observable<String> {
  
	//TODO: this method needs to return a legitimate (mock) team standing array!
	var json = "{\"teamStanding\":[]\"}";

    return of<String>(json) ;
    
  }

	markStandingsOutOfDate() : void {
		//do nothing
	}
	
	getStandingsAreCurrentFlag() : boolean {
		return true ;
	}

}