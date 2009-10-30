package edu.csus.ecs.pc2.core.db;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * interface to file/db storage.
 * 
 * Methods to save information names start with 'store'.<br>
 * Methods to load/read information names start with 'read'.<br>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IDatabase {

    void setProfile(Profile profile) throws DatabaseException;

    Profile getProfile() throws DatabaseException;

    void storeProblem(Problem problem) throws DatabaseException;

    void storeProblem(Problem problem, ProblemDataFiles problemDataFiles) throws DatabaseException;

    Problem[] readProblems() throws DatabaseException;

    Problem readProblem(ElementId elementId) throws DatabaseException;

    ProblemDataFiles readProblemDataFile(Problem problem) throws DatabaseException;

    void storeGeneralProblem(Problem problem) throws DatabaseException;

    /**
     * The General problem is used to report contest clarifications. 
     * @param elementId
     * @return
     * @throws DatabaseException
     */
    Problem readGeneralProblem(ElementId elementId) throws DatabaseException;

    void storeLanguage(Language language) throws DatabaseException;

    void storeLanguages(Language[] languages) throws DatabaseException;

    Language[] readLanguages() throws DatabaseException;

    Language readLanguage(ElementId elementId) throws DatabaseException;

    void storeContestTime(ContestTime contestTime) throws DatabaseException;

    ContestTime[] readContestTimes() throws DatabaseException;

    ContestTime readContestTime(ElementId elementId) throws DatabaseException;

    void storeSite(Site site) throws DatabaseException;

    void storeSites(Site[] sites) throws DatabaseException;

    Site[] readSites() throws DatabaseException;

    Site readSite(ElementId elementId) throws DatabaseException;

    void storeJudgement(Judgement judgement) throws DatabaseException;

    void storeJudgements(Judgement[] judgements) throws DatabaseException;

    Judgement[] readJudgements() throws DatabaseException;

    Judgement readJudgement(ElementId elementId) throws DatabaseException;

    void storeAccount(Account account) throws DatabaseException;

    void storeAccounts(Account[] accounts) throws DatabaseException;

    Account[] readAccounts() throws DatabaseException;

    Account readAccount(ElementId elementId) throws DatabaseException;

    void storeGroup(Group group) throws DatabaseException;

    void storeGroups(Group[] groups) throws DatabaseException;

    Group[] readGroups() throws DatabaseException;

    Group readGroup(ElementId elementId) throws DatabaseException;

    void storeContestInformation(ContestInformation contestInformation) throws DatabaseException;

    ContestInformation[] readContestInformations() throws DatabaseException;

    ContestInformation readContestInformation(ElementId elementId) throws DatabaseException;

    void storeBalloonSettings(BalloonSettings balloonSettings) throws DatabaseException;

    void storeBalloonSettingss(BalloonSettings[] balloonSettings) throws DatabaseException;

    BalloonSettings[] readBalloonSettingss() throws DatabaseException;

    BalloonSettings readBalloonSettings(ElementId elementId) throws DatabaseException;

    void storeClientSettings(ClientSettings clientSettings) throws DatabaseException;

    void storeClientSettingss(ClientSettings[] clientSettings) throws DatabaseException;

    ClientSettings[] readClientSettingss() throws DatabaseException;

    ClientSettings readClientSettings(ElementId elementId) throws DatabaseException;

    void storeSiteNumber(int siteNumber) throws DatabaseException;

    int readSiteNumber() throws DatabaseException;

    void storeRun(Run run) throws DatabaseException;

    void storeRuns(Run[] runs, RunFiles[] runFiles) throws DatabaseException;

    void storeRun(Run run, RunFiles runFiles) throws DatabaseException;

    Run[] readRuns() throws DatabaseException;

    Run readRun(ElementId elementId) throws DatabaseException;

    Run getRunFiles(Run run) throws DatabaseException;

    void storeClarification(Clarification clarification) throws DatabaseException;

    void storeClarifications(Clarification[] clarifications) throws DatabaseException;

    Clarification[] readClarifications() throws DatabaseException;

    Clarification readClarification(ElementId elementId) throws DatabaseException;

    void storeRunResultFiles(Run run, RunResultFiles runResultFiles) throws DatabaseException;

    RunResultFiles readRunResultFiles(Run run) throws DatabaseException;

    RunResultFiles readRunResultFiles(ElementId elementId) throws DatabaseException;
}
