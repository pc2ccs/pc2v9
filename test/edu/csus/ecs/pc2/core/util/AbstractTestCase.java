package edu.csus.ecs.pc2.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Test utilities.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AbstractTestCase extends TestCase {
    
    /**
     * For debugging, if directories do not exist create them.
     */
    private boolean createMissingDirectories = false;

    private String testDataDirectory = null;

    /**
     * Directory under which all input testing data is located.
     * 
     * Do not use directly, use {@link #getDataDirectory()}.
     */
    public static final String DEFAULT_PC2_TEST_DIRECTORY = "testdata";
    
    private String testingOutputDirectory = null;

    /**
     * Directory under which all testing output should be located.
     * 
     * Do not use directly, use {@link #getTestingOutputDirectory()}.
     */
    public static final String DEFAULT_PC2_OUTPUT_FOR_TESTING_DIRECTORY = "testing";
    
    public AbstractTestCase() {
        super();
    }

    public AbstractTestCase(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        ensureAndCreateDirectory(getDataDirectory());
    }
    
    /**
     * Get full path to test directory name.
     * 
     * @return
     */
    private String getTestDataDirectory() {
        if (testDataDirectory == null) {

            String projectPath = JUnitUtilities.locate(DEFAULT_PC2_TEST_DIRECTORY);
            if (projectPath == null) {
                projectPath = "."; //$NON-NLS-1$
                System.err.println("AbstractTestCase: Warning - unable to locate in project " + DEFAULT_PC2_TEST_DIRECTORY);
            }
            testDataDirectory = projectPath + File.separator + DEFAULT_PC2_TEST_DIRECTORY;
        }
        
        ensureAndCreateDirectory(testDataDirectory);
        return testDataDirectory;
    }
    
    
    /**
     * Return the project (with JUnit name) relative directory where test output data is located.
     * 
     * This returns the proper project-relative path for the input testing data directory.
     * 
     * @return a project-relative directory name with the JUnit classname 
     */
    public String getTestingOutputDirectory() {
        if (testingOutputDirectory == null) {

            String projectPath = JUnitUtilities.locate(DEFAULT_PC2_TEST_DIRECTORY);
            if (projectPath == null) {
                projectPath = "."; //$NON-NLS-1$
                System.err.println("AbstractTestCase: Warning - unable to locate in project " + DEFAULT_PC2_TEST_DIRECTORY);
            }
            testingOutputDirectory = projectPath + File.separator + DEFAULT_PC2_TEST_DIRECTORY;
        }
        
        ensureAndCreateDirectory (testingOutputDirectory);
        
        return testingOutputDirectory;
    }
    

    /**
     * Insure directory exists if {@link #createMissingDirectories} is true.
     * 
     * @param directoryName
     */
    private void ensureAndCreateDirectory(String directoryName) {
        if (createMissingDirectories && !new File(directoryName).isDirectory()) {
            ensureDirectory(directoryName);
            System.err.println("Created directory: " + directoryName);
        }
    }

    /**
     * Not quite insure, more of a attempt to create it if you can if you can't return false.
     * 
     * @param directoryName
     * @return true if directory exists or was created, false otherwise.
     */
    public boolean ensureDirectory(String directoryName) {
        File dir = new File(directoryName);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        return dir.isDirectory();
    }

    /**
     * Return the project (with JUnit name) relative directory where test output data is located.
     * 
     * @see #getTestingOutputDirectory()
     * @param directoryName name of directory to append to end of path
     * @return a project-relative directory name  
     */
    public String getTestingOutputDirectory (String directoryName) {
        
        String newDirName = getTestingOutputDirectory() + File.separator + getShortClassName() + File.separator + directoryName;
        ensureAndCreateDirectory (newDirName);
        return newDirName; 
    }

    /**
     * Return the base project relative directory where test input data is located.
     * 
     * This returns the proper project-relative path for the input testing data directory.
     * 
     * @return a project-relative directory name, appends the JUnit name at the end of the string. 
     */
    public String getDataDirectory() {
        
        String dirname = getTestDataDirectory() + File.separator + getShortClassName();
        ensureAndCreateDirectory(dirname);
        return dirname;
    }

    /**
     * Return the project (with JUnit name) relative directory where test input data is located.
     * 
     * @eee {@link #getDataDirectory()}
     * @param directoryName name of directory to append to end of string
     * @return a project-relative directory name  
     */
    public String getDataDirectory(String directoryName) {
        String newDirName = getDataDirectory() + File.separator + directoryName;
        ensureAndCreateDirectory (newDirName);
        return newDirName; 
    }
    

    /**
     * Get a project relative input data file name.
     * 
     */
    public String getTestFilename(String baseFilename) {
        return getDataDirectory() + File.separator + baseFilename;
    }

    /**
     * Get a project relative output data file name.
     * 
     */
    public String getOutputTestFilename(String baseFilename) {
        return getTestingOutputDirectory() + File.separator + baseFilename;
    }

    /**
     * get this class name (without package).
     * @return
     */
    protected String getShortClassName() {
        String className = this.getClass().getName();
        
        int n = className.lastIndexOf('.');
        if ( n > 0) {
            return className.substring(n + 1);
        } else {
            return className;
        }
    }
    
    /**
     * 
     * @return sample sumit input data
     */
    public  String[] getSampleDataLines () {
        String[] datalines = { "25", "50", "-25", "0" };
        return datalines;
    }
    
    public String createSampleDataFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        writeLines(writer, getSampleDataLines());
        writer.close();
        writer = null;
        return filename;
    }
    
    /**
     * 
     * @return sample sumit answer file
     */
    public  String[] getSampleAnswerLines () {
        String[] datalines = { "The sum of the integers is 75" };
        return datalines;
    }

    public String createSampleAnswerFile(String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        writeLines(writer, getSampleAnswerLines());
        writer.close();
        writer = null;
        return filename;
    }

    public void writeFileContents(String filename, String[] datalines) throws FileNotFoundException{
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        writeLines(writer, getSampleAnswerLines());
        writer.close();
        writer = null;
    }
    
    /**
     * Create N files with sample data.
     * @param strings 
     *  
     * @param n
     * @param string
     * @return
     * @throws FileNotFoundException 
     */
    public String[] createDataFiles(int n, String[] lines, String ext) throws FileNotFoundException {
        String[] names = new String[n];

        for (int i = 0; i < n; i++) {
            char let = 'a';
            let += i;
            String filename = getTestFilename(let + ext);
            writeFileContents(filename, lines);
            names[i] = filename;
        }

        return names;
    }
    
    public SerializedFile[] createSerializedFiles(String [] filenames) {
        SerializedFile []files = new SerializedFile[filenames.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new SerializedFile(filenames[i]);
        }
        return files;
    }
    
    
    /**
     * Write array to PrintWriter.
     * 
     * @param writer
     * @param datalines
     */
    public void writeLines(PrintWriter writer, String[] datalines) {
        for (String s : datalines) {
            writer.println(s);
        }
    }

    /**
     * File exist assertion.
     * 
     * @param filename
     *            file that must exist
     * @param message
     *            message to show if file does not exist
     */
    public void assertFileExists(String filename, String message) {
        assertTrue("Missing file " + message + ": " + filename, new File(filename).isFile());
    }

    /**
     * File exist assertion.
     * 
     * @param filename
     *            file that must exist
     */
    public void assertFileExists(String filename) {
        assertTrue("Missing file: " + filename, new File(filename).isFile());
    }

    /**
     * Directory exist assertion.
     * 
     * @param directoryName
     *            directory that must exist.
     * @param message
     *            message to show if file does not exist
     */
    public void assertDirectoryExists(String directoryName, String message) {
        assertTrue("Missing file " + message + ": " + directoryName, new File(directoryName).isDirectory());
    }

    /**
     * Directory exist assertion.
     * 
     * @param directoryName
     *            directory that must exist.
     */
    public void assertDirectoryExists(String directoryName) {
        assertTrue("Missing file: " + directoryName, new File(directoryName).isDirectory());
    }

    /**
     * Force a failure of the test
     * 
     * @param string
     */
    public void failTest(String string) {
        failTest(string, null);
    }
    
    /**
     * Force failure, print stack trace.
     * 
     * @param string
     * @param e
     */
    public void failTest(String string, Exception e) {

        if (e != null) {
            e.printStackTrace(System.err);
        }
        assertTrue(string, false);
    }

    /**
     * Automatically create directories if they do not exist.
     * 
     * @param createMissingDirectories
     */
    public void setCreateMissingDirectories(boolean createMissingDirectories) {
        this.createMissingDirectories = createMissingDirectories;
    }
    
}
