import { Injectable } from '@angular/core';
import { IContestService } from '../abstract-services/i-contest.service';
import { SCOREBOARD_TYPE } from 'src/constants';

@Injectable({
	providedIn: 'root'
})
export class ScoreboardModeService {

	constructor(private contestService: IContestService) { }

	/** Return true if the current contest is "pass/fail" */
	isPassFail(): boolean {
		return this.contestService.getScoreboardType() === SCOREBOARD_TYPE.PASS_FAIL;
	}

	/** Return true if the current contest is "point-scoring" */
	isPointScoring(): boolean {
		return this.contestService.getScoreboardType() === SCOREBOARD_TYPE.POINT_SCORING;
	}

	/** Return the type of scoring for the current contest (pass-fail or point-scoring) */
	get scoreboardType() {
		return this.contestService.getScoreboardType();
	}
}
