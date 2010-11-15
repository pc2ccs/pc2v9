package edu.csus.ecs.pc2.core.imports;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Contest import/export XML.
 * 
 * Class used <li>to create output XML based on contest data <li>to create a IInternalContest based on input XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestXML {

    public static final String CONTEST_TAG = "contest";

    public static final String SETTINGS_TAG = "settings";

    public static final String PROBLEM_TAG = "problem";

    public static final String LANGUAGE_TAG = "language";

    public static final String ACCOUNT_TAG = "account";

    public static final String CLARIFICATION_TAG = "clarification";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";

    public static final String FILTER_TAG = "filter";

    public static final String PROFILES_TAG = "profiles";

    public static final String BALLOON_COLORS_LIST_TAG = "ballonlist";

    public static final String BALLOON_COLOR_TAG = "ballooncolors";

    public static final String SITES_TAG = "site";

    public static final String VERSION_TAG = "version";

    public static final String FILE_INFO_TAG = "fileinfo";
    
    private boolean showPasswords = true;

    public String toXML(IInternalContest contest) throws IOException {
        return toXML(contest, new Filter());
    }

    public String toXML(IInternalContest contest, Filter filter) throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(CONTEST_TAG);

        addVersionInfo (mementoRoot, contest);
        
        addFileInfo (mementoRoot);
        
        IMemento parent = mementoRoot.createChild(SETTINGS_TAG);
        ContestInformation contestInformation = contest.getContestInformation();
        addContestInfo(parent, contestInformation);

        Site [] sites = contest.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (Site site : sites){
            if (filter.matches(site)){
                addSiteMemento(mementoRoot, site);
            }
        }

        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            if (filter.matches(language)) {
                addLanguageMemento(mementoRoot, language);
            }
        }

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                addProblemMemento(mementoRoot, problem);
            }
        }
        
        Problem generalProblem = contest.getGeneralProblem();
        if (generalProblem != null && filter.matches(generalProblem)) {
            addProblemMemento(mementoRoot, generalProblem);
        }

        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            if (filter.matches(account)){
                addAccountMemento(mementoRoot, contest, account);
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                addRunMemento(mementoRoot, contest, run);
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            if (filter.matches(clarification)) {
                addClarificationMemento(mementoRoot, contest, clarification);
            }
        }
        
        
        IMemento baloonsMemento = mementoRoot.createChild(BALLOON_COLORS_LIST_TAG);
        
        for (Site site : sites){
            if (filter.matches(site)){
                addBalloonColorMemento(baloonsMemento, contest, site.getSiteNumber());
            }
        }

        
        // Add profiles
        
        Profile[] profiles = contest.getProfiles();
        Profile currentProfile = contest.getProfile();
        Arrays.sort(profiles, new ProfileComparatorByName());
        
        for (Profile profile : profiles){
            addProfileMemento(mementoRoot, profile, currentProfile.isSameAs(profile));
        }
        
        /// Add filters, if on/defined
        
        if (filter.isFilterOn()){
            addFilterMemento(mementoRoot, filter);
        }

        return mementoRoot.saveToString();
    }

    public IMemento addFileInfo(XMLMemento mementoRoot) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm.ss.SSS");
        String dateString = simpleDateFormat.format(new Date());
        
        simpleDateFormat = new SimpleDateFormat("EEE MMMM d, yyy H:mm:ss.SSS z");
        // "yyyy-MM-dd HH:mm.ss.SSS");
        String fullDateString = simpleDateFormat.format(new Date());

        VersionInfo versionInfo = new VersionInfo();
        IMemento memento = mementoRoot.createChild(FILE_INFO_TAG);
        memento.putString("date", dateString);
        memento.putString("fulldate", fullDateString);
        memento.putString("version", versionInfo.getSystemVersionInfo());
        return memento;
    }
    
    public IMemento addVersionInfo(XMLMemento mementoRoot, IInternalContest contest) {
        VersionInfo versionInfo = new VersionInfo();
        IMemento memento = mementoRoot.createChild(VERSION_TAG);
        memento.putString("pc2build", versionInfo.getBuildNumber());
        memento.putString("builddate", versionInfo.getVersionDate());

        memento.putInteger("major", getVersionInteger(versionInfo.getVersionNumber(), 0));
        memento.putInteger("minor", getVersionInteger(versionInfo.getVersionNumber(), 1));
        memento.putInteger("subminor", getVersionInteger(versionInfo.getVersionNumber(), 2));
        memento.putString("version", getVersionTriplet(versionInfo.getVersionNumber()));
   
        memento.putString("fullversion", versionInfo.getVersionNumber());
        memento.putString("system", versionInfo.getSystemName());
        memento.putString("url", versionInfo.getSystemURL());
        return memento;
    }

    /**
     * Parses version string and returns a version triplet.
     * 
     * Expects a version string for the form: #.#.# <br>
     * Will trim input string and remove anything after the first space before parsing the string.
     * 
     * @param versionString
     * @return string in the form M.X.Y
     */
    public String getVersionTriplet(String versionString) {
        int num = getVersionInteger(versionString, 0);
        int second = getVersionInteger(versionString, 1);
        int third = getVersionInteger(versionString, 2);
        return num + "." + second + "." + third;
    }
    
    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    protected int getVersionInteger(String versionNumber, int i) {
        
        String firstPart = versionNumber.trim().replaceFirst(" .*", ""); // remove last part
        String [] parts = firstPart.split("[.]");
        
        if (i < parts.length){
            return getIntegerValue (parts[i]);
        }
        
        return 0;
    }

    public IMemento addBalloonColorMemento(IMemento mementoRoot, IInternalContest contest, int siteNumber) {

        BalloonSettings balloonSettings = contest.getBalloonSettings(siteNumber);

        Problem [] problems = contest.getProblems();
        
        if (balloonSettings == null){
            return mementoRoot;
        }
        
        int foundCount = 0;
        for (Problem problem : problems){
            
            String name = balloonSettings.getColor(problem);
            if (name != null){
                foundCount ++;
            }
        }
        
        // if found colors then output colors
        
        if (foundCount > 0){
            
            for (Problem problem : problems){
                String name = balloonSettings.getColor(problem);
                if (name != null){
                    
                    IMemento memento = mementoRoot.createChild(BALLOON_COLOR_TAG);
                    memento.putString("color", name);
                    memento.putInteger("sitenumber", siteNumber);
                    memento.putString("problem", problem.getDisplayName());
                    memento.putString("problemid", problem.getElementId().toString());
                }
            }
        }
        
        return mementoRoot;
    }

    public IMemento addSiteMemento (XMLMemento mementoRoot, Site site) {
        
        IMemento memento = mementoRoot.createChild(SITES_TAG);
        memento.putInteger("number", site.getSiteNumber());
        memento.putString("pc2id", site.getElementId().toString());
        memento.putString("name", site.getDisplayName());
        memento.putString("password", site.getPassword());
        
        String hostName = site.getConnectionInfo().getProperty(Site.IP_KEY);
        String portStr = site.getConnectionInfo().getProperty(Site.PORT_KEY);
        
        memento.putString(Site.IP_KEY, hostName);
        memento.putString(Site.PORT_KEY, portStr);
        
        return memento;
    }
    
    public IMemento addFilterMemento(XMLMemento mementoRoot, Filter filter) {
        IMemento memento = mementoRoot.createChild(FILTER_TAG);
        memento.putString("summary", filter.toString());
        return memento;
    }

    public IMemento addContestInfo(IMemento memento, ContestInformation contestInformation) {
        memento.putString("title", contestInformation.getContestTitle());
        memento.putString("url", contestInformation.getContestURL());
        memento.putLong("maxFileSize", contestInformation.getMaxFileSize());
        memento.putString("defaultAnswer", contestInformation.getJudgesDefaultAnswer());
        memento.putString("teamDisplayMode", contestInformation.getTeamDisplayMode().toString());
        return memento;
    }
    
    public IMemento addProfileMemento(IMemento mementoRoot, Profile profile, boolean currentProfile) {
        IMemento memento = mementoRoot.createChild(PROFILES_TAG);
        memento.putString("name", profile.getName());
        memento.putBoolean("currentprofile", currentProfile);
        memento.putString("contestid", profile.getContestId());
        memento.putString("description", profile.getDescription());
        memento.putString("path", profile.getProfilePath());
        memento.putString("pc2id", profile.getElementId().toString());
        return memento;
    }

    public IMemento addLanguageMemento(IMemento mementoRoot, Language language) {
        
        IMemento memento = mementoRoot.createChild(LANGUAGE_TAG);
        memento.putString("name", language.toString());
        memento.putString("compileCommandLine", language.getCompileCommandLine());
        memento.putString("ExecutableFilename", language.getExecutableIdentifierMask());
        memento.putString("ProgramExecutionCommandLine", language.getCompileCommandLine());
        memento.putString("pc2id", language.getElementId().toString());
        return memento;
    }

    public IMemento addProblemMemento(IMemento mementoRoot, Problem problem) {

        IMemento memento = mementoRoot.createChild(PROBLEM_TAG);
        memento.putString("name", problem.toString());
        if (problem.isUsingPC2Validator()) {
            memento.putBoolean("useInternalValidator", true);
            memento.putInteger("internalValidatorOption", problem.getWhichPC2Validator());
            memento.putString("validatorCommand", problem.getValidatorCommandLine());
            memento.putString("validatorProgram", problem.getValidatorProgramName());
            memento.putBoolean("ignoreSpaces", problem.isIgnoreSpacesOnValidation());
        } else if (problem.isValidatedProblem()) {
            memento.putString("validatorCommand", problem.getValidatorCommandLine());
            memento.putString("validatorProgram", problem.getValidatorProgramName());
        }

        if (problem.getDataFileName() != null) {
            memento.putString("datafilename", problem.getDataFileName());
        }

        if (problem.getAnswerFileName() != null) {
            memento.putString("answerfilename", problem.getAnswerFileName());
        }

        memento.putInteger("timeoutSecond", problem.getTimeOutInSeconds());

        memento.putBoolean("active", problem.isActive());
        memento.putBoolean("computerJudged", problem.isComputerJudged());
        memento.putBoolean("manualReview", problem.isManualReview());
        memento.putString("pc2id", problem.getElementId().toString());
        return memento;

    }

    public IMemento addAccountMemento(IMemento mementoRoot, IInternalContest contest, Account account) {

        IMemento accountMemento = mementoRoot.createChild(ACCOUNT_TAG);
        ClientId clientId = account.getClientId();
        accountMemento.putString("name", account.getDisplayName());
        if (account.getGroupId() != null) {
            accountMemento.putString("group", contest.getGroup(account.getGroupId()).toString());
        }

        accountMemento.putString("type", clientId.getClientType().toString());
        accountMemento.putInteger("number", clientId.getClientNumber());
        accountMemento.putInteger("site", clientId.getSiteNumber());

        if (showPasswords) {
            accountMemento.putString("password", account.getPassword());
        }

        accountMemento.putBoolean("allowlogin", account.isAllowed(Permission.Type.LOGIN));
        accountMemento.putBoolean("showonscoreboard", account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD));
        accountMemento.putString("pc2triplet", clientId.getTripletKey());

        return accountMemento;
    }

    public IMemento addClarificationMemento(IMemento mementoRoot, IInternalContest contest, Clarification clarification) {

        String problemName = contest.getProblem(clarification.getProblemId()).toString();

        IMemento clarificationMemento = mementoRoot.createChild(CLARIFICATION_TAG);
        clarificationMemento.putInteger("site", clarification.getSiteNumber());
        clarificationMemento.putInteger("number", clarification.getNumber());
        clarificationMemento.putString("problem", problemName);

        clarificationMemento.putString("question", clarification.getQuestion());
        if (clarification.getAnswer() != null && clarification.isAnswered()) {
            clarificationMemento.putString("answer", clarification.getAnswer());
            clarificationMemento.putBoolean("sendToAll", clarification.isSendToAll());
        }
        clarificationMemento.putString("pc2probid", contest.getProblem(clarification.getProblemId()).getElementId().toString());

        return clarificationMemento;

    }

    public IMemento addRunMemento(IMemento mementoRoot, IInternalContest contest, Run run) {

        String languageName = contest.getLanguage(run.getLanguageId()).toString();
        String problemName = contest.getProblem(run.getProblemId()).toString();

        IMemento memento = mementoRoot.createChild(RUN_TAG);
        memento.putInteger("site", run.getSiteNumber());
        memento.putInteger("number", run.getNumber());
        memento.putInteger("site", run.getSiteNumber());
        memento.putLong("elapsed", run.getElapsedMins());

        memento.putString("languageName", languageName);
        memento.putString("problemName", problemName);

        if (run.isJudged()) {
            memento.putBoolean("solved", run.isSolved());
        }

        JudgementRecord[] judgementRecords = run.getAllJudgementRecords();

        if (judgementRecords.length > 0) {
            for (int idx = judgementRecords.length - 1; idx >= 0; idx--) {
                addJudgementMemento(memento, contest, judgementRecords[idx], idx);
            }
        }

        try {
            RunFiles runFiles = contest.getRunFiles(run);
            if (runFiles != null) {
                String filename = runFiles.getMainFile().getName();
                if (filename != null) {
                    memento.putString("filename", filename);
                }
            }
        } catch (IOException e) {
            memento.putString("no_filename", "(missing)");
        } catch (ClassNotFoundException e) {
            memento.putString("no_filename", "(missing)");
        } catch (FileSecurityException e) {
            memento.putString("no_filename", "(missing)");
        }

        memento.putString("pc2id", run.getElementId().toString());
        return memento;
    }

    public IMemento addJudgementMemento(IMemento mementoRoot, IInternalContest contest, JudgementRecord judgementRecord, int number) {

        IMemento memento = mementoRoot.createChild(JUDGEMENT_TAG);
        memento.putInteger("id", number);
        memento.putBoolean("active", judgementRecord.isActive());
        memento.putBoolean("solved", judgementRecord.isSolved());
        memento.putString("judgement", contest.getJudgement(judgementRecord.getJudgementId()).toString());
        memento.putString("pc2id", judgementRecord.getJudgementId().toString());
        return memento;
    }
    
    public void setShowPasswords(boolean showPasswords) {
        this.showPasswords = showPasswords;
    }
    
    public boolean isShowPasswords() {
        return showPasswords;
    }
}

