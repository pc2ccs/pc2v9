import { Component, OnInit, OnDestroy, DoCheck } from '@angular/core';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { saveCurrentPage } from 'src/app/app.component';
import * as Constants from 'src/constants';
import { DEBUG_MODE } from 'src/constants';

@Component({
	templateUrl: './scoreboard-page.component.html',
	styleUrls: ['./scoreboard-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ScoreboardPageComponent implements OnInit, OnDestroy, DoCheck {
	
	private _unsubscribe = new Subject<void>();
	teamStandings: any = [];
	numProblems: number = 0;
	firstToSolveTimes: number[] = [];
	
	//TODO: provide support for more than 26 problems
	//TODO: provide support for the possibility that problems are not listed in alphabetical order
	problemLetters = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];

	constructor(
		private _contestService: IContestService,
		private _appTitleService: AppTitleService
	) { }

	ngOnInit(): void {

		if (DEBUG_MODE) {
			console.log("Executing ScoreboardPageComponent.ngOnInit");
		}
		
		this._appTitleService.setTitleWithTeamId("Scoreboard");
		
        	//indicate that this Scoreboard page is the most recently accessed page
        	saveCurrentPage(Constants.SCOREBOARD_PAGE);

		this.loadStandings();

		// when standings are updated, trigger a reload
		this._contestService.standingsUpdated
			.pipe(takeUntil(this._unsubscribe))
			.subscribe(_ => {
				//console.log("Scoreboard: loading standings from standingsUpdated subscription");
				this.loadStandings();
			});
	}

	ngOnDestroy(): void {
		this._unsubscribe.next();
		this._unsubscribe.complete();
		//console.log("Scoreboard OnDestroy executed.")
	}
	
	//check for scoreboard changes on every cycle
	// Note that even though this gets called frequently, it is lightweight; it only updates
	// the scoreboard when the standings have changed -- and it is never called if the scoreboard
	// is not visible because routing away from the Scoreboard destroys the current ScoreboardPage component.

	ngDoCheck(): void {
        //console.log("Scoreboard ngDoCheck(): ") ;
        if (!this._contestService.getStandingsAreCurrentFlag() ) {
	        //console.log("Standings have changed; updating...");
	        this.loadStandings();
        } else {
	        //console.log("Standings have not changed; bypassing update.");
        }
	}

	private loadStandings(): void {
		this._contestService.getStandings()
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((standings: string) => {
				//console.log("standings string:");
				//console.log(standings);
				this.teamStandings = this.getTeamStandingsArray(standings);
				this.numProblems = this.getNumProblems(standings);
				this.firstToSolveTimes = this.getFirstToSolveTimes(standings);
			});
	}


	/**
	 * Pull each teamStanding node out of the received JSON, load it into an array,
	 * and return the array of team standing elements.
	 */
	private getTeamStandingsArray(standings: any) {

		const contest = standings.contestStandings ;
		//console.log("ContestStandings element:");
		//console.log(contest);
		
		const teams = contest.teamStanding ;
		//console.log("TeamStandings elements:");
		//console.log(teams);
		
		let tempArray: any = [] ;
		
		for (let temp of teams) {
			tempArray.push(temp);
		}
		
//		console.log("Individual Team Standings:");
//		console.log(tempArray);
		
		return tempArray;
	}

	/**
	 * Returns the problem count from the specified JSON standings.
	 */
	private getNumProblems(standings: any) : number {

		const contest = standings.contestStandings ;
//		console.log("getNumProblems(): ContestStandings element:");
//		console.log(contest);
		
		const header = contest.standingsHeader ;
//		console.log("getNumProblems(): StandingsHeader elements:");
//		console.log(header);
		
		const problemCount = header.problemCount;
//		console.log ("getNumProblems(): problemCount = ", problemCount)
		return problemCount;
	}
	
	/**
	 * Pull the first-to-solve times (aka "best solution times") out of the received JSON, load them into an array
	 * (assigning -1 for unsolved problems) and return the array of best solution times.
	 */
	private getFirstToSolveTimes(standings: any) : number[] {

		const contestStandings = standings.contestStandings ;
		//console.log("ContestStandings element:");
		//console.log(contest);
		
		const header = contestStandings.standingsHeader ;
		//console.log("ContestStandings header element:");
		//console.log(header);
		
		let bestSolutionTimes: number[] = [] ;

		//get each problem out of the header array of problems (which is named 'problem', i.e. singular)
		for (let problem of header.problem) {
			//for this problem, check to see if it has a "best solution time"
			if (problem.bestSolutionTime != null) {
				//yes, it has been solved; save the best solution time
				bestSolutionTimes.push(problem.bestSolutionTime);
			} else {
				//no, it hasn't been solved; record -1 as the "solution time"
				bestSolutionTimes.push(-1);
			}
		}
		
		return bestSolutionTimes;
	}
	
	/*  Returns the best solution time for the problem whose given "id string" starts with the
	 *  problem letter.  Note that the problem solution time will be -1 if the problem has not
 	 *  been solved.
	*/
	private getBestSolutionTime(problemIdString: string) : number {
		
		//get the upper-case of the first char of the problemIdString, WHICH IS ASSUMED TO BE THE PROBLEM LETTER (!!)
		const problemLetter : string = problemIdString.charAt(0).toUpperCase();
		//map problem letters A,B... to indexes 0,1,...
		const problemNumber = problemLetter.charCodeAt(0) - 65 ;  //'A'.charCodeAt(0) === 65
		//return the solution time for the specified problem (-1 if not solved)
		return this.firstToSolveTimes[problemNumber] ;
	}
}
