package edu.csus.ecs.pc2.core.model;

import java.io.File;

import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * main prints Language definitions.
 * 
 * @author pc2@ecs.csus.edu
 */

public class LanguageAutoFillTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testisInterpretedLanguage() throws Exception {

        String[] keys = { LanguageAutoFill.PERLTITLE, LanguageAutoFill.PHPTITLE, LanguageAutoFill.PYTHONTITLE, LanguageAutoFill.PYTHON3TITLE, LanguageAutoFill.RUBYTITLE, };

        for (String key : keys) {
            assertTrue("Expecting interpreted language", LanguageAutoFill.isInterpretedLanguage(key));
        }

        String[] keys2 = { LanguageAutoFill.JAVATITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.GNUCTITLE, LanguageAutoFill.MSCTITLE, LanguageAutoFill.KYLIXTITLE,
                LanguageAutoFill.KYLIXCPPTITLE, LanguageAutoFill.FPCTITLE };
        for (String key : keys2) {
            assertFalse("Expecting NOT interpreted language", LanguageAutoFill.isInterpretedLanguage(key));
        }

    }

    public void testVariableNames() throws Exception {
        String[] keys = { LanguageAutoFill.JAVATITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.GNUCTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.PHPTITLE, LanguageAutoFill.PYTHONTITLE,
                LanguageAutoFill.RUBYTITLE, LanguageAutoFill.MSCTITLE, LanguageAutoFill.KYLIXTITLE, LanguageAutoFill.KYLIXCPPTITLE, LanguageAutoFill.FPCTITLE };

        for (String key : keys) {

            String[] values = LanguageAutoFill.getAutoFillValues(key);

            // String theKey = values[0];
            String compileCommandLine = values[1];
            // String executableFilename = values[2];
            String programExecutionCommandLine = values[3];
            // String displayName = values[4];

            assertTrue("Expected {: in " + compileCommandLine + " for " + key, compileCommandLine.indexOf("{:") > -1);
            assertTrue("Expected {: in " + programExecutionCommandLine + " for " + key, programExecutionCommandLine.indexOf("{:") > -1);

        }
    }
    
    public void testPython3Def() throws Exception {

        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.PYTHON3TITLE);

        String[] values = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.PYTHON3TITLE);

        assertEquals("interpreted ", true, language.isInterpreted());

        assertEquals("lang name ", values[0], language.getDisplayName());

        assertEquals("lang execute ", values[3], language.getProgramExecuteCommandLine());

    }
    
    /**
     * Test Mono C#.
     * 
     * Bug 1134 unit test.
     * 
     * @throws Exception
     */
    public void testMonoCSharpDef() throws Exception {
        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.MONOCSHARPTITLE);

        String[] values = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MONOCSHARPTITLE);

        assertEquals("interpreted ", false, language.isInterpreted());

        assertEquals("lang name ", values[0], language.getDisplayName());

        assertEquals("lang execute ", values[3], language.getProgramExecuteCommandLine());
        
        assertEquals("lang execute ", "mono {:basename}.exe", language.getProgramExecuteCommandLine());
        
    }

    /**
     * Test Microsoft C#.
     * 
     * Bug 1263 unit test.
     * 
     * @throws Exception
     */
    public void testMSCSharpDef() throws Exception {
        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.MSCSHARPTITLE);

        String[] values = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MSCSHARPTITLE);

        assertEquals("interpreted ", false, language.isInterpreted());

        assertEquals("lang name ", values[0], language.getDisplayName());

        assertEquals("lang execute ", values[3], language.getProgramExecuteCommandLine());
        
        assertEquals("lang execute ", "." + File.separator + "{:basename}.exe", language.getProgramExecuteCommandLine());
        
    }

    public static void printDefs() {

        String[] keys = { LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, //
                LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.GNUCTITLE, LanguageAutoFill.PERLTITLE, //
                LanguageAutoFill.MSCTITLE, LanguageAutoFill.MONOCSHARPTITLE,
                LanguageAutoFill.KYLIXTITLE, LanguageAutoFill.KYLIXCPPTITLE, // 
                LanguageAutoFill.FPCTITLE // 
        };

        for (String key : keys) {

            String[] fields = LanguageAutoFill.getAutoFillValues(key);

//            System.out.println("Language: " + fields[4]);
            if (fields.length != 6) {
                System.out.println(" Expecting 6 fields, got " + fields.length);
                System.exit(4);
            }
            
//            System.out.println("             " + fields[0]);
//            System.out.println("             " + fields[1]);
//            System.out.println("             " + fields[2]);
//            System.out.println("             " + fields[3]);
//            System.out.println("             " + fields[4]);
//            System.out.println("             " + fields[5]);

            
            System.out.println(" Title for Language          : " + fields[0]);
            System.out.println(" Compiler Command Line       : " + fields[1]);
            System.out.println(" Executable Identifier Mask  : " + fields[2]);
            System.out.println(" Execute command line        : " + fields[3]);
            System.out.println(" Interpreted                 : " + fields[5]);
            
            
            System.out.println();
        }
    }

    public static void main(String[] args) {

        printDefs();

    }
    
    /**
     * Test auto fill for Mono C# compiler.
     * 
     * Bug 1134.
     * 
     * @throws Exception
     */
    public void testMonoCsCompilerCmdLine() throws Exception {
        
        String[] fields = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MONOCSHARPTITLE);
        assertEquals("Expecting compiler command line ", "mcs {:mainfile}", fields[1]);

        fields = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MONOCSHARPTITLE);
        assertEquals("Expecting compiler command line ", "Mono C#", fields[0]);
        
        
    }
    /**
     * Test auto fill for Microsoft C# compiler.
     * 
     * Bug 1263.
     * 
     * @throws Exception
     */
    public void testMSCsCompilerCmdLine() throws Exception {
        
        String[] fields = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MSCSHARPTITLE);
        assertEquals("Expecting compiler command line ", "csc {:mainfile}", fields[1]);

        fields = LanguageAutoFill.getAutoFillValues(LanguageAutoFill.MSCSHARPTITLE);
        assertEquals("Expecting compiler command line ", "Microsoft C#", fields[0]);
        
        
    }

}
