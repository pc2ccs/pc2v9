import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
//import { Standing } from 'src/app/modules/core/models/standing';

@Component({
	templateUrl: './scoreboard-page.component.html',
	styleUrls: ['./scoreboard-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ScoreboardPageComponent implements OnInit, OnDestroy {
	

	private _unsubscribe = new Subject<void>();
	//filterForm: FormGroup;
	//teamStandings: Standing[] = [];
	teamStandings: any = [];

	constructor(
		//private _formBuilder: FormBuilder,
		private _contestService: IContestService,
		//private _matDialog: MatDialog
	) { }

	ngOnInit(): void {
		//this.buildForm();
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


/*	private buildForm(): void {
		this.filterForm = this._formBuilder.group({
			runType: ['both'],
			language: [],
			problem: [],
			judgement: []
		});

	}
*/
	private loadStandings(): void {
		this._contestService.getStandings()
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((standings: string) => {
				console.log("standings string:");
				console.log(standings);
				this.teamStandings = this.getTeamStandingsArray(standings);
			});
	}

	public reset(): void {
		//this.buildForm();
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
