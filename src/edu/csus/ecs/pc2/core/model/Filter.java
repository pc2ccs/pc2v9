package edu.csus.ecs.pc2.core.model;

import java.util.Date;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * A filter for runs, clients, etc.
 * 
 * Provides a way to determine whether a run, clarification, etc. matches
 * a list of problems, sites, etc.
 * <br>
 * Use the add methods to add items (Problems, RunStates, etc) to match against.
 * <br>
 * Use the match and matches methods to determine if the Run, Clarification, etc matches
 * the filter.
 * <br>
 * Use the setUsing methods to turn on and off filtering of Problems, RunStates, etc.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class Filter {

    // TODO code filters for account, elapsed time, site, permissions, etc.

    /**
     * collection of chosen run states
     */
    private Hashtable<RunStates, Date> runStateHash = new Hashtable<RunStates, Date>();

    /**
     * filtering on run states.
     */
    private boolean filteringRunStates = false;

    /**
     * collection of chosen clarification states
     */
    private Hashtable<ClarificationStates, Date> clarificationStateHash = new Hashtable<ClarificationStates, Date>();

    /**
     * filtering on clarification state
     */
    private boolean filteringClarificationStates = false;

    /**
     * collection of problem ids
     */
    private Hashtable<ElementId, Date> problemIdHash = new Hashtable<ElementId, Date>();

    /**
     * filtering on problem (problem id)
     */
    private boolean filteringProblems = false;

    /**
     * filtering for this site only
     */
    private boolean thisSiteOnly;

    private int siteNumber = 0;

    private boolean isThisSite(ISubmission submission) {
        return siteNumber == submission.getSiteNumber() || siteNumber == 0;
    }

    private boolean isThisSite(ClientId clientId) {
        return siteNumber == clientId.getSiteNumber() || siteNumber == 0;
    }

    /**
     * Match criteria against a run. 
     * @param run
     */
    public boolean matches(Run run) {
        return isThisSite(run) && matchesRunState(run.getStatus()) && matchesProblem(run.getProblemId());
    }

    /**
     * Match criteria against a run.
     * @param clarification
     * @return
     */
    public boolean matches(Clarification clarification) {
        return isThisSite(clarification) && matchesClarificationState(clarification.getState()) && matchesProblem(clarification.getProblemId());
    }

    /**
     * Match criteria against a clientId.
     * @param clientId
     * @return
     */
    public boolean matches(ClientId clientId) {
        return isThisSite(clientId);
    }

    public boolean isThisSiteOnly() {
        return thisSiteOnly;
    }

    public void setThisSiteOnly(boolean thisSiteOnly) {
        this.thisSiteOnly = thisSiteOnly;
    }

    public int isSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    public int getSiteNumber() {
        return siteNumber;
    }
    
    public void setUsingProblemFilter (boolean turnOn){
        filteringProblems = turnOn;
    }

    /**
     * Is filtering using problem list.
     * @return true if filter problems.
     */
    public boolean isFilteringProblems () {
        return filteringProblems;
    }
  
    /**
     * Add a problem to match against.
     * @param problem
     */
    public void addProblem (Problem problem) {
        addProblem (problem.getElementId());
    }

    /**
     * Add a problem to match against.
     * 
     * Also turns filtering on for problem list.
     * @param elementId
     */
    public void addProblem (ElementId elementId) {
        problemIdHash.put( elementId,new Date());
        filteringProblems = true;
    }

    /**
     * Return true if problem filter ON and matches a problem in the filter list.
     * @param problem
     */
    public boolean matchesProblem(Problem problem){
        return matchesProblem(problem.getElementId());
    }

    /**
     * Return true if problem filter ON and matches a problem in the filter list.
     * @param problemId
     */
    public boolean matchesProblem(ElementId problemId) {
        if (filteringProblems) {
            return problemIdHash.containsKey(problemId);
        } else {
            return true;
        }
    }
    
    /**
     * Clears all problems from filter, sets to not filtering Problems.
     */
    public void clearProblemList(){
        filteringProblems = false;
        problemIdHash = new Hashtable<ElementId, Date>();
    }
    
    // TODO code add removeProblem(Problem)
    

    public void setUsingRunStatesFilter(boolean turnOn) {
        filteringRunStates = turnOn;
    }

    public boolean isFilteringRunStates() {
        return filteringRunStates;
    }

    public void addRunState(RunStates runStates) {
        runStateHash.put(runStates, new Date());
        filteringRunStates = true;
    }

    public boolean matchesRunState(RunStates runStates) {
        if (filteringRunStates) {
            return runStateHash.containsKey(runStates);
        } else {
            return true;
        }
    }
    
    public void clearRunStatesList(){
        filteringRunStates = false;
        runStateHash = new Hashtable<RunStates, Date>();
    }

    // TODO code add removeRunState(RunStates)

    // TODO code add accessors for filteringClarificationStates
    
    // TODO code add removelarificationState(ClarificationStates)

    public boolean matchesClarificationState(ClarificationStates clarificationStates) {
        if (filteringClarificationStates) {
            return clarificationStateHash.containsKey(clarificationStates);
        } else {
            return true;
        }
    }

    public void addClarificationState(ClarificationStates clarificationStates) {
        clarificationStateHash.put(clarificationStates, new Date());
        filteringClarificationStates = true;
    }
    
    public void clearClarificationStateList(){
        filteringClarificationStates = false;
        clarificationStateHash = new Hashtable<ClarificationStates, Date>();
    }

    public String toString() {
     
        if (thisSiteOnly 
                || filteringClarificationStates 
                || filteringProblems 
                || filteringRunStates ) {
            String filterInfo = "Filter ON";
            if (thisSiteOnly){
                filterInfo += " Site "+siteNumber;
            }
            if (filteringProblems){
                filterInfo += " problem(s) ";
            }
            if (filteringRunStates){
                filterInfo += " run state(s)";
            }
            if (filteringClarificationStates){
                filterInfo += " clar state(s)";
            }
            return filterInfo;
        } else {
            return "";
        }
    }
}
