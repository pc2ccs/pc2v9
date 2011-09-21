package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;

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

    public static final String SITES_KEY = "sites";

    private static final String DELIMIT = ":";

    private static final String DEFAULT_CONTEST_YAML_FILENAME = "contest.yaml";

    private static final String DEFAULT_PROBLEM_YAML_FILENAME = "problem.yaml";

    /**
     * problemName key in problem.yaml
     */
    private static final String PROBLEM_NAME_KEY = "name";

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    /**
     * Problem input (data files) key.
     */
    private static final String PROBLEM_INPUT_KEY = "input";

    /**
     * Load contest.yaml from directory.
     * 
     * @see #fromYaml(IInternalContest, String[], String)
     * 
     * @param contest
     * @param diretoryName
     *            directory to load files from.
     * @return contest
     * @throws Exception
     */
    public IInternalContest fromYaml(IInternalContest contest, String diretoryName) throws Exception {
        String[] contents = Utilities.loadFile(diretoryName + File.separator + DEFAULT_CONTEST_YAML_FILENAME);
        return fromYaml(contest, contents, diretoryName);
    }
    
    /**
     * Get title from YAML file.
     * 
     * @param contestYamlFilename
     * @return
     * @throws IOException
     */
    public String getContestTitle(String contestYamlFilename ) throws IOException {
        String[] contents = Utilities.loadFile(contestYamlFilename);
        String contestTitle = getSequenceValue(contents, CONTEST_NAME_KEY);
        return contestTitle;
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
        
        String contestTitle = getSequenceValue(yamlLines, CONTEST_NAME_KEY);
        if (contestTitle != null) {
            setTitle(contest, contestTitle);
        }
        

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
            addProblemDefAndFiles(contest, diretoryName, problem);
            
            // TODO CCS add validator(s)
            assignDefaultValidator(problem);
        }
        

        Site[] sites = getSites(yamlLines);
        for (Site site : sites) {
            contest.addSite(site);
        }

        String[] categories = loadGeneralClarificationAnswers(yamlLines);
        for (String name : categories) {
            contest.addCategory(new Category(name));
        }
         
        // String[] answers = getGeneralAnswers(yamlLines);
        // TODO CCS load answers into contest

        Account[] accounts = getAccounts(yamlLines);
        contest.addAccounts(accounts);
        return contest;
    }

    public Site[] getSites(String[] yamlLines) {

        String[] sectionLines = getSectionLines(SITES_KEY, yamlLines);

        Vector<Site> sitesVector = new Vector<Site>();

        int idx = 1;
        String[] sequenceLines = getNextSequence(sectionLines, idx);

        while (sequenceLines.length > 0) {

            /*
             * <pre> sites: - number: 1 name: Site 1 IP: localhost port: 50002 </pre>
             */

            String siteNumberString = getSequenceValue(sequenceLines, "- number");
            String siteTitle = getSequenceValue(sequenceLines, "name").trim();
            int siteNumber = Integer.parseInt(siteNumberString.trim());

            Site site = new Site(siteTitle, siteNumber);

            String hostName = getSequenceValue(sequenceLines, "IP").trim();
            String portString = getSequenceValue(sequenceLines, "port").trim();

            String password = getSequenceValue(sequenceLines, "password");
            if (password == null) {
                password = "site" + siteNumberString;
            }
            site.setPassword(password.trim());

            Properties props = new Properties();
            props.put(Site.IP_KEY, hostName);
            props.put(Site.PORT_KEY, portString);
            site.setConnectionInfo(props);

            sitesVector.addElement(site);

            idx += sequenceLines.length;
            sequenceLines = getNextSequence(sectionLines, idx);
        }

        return (Site[]) sitesVector.toArray(new Site[sitesVector.size()]);

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

    /**
     * Add data contents into problem.
     * @param contest
     * @param directoryName
     * @param problem
     * @throws Exception
     */
    private void addProblemDefAndFiles(IInternalContest contest, String directoryName, Problem problem) throws Exception {

        String problemYamlFilename = directoryName + File.separator + problem.getShortName() + File.separator + DEFAULT_PROBLEM_YAML_FILENAME;
        String[] contents = Utilities.loadFile(problemYamlFilename);

        if (contents.length == 0) {
            throw new Exception("Can not load problem.yaml: " + problemYamlFilename);
        }

        String problemName = getSequenceValue(contents, PROBLEM_NAME_KEY);
        if (problemName != null) {
            problem.setDisplayName(problemName);
        }

        String[] sectionLines = getSectionLines(PROBLEM_INPUT_KEY, contents);

        String dataFileBaseDirectory = directoryName + File.separator + problem.getShortName() + File.separator + "data" + File.separator + "secret";

        if (sectionLines.length > 1) {
            loadPc2Problem(contest, dataFileBaseDirectory, problem, sectionLines);
        } else {
            loadCCSProblem(contest, dataFileBaseDirectory, problem);
        }

    }

    private void loadCCSProblem(IInternalContest contest, String dataFileBaseDirectory, Problem problem) throws Exception {

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);

        String[] inputFileNames = getFileNames(dataFileBaseDirectory, ".in");

        String[] answerFileNames = getFileNames(dataFileBaseDirectory, ".ans");

        if (inputFileNames.length == 0) {
            throw new Exception("No input file names found for " + problem.getDisplayName());
        }

        if (answerFileNames.length == 0) {
            throw new Exception("No input file names found for " + problem.getDisplayName());
        }

        if (inputFileNames.length == answerFileNames.length) {

            SerializedFile[] serializedFileDataFiles = new SerializedFile[inputFileNames.length];
            SerializedFile[] serializedFileAnswerFiles = new SerializedFile[inputFileNames.length];

            for (int idx = 0; idx < inputFileNames.length; idx++) {

                problem.addTestCaseFilenames(inputFileNames[idx], answerFileNames[idx]);

                String dataFileName = dataFileBaseDirectory + File.separator + inputFileNames[idx];

                String answerFileName = dataFileName.replaceAll(".in$", ".ans");
                String answerShortFileName = inputFileNames[idx].replaceAll(".in$", ".ans");

                checkForFile(dataFileName, "Missing "+inputFileNames[idx]+" file for " + problem.getShortName());
                checkForFile(answerFileName, "Missing "+answerShortFileName+" file for " + problem.getShortName());

                serializedFileDataFiles[idx] = new SerializedFile(dataFileName);
                serializedFileAnswerFiles[idx] = new SerializedFile(answerFileName);
            }

            problemDataFiles.setJudgesDataFiles(serializedFileDataFiles);
            problemDataFiles.setJudgesAnswerFiles(serializedFileAnswerFiles);

        } else {

            throw new Exception("  For " + problem.getShortName() + " Missing files -  there are " + inputFileNames.length + " .in files and " + //
                    answerFileNames.length + " .ans files ");
        }

        contest.addProblem(problem, problemDataFiles);
    }

    /**
     * Check for existence of file, if does not exist throw exception with message.
     * 
     * @param filename
     * @param message
     * @throws Exception 
     */
    private void checkForFile(String filename, String message) throws Exception {
        
        if (! (new File(filename).isFile())) {
            throw new Exception (message);
        }
    }

    /**
     * Get list of filenames with extension in directory, return in sorted order.
     * 
     * @param directoryName
     * @param extension
     * @return
     */
    protected String[] getFileNames(String directoryName, String extension) {
        
        Vector<String> list = new Vector<String>();
        File dir = new File (directoryName);
        
        String [] entries = dir.list();
        Arrays.sort(entries);
        
        for (String name : entries){
            if (name.endsWith(extension)){
                list.addElement(name);
            }
        }
        
        return (String[]) list.toArray(new String[list.size()]);
    }

    private void loadPc2Problem (IInternalContest contest, String dataFileBaseDirectory, Problem problem, String[] sectionLines) throws Exception {
        
        String dataFileName = getSequenceValue(sectionLines, "datafile");
        String answerFileName = getSequenceValue(sectionLines, "answerfile");

        ProblemDataFiles problemDataFiles = new ProblemDataFiles(problem);
    
        if (dataFileName != null || answerFileName != null) {
            
            addDataFiles (problem, problemDataFiles, dataFileBaseDirectory, dataFileName, answerFileName);

            contest.addProblem(problem, problemDataFiles);

        } else {
            contest.addProblem(problem);
        }
        
    }

    private void addDataFiles(Problem problem, ProblemDataFiles problemDataFiles, String dataFileBaseDirectory, String dataFileName, String answerFileName) throws Exception {

        // load judge data file
        if (dataFileName != null) {
            String dataFilePath = dataFileBaseDirectory + File.separator + dataFileName;
            if (fileNotThere(dataFilePath)) {
                throw new Exception("Missing data file " + dataFilePath);
            }

            problem.setDataFileName(dataFileName);
            problem.setReadInputDataFromSTDIN(false);

            SerializedFile serializedFile = new SerializedFile(dataFilePath);
            problemDataFiles.setJudgesDataFile(serializedFile);
        }

        // load judge answer file
        if (answerFileName != null) {
            String answerFilePath = dataFileBaseDirectory + File.separator + answerFileName;
            if (fileNotThere(answerFilePath)) {
                throw new Exception("Missing data file " + answerFilePath);
            }

            problem.setAnswerFileName(answerFileName);

            SerializedFile serializedFile = new SerializedFile(answerFilePath);
            problemDataFiles.setJudgesAnswerFile(serializedFile);
        }
        
    }

    private boolean fileNotThere(String name) {
        return !new File(name).isFile();
    }

    private void assignDefaultValidator(Problem problem) {
        addInternalValidator(problem, 1);

    }

    public Problem addInternalValidator(Problem problem, int optionNumber) {

        problem.setValidatedProblem(true);
        problem.setUsingPC2Validator(true);
        problem.setWhichPC2Validator(optionNumber);
        problem.setIgnoreSpacesOnValidation(true);

        problem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + problem.getWhichPC2Validator() + " " + problem.isIgnoreSpacesOnValidation());
        problem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);

        return problem;

    }

    /**
     * Load list of strings.
     * 
     * @param key
     * @param yamlLines
     * @return
     */
    public String[] loadStringList(String key, String[] yamlLines) {
        String[] sectionLines = getSectionLines(key, yamlLines);

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

    public String[] loadGeneralClarificationAnswers(String[] yamlLines) {
        return loadStringList(CLAR_CATEGORIES_KEY, yamlLines);
    }

    public String[] getGeneralAnswers(String[] yamlLines) {
        return loadStringList(DEFAULT_CLARS_KEY, yamlLines);
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
     * Fill in Language fields from LanguageAutoFill fields.
     * 
     * @param language
     * @param values
     * @param fullLanguageName
     */
    private void fillLanguage(Language language, String[] values, String fullLanguageName) {
        // values array
        // 0 Title for Language
        // 1 Compiler Command Line
        // 2 Executable Identifier Mask
        // 3 Execute command line
        // 5 "interpreted" if interpreter.

        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
        boolean isScript = LanguageAutoFill.isInterpretedLanguage(fullLanguageName);
        language.setInterpreted(isScript);
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

                Language language = new Language(name);

                if (compilerName == null || compilerName.trim().length() == 0) {

                    for (String langName : LanguageAutoFill.getLanguageList()) {
                        if (name.startsWith(langName)) {

                            String[] values = LanguageAutoFill.getAutoFillValues(name);
                            fillLanguage(language, values, langName);
                            languageList.addElement(language);
                        }
                    }
                } else {

                    String compilerArgs = getSequenceValue(sequenceLines, "compiler-args");
                    String interpreter = getSequenceValue(sequenceLines, "runner");
                    String interpreterArgs = getSequenceValue(sequenceLines, "runner-args");

                    language.setCompileCommandLine(compilerName + " " + compilerArgs);
                    language.setExecutableIdentifierMask("a.out");

                    String programExecuteCommandLine = interpreter + " " + interpreterArgs;
                    if (interpreter == null) {
                        programExecuteCommandLine = "a.out";
                    }
                    language.setProgramExecuteCommandLine(programExecuteCommandLine);

                    languageList.addElement(language);
                }

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
     * @throws Exception 
     */
    public Problem[] getProblems(String[] yamlLines) throws Exception {

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
            problem.setShortName(problemKeyName);

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
            return !((firstChar == ' ') || (firstChar == '-') || (firstChar == '#'));
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
