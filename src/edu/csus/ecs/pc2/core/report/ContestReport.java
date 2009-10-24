package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
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
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Contest XML output.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class ContestReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 8827529273455158045L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    public IMemento getLanguageMemento(IMemento memento, Language language) {
        memento.putString("name", language.toString());
        memento.putString("compileCommandLine", language.getCompileCommandLine());
        memento.putString("ExecutableFilename", language.getExecutableIdentifierMask());
        memento.putString("ProgramExecutionCommandLine", language.getCompileCommandLine());
        return memento;
    }

    public IMemento getProblemMemento(XMLMemento mementoRoot, Problem problem) {

        IMemento problemMemento = mementoRoot.createChild("Problem");
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
        return problemMemento;

    }

    public String getContestXML() throws IOException {

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("contest");

        ContestInformation contestInformation = contest.getContestInformation();

        IMemento infoMemento = mementoRoot.createChild("Settings");
        infoMemento.putString("title", contestInformation.getContestTitle());
        infoMemento.putString("url", contestInformation.getContestURL());

        infoMemento.putLong("maxFileSize", contestInformation.getMaxFileSize());

        infoMemento.putString("defaultAnswer", contestInformation.getJudgesDefaultAnswer());
        infoMemento.putString("teamDisplayMode", contestInformation.getTeamDisplayMode().toString());

        Language[] languages = contest.getLanguages();
        for (Language language : languages) {
            getLanguageMemento(mementoRoot, language);
        }

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {

            getProblemMemento(mementoRoot, problem);
        }
        
        Vector<Account> accountList = new Vector<Account>();
        for (ClientType.Type ctype : ClientType.Type.values()) {
            accountList.addAll( contest.getAccounts(ctype));
        }
        
        Account [] accounts = (Account[]) accountList.toArray(new Account[accountList.size()]);
        Arrays.sort(accounts, new AccountComparator());
        
        for (Account account : accounts){
            getAccountMemento(mementoRoot, account);
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                getRunMemento(mementoRoot, run);
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            if (filter.matches(clarification)) {
                getClarificationMemento(mementoRoot, clarification);
            }
        }

        return mementoRoot.saveToString();
    }
    
    private IMemento getAccountMemento(XMLMemento mementoRoot, Account account) {

        IMemento accountMemento = mementoRoot.createChild("Account");
        
        ClientId clientId = account.getClientId();
        accountMemento.putString("name", account.getDisplayName());
        if ( account.getGroupId() != null ){
            accountMemento.putString("group", contest.getGroup(account.getGroupId()).toString());    
        }
        
        accountMemento.putString("type", clientId.getClientType().toString());
        accountMemento.putInteger("number", clientId.getClientNumber());
        accountMemento.putInteger("site", clientId.getSiteNumber());
        
        accountMemento.putString("password", account.getPassword());
        
        accountMemento.putBoolean("allowlogin", account.isAllowed(Permission.Type.LOGIN));
        accountMemento.putBoolean("showonscoreboard", account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD));
        
        return accountMemento;
    }

    private void getClarificationMemento(XMLMemento mementoRoot, Clarification clarification) {

        String problemName = contest.getProblem(clarification.getProblemId()).toString();

        IMemento clarificationMemento = mementoRoot.createChild("Clarification");
        clarificationMemento.putInteger("site", clarification.getSiteNumber());
        clarificationMemento.putInteger("number", clarification.getNumber());
        clarificationMemento.putString("problem", problemName);

        clarificationMemento.putString("question", clarification.getQuestion());
        if (clarification.getAnswer() != null && clarification.isAnswered()) {
            clarificationMemento.putString("answer", clarification.getAnswer());
            clarificationMemento.putBoolean("sendToAll", clarification.isSendToAll());
        }
    }
   

    private void getRunMemento(XMLMemento mementoRoot, Run run) {

        String languageName = contest.getLanguage(run.getLanguageId()).toString();
        String problemName = contest.getProblem(run.getProblemId()).toString();

        IMemento runMemento = mementoRoot.createChild("Run");
        runMemento.putInteger("site", run.getSiteNumber());
        runMemento.putInteger("number", run.getNumber());
        runMemento.putInteger("site", run.getSiteNumber());
        runMemento.putLong("elapsed", run.getElapsedMins());

        runMemento.putString("languageName", languageName);
        runMemento.putString("problemName", problemName);
        
        // TODO solved
        
        JudgementRecord [] judgementRecords = run.getAllJudgementRecords();
        Arrays.sort (judgementRecords, Collections.reverseOrder()); 
        
        int idx = 1;
        for (JudgementRecord judgementRecord : judgementRecords){
            getJudgementMemento(runMemento, judgementRecord, idx++);
        }

        // TODO add filename to XML
        /**
         * This contest.getRunFiles is commented out because non-local runs cause exceptions that are logged, if there are enough exceptions it causes the Logging to lock up the JVM (or at least make
         * it spin)
         * 
         * See Bug 339.
         */

        // RunFiles runFiles = contest.getRunFiles(run);
        //
        // if (runFiles != null) {
        // String filename = runFiles.getMainFile().getName();
        // if (filename != null) {
        // runMemento.putString("filename", filename);
        // }
        // }
    }

    private IMemento getJudgementMemento(IMemento mementoRoot, JudgementRecord judgementRecord, int number) {
        
        IMemento judgementMemento = mementoRoot.createChild("Judgement");
        judgementMemento.putInteger("id", number);
        judgementMemento.putBoolean("active", judgementRecord.isActive());
        judgementMemento.putBoolean("solved", judgementRecord.isSolved());
        judgementMemento.putString("judgement", contest.getJudgement(judgementRecord.getJudgementId()).toString());

        return judgementMemento;
    }

    public void writeReport(PrintWriter printWriter) throws IOException {

        String xmlString = getContestXML();
        printWriter.println(xmlString);
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");
    }

    /**
     * 
     */
    public void createReportFile(String filename, Filter inFilter) throws IOException {

        filter = inFilter;

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public String[] createReport(Filter inFilter) {
        filter = inFilter;
        return new String[0];
    }

    public String createReportXML(Filter arg0) {
        try {
            return getContestXML();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public String getReportTitle() {
        return "Contest";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Contest Report";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
