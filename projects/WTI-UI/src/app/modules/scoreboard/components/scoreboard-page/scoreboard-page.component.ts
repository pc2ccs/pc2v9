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
		
		console.log("Individual Team Standings:");
		console.log(tempArray);
		
		return tempArray;
	}

	/**
	 * Returns the problem count from the specified JSON standings.
	 */
	private getNumProblems(standings: any) : number {

		const contest = standings.contestStandings ;
		console.log("getNumProblems(): ContestStandings element:");
		console.log(contest);
		
		const header = contest.standingsHeader ;
		console.log("getNumProblems(): StandingsHeader elements:");
		console.log(header);
		
		const problemCount = header.problemCount;
		console.log ("getNumProblems(): problemCount = ", problemCount)
		return problemCount;
	}
}
