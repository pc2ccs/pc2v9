import { Component, OnInit, OnDestroy, DoCheck } from '@angular/core';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import * as Constants from 'src/constants';
import { DEBUG_MODE } from 'src/constants';
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service';

interface ProblemHeader {
    label: string;
    color: [number, number, number];
    textColor: 'black' | 'white';
    url: string;
}
  
@Component({
	templateUrl: './scoreboard-page.component.html',
	styleUrls: ['./scoreboard-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ScoreboardPageComponent implements OnInit, OnDestroy, DoCheck {
	
	private _unsubscribe = new Subject<void>();
	teamStandings: any = [];
	numProblems: number = 0;
	problemDetailHeaders: ProblemHeader[] = [];
	
	constructor(
		private _contestService: IContestService,
		private _appTitleService: AppTitleService,
		private _uiHelper: UiHelperService) { }

	ngOnInit(): void {

		if (DEBUG_MODE) {
			console.log("Executing ScoreboardPageComponent.ngOnInit");
		}
		
		this._appTitleService.setTitleWithTeamId("Scoreboard");
		
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
				this.problemDetailHeaders = this.getProblemDetailHeaders(standings);
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
	
		
	/** Returns an array of elements, one for each problem, containing text label (problem letter),
	 * RGB color value (for use as a background), the optimum text color for the problem label and a URL
	 * for accessing the corresponding problem writeup.
	 */
	private getProblemDetailHeaders(standings:any) : ProblemHeader [] {
		
		const contestStandings = standings.contestStandings ;
		//console.log("ContestStandings element:");
		//console.log(contestStandings);
		
		const standingsHeader = contestStandings.standingsHeader ;
		//console.log("ContestStandings header element:");
		//console.log(standingsHeader);
		
		//get the "problem" array out of standingsHeader, or use an empty array ("[]"] if the problem array is null
		const problems = standingsHeader.problem ?? [];
		
		//Build an array of problem detail headers by looking at every problem in the "problem" array
  		const headers: ProblemHeader[] = problems
  			//sanity check -- filter out any problems that don't have 'rbg' and 'url' and 'letter' fields.
  			.filter((p: any) => p.rgb && p.url && p.letter)
  			
  			//map each selected problem into a ProblemHeader array element
  			.map((p: any) => {
  			    
  			    //convert the hex color (e.g. "#00FF00") to an RGB array (like [R, G, B])
      			const color = this.hexToRgb(p.rgb);
  			    
  			    //construct and return an object representing this problem, with letter, color (background color), textColor, and URL fields.
      			return {
        			label: p.letter,
        			color,
        			textColor: this.getBestTextColor(color),
        			url: p.url
      			};
    		});
		
			//return an array where each element contains a {label,color,textColor,url} for one problem.
			return headers;
	}
	
		
	/* Accepts a string containing a hex RGB value, possibly with a leading "#" and returns
	 * an array containing the individual RGB values.
	 */
	private hexToRgb(hex: string): [number, number, number] {
  		const sanitized = hex.replace(/^#/, '');
  		const intVal = parseInt(sanitized, 16);
  		
   		const r = (intVal >> 16) & 255;
  		const g = (intVal >> 8) & 255;
  		const b = intVal & 255;

  		return [r, g, b];
	}

	private getLuminance([r, g, b]: [number, number, number]): number {
  		const channel = (c: number) => {
    		const v = c / 255;
    		return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
  		};
 		 return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b);
	}

	private getBestTextColor(color: [number, number, number]): 'black' | 'white' {
  		return this.getLuminance(color) > 0.179 ? 'black' : 'white';
	}
	
	private rgbToCss(color: [number, number, number]): string {
  		return `rgb(${color[0]}, ${color[1]}, ${color[2]})`;
	}
	
	private isContestStarted() : boolean {
		return this._contestService.getElapsedSecs()>0 ;
	}
	
	/** Suppress navigation to the Problem PDFs, and pop up an error dialog, if the contest hasn't started. 
	 */
	onLinkClick(event: MouseEvent, url: string): void {
  		if (!this.isContestStarted()) {
    		event.preventDefault(); // block the link from navigating
    		this._uiHelper.alertError('Contest has not started yet.') ;
    	}
    	return;
  	}
  	
  	/** Return problem detail headers if contest is started, otherwise return an empty array.
  	 */
  	get visibleProblemDetailHeaders() {
  		return this.isContestStarted() ? this.problemDetailHeaders as ProblemHeader[] : [];
	}
}
