package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Create contest from YAML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestYAMLLoader.java 225 2011-09-02 05:22:43Z laned $
 */

// TODO CCS REALLY IMPORTANT USER INTERFACE WORK - MUST SHOW SYNTAX ERRORS AND INPUT FILE PROBLEMS TO USER!!! 

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/ContestYAMLLoader.java $
public class ContestYAMLLoader {

    public static final String CONTEST_NAME_KEY = "name";

    public static final String CONTEST_SHORT_NAME = "short-name";

    public static final String CONTEST_START_TIME = "start-time";

    public static final String CONTEST_DURATION = "duration";

    public static final String SCOREBOARD_FREEZE = "scoreboard-freeze";

    public static final String LANGUAGE_KEY = "languages";

    public static final String DEFAULT_CLARS_KEY = "default-clars";

    public static final String CLAR_CATEGORIES_KEY = "clar-categories";

    public static final String PROBLEMS_KEY = "problemset";

    public static final String ACCOUNTS_KEY = "accounts";

    private static final String DELIMIT = ":";

    private static final String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

//    private static final String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    /**
     * Load contest.yaml from directory.
     * 
     * @see #fromYaml(IInternalContest, String[], String)
     * 
     * @param contest 
     * @param diretoryName directory to load files from.
     * @return contest
     * @throws Exception
     */
    public IInternalContest fromYaml(IInternalContest contest, String diretoryName) throws Exception {
        String[] contents = Utilities.loadFile(diretoryName + File.separator + DEFAULT_CONTEST_YAML_FILENAME);
        return fromYaml(contest, contents, diretoryName);
    }

    /**
     * Load/Create contest from YAML lines.
     * 
     * @param contest
     *            update/overwrite contest, if null creates new contest.
     * @param yamlLines
     *            lines from YAML file
     * @return
     */
    public IInternalContest fromYaml(IInternalContest contest, String[] yamlLines, String diretoryName) throws Exception {

        if (contest == null) {
            contest = new InternalContest();
            contest.setSiteNumber(1);
        }

        // name: ACM-ICPC World Finals 2011

        for (String line : yamlLines) {
            if (line.startsWith(CONTEST_NAME_KEY + DELIMIT)) {
                setTitle(contest, line.substring(line.indexOf(DELIMIT) + 1).trim());
            }
        }

        // TODO CCS add settings
        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        Language[] languages = getLanguages(yamlLines);
        for (Language language : languages) {
            contest.addLanguage(language);
        }

        Problem[] problems = getProblems(yamlLines);
        for (Problem problem : problems) {
            addProblemDefAndFiles(contest, diretoryName, problem, problem.getDisplayName());
        }

        // TODO CCS load categories into contest
        // String [] categories = loadGeneralClarificationAnswers(yamlLines);

        // TODO CCS load answers into contest
        // String[] answers = getGeneralAnswers(yamlLines);

        Account[] accounts = getAccounts(yamlLines);
        contest.addAccounts(accounts);
        return contest;
    }

    private Account[] getAccounts(String[] yamlLines) {

        String[] sectionLines = getSectionLines(ACCOUNTS_KEY, yamlLines);

        Vector<Account> accountVector = new Vector<Account>();

        AccountList accountList = new AccountList();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {

            // * - account: TEAM

            String accountType = getSequenceValue(sequenceLines, "- account");
            ClientType.Type type = ClientType.Type.valueOf(accountType.trim());

            String countString = getSequenceValue(sequenceLines, "count").trim();
            int count = 1;
            if (countString.length() != 0) {
                count = Integer.parseInt(countString);
            }
            String siteString = getSequenceValue(sequenceLines, "site");
            int siteNumber = 1;
            if (siteString.length() != 0) {
                siteNumber = Integer.parseInt(siteString);
            }

            /**
             * <pre>
             * 
             * accounts:
             *   -account: TEAM
             *       site: 1
             *      count: 14
             * 
             *   -account: JUDGE
             *       site: 1
             *      count: 12
             * </pre>
             */

            Vector<Account> newAccounts = accountList.generateNewAccounts(type, count, PasswordType.JOE, siteNumber, true);
            accountVector.addAll(newAccounts);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Account[]) accountVector.toArray(new Account[accountVector.size()]);
    }

    private void addProblemDefAndFiles(IInternalContest contest, String diretoryName, Problem problem, String shortName) throws Exception {

        // TODO CCS code loading of problem.yaml file.

//        String problemYamlFilename = diretoryName + File.separator + shortName + File.separator + DEFAULT_PROBLEM_YAML_FILENAME;
//        String[] contents = Utilities.loadFile(problemYamlFilename);

        // assign full problem name
        // get name from problem.yaml file

        // TODO add test case files
        // TODO add validator(s)

        // IF THERE ARE data files - add them
        // ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        // contest.addProblem(problem, dataFiles);

        // load judge data file
        // load judge answer file

        // else

        contest.addProblem(problem);

    }

    public String[] getGeneralAnswers(String[] yamlLines) {
        String[] sectionLines = getSectionLines(DEFAULT_CLARS_KEY, yamlLines);

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        Vector<String> outArrays = new Vector<String>();

        while (sequenceLines.length > 0) {
            String line = sequenceLines[0].trim();
            if (line.startsWith("-")) {
                line = line.substring(1);
            }
            outArrays.addElement(line.trim());
            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }
        return (String[]) outArrays.toArray(new String[outArrays.size()]);

    }

