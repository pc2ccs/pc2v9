package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * A filter for runs, clarifications by site, clients, problems, languages.
 * 
 * Provides a way to determine whether a run, clarification, etc. matches a list of problems, sites, etc. <br>
 * <P>
 * An example of filter to print count of matching the filter.
 * 
 * <pre>
 * IInternalContest contest;
 * 
 * Problem problem = contest.getProblems()[0];
 * 
 * Run[] runs = contest.getRuns();
 * 
 * Filter filter = new Filter();
 * filter.addProblem(problem);
 * 
 * System.out.println(&quot;Count of filtered runs : &quot; + filter.countRuns(runs));
 * System.out.println(&quot;Count of all runs      : &quot; + runs.length);
 * 
 * </pre>
 * 
 * A newly constructed instance of Filter always returns true for all <code>matches</code> methods. In this way the filter matches, like {@link #matches(Run)}, method can be used/coded
 * unconditionally and match all {@link edu.csus.ecs.pc2.core.model.Run}s, then when a criteria is added (via the Filter <code>add</code> methods) the runs will be filtered/matched appropriately.
 * <P>
 * 
 * This example shows how to use the filter unconditionally, if there are no criteria in the filter then all Runs will be processed. If there are criteria only runs matching the filter will be
 * processed.
 * 
 * <pre>
 * for (Run run : runs) {
 *     if (filter.matches(run)) {
 *         // process run here
 *     }
 * }
 * </pre>
 * 
 * Individual classes (no pun) of criteria can be turned on and off via the <code>setUsing<Class></code> methods, for example {@link #setUsingSitesFilter(boolean)}, if set to false, then all site
 * criteria in the filter will be ignored.
 * <P>

 * Likewise the entire filter can be turned on or off using the {@link #setFilterOn()} or {@link #setFilterOff()} method.
 * <P>
 * There are <code>count</code> methods that can be used to determine how many items match the Filter criteria. To learn the number of matching accounts, one can use the
 * {@link #countAccounts(Account[])} method, there other <code>count</code> methods {@link #countRuns(Run[])} to count {@link edu.csus.ecs.pc2.core.model.Run}s.
 * <P>
 * 
 * Each criterial class, there are a number of methods to add, remove, clar and turn of the filter for that class.
 * <li> {@link #addSite(Site)} - add a site to the filter and activates site filter 
 * <li> {@link #addSite(int)} - add a site by site number to the filter and activates site filter
 * <li> {@link #clearSiteList()} - clear all site filter and turn off site filter
 * <li> {@link #removeSite(Site)} - remove a site from the filter
 * <li> {@link #setUsingSitesFilter(boolean)}   to determine if site filter is on use {@link #isFilteringSites()}
 * <P> 
 * 
 * Here is a list of methods that use the filter on a class:
 * <li>{@link #countRuns(Run[])} - count runs matching this Filter instance.
 * <li>{@link #matches(Run)} - return true if criteria matches the input {@link edu.csus.ecs.pc2.core.model.Run}
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Filter implements Serializable {

    // TODO filter for permissions

    /**
     * 
     */
    private static final long serialVersionUID = -8373119928926075959L;

    /**
     * collection of chosen run states
     */
    private Hashtable<RunStates, Date> runStateHash = new Hashtable<RunStates, Date>();

    /**
     * filtering on run states.
     */
    private boolean filteringRunStates = false;

    private boolean filteringElapsedTime = false;

    private long startElapsedTimeMinutes = -1;

    private long endElapsedTimeMinutes = -1;

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
     * collection of problem ids
     */
    private Hashtable<Permission.Type, Date> permissionsHash = new Hashtable<Permission.Type, Date>();
    
    private boolean filteringPermissions = false;

    /**
     * collection of language ids
     */
    private Hashtable<ElementId, Date> languageIdHash = new Hashtable<ElementId, Date>();
    
    /**
     * Collection of site ids.
     */
    private Hashtable<Integer, Date> siteIdHash = new Hashtable<Integer, Date>();
    

    /**
     * filtering on language (language id)
     */
    private boolean filteringLanguages = false;
    
    /**
     * filtering on site (site number)
     */
    private boolean filteringSites = false;


    /**
     * collection of judgement ids
     */
    private Hashtable<ElementId, Date> judgementIdHash = new Hashtable<ElementId, Date>();

    /**
     * filtering on judgements (judgement id)
     */
    private boolean filteringJudgements = false;

    /**
     * Collection of Account/Clients
     */
    private Hashtable<ClientId, Date> clientIdHash = new Hashtable<ClientId, Date>();

    private Hashtable<Type, Date> clientTypeHash = new Hashtable<Type, Date>();

    private boolean filteringAccounts = false;

    /**
     * filtering for this site only
     */
    private boolean thisSiteOnly;

    private int filterSiteNumber = 0;

    private boolean filterEnabled = true;

    private boolean filteringDeleted = false;

    private ElementId getJudgementId(Run run) {
        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord != null) {
            return judgementRecord.getJudgementId();
        }
        return null;
    }
    
    /**
     * Returns true ("matching") if Run matches filter criteria.
     * 
     * @param run
     * @return true if the Run maches the filter, false otherwise.
     */
    public boolean matches(Run run) {
        if (filterEnabled){
            ElementId judgementElementId = getJudgementId(run);
            return matchesSites(run) && matchesAccount(run.getSubmitter()) && matchesRunState(run.getStatus()) && matchesProblem(run.getProblemId()) && matchesLanguage(run.getLanguageId())
             && matchesJudgement(judgementElementId) && matchesElapsedTimeSubmission(run) && matchesDeleted(run);
        } else {
            return true;
        }
    }

    private boolean matchesDeleted(Run run) {
        if (filterEnabled) {
            if (isFilteringDeleted()) {
                return !run.isDeleted();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean isFilteringDeleted() {
        return filteringDeleted;
    }

    /**
     * Match criteria against a clar.
     * 
     * @param clarification
     * @return true if the clarifications matches the filter
     */
    public boolean matches(Clarification clarification) {
        if (filterEnabled){
            return matchesSites(clarification) && matchesAccount(clarification.getSubmitter()) && matchesClarificationState(clarification.getState()) 
            && matchesProblem(clarification.getProblemId()) && matchesLanguage(clarification.getLanguageId()) && matchesElapsedTimeSubmission(clarification);

        } else {
            return true;
        }
    }

    /**
     * Match criteria against a clientId.
     * 
     * @param clientId
     * @return true if the sites match
     */
    public boolean matches(ClientId clientId) {
        if (filterEnabled){
            return matchesSites(clientId) && matchesAccount(clientId);
        } else {
            return true;
        }
    }

    public boolean matchesSites(ClientId clientId) {
        return matchesSites(clientId.getSiteNumber());
    }

    public boolean isThisSiteOnly() {
        return thisSiteOnly;
    }

    public void setThisSiteOnly(boolean thisSiteOnly) {
        this.thisSiteOnly = thisSiteOnly;
    }

    // TODO unused code ?
//    public int isSiteNumber() {
//        return filterSiteNumber;
//    }

    public void setSiteNumber(int siteNumber) {
        this.filterSiteNumber = siteNumber;
    }

    public int getSiteNumber() {
        return filterSiteNumber;
    }

    public void setUsingJudgementFilter(boolean turnOn) {
        filteringJudgements = turnOn;
    }

    /**
     * Is filtering using judgement list.
     * 
     * @return true if filter judgements.
     */
    public boolean isFilteringJudgements() {
        return filteringJudgements;
    }

    /**
     * Add a judgement to match against.
     * 
     * @param judgement
     */
    public void addJudgement(Judgement judgement) {
        addJudgement(judgement.getElementId());
    }

    /**
     * Add a judgement to match against.
     * 
     * Also turns filtering on for judgement list.
     * 
     * @param elementId
     */
    private void addJudgement(ElementId elementId) {
        filteringJudgements = true;
        judgementIdHash.put(elementId, new Date());
    }

    /**
     * Return true if judgement filter ON and matches a judgement in the filter list.
     * 
     * @param judgement
     */
    public boolean matches(Judgement judgement) {
        return matchesJudgement(judgement.getElementId());
    }
    /**
     * 
     * @param judgement
     * @deprecated use {@link #matches(Judgement)}
     */
    public boolean matchesJudgement(Judgement judgement) {
        return matchesJudgement(judgement.getElementId());
    }

    /**
     * Return true if judgement filter ON and matches a judgement in the filter list.
     * 
     * @param judgementId
     */
    public boolean matchesJudgement(ElementId judgementId) {

        if (filteringJudgements) {
            if (judgementId == null) {
                return false;
            } else {
                return judgementIdHash.containsKey(judgementId);
            }

        } else {
            return true;
        }
    }

    public void setUsingProblemFilter(boolean turnOn) {
        filteringProblems = turnOn;
    }

    /**
     * Is filtering using problem list.
     * 
     * @return true if filter problems.
     */
    public boolean isFilteringProblems() {
        return filteringProblems;
    }

    /**
     * Add a problem to match against.
     * 
     * @param problem
     */
    public void addProblem(Problem problem) {
        addProblem(problem.getElementId());
    }

    /**
     * Add a problem to match against.
     * 
     * Also turns filtering on for problem list.
     * 
     * @param elementId
     */
    private void addProblem(ElementId elementId) {
        problemIdHash.put(elementId, new Date());
        filteringProblems = true;
    }
    
    /**
     * Add a permission type to match against.
     * 
     * @param type
     */
    public void addPermission(Permission.Type type) {
        permissionsHash.put(type, new Date());
        filteringPermissions = true;
    }

    /**
     * Return true if problem filter ON and matches a problem in the filter list.
     * 
     * @param problem
     * @deprecated use {@link #matches(Problem)}
     */
    public boolean matchesProblem(Problem problem) {
        return matches(problem);
    }
    
    public boolean matches(Problem problem) {
        return matchesProblem(problem.getElementId());
    }

    /**
     * Return true if problem filter ON and matches a problem in the filter list.
     * 
     * @param problemId
     */
    public boolean matchesProblem(ElementId problemId) {
        if (filteringProblems) {
            return problemIdHash.containsKey(problemId);
        } else {
            return true;
        }
    }
    
    public boolean matches(Permission.Type type) {
        if (filteringPermissions) {
            return permissionsHash.containsKey(type);
        } else {
            return true;
        }
    }
    
 
    public void setUsingPermissionFilter (boolean turnOn){
        filteringPermissions = turnOn;
    }

    public void setUsingLanguageFilter(boolean turnOn) {
        filteringLanguages = turnOn;
    }

    /**
     * Turn the sites filter on or off.
     * 
     * This does not clear the sites filter information,
     * the {@link #matches(Run)} and other <code>matches</code>
     * methods will ignore the sites criteria.
     * <P>
     * The {@link #addSite(int)} and {@link #addSite(Site)} will
     * effectively invoke a setUsingSitesFilter(true).
     * 
     * @param turnOn true means ignore site criteria
     */
    public void setUsingSitesFilter(boolean turnOn) {
        filteringSites = turnOn;
    }

    public boolean isFilteringSites() {
        return filteringSites;
    }
    
    /**
     * Is filtering using permissions list.
     * 
     * @return true if filter languages.
     */
    public boolean isFilteringPermissions() {
        return filteringPermissions;
    }
    
    /**
     * Is filtering using language list.
     * 
     * @return true if filter languages.
     */
    public boolean isFilteringLanguages() {
        return filteringLanguages;
    }

    /**
     * Add a language to match against.
     * 
     * @param language
     */
    public void addLanguage(Language language) {
        addLanguage(language.getElementId());
    }

    /**
     * Add a language to match against.
     * 
     * Also turns filtering on for language list.
     * 
     * @param elementId
     */
    private void addLanguage(ElementId elementId) {
        languageIdHash.put(elementId, new Date());
        filteringLanguages = true;
    }

    /**
     * Return true if language filter ON and matches a language in the filter list.
     * 
     * @param language
     * @deprecated use {@link #matches(Language)}
     */
    
    public boolean matchesLanguage(Language language) {
        return matchesLanguage(language.getElementId());
    }
    
    public boolean matches(Language language) {
        return matchesLanguage(language.getElementId());
    }

    /**
     * Return true if language filter ON and matches a language in the filter list.
     * 
     * @param languageId
     */
    public boolean matchesLanguage(ElementId languageId) {
        if (filteringLanguages) {
            return languageIdHash.containsKey(languageId);
        } else {
            return true;
        }
    }

    /**
     * Add a site to match against.
     * 
     * @param site
     */
    public void addSite(Site site) {
        addSite(site.getSiteNumber());
    }

    /**
     * Add a site to match against.
     * 
     * Also turns filtering on for site list.
     * 
     * @param siteNumber
     */
    public void addSite(int siteNumber) {
        siteIdHash.put(new Integer(siteNumber), new Date());
        filteringSites = true;
    }
    
    /**
     * Returns true if submission matches sites filter.
     * @param submission a run or clarification
     * @return true if matches sites filter.
     */
    private boolean matchesSites (ISubmission submission) {
        return matchesSites (submission.getSiteNumber());
    }


    private boolean matchesSites(int siteNumber) {
        if (filteringSites) {
            return siteIdHash.containsKey(new Integer(siteNumber));
        } else {
            return true;
        }
    }

    /**
     * Return true if site filter ON and matches a site in the filter list.
     * 
     * @param site
     * @deprecated use {@link #matches(Site)}
     */
    public boolean matchesSite(Site site) {
        return matches(site);
    }
    
    public boolean matches(Site site) {
        return matchesSites(site.getSiteNumber());
    }

    /**
     * 
     * @param account
     * @return
     * @deprecated use {@link #matches(Account)}
     */
    public boolean matchesAccount(Account account) {
        return matches(account);
    }
    
    
    public boolean matches(Account account) {
        
        ClientId clientId = account.getClientId();
        
        if (! matchesSites(clientId.getSiteNumber())) {
            return false;
        }
        
        if (! matchesPermission(account)) {
            return false;
        }
        
        if (filteringAccounts) {
            if (matches(clientId)) {
                return matches(clientId.getClientType());
            }
            return false;
        } else {
            return true;
        }
    }
    
    public Permission.Type[] getPermissionsList() {
        Permission.Type[] permList = new Permission.Type[permissionsHash.size()];
        Enumeration<Permission.Type> enumeration = permissionsHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            Permission.Type element = (Permission.Type) enumeration.nextElement();
            permList[i] = element;
            i++;
        }
        return permList;
    }

    private boolean matchesPermission(Account account) {
        
        if (filteringPermissions) {

            Enumeration<Permission.Type> enumeration = permissionsHash.keys();
            while (enumeration.hasMoreElements()) {
                Permission.Type type = (Permission.Type) enumeration.nextElement();
                if (!account.isAllowed(type)) {
                    return false;
                }
            }

            return true;

        } else {
            return true;
        }
    }

    public boolean matches(Type type) {
        if (filteringAccounts) {
            if (clientTypeHash.size() == 0) {
                return true;
            } else {
                if (clientTypeHash.containsKey(Type.ALL)) {
                    return true;
                } else {
                    return clientTypeHash.containsKey(type);
                }

            }
        } else {
            return false;
        }
    }

    /**
     * Client Id matches both account filter and sites filter.
     * 
     * @param clientId
     * @return true if matches filter criteria
     */
    public boolean matchesAccount(ClientId clientId) {
        if (filteringAccounts) {
            // System.out.println(new FilterFormatter().getClientsShortList(getAccountList()));
            
            if (matchesSites(clientId)) {
                if (clientIdHash.size() == 0) {
                    return true;
                } else {
                    return clientIdHash.containsKey(clientId);
                }
            } else {
                return false;
            }
        } else {
            return matchesSites(clientId);
        }
    }
    
    /**
     * Clear judgements and turn judgement filtering off.
     *
     */
    public void clearJudgementList() {
        filteringJudgements = false;
        judgementIdHash = new Hashtable<ElementId, Date>();
    }



    /**
     * Clear accounts and turn account filtering off.
     *
     */
    public void clearAccountList() {
        filteringAccounts = false;
        clientIdHash = new Hashtable<ClientId, Date>();
    }
    
    public void clearClientTypesList() {
        clientTypeHash = new Hashtable<ClientType.Type, Date>();
    }

    /**
     * Clear problem and turn problem filtering off.
     *
     */
    public void clearProblemList() {
        filteringProblems = false;
        problemIdHash = new Hashtable<ElementId, Date>();
    }
    
    public void clearPermissionsList() {
        permissionsHash = new Hashtable<Permission.Type, Date>();
    }
    
    /**
     * Clear language and turn language filtering off.
     *
     */
    public void clearLanguageList() {
        filteringLanguages = false;
        languageIdHash = new Hashtable<ElementId, Date>();
    }
    
    /**
     * Clear site and turn site filtering off.
     *
     */
    public void clearSiteList() {
        filteringSites = false;
        siteIdHash = new Hashtable<Integer, Date>();
    }

    /**
     * Remove the input site from the site filter.
     * @param site
     */
    public void removeSite(Site site) {
        if (siteIdHash.containsKey(site.getElementId())) {
            siteIdHash.remove(site.getElementId());
            // TODO add setUsingSitesFilter(false); 
        }
    }

    /**
     * Remove problem from the problem filter.
     * @param problem
     */
    public void removeProblem(Problem problem) {
        if (problemIdHash.containsKey(problem.getElementId())) {
            problemIdHash.remove(problem.getElementId());
            // TODO add setUsingProblemFilter(false); 
        }
    }
    
    /**
     * Remove permission from the permission filter.
     * @param permission
     */
    public void removePermission(Permission.Type type) {
        if (permissionsHash.containsKey(type)) {
            permissionsHash.remove(type);
        }
    }
    
    /**
     * Remove language from the language filter.
     * 
     * @param language
     */
    public void removeLanguage(Language language) {
        if (languageIdHash.containsKey(language.getElementId())) {
            languageIdHash.remove(language.getElementId());
        }
    }

    /**
     * Remove permission from the permission filter.
     * 
     * @param permission
     */
    public void removePermission(Permission type) {
        if (permissionsHash.containsKey(type)) {
            permissionsHash.remove(type);
        }
    }
    
    /**
     * Get list of ElementIds for the judgement in the filter list.
     * 
     * @return list of element ids.
     */
    public ElementId[] getJudgementIdList() {
        ElementId[] elementIds = new ElementId[judgementIdHash.size()];
        Enumeration<ElementId> enumeration = judgementIdHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            ElementId element = (ElementId) enumeration.nextElement();
            elementIds[i] = element;
            i++;
        }
        return elementIds;
    }
   

    /**
     * Get list of ElementIds for the problems in the filter list.
     * 
     * @return list of element ids.
     */
    public ElementId[] getProblemIdList() {
        ElementId[] elementIds = new ElementId[problemIdHash.size()];
        Enumeration<ElementId> enumeration = problemIdHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            ElementId element = (ElementId) enumeration.nextElement();
            elementIds[i] = element;
            i++;
        }
        return elementIds;
    }
    
    /**
     * Get list of ElementIds for the languages in the filter list.
     * 
     * @return list of element ids.
     */
    public ElementId[] getLanguageIdList() {
        ElementId[] elementIds = new ElementId[languageIdHash.size()];
        Enumeration<ElementId> enumeration = languageIdHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            ElementId element = (ElementId) enumeration.nextElement();
            elementIds[i] = element;
            i++;
        }
        return elementIds;
    }

    /**
     * Get list of ElementIds for the sites in the filter list.
     * 
     * @return list of element ids.
     */
    public Integer[] getSiteIdList() {
        Integer[] elementIds = new Integer[siteIdHash.size()];
        Enumeration<Integer> enumeration = siteIdHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            Integer element = (Integer) enumeration.nextElement();
            elementIds[i] = element;
            i++;
        }
        return elementIds;
    }
    
    /**
     * Get list of ClientIds for the accounts in the filter list.
     * 
     * @return list of ClientId.
     */
    public ClientId[] getAccountList() {
        ClientId[] clientIds = new ClientId[clientIdHash.size()];
        Enumeration<ClientId> enumeration = clientIdHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            ClientId element = (ClientId) enumeration.nextElement();
            clientIds[i] = element;
            i++;
        }
        return clientIds;
    }
    
    /**
     * Get list of Types in filter.
     * 
     * @return
     */
    public ClientType.Type[] getClientTypes() {
        ClientType.Type[] types = new ClientType.Type[clientTypeHash.size()];
        Enumeration<ClientType.Type> enumeration = clientTypeHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            ClientType.Type value = (ClientType.Type) enumeration.nextElement();
            types[i] = value;
            i++;
        }
        return types;
        
    }
    
    public void setUsingRunStatesFilter(boolean turnOn) {
        filteringRunStates = turnOn;
    }

    public boolean isFilteringRunStates() {
        return filteringRunStates;
    }
    
    /**
     * Add an account to filter with.
     * @param account
     */
    public void addAccount(Account account) {
        addAccount(account.getClientId());
    }

    /**
     * Add an account to filter with.
     * @param clientId
     */
    public void addAccount(ClientId clientId) {
        filteringAccounts = true;
        clientIdHash.put(clientId, new Date());
    }

    /**
     * Add an account to filter with.
     * @param accounts
     */
    public void addAccounts(Account[] accounts) {
        for (Account account : accounts) {
            addAccount(account);
        }
    }

    public void removeAccount(Account account) {
        if (clientIdHash.get(account.getClientId()) != null) {
            clientIdHash.remove(account.getClientId());
        }
    }

    public void removeAccounts(Account[] accounts) {
        for (Account account : accounts) {
            removeAccount(account);
        }
    }

    public void addClientType(Type type) {
        filteringAccounts = true;
        clientTypeHash.put(type, new Date());
    }

    public void addRunState(RunStates runStates) {
        runStateHash.put(runStates, new Date());
        filteringRunStates = true;
    }

    /**
     * 
     * @param runStates
     * @return
     * @deprecated use {@link #matches(RunStates)}
     */
    public boolean matchesRunState(RunStates runStates) {
        return matches(runStates);
    }

    public boolean matches(RunStates runStates) {
        if (filteringRunStates) {
            return runStateHash.containsKey(runStates);
        } else {
            return true;
        }
    }

    
    public boolean matchesElapsedTime(Run run) {
        return matchesElapsedTimeSubmission(run);
    }

    public boolean matchesElapsedTime(Clarification clarification) {
        return matchesElapsedTimeSubmission(clarification);
    }

    public boolean matchesElapsedTimeSubmission(ISubmission submission) {
        long elapsedTime = submission.getElapsedMins();
        return matchesElapsedTime(elapsedTime);
    }
    
    protected boolean matchesElapsedTime(long elapsedTimeMinutes) {
        if (filteringElapsedTime) {
            if (startElapsedTimeMinutes != -1) {
                if (elapsedTimeMinutes < startElapsedTimeMinutes) {
                    return false;
                }
            }

            if (endElapsedTimeMinutes != -1) {
                if (elapsedTimeMinutes > endElapsedTimeMinutes) {
                    return false;
                }
            }

            return true;

        } else {
            return true;
        }
    }

    public void clearRunStatesList() {
        filteringRunStates = false;
        runStateHash = new Hashtable<RunStates, Date>();
    }

    public void removeRunState(RunStates runStates) {
        if (runStateHash.containsKey(runStates)) {
            runStateHash.remove(runStates);
        }
    }

    public void removeClarificationStates(ClarificationStates clarificationStates) {
        if (clarificationStateHash.containsKey(clarificationStates)) {
            clarificationStateHash.remove(clarificationStates);
        }
    }

    public ClarificationStates[] getClarificationStatesList() {
        ClarificationStates[] clarificationStates = new ClarificationStates[clarificationStateHash.size()];
        Enumeration<ClarificationStates> enumeration = clarificationStateHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            clarificationStates[i] = (ClarificationStates) enumeration.nextElement();
            i++;
        }
        return clarificationStates;
    }

    public RunStates[] getRunStates() {
        RunStates[] runStates = new RunStates[runStateHash.size()];
        Enumeration<RunStates> enumeration = runStateHash.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            runStates[i] = (RunStates) enumeration.nextElement();
            i++;
        }
        return runStates;
    }
    /**
     * 
     * @param clarificationStates
     * @return
     * @deprecated use {@link #matches(ClarificationStates)}
     */
    public boolean matchesClarificationState(ClarificationStates clarificationStates) {
        return matches(clarificationStates);
    }
    public boolean matches(ClarificationStates clarificationStates) {
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

    public void clearClarificationStateList() {
        filteringClarificationStates = false;
        clarificationStateHash = new Hashtable<ClarificationStates, Date>();
    }

    public String toString() {

        if (filterEnabled) {
            
            String filterInfo = "Filter ON";
            
            if (thisSiteOnly || filteringSites) {
                filterInfo += " Site(s) ";
            }
            if (filteringProblems) {
                filterInfo += " problem(s) ";
            }
            if (filteringJudgements) {
                filterInfo += " judgement(s) ";
            }
            if (filteringLanguages) {
                filterInfo += " language(s) ";
            }
            if (filteringRunStates) {
                filterInfo += " run state(s)";
            }
            if (filteringClarificationStates) {
                filterInfo += " clar state(s)";
            }
            if (filteringAccounts) {
                filterInfo += " account(s))";
            }
            if (filteringPermissions) {
                filterInfo += " permissions(s))";
            }

            return filterInfo;
        } else {
            return "";
        }
    }

    /**
     * Start time range time.
     * @return start time in minutes
     */
    public long getStartElapsedTime() {
        return startElapsedTimeMinutes;
    }

    /**
     * Set the start time for the elapsed time range.
     * @param newStartElapsedTimeMinutes
     */
    public void setStartElapsedTime(long newStartElapsedTimeMinutes) {
        filteringElapsedTime = true;
        this.startElapsedTimeMinutes = newStartElapsedTimeMinutes;
    }

    public long getEndElapsedTime() {
        return endElapsedTimeMinutes;
    }

    /**
     * Set the endtime time for the elapsed time range.
     * @param endElapsedTimeMS end elapsed time in MS.
     */
    public void setEndElapsedTime(long endElapsedTimeMS) {
        filteringElapsedTime = true;
        this.endElapsedTimeMinutes = endElapsedTimeMS;
    }

    public boolean isFilteringElapsedTime() {
        return filteringElapsedTime;
    }

    public void setFilteringElapsedTime(boolean filteringElapsedTime) {
        this.filteringElapsedTime = filteringElapsedTime;
    }

    public boolean isFilteringAccounts() {
        return filteringAccounts;
    }

    public void setFilteringAccounts(boolean filteringAccounts) {
        this.filteringAccounts = filteringAccounts;
    }

    public void setFilterOff() {
        filterEnabled = false;
    }

    public void setFilter (boolean filterOn){
        filterEnabled = filterOn;
    }
    
    public void setFilterOn () {
        filterEnabled = true;
    }

    /**
     * Is one of the filters active?.
     * 
     * @return true if filter is on, false if not filtering.
     */
    public boolean isFilterOn() {
        if (filterEnabled) {
            return filteringSites || filteringAccounts || filteringClarificationStates || filteringProblems 
                || filteringJudgements || filteringLanguages || filteringElapsedTime || filteringRunStates
                || filteringPermissions
                || thisSiteOnly;
        } else {
            return false;
        }
    }

    public boolean isFilteringClarificationStates() {
        return filteringClarificationStates;
    }

    /**
     * Clear elapsed time range (filtering on time range)
     */
    public void clearElapsedTimeRange() {
        startElapsedTimeMinutes = -1;
        endElapsedTimeMinutes = -1;
        filteringElapsedTime = false;
    }
    
    /**
     * Count the runs that match this filter.
     * 
     * @param runs - list of runs
     * @return number of runs that match this filter.
     */
    public int countRuns(Run [] runs){
        int count = 0;
        for (Run run : runs) {
            if (matches(run)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the clarifications that match this filter.
     * @param clarifications
     * @return number of clarifications matching this filter.
     */
    public int countClarifications(Clarification[] clarifications) {
        int count = 0;
        for (Clarification clarification : clarifications) {
            if (matches(clarification)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count the accounts that match this filter.
     * @param accounts
     * @return number of accounts matching this filter.
     */
    public int countAccounts (Account [] accounts) {
        int count = 0;
        for (Account account : accounts) {
            if (matches(account)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count the ClientIds that match this filter.
     * @param clientIds
     * @return number of ClientIds matching this filter.
     */
    public int countClientIds (ClientId [] clientIds) {
        int count = 0;
        for (ClientId clientId : clientIds) {
            if (matchesAccount(clientId)){
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns list of runs for current filter.
     * 
     * @param runs
     * @return list of runs which match this filter.
     */
    public Run[] getRuns(Run[] runs) {

        Vector<Run> listOfRuns = new Vector<Run>();

        for (Run run : runs) {
            if (matches(run)) {
                listOfRuns.add(run);
            }
        }

        return (Run[]) listOfRuns.toArray(new Run[listOfRuns.size()]);
    }

    /**
     * Returns list of clarifications for current filter.
     * 
     * @param clarifications
     * @return list of clarifications which match this filter.
     */
    public Clarification[] getClarifications(Clarification[] clarifications) {

        Vector<Clarification> list = new Vector<Clarification>();

        for (Clarification clarification : clarifications) {
            if (matches(clarification)) {
                list.add(clarification);
            }
        }

        return (Clarification[]) list.toArray(new Clarification[list.size()]);
    }

    public boolean matchesElapsedTime(RunTestCase runTestCase) {
        long elapsedTime = runTestCase.getElapsedMins();
        return matchesElapsedTime(elapsedTime);
    }

    public void setFilteringDeleted(boolean b) {
        filteringDeleted = b;
        
    }

}

