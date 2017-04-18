package edu.csus.ecs.pc2.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.junit.ComparisonFailure;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.LogFormatter;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.report.IReport;

/**
 * Test utility methods.
 * 
 * <br>
 * 
 * Fetch directory or filename relative to directory methods.  These should
 * be used to find source filenames as well as test filenames.
 * <ol>
 * <li>Get test input data filename - {@link #getTestFilename(String)}
 * <li>Get test output filename = {@link #getOutputTestFilename(String)}
 * <li>Get a data/source file name - {@link #getSamplesSourceFilename(String)}
 * </ol>
 * 
 * Other locations/directories, instead of using these directly use 
 * {@link #getTestFilename(String)}, {@link #getOutputTestFilename(String)} and
 * {@link #getSamplesSourceFilename(String)}.
 * <ol>
 * <li>Project Root directory - {@link #getProjectRootDirectory()}
 * <li>The root testdata directory - {@link #getRootInputTestDataDirectory()}
 * <li>Input JUnit method test directories - {@link #getDataDirectory()} and {@link #getDataDirectory(String)} 
 * <li>Get directory where sample source files are - {@link #getTestSamplesSourceDirectory()}
 * </ol>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AbstractTestCase extends TestCase {

    /**
     * A setting to speed up tests.
     * 
     * Used to skip tests that take longer than a second.
     * 
     */
