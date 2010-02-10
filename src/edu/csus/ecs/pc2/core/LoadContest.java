package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.LanguagesReport;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Load/Create contest data based on Runs Version 8 content and format report.
 * 
 * Will create a new Contest or add to the existing contest, see {@link #loadContest(String)} and
 * {@link #loadContest(String, IInternalContest)}.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadContest {

    /**
     * Creates IInternalContestontest from Runs Version 8 content and format report.
     * 
     * Will create a new Contest or add to the existing contest, see {@link #loadContest(String)} and
     * {@link #loadContest(String, IInternalContest)}.
     * 
     * <P>
     * See {@link #addRunToContest(IInternalContest, String[])} for details about the input file lines format.
     * <P>
     * Will ignore blank lines, lines starting with # and lines that do not have exactly N pipe-delimited fields.
     * 
     * @param filename
     * @return
     * @throws IOException
     */
    public IInternalContest loadContest(String filename) throws IOException {

        return loadContest(filename, null);
    }

    /**
     * Loads contest data based on version 8 run file.
     * 
     * If contest is null will create new contest, to just create a contest from the file use {@link #loadContest(String)}.
     * <P>
     * See {@link #addRunToContest(IInternalContest, String[])} for details about the input file lines format.
     * <P>
     * Will ignore blank lines, lines starting with # and lines that do not have exactly N pipe-delimited fields.
     * 
     * @param filename
     * @param contest
     * @return contest based on info in filename.
     * @throws IOException
     */
    public IInternalContest loadContest(String filename, IInternalContest contest) throws IOException {

        if (contest == null) {
            contest = new InternalContest();
        }

        RandomAccessFile file = new RandomAccessFile(filename, "r");
        String line;

        int runCount = 0;

        while ((line = file.readLine()) != null) {

            if (line.trim().startsWith("#")) {
                continue;
            }

            if (line.trim().length() == 0) {
                continue;
            }

            String[] fields = line.trim().split("[|]");

            if (fields.length == 19) {
                try {
                    Date startTime = new Date();
                    Run run = addRunToContest(contest, fields);
                    long seconds = new Date().getTime() - startTime.getTime();
                    runCount++;
                    System.out.println(seconds + " ms, count=" + runCount + " for " + run + " elap=" + run.getElapsedMins());
                } catch (ClassNotFoundException e) {
                    System.out.println("Exception for line: " + line);
                    e.printStackTrace();
                } catch (FileSecurityException e) {
                    System.out.println("Exception for line: " + line);
                    e.printStackTrace();
                }
            }
        }
        file.close();
        return contest;
    }

    /**
     * Add a run into the contest from fields.
     * 
     * Fields are from the Version 8 run report.
     * 
     * <pre>
     * Run report example field data
     * 0 run 1
     * 1 site 1
     * 2 proxy
     * 3 team 7
     * 4 team7:Belarusian State University
     * 5 prob F - Glenbow Museum-751569314436954419:F - Glenbow Museum
     * 6 lang GNU C++--7537629642328856939:GNU C++
     * 7 tocj
     * 8 os Linux
     * 9 sel false
     * 10 tocj false
     * 11 jc true
     * 12 17
     * 13 rid Run--4515313715917613918
     * 14 mmfr false
     * 15 del? false
     * 16 jt 0
     * 17 jby judge7
     * 18 jci No - Wrong Answer
     * </pre>
     * 
     * @param contest
     * @param fields
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Run addRunToContest(IInternalContest contest, String[] fields) throws IOException, ClassNotFoundException,
            FileSecurityException {

        int siteNumber = getNumber(stripString(fields[1], "site "));
        // int runNumber = getNumber(stripString(fields[0], "run "));
        int teamNumber = getNumber(stripString(fields[3], "team "));

        long elapsedMinutes = getNumber(stripString(fields[12], ""));

        String teamName = getField(fields[4], ":", 1).trim();
        if (teamName.length() == 0) {
            teamName = "team" + teamNumber;
        }
        String languageName = getField(fields[6], ":", 1);
        String problemName = getField(fields[5], ":", 1);

        Language language = createLanguage(contest, languageName);
        Problem problem = createProblem(contest, problemName);
        Account account = createAccount(contest, siteNumber, teamNumber, teamName);

//        System.out.println(" debug 22 " + account.getDisplayName());

        Run run = new Run(account.getClientId(), language, problem);

        // Add Judgement

        boolean solved = false;

        String judgingComplete = stripString(fields[11], "jc ");
        if (judgingComplete.trim().equals("true")) {

            solved = stripString(fields[14], "mmfr").trim().equals("true");

            String judgementName = stripString(fields[18], "jci").trim();

            if (!solved) {
                if (!judgementName.startsWith("No - ")) {
                    judgementName = "No - " + judgementName;
                }
            }

            Judgement judgement = createJudgement(contest, judgementName.trim());

            String judgedBy = stripString(fields[17], "jby judge");
            Account judgeAccount = null;
            String judgeAccountName = "judge";

            boolean isAdminAccount = judgedBy.startsWith("jby adm");
            if (isAdminAccount) {
                judgedBy = stripString(judgedBy, "jby administrator");
                judgeAccountName = "administrator";
            }
            int judgeNumber = getNumber(judgedBy);
            judgeAccountName += judgeNumber;

            if (isAdminAccount) {
                judgeAccount = createAccount(contest, siteNumber, judgeNumber, judgeAccountName, Type.ADMINISTRATOR);

            } else {
                judgeAccount = createAccount(contest, siteNumber, judgeNumber, judgeAccountName, Type.JUDGE);
            }

            JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), judgeAccount.getClientId(), solved,
                    false, false);
            run.addJudgement(judgementRecord);
            run.setStatus(RunStates.JUDGED);
        }

        int maxRunNumber = getMaxRun(contest, siteNumber);

        createSite(contest, siteNumber, "Site " + siteNumber);

        run.setNumber(maxRunNumber + 1);
        run.setSiteNumber(siteNumber);

        run.setElapsedMins(elapsedMinutes);
        contest.addRun(run);

        // System.out.println(" debug run "+run.getNumber()+" "+problem+" "+language);
        // System.out.println(" debug run "+run);

        return run;
    }

    private Site createNewSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            props.put(Site.IP_KEY, "localhost");
        }

        if (portNumber == 0) {
            portNumber = 50002 + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }

    private Site createSite(IInternalContest contest, int siteNumber, String siteName) {

        Site site = contest.getSite(siteNumber);

        if (site == null) {
            site = createNewSite(siteNumber, siteName, null, 0);
            contest.addSite(site);
        }
        return site;
    }

    private int getMaxRun(IInternalContest contest, int siteNumber) {
        int count = 0;
        for (Run run : contest.getRuns()) {
            if (run.getSiteNumber() == siteNumber) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return existing judgement or create a new judgement in the contest.
     * 
     * If no judgements exist, will also create first (Yes) judgement.
     * 
     * @param contest
     * @param judgementName
     * @return
     */
    public Judgement createJudgement(IInternalContest contest, String judgementName) {

        if (contest.getJudgements().length == 0) {
            contest.addJudgement(new Judgement("Yes"));
        }

        for (Judgement judgement : contest.getJudgements()) {
            if (judgement.getDisplayName().equals(judgementName)) {
                return judgement;
            }
        }

        Judgement newJudgement = new Judgement(judgementName);
        contest.addJudgement(newJudgement);
        return newJudgement;
    }

    /**
     * Create a team account, or return it if it already exists.
     * 
     * @param contest
     * @param siteNumber
     * @param accountNumber
     * @param displayName
     * @return
     */
    public Account createAccount(IInternalContest contest, int siteNumber, int accountNumber, String displayName) {
        return createAccount(contest, siteNumber, accountNumber, displayName, ClientType.Type.TEAM);
    }

    /**
     * Create an account, or return it if it already exists.
     * 
     * @param contest
     * @param siteNumber
     * @param accountNumber
     * @param displayName
     * @param clientType
     * @return
     */
    public Account createAccount(IInternalContest contest, int siteNumber, int accountNumber, String displayName, Type clientType) {

        ClientId clientId = new ClientId(siteNumber, clientType, accountNumber);

        Account account = contest.getAccount(clientId);

        if (account == null) {
            String password = clientType.toString().toLowerCase() + accountNumber;
            account = new Account(clientId, password, clientId.getSiteNumber());
            account.setDisplayName(displayName);
            contest.addAccount(account);
        }
        return account;
    }

    /**
     * Create a problem, or return it if it already exists.
     * 
     * @param contest
     * @param problemName
     * @return
     */
    public Problem createProblem(IInternalContest contest, String problemName) {
        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {
            if (problem.getDisplayName().equals(problemName)) {
                return problem;
            }
        }

        Problem newProblem = new Problem(problemName);
        contest.addProblem(newProblem);
        return newProblem;
    }

    /**
     * Create a language, or return it if it already exists.
     * 
     * @param contest
     * @param languageName
     * @return
     */
    public Language createLanguage(IInternalContest contest, String languageName) {

        Language[] languages = contest.getLanguages();

        for (Language language : languages) {
            if (language.getDisplayName().equals(languageName)) {
                return language;
            }
        }

        for (String langName : LanguageAutoFill.getLanguageList()) {
            if (langName.equals(languageName)) {
                // Use auto fill values
                String[] values = LanguageAutoFill.getAutoFillValues(langName);
                Language language = new Language(langName);
                language.setCompileCommandLine(values[1]);
                language.setExecutableIdentifierMask(values[2]);
                language.setProgramExecuteCommandLine(values[3]);
                contest.addLanguage(language);
                return language;
            }
        }

        Language newLanguage = new Language(languageName);
        contest.addLanguage(newLanguage);
        return newLanguage;
    }

    /**
     * String a string of the stringToStrip.
     * 
     * @param string
     * @param stringToStrip
     * @return
     */
    public String stripString(String string, String stringToStrip) {
        int idx = string.indexOf(stringToStrip);

        if (idx > -1) {
            string = string.replaceAll(stringToStrip, "");
        }
        return string;
    }

    /**
     * Fetch field fieldNumber from a string with fields delimited by delimit.
     * 
     * @param string
     * @param delimit
     * @param fieldNumber
     * @return
     */
    public String getField(String string, String delimit, int fieldNumber) {
        String[] fields = string.split(delimit);
        if (fieldNumber < fields.length) {
            return fields[fieldNumber];
        } else {
            return string;
        }
    }

    /**
     * Return an integer from the input string.
     * 
     * @param source
     * @return
     * @throws NumberFormatException
     */
    public int getNumber(String source) {
        source = source.trim();
        return Integer.parseInt(source);
    }

    public static void main(String[] args) {
        LoadContest load = new LoadContest();

        String filename = args[0];

        IInternalContest contest;
        try {
            contest = load.loadContest(filename);
            System.out.println("runs " + contest.getRuns().length);
            System.out.println("teams " + contest.getAccounts(ClientType.Type.TEAM).size());
            System.out.println("probs " + contest.getProblems().length);
            System.out.println("langs " + contest.getLanguages().length);
            System.out.println("judgements " + contest.getJudgements().length);

            System.out.println();

            /**
             * AccountsReport BalloonSummaryReport AllReports ContestSettingsReport ContestReport ContestAnalysisReport
             * SolutionsByProblemReport ListRunLanguages FastestSolvedReport StandingsReport LoginReport ProfileReport RunsReport
             * ClarificationsReport ProblemsReport LanguagesReport JudgementReport RunsByTeamReport BalloonSettingsReport
             * ClientSettingsReport GroupsReport EvaluationReport OldRunsReport RunsReport5 AccountPermissionReport
             * BalloonDeliveryReport ExtractPlaybackLoadFilesReport RunJudgementNotificationsReport JudgementNotificationsReport
             * InternalDumpReport
             */

            // IReport report = new RunsReport();
            IReport report = new LanguagesReport();
            // IReport report = new JudgementReport();
            // IReport report = new ProblemsReport();

            Filter filter = new Filter();
            IInternalController controller = new InternalController(contest);
            report.setContestAndController(contest, controller);
            report.setFilter(filter);

            String outfilename = "stuf2.rpt.txt";
            report.createReportFile(outfilename, filter);

            RandomAccessFile file = new RandomAccessFile(outfilename, "r");
            String line;
            while ((line = file.readLine()) != null) {
                System.out.println(line);
            }
            new File(outfilename).deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
