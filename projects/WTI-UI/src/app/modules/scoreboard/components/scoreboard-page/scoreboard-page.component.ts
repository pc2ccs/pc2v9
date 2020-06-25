import { Component, OnInit, OnDestroy, DoCheck } from '@angular/core';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
	templateUrl: './scoreboard-page.component.html',
	styleUrls: ['./scoreboard-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ScoreboardPageComponent implements OnInit, OnDestroy, DoCheck {
	
	private _unsubscribe = new Subject<void>();
	teamStandings: any = [];

	constructor(
		private _contestService: IContestService,
	) { }

	ngOnInit(): void {
		this.loadStandings();

		// when standings are updated, trigger a reload
		this._contestService.standingsUpdated
			.pipe(takeUntil(this._unsubscribe))
			.subscribe(_ => {
				this.loadStandings();
			});
	}

	ngOnDestroy(): void {
		this._unsubscribe.next();
		this._unsubscribe.complete();
	}
	
	//check for scoreboard changes on every cycle
	// Note that even though this gets called frequently, it is lightweight; it only updates
	// the scoreboard when it has changed
	ngDoCheck(): void {
        //console.log("Scoreboard ngDoCheck(): ")
        if (!this._contestService.getStandingsAreCurrentFlag()) {
	        //console.log("Standings have changed; updating...");
	        this.loadStandings();
        } else {
	        //console.log("Standings have not changed");
        }
	}

	private loadStandings(): void {
		this._contestService.getStandings()
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((standings: string) => {
				//console.log("standings string:");
				//console.log(standings);
				this.teamStandings = this.getTeamStandingsArray(standings);
			});
	}


	/**
	 * Pull each teamStanding node out of the received JSON, load it into an array,
	 * and return the array of team standing elements.
	 */
	private getTeamStandingsArray(standings: any) {

		const contest = standings.contestStandings ;
		console.log("ContestStandings element:");
		console.log(contest);
		
		const teams = contest.teamStanding ;
		console.log("TeamStandings elements:");
		console.log(teams);
		
		let tempArray: any = [] ;
		
		for (let temp of teams) {
			tempArray.push(temp);
		}
		
		console.log("Individual Team Standings:");
		console.log(tempArray);
		
		return tempArray;
	}

}
