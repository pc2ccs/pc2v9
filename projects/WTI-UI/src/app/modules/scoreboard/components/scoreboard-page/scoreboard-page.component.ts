import { Component, OnInit, OnDestroy, DoCheck } from '@angular/core';
import { IContestService } from 'src/app/modules/core/abstract-services/i-contest.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AppTitleService } from 'src/app/modules/core/services/app-title.service';
import { DEBUG_MODE } from 'src/constants';
import { UiHelperService } from 'src/app/modules/core/services/ui-helper.service';
import { SCOREBOARD_TYPE } from 'src/constants';
import type { ScoreboardType } from 'src/constants';
import { ensureArray } from 'src/app/modules/core/utils/json-utils';
import { ScoreboardModeService } from 'src/app/modules/core/services/scoreboard-mode.service';

interface ProblemHeader {
    label: string;
    color: [number, number, number];
    textColor: 'black' | 'white';
    url: string;
}

interface ScoreboardGroupOption {
	id: string;
	displayName: string;
}
  
@Component({
	templateUrl: './scoreboard-page.component.html',
	styleUrls: ['./scoreboard-page.component.scss', '../../../../../styles/filter_table.scss']
})
export class ScoreboardPageComponent implements OnInit, OnDestroy, DoCheck {
	
	private _unsubscribe = new Subject<void>();
	teamStandings: any = [];
	private fullTeamStandings: any[] = [];
	groupOptions: ScoreboardGroupOption[] = [];
	selectedGroupId = '';

	numProblems: number = 0;
	problemDetailHeaders: ProblemHeader[] = [];
	
	readonly SCOREBOARD_TYPE = SCOREBOARD_TYPE;
	scoreboardType!: ScoreboardType;

