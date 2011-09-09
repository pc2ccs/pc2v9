package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.PrintStream;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test for ContestYAMLLoader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestYAMLLoaderTest.java 225 2011-09-02 05:22:43Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/imports/ccs/ContestYAMLLoaderTest.java $
public class ContestYAMLLoaderTest extends TestCase {

    private boolean debugFlag = true;

    private ContestYAMLLoader loader;

    private String testDirectory = "testdata" + File.separator + "yaml";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loader = new ContestYAMLLoader();

        String testDir = "testdata";
        String projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            projectPath = "."; //$NON-NLS-1$
            System.err.println("ContestYAMLLoaderTest: Unable to locate " + testDir);
        }
        testDirectory = projectPath +  File.separator + "testdata" + File.separator + "yaml";

        if (debugFlag) {
            System.out.println("ContestYAMLLoaderTest: test directory: " + getYamlTestDirectory());
            System.out.println("ContestYAMLLoaderTest: test      file: " + getYamlTestFileName());
        }
    }

    @SuppressWarnings("unused")
    private void writeRow(PrintStream printWriter, Language language) {

        String deletedText = "";
        if (!language.isActive()) {
            deletedText = " [HIDDEN] ";
        }

        printWriter.println("  Language  '" + language + deletedText + "' v" + language.getElementId().getVersionNumber() + " id=" + language.getElementId());
        printWriter.println("    site number         : " + language.getSiteNumber());
        printWriter.println("    compiler command    : " + language.getCompileCommandLine());
        printWriter.println("    executable mask     : " + language.getExecutableIdentifierMask());
        printWriter.println("    program execute cmd : " + language.getProgramExecuteCommandLine());
    }

    /**
     * 
     * @throws Exception
     */
    public void testLoaderMethods() throws Exception {

        IInternalContest contest = loader.fromYaml(null, new String[0], "NAD");
        assertNotNull(contest);

        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        contest = loader.fromYaml(null, contents, getYamlTestDirectory());

        assertNotNull(contest);

        // name: ACM-ICPC World Finals 2011
        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        Language[] languages = loader.getLanguages(contents);

        assertEquals("Number of languages", 3, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Problem[] problems = loader.getProblems(contents);

        assertEquals("Number of problems", 5, problems.length);

        assertEquals("Expected problem name ", "apl", problems[0].getDisplayName());
        assertEquals("Expected problem name ", "barcodes", problems[1].getDisplayName());
        assertEquals("Expected problem name ", "biobots", problems[2].getDisplayName());
        assertEquals("Expected problem name ", "castles", problems[3].getDisplayName());
        assertEquals("Expected problem name ", "channel", problems[4].getDisplayName());

    }

    public void testLoader() throws Exception {

        IInternalContest contest = loader.fromYaml(null, new String[0], "NAD");
        assertNotNull(contest);

        contest = loader.fromYaml(null, getYamlTestDirectory());

        assertNotNull(contest);

        assertEquals("Contest title", "ACM-ICPC World Finals 2011", contest.getContestInformation().getContestTitle());

        // short-name: ICPC WF 2011
        // start-time: 2011-02-04 01:23Z
        // duration: 5:00:00
        // scoreboard-freeze: 4:00:00

        Language[] languages = contest.getLanguages();

        assertEquals("Number of languages", 3, languages.length);

        assertEquals("Expected language name ", "C++", languages[0].getDisplayName());
        assertEquals("Expected language name ", "C", languages[1].getDisplayName());
        assertEquals("Expected language name ", "Java", languages[2].getDisplayName());

        // for (Language language : languages){
        // writeRow(System.out, language);
        // }

        Problem[] problems = contest.getProblems();

        assertEquals("Number of problems", 5, problems.length);

        assertEquals("Expected problem name ", "apl", problems[0].getDisplayName());
        assertEquals("Expected problem name ", "barcodes", problems[1].getDisplayName());
        assertEquals("Expected problem name ", "biobots", problems[2].getDisplayName());
        assertEquals("Expected problem name ", "castles", problems[3].getDisplayName());
        assertEquals("Expected problem name ", "channel", problems[4].getDisplayName());

        Account[] accounts = contest.getAccounts();

        assertEquals("Number of accounts", 55, accounts.length);

        ClientType.Type type = ClientType.Type.TEAM;
        assertEquals("Number of accounts " + type.toString(), 35, getClientCount(contest, type));
        type = ClientType.Type.JUDGE;
        assertEquals("Number of accounts " + type.toString(), 20, getClientCount(contest, type));
        type = ClientType.Type.ADMINISTRATOR;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));
        type = ClientType.Type.SCOREBOARD;
        assertEquals("Number of accounts " + type.toString(), 0, getClientCount(contest, type));

    }

    private Object getClientCount(IInternalContest contest, Type type) {
        return contest.getAccounts(type).size();
    }

    @SuppressWarnings("unused")
    private void printProblems(IInternalContest contest) {
        Problem[] problems = contest.getProblems();

        System.out.println("printProblems: " + problems.length + " problems");
        for (Problem problem : problems) {
            System.out.println(problem);
        }

    }

    public void testLoadClarCategories() throws Exception {

        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        String[] answers = loader.getClarificationCategories(contents);

        assertEquals("Expected clar category ", "General", answers[0]);
        assertEquals("Expected clar category ", "SysOps", answers[1]);
        assertEquals("Expected clar category ", "Operations", answers[2]);

    }

    public void testGeneralAnswsers() throws Exception {
        String[] contents = Utilities.loadFile(getYamlTestFileName());

        assertFalse("File missing " + getYamlTestFileName(), contents.length == 0);

        String[] strings = loader.getGeneralAnswers(contents);

        assertEquals("Expected gen. clar answer ", "No comment, read problem statement.", strings[0]);
        assertEquals("Expected gen. clar answer ", "This will be answered during the answers to questions session.", strings[1]);
    }

    public void testgetSectionLines() throws Exception {

        String contestYamlFilename = getYamlTestFileName();

        assertTrue("Test file does not exist " + contestYamlFilename, Utilities.isFileThere(contestYamlFilename));
        String[] contents = Utilities.loadFile(contestYamlFilename);

        assertFalse("File missing " + contestYamlFilename, contents.length == 0);

        String key = ContestYAMLLoader.LANGUAGE_KEY;
        String[] sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 15, sectionLines.length);

        key = ContestYAMLLoader.DEFAULT_CLARS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 4, sectionLines.length);

        key = ContestYAMLLoader.CLAR_CATEGORIES_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 5, sectionLines.length);

        key = ContestYAMLLoader.PROBLEMS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 26, sectionLines.length);

        key = ContestYAMLLoader.ACCOUNTS_KEY;
        sectionLines = loader.getSectionLines(key, contents);
        assertEquals(key + " lines.", 18, sectionLines.length);
    }

    String getYamlTestDirectory() {
        return testDirectory;
    }

    String getYamlTestFileName() {
        return getYamlTestDirectory() + File.separator + ExportYAML.CONTEST_FILENAME;
    }

}
