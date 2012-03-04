package edu.csus.ecs.pc2.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.model.SerializedFile;

import junit.framework.TestCase;

/**
 * A base TestCase case with utilities.
 * 
 * All PC^2 TestCase should use this to get the test directory names
 * for individual JUnits, will locate test directory under ({@link #DEFAULT_PC2_TEST_DIRECTORY}) 
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AbstractTestCase extends TestCase {

    private String testDataDirectory = null;

    public static final String DEFAULT_PC2_TEST_DIRECTORY = "testdata";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        insureDataDirectory();
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
        return testDataDirectory;
    }

    /**
     * Get Data directory for the JUnit.
     * 
     * Name will be {@link #getDataDirectory()} name and extending class
     * name.
     * 
     * @return
     */
    public String getDataDirectory() {
        return getTestDataDirectory() + File.separator + getShortClassName();
    }
    
    public String getDataDirectory(String testName) {
        return getTestDataDirectory() + File.separator + getShortClassName() + File.separator + testName;
    }
    

    /**
     * Get test case (JUnit) test file name (full path).
     * 
     * @param testJUnitName
     * @return
     */
    public String getTestFilename(String filename) {
        return getDataDirectory() + File.separator + filename;
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
        // System.out.println("debug 22 wrote ans to "+filename);
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
        // System.out.println("debug 22 wrote ans to "+filename);
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
    
    public void insureDataDirectory() {
        new File(getDataDirectory()).mkdirs();
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
}
