package edu.csus.ecs.pc2.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
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
    
    private PermissionGroup permissionGroup = new PermissionGroup();

    private boolean debugMode = false;

    private Random random  = new Random(System.currentTimeMillis());
    
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
     * @param filename
     */
    public String getRootInputTestFile(String filename){
        return getRootInputTestDataDirectory() + File.separator + filename;
    }
    
    
    /**
     * Get the full path to the input test directory under the project.
     * 
     * @see {@link #getTestFilename(String)}
     * @see {@link #getOutputTestFilename(String)}
     * @see {@link #getSamplesSourceFilename(String)}
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
      * @see {@link #getTestFilename(String)}
      * @see {@link #getOutputTestFilename(String)}
      * @see {@link #getSamplesSourceFilename(String)}
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
     * @param subDirectoryName
     * @return
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
    protected String getOutputDataDirectory(String directoryName) {

        String newDirName = getRootOutputTestDataDirectory() + File.separator + getShortClassName() + File.separator + directoryName;
        return newDirName;
    }

    /**
     * Return a test data directory for the current JUnit/class.
     * 
     * If directory does not exist will create it.
     * 
     * @see {@link #getTestFilename(String)}
     * @see {@link #getOutputTestFilename(String)}
     * @see {@link #getSamplesSourceFilename(String)}
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
     * @see {@link #getTestFilename(String)}
     * @see {@link #getOutputTestFilename(String)}
     * @see {@link #getSamplesSourceFilename(String)}
     * 
     * @param directoryName name of directory to append to end of string
     * @return a project-relative directory name  
     */
    public String getDataDirectory(String directoryName) {
        String newDirName = getDataDirectory() + File.separator + directoryName;
        assertDirectoryExists(newDirName);
        return newDirName; 
    }

   /**
    * Get input data file name.
    * 
    * Use this method to get JUnit test specific input filenames.
    * 
    * @param baseFilename
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
     * @param logFileBaseName
     * @return
     */
    public Log createLog(String logFileBaseName) {
        String logfilename = logFileBaseName + ".log";
        return new Log(getOutputDataDirectory(), logfilename);
    }
    
    /**
     * Get output file name relative to the output directory/
     * 
     * Use this method to get JUnit test specific output filename.
     * 
     * @see #getOutputDataDirectory().
     * @param baseFilename
     * @return
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
        assertTrue("Missing directory: " + directoryName, new File(directoryName).isDirectory());
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
     * Recursively remove directory and all sub directories/files.
     * 
     * @param dirName
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
     * remove a file.
     * @param filename
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
     * @param filename
     * @return
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
     * @param string
     * @param ch
     * @return
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
     * @param contest
     * @param xPathExpression
     * @param filter
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
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
     * @param contest
     * @param report
     * @param filter
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
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
     * @param report
     * @param filter
     * @return
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

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Write temporary files.
     * 
     * @param lines
     * @return
     * @throws IOException
     */
    public File writeTempFile (String [] lines) throws IOException{
        File tempFile  = File.createTempFile(getName(), "txt");
        writeFileContents(tempFile.getAbsolutePath(), lines);
        return tempFile;
    }

    /**
     * Write lines and start editor on temp file.
     * 
     * @param lines
     * @throws IOException
     */
    public void editTempFile(String  line) throws IOException {
        String [] lines = { line };
        File file = writeTempFile(lines);
        editFile (file.getAbsolutePath());
    }
    
    /**
     * Write lines and start editor on temp file.
     * 
     * @param lines
     * @throws IOException
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
     * @param xml
     */
    public void testForValidXML(String xml) throws Exception {
        
        assertFalse("Expected XML, found null", xml == null);
        assertFalse("Expected XML, found empty string", xml.length() == 0);
        
        getDocument(xml);
    }
    
    /**
     * Test for well formed XML that passes schema validation.
     * 
     * @param xml
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
     * @param xml
     */
    public void testForValidXML(String xml, File schemaFile) throws Exception {
        String [] lines = Utilities.loadFile(schemaFile.getCanonicalPath());
        StringBuffer buffer = Utilities.join(" ", lines);
        testForValidXML(xml, buffer.toString());
    }

    /**
     * Parse input string and return a Document.
     * 
     * @param xmlString
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document getDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return  documentBuilder.parse(new InputSource(new StringReader(xmlString)));
    }

    /**
     * Asserts that there are expectedCount occurrences of c in sourceString.
     * @param message
     * @param expectedCount
     * @param c
     * @param sourceString
     */
    public void assertCount(String message, int expectedCount, char c, String sourceString) {
        int actualCount = countCharacters(sourceString, c);
        assertEquals(message, expectedCount, actualCount);

    }

    /**
     * Asserts that there are expectedCount occurrences of stringToFind in sourceString.
     * @param message descriptive message for assert failure.
     * @param expectedCount
     * @param stringToFind
     * @param sourceString
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
     * @throws IOException
     */
    public void startExplorer(File dir) throws IOException
    {
//       String[] command = {"explorer.exe /e,"+directoryName.getAbsolutePath()};
       String command = "explorer.exe /e,"+dir.getAbsolutePath();
       System.out.println("cmd = "+command);
       Runtime.getRuntime().exec(command);
    }

}
