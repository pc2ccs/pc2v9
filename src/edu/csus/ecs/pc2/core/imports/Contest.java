package edu.csus.ecs.pc2.core.imports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
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
public class Contest {

    public static final String CONTEST_TAG = "contest";

    public static final String SETTINGS_TAG = "settings";

    public static final String PROBLEM_TAG = "problem";

    public static final String ACCOUNT_TAG = "account";

    public static final String CLARIFICATION_TAG = "clarification";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";

    // todo CODE

    // public IInternalContest createContest(String filename) throws IOException {
    //
    // RandomAccessFile file = new RandomAccessFile(filename, "r");
    // String line = null;
    //
    // // TODO process input file
    // while ((line = file.readLine()) != null) {
    //
    // }
    //
    // file.close();
    // file = null;
    //
    // return new InternalContest();
    // }

    public String toXML(IInternalContest contest) throws IOException {
        return toXML(contest, new edu.csus.ecs.pc2.core.model.Filter());
    }

    public String toXML(IInternalContest contest, Filter filter) throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(CONTEST_TAG);

        ContestInformation contestInformation = contest.getContestInformation();

        IMemento infoMemento = mementoRoot.createChild("settings");

        addContestInfo(infoMemento, contestInformation);

        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            addLanguageMemento(mementoRoot, language);
        }

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {

            addProblemMemento(mementoRoot, problem);
        }

        Vector<Account> accountList = new Vector<Account>();
        for (ClientType.Type ctype : ClientType.Type.values()) {
            accountList.addAll(contest.getAccounts(ctype));
        }

        Account[] accounts = (Account[]) accountList.toArray(new Account[accountList.size()]);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            addAccountMemento(mementoRoot, contest, account);
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

        return mementoRoot.saveToString();
    }

    private IMemento addContestInfo(IMemento infoMemento, ContestInformation contestInformation) {
        infoMemento.putString("title", contestInformation.getContestTitle());
        infoMemento.putString("url", contestInformation.getContestURL());

        infoMemento.putLong("maxFileSize", contestInformation.getMaxFileSize());

        infoMemento.putString("defaultAnswer", contestInformation.getJudgesDefaultAnswer());
        infoMemento.putString("teamDisplayMode", contestInformation.getTeamDisplayMode().toString());
        return infoMemento;
    }

    public IMemento addProfileMemento(IMemento memento, Profile profile, boolean currentProfile) {
        memento.putString("name", profile.getName());
        memento.putBoolean("currentprofile", currentProfile);
        memento.putString("contestid", profile.getContestId());
        memento.putString("description", profile.getDescription());
        memento.putString("path", profile.getProfilePath());
        memento.putString("pc2id", profile.getElementId().toString());
        return memento;
    }

    public IMemento addLanguageMemento(IMemento memento, Language language) {
        memento.putString("name", language.toString());
        memento.putString("compileCommandLine", language.getCompileCommandLine());
        memento.putString("ExecutableFilename", language.getExecutableIdentifierMask());
        memento.putString("ProgramExecutionCommandLine", language.getCompileCommandLine());
        memento.putString("pc2id", language.getElementId().toString());
        return memento;
    }

    public IMemento addProblemMemento(XMLMemento mementoRoot, Problem problem) {

        IMemento problemMemento = mementoRoot.createChild(PROBLEM_TAG);
        problemMemento.putString("name", problem.toString());
        if (problem.isUsingPC2Validator()) {
            problemMemento.putBoolean("useInternalValidator", true);
            problemMemento.putInteger("internalValidatorOption", problem.getWhichPC2Validator());
            problemMemento.putString("validatorCommand", problem.getValidatorCommandLine());
            problemMemento.putString("validatorProgram", problem.getValidatorProgramName());
            problemMemento.putBoolean("ignoreSpaces", problem.isIgnoreSpacesOnValidation());
        } else if (problem.isValidatedProblem()) {
            problemMemento.putString("validatorCommand", problem.getValidatorCommandLine());
            problemMemento.putString("validatorProgram", problem.getValidatorProgramName());
        }

        if (problem.getDataFileName() != null) {
            problemMemento.putString("datafilename", problem.getDataFileName());
        }

        if (problem.getAnswerFileName() != null) {
            problemMemento.putString("answerfilename", problem.getAnswerFileName());
        }

        problemMemento.putInteger("timeoutSecond", problem.getTimeOutInSeconds());

        problemMemento.putBoolean("active", problem.isActive());
        problemMemento.putBoolean("computerJudged", problem.isComputerJudged());
        problemMemento.putBoolean("manualReview", problem.isManualReview());
        problemMemento.putString("pc2id", problem.getElementId().toString());
        return problemMemento;

    }

    private IMemento addAccountMemento(XMLMemento mementoRoot, IInternalContest contest, Account account) {

        IMemento accountMemento = mementoRoot.createChild(ACCOUNT_TAG);

        ClientId clientId = account.getClientId();
        accountMemento.putString("name", account.getDisplayName());
        if (account.getGroupId() != null) {
            accountMemento.putString("group", contest.getGroup(account.getGroupId()).toString());
        }

        accountMemento.putString("type", clientId.getClientType().toString());
        accountMemento.putInteger("number", clientId.getClientNumber());
        accountMemento.putInteger("site", clientId.getSiteNumber());

        accountMemento.putString("password", account.getPassword());

        accountMemento.putBoolean("allowlogin", account.isAllowed(Permission.Type.LOGIN));
        accountMemento.putBoolean("showonscoreboard", account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD));
        accountMemento.putString("pc2triplet", clientId.getTripletKey());

        return accountMemento;
    }

    private IMemento addClarificationMemento(XMLMemento mementoRoot, IInternalContest contest, Clarification clarification) {

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

    private IMemento addRunMemento(XMLMemento mementoRoot, IInternalContest contest, Run run) {

        String languageName = contest.getLanguage(run.getLanguageId()).toString();
        String problemName = contest.getProblem(run.getProblemId()).toString();

        IMemento runMemento = mementoRoot.createChild("Run");
        runMemento.putInteger("site", run.getSiteNumber());
        runMemento.putInteger("number", run.getNumber());
        runMemento.putInteger("site", run.getSiteNumber());
        runMemento.putLong("elapsed", run.getElapsedMins());

        runMemento.putString("languageName", languageName);
        runMemento.putString("problemName", problemName);

        if (run.isJudged()) {
            runMemento.putBoolean("solved", run.isSolved());
        }

        JudgementRecord[] judgementRecords = run.getAllJudgementRecords();

        if (judgementRecords.length > 0) {
            for (int idx = judgementRecords.length - 1; idx >= 0; idx--) {
                addJudgementMemento(runMemento, contest, judgementRecords[idx], idx);
            }
        }

        try {
            RunFiles runFiles = contest.getRunFiles(run);
            if (runFiles != null) {
                String filename = runFiles.getMainFile().getName();
                if (filename != null) {
                    runMemento.putString("filename", filename);
                }
            }
        } catch (IOException e) {
            runMemento.putString("no_filename", "(missing)");
        } catch (ClassNotFoundException e) {
            runMemento.putString("no_filename", "(missing)");
        } catch (FileSecurityException e) {
            runMemento.putString("no_filename", "(missing)");
        }

        runMemento.putString("pc2id", run.getElementId().toString());
        return runMemento;
    }

    private IMemento addJudgementMemento(IMemento mementoRoot, IInternalContest contest, JudgementRecord judgementRecord, int number) {

        IMemento judgementMemento = mementoRoot.createChild(JUDGEMENT_TAG);
        judgementMemento.putInteger("id", number);
        judgementMemento.putBoolean("active", judgementRecord.isActive());
        judgementMemento.putBoolean("solved", judgementRecord.isSolved());
        judgementMemento.putString("judgement", contest.getJudgement(judgementRecord.getJudgementId()).toString());
        judgementMemento.putString("pc2id", judgementRecord.getJudgementId().toString());
        return judgementMemento;
    }

}