    /**
     * Get list of Clarification Categories from YAML.
     * 
     * @param yamlLines
     * @return
     */
    public String[] getClarificationCategories(String[] yamlLines) {
        String[] sectionLines = getSectionLines(CLAR_CATEGORIES_KEY, yamlLines);

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        Vector<String> outArrays = new Vector<String>();

        while (sequenceLines.length > 0) {
            String line = sequenceLines[0].trim();
            if (line.startsWith("-")) {
                line = line.substring(1);
            }
            outArrays.addElement(line.trim());
            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }
        return (String[]) outArrays.toArray(new String[outArrays.size()]);
    }

    /**
     * Get/create {@link Language}s from YAML lines.
     * 
     * @param yamlLines
     * @return list of {@link Language}s
     */
    public Language[] getLanguages(String[] yamlLines) {

        String[] sectionLines = getSectionLines(LANGUAGE_KEY, yamlLines);

        Vector<Language> languageList = new Vector<Language>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {
            String name = getSequenceValue(sequenceLines, "name");

            if (name == null) {
                syntaxError("Language name field missing in languages section");
            } else {
                String compilerName = getSequenceValue(sequenceLines, "compiler");
                String compilerArgs = getSequenceValue(sequenceLines, "compiler-args");
                String interpreter = getSequenceValue(sequenceLines, "runner");
                String interpreterArgs = getSequenceValue(sequenceLines, "runner-args");

                // TODO remove debugging output code
                // System.out.println("Language  : " + name);
                // System.out.println(" compiler : " + compilerName);
                // System.out.println("    args  : " + compilerArgs);
                // System.out.println(" runner   : " + interpreter);
                // System.out.println("    args  : " + interpreterArgs);

                Language language = new Language(name);
                language.setCompileCommandLine(compilerName + " " + compilerArgs);
                language.setExecutableIdentifierMask("a.out");

                String programExecuteCommandLine = interpreter + " " + interpreterArgs;
                if (interpreter == null) {
                    programExecuteCommandLine = "a.out";
                }
                language.setProgramExecuteCommandLine(programExecuteCommandLine);

                languageList.addElement(language);
            }

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Language[]) languageList.toArray(new Language[languageList.size()]);
    }

    /**
     * Get {@link Problem}s from YAML file.
     * 
     * @param yamlLines
     * @return list of {@link Problem}
     */
    public Problem[] getProblems(String[] yamlLines) {

        String[] sectionLines = getSectionLines(PROBLEMS_KEY, yamlLines);

        Vector<Problem> problemList = new Vector<Problem>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {

            String problemKeyName = getSequenceValue(sequenceLines, "short-name");
            String problemTitle = problemKeyName;
            /**
             * <pre>
             *  problemset:
             *    - letter:     A
             *      short-name: apl
             *      color:      yellow
             *      rgb:        #ffff00
             * </pre>
             */

            Problem problem = new Problem(problemTitle);

            // String problemLetter = getSequenceValue(sequenceLines, "letter");
            // String colorName = getSequenceValue(sequenceLines, "color");
            // String colorRGB = getSequenceValue(sequenceLines, "rgb");

            // TODO CCS add Problem set
            // problem.setLetter(problemLetter);
            // problem.setColorName(colorName);
            // problem.setColorRGB(colorRGB);

            // debug code
            // System.out.println("Problem   : " + problemKeyName);
            // System.out.println(" letter   : " + problemLetter);
            // System.out.println(" color    : " + colorName);
            // System.out.println(" RGB      : " + colorRGB);

            problemList.addElement(problem);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Problem[]) problemList.toArray(new Problem[problemList.size()]);
    }

    private void syntaxError(String string) {

        new Exception("Syntax error: " + string).printStackTrace();

    }

    private String getSequenceValue(String[] lines, String key) {
        for (String line : lines) {
            String keyString = key + DELIMIT;
            if (line.trim().startsWith(keyString)) {
                return line.trim().substring(keyString.length()).trim();
            }

            keyString = "- " + key + DELIMIT;
            if (line.trim().startsWith(keyString)) {
                return line.trim().substring(keyString.length()).trim();
            }
        }
        return null;
    }

    public String[] getNextSequence(String[] sectionLines, int idx) {

        Vector<String> lines = new Vector<String>();

        for (int i = idx; i < sectionLines.length; i++) {
            String line = sectionLines[i];
            if (i > idx && line.trim().startsWith("-")) {
                break;
            }
            lines.addElement(line);
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    public boolean isNewSection(String line) {
        if (line == null) {
            return false;
        }
        if (line.indexOf(DELIMIT) > 1) {
            char firstChar = line.charAt(0);
            return ! ( (firstChar == ' ') || (firstChar == '-') || (firstChar == '#') );
        }
        return false;
    }

    /**
     * get all section lines including section name line.
     * 
     * @param key
     * @param yamlLines
     * @return all lines in a section.
     */
    public String[] getSectionLines(String key, String[] yamlLines) {

        Vector<String> lines = new Vector<String>();

        boolean inSection = false;

        for (String line : yamlLines) {

            if (isNewSection(line)) {
                inSection = false;
                if (line.startsWith(key + DELIMIT)) {
                    inSection = true;
                }
            } // no else

            if (inSection) {
                lines.addElement(line);
            }
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    private void setTitle(IInternalContest contest, String title) {
        ContestInformation contestInformation = contest.getContestInformation();
        contestInformation.setContestTitle(title);
    }
}
