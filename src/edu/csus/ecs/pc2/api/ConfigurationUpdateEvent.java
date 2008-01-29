package edu.csus.ecs.pc2.api;


/**
 * Contest Data Update, both configuration and run-time.
 * 
 * This event is triggered when any configuration data is changed (added, removed, updated).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
 // $HeadURL$
 
public class ConfigurationUpdateEvent {

    /**
     * Actions for ConfigurationUpdateEvent.
     * 
     * @see ConfigurationUpdateEvent
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {
        /**
         * Reset.
         * 
         */
        RESET,
        /**
         * Account added.
         */
        ACCOUNT_ADDED,
        /**
         * Account removed.
         */
        ACCOUNT_REMOVED,
        /**
         * Account updated.
         */
        ACCOUNT_UPDATED,
        /**
         * Title updated
         */
        TITLE_UPDATED,
        /**
         * Language added.
         */
        LANGUAGE_ADDED,
        /**
         * Language removed.
         */
        LANGUAGE_REMOVED,
        /**
         * Language updated.
         */
        LANGUAGE_UPDATED,
        /**
         * Problem added.
         */
        PROBLEM_ADDED,
        /**
         * Problem removed.
         */
        PROBLEM_REMOVED,
        /**
         * Problem updated.
         */
        PROBLEM_UPDATED,
        /**
         * Judgement added.
         */
        JUDGEMENT_ADDED,
        /**
         * Judgement removed.
         */
        JUDGEMENT_REMOVED,
        /**
         * Judgement updated.
         */
        JUDGEMENT_UPDATED,
    }

    private Action action;
    
    private IProblem problem;
    
    private ITeam team;
    
    private ILanguage language;
    
    private IJudgement judgement;

    // TODO DOC PARAMS
 
    /**
	 * @param action
	 * @param team
	 */
	public ConfigurationUpdateEvent(Action action, ITeam team) {
		super();
		this.action = action;
		this.team = team;
	}

	/**
	 * @param action
	 * @param problem
	 */
	public ConfigurationUpdateEvent(Action action, IProblem problem) {
		super();
		this.action = action;
		this.problem = problem;
	}
	
	/**
	 * @param action
	 * @param language
	 */
	public ConfigurationUpdateEvent(Action action, ILanguage language) {
		super();
		this.action = action;
		this.language = language;
	}

	/**
	 * @param action
	 * @param judgement
	 */
	public ConfigurationUpdateEvent(Action action, IJudgement judgement) {
		super();
		this.action = action;
		this.judgement = judgement;
	}


	/**
	 * Judgement that was changed.
	 * @return judgement info.
	 */
	public IJudgement getJudgement() {
		return judgement;
	}

	/**
	 * Language that was changed.
	 * @return language info.
	 */
	public ILanguage getLanguage() {
		return language;
	}

	/**
	 * Team that was changed
	 * @return Team information
	 */
	public ITeam getTeam() {
		return team;
	}

	/**
     * Get the action triggered.
     * 
     * @return the action triggered.
     */
    public Action getAction() {
        return action;
    }

    /**
     * Get problem which was changed.
     * @return
     */
	public IProblem getProblem() {
		return problem;
	}
}