//    private boolean fastJUnitTesting = true;
    private boolean fastJUnitTesting = false;
    
    private String testDataDirectory = null;

    /**
     * Directory under which all input testing data is located.
     * 
     * Do not use directly, use {@link #getDataDirectory()}.
     */
    public static final String DEFAULT_PC2_TEST_DIRECTORY = "testdata";
    
    public static final String CCS_EVENT_FEED_SCHEMA_2013 = "event-feed-2013.xsd";

    public static final String CCS_EVENT_FEED_SCHEMA = "event-feed.xsd";

    private String testingOutputDirectory = null;

    /**
     * Directory under which all testing output should be located.
     * 
     * Do not use directly, use {@link #getTestingOutputDirectory()}.
     */
    public static final String DEFAULT_PC2_OUTPUT_FOR_TESTING_DIRECTORY = "testout";
    
    // this file is all lowercase, not mixed-case like Sumit
    public static final String HELLO_SOURCE_FILENAME = "hello.java";
    
    public static final String SUMIT_SOURCE_FILENAME = "Sumit.java";
    
    /**
     * Environment variable to use fast test.
     * 
     * If {@value #ENV_KEY_FASTTEST} is defined then long tests will be skipped.
     * 
     * @see #isFastJUnitTesting()
     */
    private static final String ENV_KEY_FASTTEST = "pc2fasttest";
    
    private PermissionGroup permissionGroup = new PermissionGroup();

    private boolean debugMode = false;

    private Random random  = new Random(System.currentTimeMillis());

    private boolean usingGUI = false;
    
    public AbstractTestCase() {
        super();
        ensureOutputDirectory();
    }

    public AbstractTestCase(String testName) {
        super(testName);
        ensureOutputDirectory();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ensureOutputDirectory();
    }
    
    /**
     * Get the full filename for a file in the root testdata directory.
     * 
     * @see #getRootInputTestDataDirectory()
     * @param filename the name of the file for which the full file path and name is to be returned
     * @return a String containing the full path name of the specified file
     */
    public String getRootInputTestFile(String filename){
        return getRootInputTestDataDirectory() + File.separator + filename;
    }
    
    
    /**
     * Get the full path to the input test directory under the project.
     * 
     * @see #getTestFilename(String)
     * @see #getOutputTestFilename(String)
     * @see #getSamplesSourceFilename(String)
     * @return full path to the project test directory.
     */
    // SOMEDAY deprecated this method and use test method directory names for test data input
     public String getRootInputTestDataDirectory() {
        if (testDataDirectory == null) {

            String projectPath = JUnitUtilities.locate(DEFAULT_PC2_TEST_DIRECTORY);
            if (projectPath == null) {
                projectPath = "."; //$NON-NLS-1$
                System.err.println("AbstractTestCase: Warning - unable to locate in project " + DEFAULT_PC2_TEST_DIRECTORY);
            }
            testDataDirectory = projectPath + File.separator + DEFAULT_PC2_TEST_DIRECTORY;
        }
        
        assertDirectoryExists(testDataDirectory);
        return testDataDirectory;
    }
     
     /**
      * Project root directory.
      * 
      * @see #getTestFilename(String)
      * @see #getOutputTestFilename(String)
      * @see #getSamplesSourceFilename(String)
      * 
      * @return directory
      */
    public String getProjectRootDirectory() {

        String dirname = getRootInputTestDataDirectory();
        assertDirectoryExists(dirname);

        // remove default test dir and dir sep off end of string
        dirname = trimFromEnd  (dirname, 1 + DEFAULT_PC2_TEST_DIRECTORY.length());

        return dirname;
    }
    

    /**
     * Remove len characters off end of string.
     * 
     * @param string
     * @param len
     * @return shorter string.
     */
    private String trimFromEnd(String string, int len) {
        return string.substring(0, string.length() - len);
    }

    /**
     * Return the project (with JUnit name) relative directory where test output data is located.
     * 
     * This returns the proper project-relative path for the input testing data directory.
     * 
     * @see #getOutputTestFilename(String)
     * @return a project-relative directory name with the JUnit classname 
     */
    protected String getRootOutputTestDataDirectory() {
        if (testingOutputDirectory == null) {

            String outputDirectoryName = DEFAULT_PC2_OUTPUT_FOR_TESTING_DIRECTORY;
            
            String projectPath = JUnitUtilities.locate(outputDirectoryName);
            if (projectPath == null) {
                projectPath = "."; //$NON-NLS-1$
                System.err.println("AbstractTestCase: Warning - unable to locate in project " + outputDirectoryName);
            }
            testingOutputDirectory = projectPath + File.separator + outputDirectoryName;
        }
        
        return testingOutputDirectory;
    }
    
    /**
     * Ensures directory.
     * 
     * If directory cannot be created then returns false.
     * 
     * @param directoryName a String giving the name of the directory which should be verified as existing, 
     *                  or created if it doesn't already exist
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
     * Ensure output directory exists.
     * 
     * @see #getOutputDataDirectory()
     * @return true if directory exists or was created, false otherwise.
     */
    public boolean ensureOutputDirectory() {
        return ensureDirectory(getOutputDataDirectory());
    }
    
    /**
     * Ensure output sub directory exists under output directory.
     * 
     * @see #ensureOutputDirectory()
     * @param subDirectoryName - the name of the subdirectory which should be ensured to exist under the output directory
     * @return true if the specified subdirectory exists (or was created) in the output directory; false otherwise
     */
    public boolean ensureOutputDirectory(String subDirectoryName) {
        return ensureDirectory(getOutputDataDirectory(subDirectoryName));
    }
    
    

    /**
     * Return the project (with JUnit name) relative directory where test output data is located.
     * 
     * @param directoryName name of directory to append to end of path
     * @return a project-relative directory name  
     */
    public String getOutputDataDirectory(String directoryName) {

        String newDirName = getRootOutputTestDataDirectory() + File.separator + getShortClassName() + File.separator + directoryName;
        return newDirName;
    }

    /**
     * Return a test data directory for the current JUnit/class.
     * 
     * If directory does not exist will create it.
     * 
     * @see #getTestFilename(String)
     * @see #getOutputTestFilename(String)
     * @see #getSamplesSourceFilename(String)
     * 
     * @return a project-relative directory name, appends the JUnit name at the end of the string. 
     */
    public String getDataDirectory() {
        String dirname = getRootInputTestDataDirectory() + File.separator + getShortClassName();
        ensureDirectory(dirname);
        return dirname;
    }

    /**
     * Return the project (with JUnit name) relative directory where test input data is located.
     * 
     * @see #getTestFilename(String)
     * @see #getOutputTestFilename(String)
     * @see #getSamplesSourceFilename(String)
     * 
     * @param directoryName name of directory to append to end of string
     * @return a project-relative directory name  
     */
    public String getDataDirectory(String directoryName) {
        String newDirName = getDataDirectory() + File.separator + directoryName;
        return newDirName; 
    }

   /**
    * Get input data file name.
    * 
    * Use this method to get JUnit test specific input filenames.
    * 
    * @param baseFilename a String giving the name of the file whose full path name in the data directory should be returned
    * @return project and JUnit method relative filename.
    */
    public String getTestFilename(String baseFilename) {
        return getDataDirectory() + File.separator + baseFilename;
    }

    /**
     * Create log relative to the output data directory.
     * 
     * @see #getOutputDataDirectory()
     * 
     * @param logFileBaseName a String giving the base name of the .log file which should be created in the output data directory
     * @return a {@link edu.csus.ecs.pc2.core.log.Log} object with the specified log file name
     */
    public Log createLog(String logFileBaseName) {
        String logfilename = logFileBaseName + ".log";
        return new Log(getOutputDataDirectory(), logfilename);
    }
    
    /**
     * Get output file name relative to the output directory.
     * 
     * Use this method to get JUnit test specific output filename.
     * 
     * @see #getOutputDataDirectory()
     * @param baseFilename a String giving the base name of a file in the output data directory
     * @return the full path name of the specified file
     */
    public String getOutputTestFilename(String baseFilename) {
        return getOutputDataDirectory() + File.separator + baseFilename;
    }
    
    
    /**
     * Return an output data directory for the current JUnit/class.
     * 
     * Creates directory if needed.
     * 
     * @return a test output directory for the current JUnit/class.the output
     */
    public String getOutputDataDirectory() {
        String dirname = getRootOutputTestDataDirectory() + File.separator + getShortClassName();
        return dirname;
    }

    /**
     * get this class name (without package).
     * 
     * @return class name for the current JUnit test class.
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
        writeLines(writer, datalines);
        writer.close();
        writer = null;
    }
    
    /**
     * Create N files with sample data.
     *  
     * @param n the number of files which should be created
     * @param lines a String array containing the lines to be written into each created file
     * @param ext a String giving the file name extension to be applied to each created file
     * @return an array of Strings containing the names of the created files
     * @throws FileNotFoundException if a file of the specified name could not be created
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
     * @param writer a PrintWriter to which the specified array of Strings should be written
     * @param datalines a String array containing the lines of data to be written to the specified PrintWriter
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
     *            message to show if directory does not exist
     */
    public void assertDirectoryExists(String directoryName, String message) {
        assertTrue("Missing directory " + message + ": " + directoryName, new File(directoryName).isDirectory());
    }

    /**
     * Directory exist assertion.
     * 
     * @param directoryName
     *            directory that must exist.
     */
    public void assertDirectoryExists(String directoryName) {
        assertTrue("Missing directory: " + directoryName, new File(directoryName).isDirectory());
    }

    /**
     * Force a failure of the test.
     * 
     * @param string A String which will be written out as part of the failure message
     */
    public void failTest(String string) {
        failTest(string, null);
    }
    
    /**
     * Force failure, print stack trace.
     * 
     * @param string a String which will be written out as part of the failure message
     * @param e an Exception whose StackTrace will be written to System.err
     */
    public void failTest(String string, Exception e) {

        if (e != null) {
            e.printStackTrace(System.err);
        }
        assertTrue(string, false);
    }

    /**
     * Recursively remove directory and all sub directories/files.
     * 
     * @param dirName the name of the directory to be removed
     * @return true if all directories removed.
     */
    public boolean removeDirectory(String dirName) {
        boolean result = true;
        File dir = new File(dirName);
        
        if (! dir.exists()){
            // If not there then it is removed !! :)
            return true;
        }
        
        String[] filesToRemove = dir.list();
        for (int i = 0; i < filesToRemove.length; i++) {
            File fn1 = new File(dirName + File.separator + filesToRemove[i]);
            if (fn1.isDirectory()) {
                // recurse through any directories
                result &= removeDirectory(dirName + File.separator + filesToRemove[i]);
            }
            result &= fn1.delete();
        }
        return (result);
    }

    /**
     * Remove a file.
     * @param filename the name of the file to be removed
     * @throws RuntimeException if cannot remove the directory entry/file
     */
    public void removeFile(String filename) {

        File file = new File(filename);
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new RuntimeException("Unable to remove directory " + filename);
            } else {
                file.delete();
            }
        } // no else if it does not iexst, then it iss delete

        file = new File(filename);
        if (file.exists()) {
            throw new RuntimeException("Unable to remove file " + filename);
        }
    }

    /**
     * Get sample source or data filename.
     * 
     * Examples.
     * <pre>
     * String filename = getSamplesSourceFilename("Sumit.java");
     * 
     * String filename = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
     * 
     * </pre>
     *  
     * @param filename the name of a file
     * @return the full path name to the specified file
     */
    public String getSamplesSourceFilename(String filename) {

        String name = getTestSamplesSourceDirectory() + File.separator + filename;
        assertFileExists(name);
        return name;
    }
 
    /**
     * Directory for pc2 samples.
     * 
     * @see #getSamplesSourceFilename(String)
     * @return directory
     */
    public String getTestSamplesSourceDirectory() {
        return getProjectRootDirectory() + File.separator + "samps" + File.separator + "src";
    }
    
    public String getSchemaFilename(String filename){
        String name = getSchemaDirectory() + File.separator + filename;
        assertFileExists(name);
        return name;
    }
    
    public String getSchemaDirectory() {
        return getProjectRootDirectory() + File.separator + "samps" + File.separator + "schema";
    }
    
    /**
     * Remove every ch from string.
     * @param string the String from which characters are to be removed
     * @param ch the character which is to be removed from the specified String
     * @return a new String with the specified character removed, or the original String if the
     *      character did not exist in the String
     */
    public String stripChar(String string, char ch) {
        int idx = string.indexOf(ch);
        while (idx > -1) {
            // only strip out if ch is in string.
            StringBuffer sb = new StringBuffer(string);
            idx = sb.indexOf(ch + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch + "");
            }
            return sb.toString();
        }
        return string;
    }

    /**
     * Get nodelist based on XPath expression.
     * 
     * @param report a {@link edu.csus.ecs.pc2.core.report.IReport} from which a NodeList is to be extracted
     * @param xPathExpression a {@link javax.xml.xpath.XPathExpression} to be evaluated
     * @param filter a {@link edu.csus.ecs.pc2.core.model.Filter} to be applied to the specified IReport
     * @return an {@link org.w3c.dom.NodeList} extracted from the specified {@link edu.csus.ecs.pc2.core.report.IReport}
     * @throws SAXException if a {@link org.w3c.dom.Document} cannot be created from the specified IReport
     * @throws IOException if a {@link org.w3c.dom.Document} cannot be created from the specified IReport
     * @throws ParserConfigurationException if a {@link org.w3c.dom.Document} cannot be created from the specified IReport
     * @throws XPathExpressionException if the specified xPathExpression cannot be compiled
     */
    public NodeList getXMLNodeList(IReport report, String xPathExpression, Filter filter) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Document doc = createDocument(report, filter);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile(xPathExpression);
        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        return nl;
    }

    /**
     * Create document for report.
     * 
     * @param report an {@link edu.csus.ecs.pc2.core.report.IReportFile} 
     * @param filter a {@link edu.csus.ecs.pc2.core.model.Filter}
     * @return an {@link org.w3c.dom.Document} created from the specified IReport with the specified Filter applied
     * @throws ParserConfigurationException if the Document could not be created
     * @throws IOException if the Document could not be created
     * @throws SAXException if the Document could not be created
     */
    public Document createDocument(IReport report, Filter filter) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xmlString = report.createReportXML(filter);
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        Document doc = builder.parse(inputStream);
        return doc;
    }

    /**
     * Checks whether report creates valid XML, throws Exception if not.
     * @param report the {@link edu.csus.ecs.pc2.core.report.IReport} to be checked
     * @param filter the {@link edu.csus.ecs.pc2.core.model.Filter} to be applied to the IReport
     * @return an Exception object if the report fails to create valid XML, or null if the report does create valid XML
     */
    public Exception isValidXML(IReport report, Filter filter) {
        try {
            createDocument(report, filter);
            return null;
        } catch (Exception e) {
            return e;
        }
    }
    
    public edu.csus.ecs.pc2.core.security.Permission.Type[] getPermList(Type type) {
        return permissionGroup.getPermissionList(type).getList();
    }

    /**
     * Set debug model.
     * 
     * @param debugMode if true then {@link #debugPrint(String)} methods will output text.
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Write data to a temporary file.
     * The file name will be the name of this JUnit test, with an extension of ".txt".
     * 
     * @param lines an array of Strings containing the data to be written to the file
     * @return the created File object
     * @throws IOException if the temporary file cannot be created
     */
    public File writeTempFile (String [] lines) throws IOException{
        File tempFile  = File.createTempFile(getName(), "txt");
        writeFileContents(tempFile.getAbsolutePath(), lines);
        return tempFile;
    }

    /**
     * Write lines and start editor on temp file.
     * 
     * @param line a String to be written to the temporary file
     * @throws IOException if the temporary file cannot be created
     */
    public void editTempFile(String  line) throws IOException {
        String [] lines = { line };
        File file = writeTempFile(lines);
        editFile (file.getAbsolutePath());
    }
    
    /**
     * Write lines and start editor on temp file.
     * 
     * @param lines an array of Strings to be written to a temporary file
     * @throws IOException if the temporary file cannot be written
     */
    public void editTempFile(String [] lines) throws IOException {
        File file = writeTempFile(lines);
        editFile (file.getAbsolutePath());
    }
    
    public void editFile(String filename) {

        String viFile = findViExecutable();

        String[] command = { viFile, filename };
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println("Could not run command " + Arrays.toString(command));
            e.printStackTrace(System.err);
        }
    }
    
    private String findViExecutable() {

        String[] names = //
        { //
        File.separator + "windows" + File.separator + "vi.bat", //
                File.separator + "windows" + File.separator + "vi.exe",//
                File.separator + "windows" + File.separator + "gvim.exe",//
                //
                File.separator + "bin" + File.separator + "vi",//
        };

        for (String name : names) {
            if (new File(name).isFile()) {
                return name;
            }
        }

        return "vi";
    }


    /**
     * Test for well formed XML.
     * 
     * @param xml a String containing XML
     * 
     * @throws Exception if the specified XML string is not valid XML
     */
    public void testForValidXML(String xml) throws Exception {
        
        assertFalse("Expected XML, found null", xml == null);
        assertFalse("Expected XML, found empty string", xml.length() == 0);
        
        getDocument(xml);
    }
    
    /**
     * Test for well formed XML that passes schema validation.
     * 
     * @param xml a String containing XML
     * @param schemaString a String containing the {@link javax.xml.validation.Schema} for the specified XML string
     * 
     * @throws Exception if the xml fails to parse according to the specified schema
     */
    public void testForValidXML(String xml, String schemaString) throws Exception {

        assertFalse("Expected XML, found null", xml == null);
        assertFalse("Expected XML, found empty string", xml.length() == 0);

        /**
         * Test for valid XML.
         */
        getDocument(xml);

        // Source schemaFile = new StreamSource(new File("schema.xsd"));
        // Source xmlFile = new StreamSource(new File("test_xml.xml"));
        // Source inputXML = new StreamSource(new StringReader(xmlString));
        // Source inputXSL = new StreamSource(xslFile);

        Source schemaSource = new StreamSource(new StringReader(schemaString));
        Source xmlSource = new StreamSource(new StringReader(xml));

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaSource);
        Validator validator = schema.newValidator();

        validator.validate(xmlSource);
        // System.out.println(xmlSource.getSystemId() + " is valid");

    }

    /**
     * Test for well formed XML that passes schema validation.
     * 
     * @param xml a String containing XML
     * @param schemaFile a File containing the Schema for the XML
     * 
     * @throws IOException if the schema file could not be loaded
     * @throws Exception if the XML string fails to match the schema
     */
    public void testForValidXML(String xml, File schemaFile) throws Exception {
        String [] lines = Utilities.loadFile(schemaFile.getCanonicalPath());
        StringBuffer buffer = Utilities.join(" ", lines);
        testForValidXML(xml, buffer.toString());
    }

    /**
     * Parse input string and return a Document.
     * 
     * @param xmlString a String containing XML
     * @return a Document constructed from the xmlString
     * @throws ParserConfigurationException if a DocumentBuilder could not be constructed
     * @throws SAXException if the specified xmlString did not parse correctly
     * @throws IOException if any I/O error occurs
     */
    public Document getDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return  documentBuilder.parse(new InputSource(new StringReader(xmlString)));
    }

    /**
     * Compare two text files, fail if contents not identical.
     * 
     * @param expectedFile the first file to be compared
     * @param actualFile the second file to be compared
     * @throws IOException if any error occurs in comparing the files
     */
    public void assertFileContentsEquals(File expectedFile, File actualFile) throws IOException{
        // Compare two files 
        // file comparison 
        assertFileContentsEquals(expectedFile, actualFile, 1);
    }
    
    
    /**
     * Override for string compare
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    public interface OverrideStringCompare {
        /**
         * 
         * @return true if strings consider equaled
         */
        public boolean stringEquals(String one, String two);
    }
    
    public void assertFileContentsEquals(File expectedFile, File actualFile, int startLine, OverrideStringCompare comp) throws IOException{

        if (! expectedFile.isFile()){
            throw new FileNotFoundException(expectedFile.getAbsolutePath());
        }

        if (! actualFile.isFile()){
            throw new FileNotFoundException(actualFile.getAbsolutePath());
        }
        
        String [] expectedContents = Utilities.loadFile(expectedFile.getAbsolutePath());
        String [] actualContents = Utilities.loadFile(actualFile.getAbsolutePath());
        
        if (expectedContents.length  != actualContents.length){
            throw new ComparisonFailure("File contents different number of lines", ""+expectedContents.length, ""+actualContents.length);
        }

        for (int i = startLine - 1; i < expectedContents.length; i++) {
            String expected = expectedContents[i];
            String actual = actualContents[i];
            boolean stringsDiffer = true;
            if (comp != null) {
                stringsDiffer = ! comp.stringEquals(expected, actual);
                if (stringsDiffer){
                    System.err.println("assertFileContentsEquals: using OverrideStringCompare to compare lines ");
                }
            } else {
                stringsDiffer = !expected.equals(actual);
            }
            if (stringsDiffer){
                System.err.println("assertFileContentsEquals: expected file: "+expectedFile.getAbsolutePath());
                System.err.println("assertFileContentsEquals: actual   file: "+actualFile.getAbsolutePath());
                System.err.println("assertFileContentsEquals: Diff lines at: "+(i+1));
                System.err.println("assertFileContentsEquals: expected     : '"+expected+"'");
                System.err.println("assertFileContentsEquals: actual       : '"+actual+"'");
                assertEquals("Expecting same line ine "+(i+1), expected, actual);
            }
        }

    }
    /**
     * Text file contents equal.
     * 
     * @param expectedFile the first file to be compared
     * @param actualFile the second file to be compared
     * @param startLine (base 1) first line to compare in files.
     * 
     * @throws FileNotFoundException if either of the specified files cannot be found
     * @throws IOException if an error occurs in reading the files
     */
    public void assertFileContentsEquals(File expectedFile, File actualFile, int startLine) throws IOException{
        
        assertFileContentsEquals (expectedFile, actualFile, startLine, null);
            }
    
    /**
     * Asserts that there are expectedCount occurrences of c in sourceString.
     * @param message a String to be displayed if the assertion fails
     * @param expectedCount the number of expected occurences of c in the sourceString
     * @param c the character to be checked for
     * @param sourceString the String in which to check for occurrences of c
     */
    public void assertCount(String message, int expectedCount, char c, String sourceString) {
        int actualCount = countCharacters(sourceString, c);
        assertEquals(message, expectedCount, actualCount);

    }

    /**
     * Asserts that there are expectedCount occurrences of stringToFind in sourceString.
     * @param message a String to be displayed if the assertion fails
     * @param expectedCount the number of expected occurences of stringToFind in the sourceString
     * @param stringToFind the String to be searched for
     * @param sourceString the String to search 
     */
    public void assertCount(String message, int expectedCount, String stringToFind, String sourceString) {
        int actualCount = countString(sourceString, stringToFind);
        if (actualCount != expectedCount){
            System.out.println("Searching for "+stringToFind+" count = "+actualCount+" '"+sourceString+"'");
        }
        assertEquals(message, expectedCount, actualCount);

    }

    /**
     * Returns number of occurrences of string in source.
     * 
     * @param source the String to search
     * @param string the String to search for in the source string
     * 
     * @return the number of occurrences of the specified string in the source string
     */
    public int countString(String source, String string) {
        int idx = source.indexOf(string);
        if (idx != -1) {
            int count = 0;
            while (idx != -1) {
                idx = source.indexOf(string, idx + string.length());
                count++;
            }
            return count;
        } else {
            return 0;
        }
    }

    /**
     * return number of chartocount in source.
     * 
     * @param source the String to search
     * @param chartocount the char to search for in the source string
     * 
     * @return the count of occurrences of the specified character in the specified source string
     */
    public int countCharacters(String source, char chartocount) {
        return source.split("\\" + chartocount, -1).length - 1;
    }
    
    public Account getRandomTeam(IInternalContest contest) {
        return getRandomAccount(contest, Type.TEAM);
    }

    public Problem getRandomProblem(IInternalContest contest) {
        Problem [] problems = contest.getProblems();
        int randomProblemIndex = random.nextInt(problems.length);
        return problems[randomProblemIndex];
    }
    
    public Language getRandomLanguage(IInternalContest contest) {
        Language[] languages = contest.getLanguages();
        int randomLangIndex = random.nextInt(languages.length);
        return languages[randomLangIndex];
        
    }
    
    public Account getRandomAccount(IInternalContest contest, Type type) {
        Account[] accounts = getAccounts(contest, type);
        int randomIndex = random.nextInt(accounts.length);
        return accounts[randomIndex];
    }
    
    public Account[] getAccounts(IInternalContest contest, ClientType.Type type) {
        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Start Windows Explorer.
     * 
     * @param dir directory to display
     * @throws IOException if the Runtime package is unable to launch Windows Explorer
     */
    public void startExplorer(File dir) throws IOException {
//       String[] command = {"explorer.exe /e,"+directoryName.getAbsolutePath()};
       String command = "explorer.exe /e,"+dir.getAbsolutePath();
       System.out.println("cmd = "+command);
       Runtime.getRuntime().exec(command);
    }
    
    /**
     * Start Windows Explorer on  directory.
     * 
     * @param directoryName the directory in which Windows Explorer should be started
     * 
     * @throws IOException if Windows Explorer could not be started in the specified directory
     */
    public void startExplorer(String directoryName) throws IOException {
        File dir = new File(directoryName);
        startExplorer(dir);
    }
    
    /**
     * Start Windows Explorer on data directory.
     * @throws IOException if Windows Explorer cannot be started
     */
    public void startExplorer() throws IOException {
        startExplorer(getDataDirectory());
    }

    /**
     * Compares strings, if strings are equal fails. 
     * 
     * @param message a String to be displayed if the equality test fails
     * @param expected the first String to be compared
     * @param actual the second string to be compared
     */
    public void assertNotEquals(String message, String expected, String actual) {
        if (actual.equals(expected)){
            throw new ComparisonFailure(message, expected, actual);
        }
    }
    

    public Account getFirstJudge(IInternalContest contest) {
        Account[] accounts = new SampleContest().getJudgeAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        return accounts[0];
    }
    
    
    /**
     * Compares count of directory entries in dir with expectedNumberOfFiles.
     * @param string a message to be displayed if the number of files in the directory doesn't match
     * @param dir the directory to be checked
     * @param expectedNumberOfFiles the number of files expected to be found in the directory
     */
    public void assertExpectedFileCount(String string, File dir, int expectedNumberOfFiles){
        
        int entryCount = 0;
        File[] entries = dir.listFiles();
        for (File file : entries) {
            if (file.isFile()){
                entryCount++;
            }
        }
        
        if (expectedNumberOfFiles != entryCount){
            throw new ComparisonFailure(string+" in "+dir.getAbsolutePath(), Integer.toString(expectedNumberOfFiles), Integer.toString(entryCount));
        }
    }

    /**
     * Test if any files in input directory have zero bytes.
     * 
     * @param dir a directory to be checked for having no zero-size files
     */
    public void assertNoZeroSizeFiles(File dir) {
       
        File[] list = dir.listFiles();
        for (File file : list) {
            if (file.isFile() && file.length() == 0) {
                fail("Found zero byte file " + file.getAbsolutePath());
            }
        }
    }
    
    
    public String join(String delimit, String[] names) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            buffer.append(names[i]);
            if (i < names.length - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    /**
     * Print log output to console/stdout.
     * 
     * @param log the Log object to be displayed on the console
     */
    public void addConsoleHandler(Log log) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        log.addHandler(consoleHandler);
    }
    
    /**
     * Add output to console.
     * 
     * Example of verbose log entry:
     * 170222 074047.361|INFO|main|testCorruptSettingsFile|Logging started for testCorruptSettingsFile
     * 
     * @param log
     * @param verbose prefix each line with date,time|Level|method name|
     */
    public void addConsoleHandler(Log log, boolean verbose) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        if (verbose) {
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new LogFormatter(true));
        }
        log.addHandler(consoleHandler);
    }

    /**
     * Set log level to DEBUG.
     * 
     * @param the Log whose level is being set
     */
    public void setDebugLevel(Log log) {
        log.setLevel(Log.DEBUG);
    }

    /**
     * Print string if debugMode.
     * 
     * @see #setDebugMode(boolean)
     * @param string the String to be printed if in debugMode
     */
    public void debugPrint(String string) {
        if (debugMode){
            System.err.print(string);
        }
    }
    
    /**
     * Print string if debugMode.
     * @param string the String to be printed if in debugMode
     */
    public void debugPrintln(String string) {
        if (debugMode){
            System.err.println(string);
        }
    }
    public void debugPrintln() {
        if (debugMode){
            System.err.println();
        }
    }
    

    public void testNull() throws Exception {
        
        /**
         * Stub test so JUnit runner doesn't complain
         */
    }

    /**
     * Get a SERVER client id.
     * 
     * This is a generic send to all server and clients ClientId.
     * @param contest  the Contest for which a Site Number is returned
     * 
     * @return a generic all sites server client id
     */
    public ClientId getServerClientId(IInternalContest contest) {
        return new ClientId(contest.getSiteNumber(), ClientType.Type.SERVER, 0);
    }
    
    public void copyFileOverwrite(String source, String target) throws IOException {
        File file = new File(target);
        if (file.isFile()) {
            file.delete();
        }
        Files.copy(new File(source).toPath(), new File(target).toPath());
    }

    public void copyFileOverwrite(String source, String target, Log log) throws IOException {
        copyFileOverwrite(source, target);
    }

    /**
     * Comparator of ElemendIds
     */
     public class ElementIdComparator implements Comparator<ElementId> {
        public int compare(ElementId elementIdOne, ElementId elementIdTwo) {
            return elementIdOne.toString().compareTo(elementIdTwo.toString());
        }
    }

    public void assertEquals(String message, Filter expected, Filter actual) {

        assertEquals(message + " problem list ", expected.getProblemIdList(), actual.getProblemIdList());
        assertEquals(message + " language list ", expected.getLanguageIdList(), actual.getLanguageIdList());
        assertEquals(message + " language list ", expected.getJudgementIdList(), actual.getJudgementIdList());

        assertEquals("filter ", expected.toString(), actual.toString());

    }

    private void assertEquals(String message, ElementId[] expectedList, ElementId[] actualList) {

        int maxValue = Math.max(expectedList.length, actualList.length);
        Arrays.sort(expectedList, new ElementIdComparator());
        Arrays.sort(actualList, new ElementIdComparator());

        int exCount = expectedList.length;
        int actCount = actualList.length;

        for (int i = 0; i < maxValue; i++) {
            if (i < expectedList.length && i < actualList.length) {

                if (!expectedList[i].equals(actualList[i])) {
                    if (exCount != actCount) {
                        message += " expected#= " + exCount + " act#= " + actCount;
                    }
                    // System.out.println("exp = " + expectedList[i].toString());
                    // System.out.println("act = " + actualList[i].toString());
                    throw new ComparisonFailure(message, expectedList[i].toString(), actualList[i].toString());
                }
            }
        }

        assertEquals(message + " list length ", exCount, actCount);
        
//        System.out.println("act list = "+actCount+" exp list "+exCount);
    }


    /**
     * Flag to skip tests that take a long time.
     * 
     * @return true to skip, else false.
     */
    public boolean isFastJUnitTesting() {

        if (isInEnvironment(ENV_KEY_FASTTEST)) {
            return true;
        }

        return fastJUnitTesting;
    }
    

    private boolean isInEnvironment(String string) {
        Map<String, String> map = System.getenv();
        return map.containsKey(string);
    }

    /**
     * Test xml tag for value, compare against expected value.
     * 
     * @param xml - input string
     * @param tagName - XML tag name, ex. info
     * @param expectedValue - value that should be in the first matched tag
     */
    public void assertFirstValueFound(String xml, String tagName, String expectedValue) {

        String startTag = "<" + tagName + ">";

        int startIndex = xml.indexOf(startTag);
        if (startIndex == -1) {
            fail("No tag " + tagName + " found in XML ");
        }

        String endTag = "</" + tagName + ">";
        int endIndex = xml.indexOf(endTag);
        if (endIndex == -1) {
            fail("No end tag " + tagName + " found in XML ");
        }

        if (endIndex <= startIndex) {
            fail("End tag appears before start tag " + tagName);
        }

        String value = xml.substring(startIndex + startTag.length(), endIndex);
        assertEquals("Expecting tag value for " + startTag, expectedValue, value);

    }
    
    
    /**
     * Reads input file and convert input file ./ to native OS path/file separators.
     * 
     * Only applies to ./ string.
     * 
     * @param filename
     * @throws Exception
     */
    public void convertToNativeFileSeperators(String filename) throws Exception {

        File actualFile = new File(filename);
        File newFile = new File(filename + ".tmp");
        PrintWriter printWriter = new PrintWriter(newFile);
        String[] actualContents = Utilities.loadFile(actualFile.getAbsolutePath());
        for (int i = 0; i < actualContents.length; i++) {
            String string = actualContents[i];
            string = string.replace("./", ".\\");
            printWriter.println(string);
        }
        printWriter.close();
        actualFile.delete();
        boolean result = newFile.renameTo(actualFile);
        assertTrue("rename Failed", result);
    }

    /**
     * 
     * @return
     */
    public boolean isGui() {
        return usingGUI;
    }
    
    public void setGUI(boolean useGUI) {
        this.usingGUI = useGUI;
    }
    
}