	//TODO: provide support for more than 26 problems
	//TODO: provide support for the possibility that problems are not listed in alphabetical order
	problemLetters = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];

	constructor(
		private _contestService: IContestService,
		private _appTitleService: AppTitleService,
		private _uiHelper: UiHelperService,
		private _scoreboardMode: ScoreboardModeService) { }

	ngOnInit(): void {

		if (DEBUG_MODE) {
			console.log("Executing ScoreboardPageComponent.ngOnInit");
		}
		
		this._appTitleService.setTitleWithTeamId("Scoreboard");
		
		this.scoreboardType = this._contestService.getScoreboardType();
		
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
			.subscribe((standings: any) => {
				// Snapshot all team rows, derive dropdown options from the header groupList (or from rows if the header is unusable),
				// reset an invalid filter choice, then derive visible rows and table metadata from the same payload.
				const rows = this.getTeamStandingsArray(standings);
				this.fullTeamStandings = rows;
				this.groupOptions = this.buildGroupDropdownOptions(standings, rows);
				// Hide the filter unless two or more groups qualify; clear selection if the chosen group vanished.
				if (this.groupOptions.length <= 1) {
					this.selectedGroupId = '';
				} else if (
					this.selectedGroupId !== '' &&
					!this.groupOptions.some(g => g.id === this.selectedGroupId)
				) {
					this.selectedGroupId = '';
				}
				this.teamStandings = this.getFilteredStandings(rows);
				this.numProblems = this.getNumProblems(standings);
				this.problemDetailHeaders = this.getProblemDetailHeaders(standings);
			});
	}

	/**
	 * Update scoreboard standings whenever group dropdown ("filter") changes
	 */
	onGroupFilterChange(): void {
		this.teamStandings = this.getFilteredStandings(this.fullTeamStandings);
	}

	/**
	 * Returns the value shown in the Rank column for one team row, depending on whether a group filter is active.
	 *
	 * - No group selected: use contest-wide {@code rank} from the standings payload (PC2 overall placement).
	 * - Group selected and this row's primary group matches the filter ({@code teamGroupId}): use {@code groupRank}
	 *   when present (within-group rank from PC2 for that primary group).
	 * - Group selected but the row is included because of another group (e.g. {@code teamGroupIds}) or {@code groupRank}
	 *   is missing: use {@code filteredIndex + 1} for a simple 1-based order in the currently filtered table.
	 *
	 * PC2 only emits {@code groupRank} for the team's primary group; it is not reused when the user filters to a
	 * non-primary group membership.
	 *
	 * @param team one element from {@code teamStandings} (filtered or full)
	 * @param filteredIndex zero-based index in the current {@code teamStandings} array (after group filter)
	 */
	getDisplayRank(team: any, filteredIndex: number): string | number {
		if (this.selectedGroupId === '') {
			return team.rank;
		}
		const primaryMatches = String(team.teamGroupId ?? '') === this.selectedGroupId;
		if (primaryMatches) {
			const gr = team.groupRank;
			if (gr !== undefined && gr !== null && String(gr) !== '') {
				return gr;
			}
		}
		return filteredIndex + 1;
	}


	/**
	 * Pull each teamStanding node out of the received JSON, load it into an array,
	 * and return the array of team standing elements.
	 * Normalizes each row's problemSummaryInfo to an array so the template NgFor never receives a lone object.
	 */
	private getTeamStandingsArray(standings: any): any[] {
		const contest = standings?.contestStandings;
		if (!contest) {
			return [];
		}
		const teams = this.normalizeToArray(contest.teamStanding);
		for (const team of teams) {
			this.ensureProblemSummaryInfoArray(team);
		}
		return teams;
	}

	/** Repeated XML elements may arrive as one object or an array depending on parser and row count.
	  * This method ensures the specified "nodes" argument is an iterable array.
	  */
	private normalizeToArray(nodes: unknown): any[] {
		if (nodes == null) {
			return [];
		}
		return Array.isArray(nodes) ? nodes : [nodes];
	}

	/**
	 * XML-to-JSON may emit a lone problemSummaryInfo object; template NgFor requires an iterable array.
	 * Missing or null becomes an empty array.
	 */
	private ensureProblemSummaryInfoArray(team: unknown): void {
		if (!team || typeof team !== 'object') {
			return;
		}
		const row = team as Record<string, unknown>;
		const psi = row.problemSummaryInfo;
		if (psi == null) {
			row.problemSummaryInfo = [];
		} else if (!Array.isArray(psi)) {
			row.problemSummaryInfo = [psi];
		}
	}

	/**
	 * Returns an array of options for populating the scoreboard Group dropdown. The preferred source is 
	 * standingsHeader.groupList.group (see DefaultScoringAlgorithm.dumpGroupList)
	 * with teamCount > 0. Option values use each group's "id" (matches WTI-enriched teamGroupIds / legacy teamGroupId).
	 * If the header omits usable group rows, falls back to groups inferred from team standings.
	 */
	private buildGroupDropdownOptions(standings: any, teams: any[]): ScoreboardGroupOption[] {
		const fromHeader = this.buildGroupOptionsFromHeader(standings);
		if (fromHeader.length > 0) {
			return fromHeader;
		}
		return this.buildGroupOptionsFromTeamRows(teams);
	}

	/** Builds an array of Group Options from standingsHeader.groupList, using only entries with teamCount 
	 *  strictly greater than zero. 
	 */
	private buildGroupOptionsFromHeader(standings: any): ScoreboardGroupOption[] {
		const header = standings?.contestStandings?.standingsHeader;
		//if header is missing, the groupList is empty
		if (!header) {
			return [];
		}
		//if there is no grouplist (aka groupList) in the header, the group list is empty
		const groupList = header.groupList ?? header.grouplist;
		if (!groupList || typeof groupList !== 'object') {
			return [];
		}
		
		//get the list of groups
		const raw = (groupList as { group?: unknown }).group;
		
		//ensure we have an ARRAY of group objects
		const groups = this.normalizeToArray(raw);
		
		//start with an empty list of group dropdown options
		const out: ScoreboardGroupOption[] = [];
		
		//check each group in the groupList.  For each group, safely read id/title/teamCount across JSON quirks; 
		//keep only groups with a non-empty id and positive team count; assign a readable label.
		for (const g of groups) {
		
			//normalize each item in this group to an object, either what it was to start with or an empty-string Record
			const row = g && typeof g === 'object' ? (g as Record<string, unknown>) : {};
			
			//pull the standard fields (id, title, teamCount, etc.) out of the group row
			const id = this.readXmlishField(row, 'id');
			const title = this.readXmlishField(row, 'title');
			const teamCountVal = this.readXmlishField(row, 'teamCount');
			const teamCountAlt = row['teamcount'] ?? row['team_count'];
			const tc = this.parsePositiveCount(teamCountVal ?? teamCountAlt);
			const stringGroupId = id !== undefined && id !== null && String(id).trim() !== ''
				? String(id).trim()
				: '';
				
			//skip invalid or empty groups
			if (stringGroupId === '' || !(tc > 0)) {
				continue;
			}
			
			//If title is present (after trim), use it as label; otherwise use 'Group+stringId' as label
			const label = title !== undefined && title !== null && String(title).trim() !== ''
				? String(title).trim()
				: `Group ${stringGroupId}`;
				
				
			//insert the stringGroupId and label into the output array
			out.push({ id: stringGroupId, displayName: label });
		}
		
		//return options sorted alphabetically by display name, case-insensitive
		return out.sort((a, b) =>
			a.displayName.localeCompare(b.displayName, undefined, { sensitivity: 'base' })
		);
	}

	/**
	 * The fields in scoreboard records come from XML converted to JSON.  Depending on what XML serializer and
	 * schema was used, the same conceptual attribute (id, title, teamCount, ...) can appear either
	 * directly as a property on the object representing the <group> element, e.g. { id: "1", title: "..." }, or
	 * inside a synthetic child keyed something like @attributes, e.g. { "@attributes": { id: "1", title: "..." } }.
	 * readXmlishField is a tiny normalization helper: "give me the value for logical field field from this JSON object, 
	 * no matter which of those two layouts we got."
	 */
	private readXmlishField(obj: Record<string, unknown>, field: string): unknown {
		// JSON-from-XML sometimes puts attributes on @attributes instead of alongside child nodes.
		if (field in obj) {
			return obj[field];
		}
		const attr = obj['@attributes'];
		if (attr && typeof attr === 'object' && field in (attr as object)) {
			return (attr as Record<string, unknown>)[field];
		}
		return undefined;
	}

	private parsePositiveCount(value: unknown): number {
		if (value === undefined || value === null) {
			return NaN;
		}
		if (typeof value === 'number') {
			return Number.isFinite(value) ? value : NaN;
		}
		const n = parseInt(String(value).trim(), 10);
		return Number.isFinite(n) ? n : NaN;
	}

	/** Fallback: one row per teamGroupId present on team standings (legacy / missing header). */
	private buildGroupOptionsFromTeamRows(teams: any[]): ScoreboardGroupOption[] {
		const idToLabel = new Map<string, string>();
		for (const row of teams) {
			const id = row?.teamGroupId;
			const name = row?.teamGroupName;
			if (id === undefined || id === null || String(id).trim() === '') {
				continue;
			}
			const sid = String(id);
			idToLabel.set(sid, (name !== undefined && name !== null && String(name).trim() !== '')
				? String(name)
				: `Group ${sid}`);
		}
		return Array.from(idToLabel.entries())
			.sort((a, b) => a[1].localeCompare(b[1], undefined, { sensitivity: 'base' }))
			.map(([id, displayName]) => ({ id, displayName }));
	}

	private getFilteredStandings(allRows: any[]): any[] {
		if (this.selectedGroupId === '') {
			return allRows.slice();
		}
		const sid = this.selectedGroupId;
		// WTI puts all group ids on each row; otherwise only PC2 primary teamGroupId exists.
		return allRows.filter(row => this.rowBelongsToSelectedGroup(row, sid));
	}

	/**
	 * Prefer WTI-enriched `teamGroupIds` (all groups the team belongs to).
	 * Otherwise fall back to PC2's single {@code teamGroupId} (primary group only).
	 */
	private rowBelongsToSelectedGroup(row: any, selectedId: string): boolean {
		const ids = this.getTeamGroupIdsFromRow(row);
		if (ids.length > 0) {
			return ids.indexOf(selectedId) >= 0;
		}
		return String(row.teamGroupId ?? '') === selectedId;
	}

	private getTeamGroupIdsFromRow(row: any): string[] {
		const raw = row?.teamGroupIds;
		if (raw == null) {
			return [];
		}
		if (Array.isArray(raw)) {
			return raw
				.map((x: unknown) => String(x).trim())
				.filter(s => s !== '');
		}
		return [];
	}

	/**
	 * Returns the problem count from the specified JSON standings.
	 */
	private getNumProblems(standings: any) : number {

		const contest = standings.contestStandings ;
		if (DEBUG_MODE) {
			console.log("getNumProblems(): ContestStandings element:");
			console.log(contest);
		}
		
		const header = contest.standingsHeader ;
		if (DEBUG_MODE) {
			console.log("getNumProblems(): StandingsHeader elements:");
			console.log(header);
		}
		
		const problemCount = header.problemCount;
		if (DEBUG_MODE){
			console.log ("getNumProblems(): problemCount = ", problemCount)
		}
		return problemCount;
	}
	
		
	/** Returns an array of elements, one for each problem, containing text label (problem letter),
	 * RGB color value (for use as a background), the optimum text color for the problem label and a URL
	 * for accessing the corresponding problem writeup.
	 */
	private getProblemDetailHeaders(standings:any) : ProblemHeader [] {
		
		const contestStandings = standings.contestStandings ;
		if (DEBUG_MODE) {
			console.log("ContestStandings element:");
			console.log(contestStandings);
		}
		
		const standingsHeader = contestStandings.standingsHeader ;
		if (DEBUG_MODE) {
			console.log("ContestStandings header element:");
			console.log(standingsHeader);
		}
		
		//create an array containing the problems from standingsHeader, or an empty array ("[]"] if the problem array is null
		
		const problemsRaw = standingsHeader.problem;

		let problems: any[];

		if (Array.isArray(problemsRaw)) {
  			problems = problemsRaw;
		} else if (problemsRaw) {
  			problems = [problemsRaw];
		} else {
  			problems = [];
		}

		
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
	
	get isPassFail(): boolean {
		return this._scoreboardMode.isPassFail();
	}

	get isPointScoring(): boolean {
		return this._scoreboardMode.isPointScoring();
	}

}
