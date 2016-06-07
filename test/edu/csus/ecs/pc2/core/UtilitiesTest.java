package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.csus.ecs.pc2.core.Utilities.DataFileType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public class UtilitiesTest extends AbstractTestCase {

    public void testOne() {
        char[] array1 = null;
        char[] array2 = null;
        assertTrue("arrays are null", Utilities.isEquals(array1, array2));
        array2 = new char[1];
        array2[0] = 'C';
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
        array1 = new char[1];
        array1[0] = 'C';
        assertTrue("arrays are equal", Utilities.isEquals(array1, array2));
        array1[0] = 'D';
        assertFalse("arrays are not equal", Utilities.isEquals(array1, array2));
        array2 = null;
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
    }

    public void testBasename() {
        // public static void main(String[] args) {

        String[][] entries = { { "foo.c", "foo.c" }, { ";usr;bin;basename", "basename" }, { ";usr;basename", "basename" }, { ";bin;ls", "ls" }, };

        for (String[] row : entries) {

            String string1 = replaceChar(row[0], ';', File.separatorChar);
            String string2 = replaceChar(row[1], ';', File.separatorChar);

            // System.out.println(string1);
            // System.out.println(string2);

            string1 = Utilities.basename(string1);
            // System.out.println(string1);
            // System.out.println();

            assertEquals(string1, string2);
        }

    }

    public void testDirname() {
        // public static void main(String[] args) {

        String[][] entries = { { "foo.c", "foo.c" }, { ";usr;bin;basename", ";usr;bin" }, { ";usr;basename", ";usr" }, { ";bin;ls", ";bin" }, { ";", ";" }, };

        for (String[] row : entries) {

            String string1 = replaceChar(row[0], ';', File.separatorChar);
            String string2 = replaceChar(row[1], ';', File.separatorChar);

            // System.out.println(string1);
            // System.out.println(string2);

            string1 = Utilities.dirname(string1);
            // System.out.println(string1);
            // System.out.println();

            assertEquals(string1, string2);
        }

    }

    private static String replaceChar(String string, char c, char separatorChar) {

        // Maybe use string buffer sometime

        int index = string.indexOf(c);
        while (index > -1) {
            string = string.replace(c, separatorChar);
            index = string.indexOf(c);
        }
        return string;
    }

    public void testConvertLetter() {
        int[] testCases = { 1, 2, 5, 26, 27, 28, 30, 51, 52, 53, 702 };
        String[] expectedAnswers = { "A", "B", "E", "Z", "AA", "AB", "AD", "AY", "AZ", "BA", "ZZ" };
        for (int i = 0; i < testCases.length; i++) {
            String result = Utilities.convertNumber(testCases[i]);
            assertEquals("testCase" + i + "(" + testCases[i] + ")", expectedAnswers[i], result);
        }

    }

    public void testgetDateTime() throws Exception {

        String actual = Utilities.getDateTime();

        // may not match actual milliseconds.
        String nearlyExpected = new SimpleDateFormat(Utilities.DATE_TIME_FORMAT_STRING).format(new Date());

        assertEquals("Should be same length ", nearlyExpected.length(), actual.length());

        // match pattern up to . where pattern is yyyyddMMhhmmss.SSS
        assertTrue("First part of string should match ", actual.substring(0, 15).equals(nearlyExpected.substring(0, 15)));
    }

    public void testHHMMSStoString() throws Exception {

        String[] testData = { //
        // HH:MM:SS,seconds
                "0,0", //
                "1:00:00,3600", //
                "1:00,60", //
                "59,59", //
                "5:00:00,18000", //
                "59:59,3599", //
                "23:59:58,86398", //

        };

        for (String timeString : testData) {

            String[] fields = timeString.split(",");

            long actualSeconds = Utilities.convertStringToSeconds(fields[0]);
            long expectedSeconds = Long.parseLong(fields[1]);

            assertEquals("Expected seconds ", expectedSeconds, actualSeconds);
            // System.out.println("\""+fields[0]+","+actualSeconds+"\", //");
        }
    }

    public void testfindDataBasePath() throws Exception {

        String secretDir = Utilities.SECRET_DATA_DIR;

        String expected = "cdp/config/problema/";
        String input = expected + secretDir;

        String actual = Utilities.findDataBasePath(input);
        assertEquals("Expecting same path", expected, actual);

        expected = "testdir/problema";
        input = expected;

        actual = Utilities.findDataBasePath(input);
        assertEquals("Expecting same path", expected, actual);
    }

    public void testCreateZeroByteFile() throws Exception {

        String outdir = getOutputDataDirectory(this.getName());
        ensureDirectory(outdir);
        // startExplorer(outdir);

        String zeroByteFile = outdir + File.separator + "zerobyte";

        createZeroByteFile(zeroByteFile);

        SerializedFile serializedFile = new SerializedFile(zeroByteFile);

        String newfilename = outdir + File.separator + "zerobyte";
        Utilities.createFile(serializedFile, newfilename);

        assertFileExists(newfilename, "zero byte file");

        File file = new File(newfilename);
        assertEquals("Expecting zero byte file for " + newfilename, 0, file.length());

        newfilename = outdir + File.separator + "afile";

        serializedFile = new SerializedFile(newfilename);
        Utilities.createFile(serializedFile, newfilename);

        assertFileExists(newfilename, "zero byte file");
        file = new File(newfilename);
        assertEquals("Expecting zero byte file for " + newfilename, 0, file.length());

    }

    private void createZeroByteFile(String filename) throws FileNotFoundException {

        PrintWriter printWriter = null;
        printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        printWriter.print("");
        printWriter.close();

    }

    /**
     * 
     * @throws Exception
     */
    public void testvalidateCDP() throws Exception {

        String testDirectory = getDataDirectory(this.getName());

        // create contest.yaml and dirs
        // SampleContest sample = new SampleContest();
        // IInternalContest contest = sample.createStandardContest();
        // ExportYAML exportYAML = new ExportYAML();
        // exportYAML.exportFiles(testDirectory, contest);

        // startExplorer(testDirectory);

        ContestYAMLLoader loader = new ContestYAMLLoader();
        IInternalContest contest = loader.fromYaml(null, testDirectory);

        Problem[] problems = contest.getProblems();
        Problem problem1 = problems[0];
        assertEquals("test data sets for " + problem1, 5, problem1.getNumberTestCases());

        Utilities.validateCDP(contest, testDirectory);
    }

    // private static String selectFile(String dialogTitle) {
    //
    // String directory = null;
    //
    // JFileChooser chooser = new JFileChooser();
    //
    // chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    // if (dialogTitle != null) {
    // chooser.setDialogTitle(dialogTitle);
    // }
    // try {
    // // int returnVal = chooser.showOpenDialog(this);
    // int returnVal = chooser.showOpenDialog(null);
    // if (returnVal == JFileChooser.APPROVE_OPTION) {
    // directory = chooser.getSelectedFile().toString();
    // }
    // } catch (Exception e) {
    // // getController().getLog().log(Log.INFO, "Error getting selected file, try again.", e);
    // }
    // chooser = null;
    // return directory;
    // }
    //
    // public static void main(String[] args) {
    // String dir = selectFile("Poo Poo");
    // System.out.println("dir is "+dir);
    // }

    /**
     * test
     * 
     * @throws Exception
     */
    public void testTestLocateFile() throws Exception {

        String testDir = getDataDirectory(this.getName());

        String judgeCDP = testDir + File.separator + "jcdp";

        IInternalContest contest = new SampleContest().createStandardContest();
        Problem firstProblem = contest.getProblems()[0];
        firstProblem.setUsingExternalDataFiles(true);

        String problemDir = judgeCDP + File.separator + firstProblem.getShortName();

        // startExplorer(problemDir, true);

        String problemFileName = problemDir + File.separator + "testdata.in";

        assertFileExists(problemFileName);

        // create external problem name
        SerializedFile serializedFile = new SerializedFile(problemFileName, true);

        DataFileType judgeDataFile = DataFileType.JUDGE_DATA_FILE;

        String location = Utilities.locateJudgesDataFile(firstProblem, serializedFile, judgeCDP, judgeDataFile);

        assertEquals("Expecting location ", problemFileName, location);

    }

    /**
     * Test java.util.Arrays.equals.
     * 
     * @throws Exception
     */
    public void testArrayCompare() throws Exception {

        char[] buf1 = null;
        char[] buf2 = null;

        boolean b = java.util.Arrays.equals(buf1, buf2);
        assertTrue(b);

        buf1 = "foo".toCharArray();
        b = java.util.Arrays.equals(buf1, buf2);
        assertFalse("Expected to not be equal", b);

        b = java.util.Arrays.equals(buf2, buf1);
        assertFalse("Expected to not be equal", b);
    }

    public void testgetProblemLetter() throws Exception {

        int problemNumber = 1;
        String actual = Utilities.getProblemLetter(problemNumber);
        assertEquals("Expect letter for " + problemNumber, "A", actual);

        problemNumber = 26;
        actual = Utilities.getProblemLetter(problemNumber);
        assertEquals("Expect letter for " + problemNumber, "Z", actual);

        problemNumber = 5;
        actual = Utilities.getProblemLetter(problemNumber);
        assertEquals("Expect letter for " + problemNumber, "E", actual);

    }

    public void testgetProblemNumber() throws Exception {

        IInternalContest contest = new SampleContest().createStandardContest();

        Problem[] problems = contest.getProblems();
        int expected = 1;
        for (Problem problem : problems) {

            int actual = Utilities.getProblemNumber(contest, problem);
            assertEquals("Expeting number " + expected + " for problem " + problem, expected, actual);
            expected++;
        }

        Problem probMissing = new Problem("Not in contest");
        int actual2 = Utilities.getProblemNumber(contest, probMissing);
        expected = 0;
        assertEquals("Expeting number " + expected + " for problem " + probMissing, expected, actual2);
    }
}
