// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LanguageTest extends AbstractTestCase {
    
    
    public LanguageTest(String name) {
        super(name);
    }


    public void testJudgeProgramExecuteCommandLine() throws Exception {
        
         Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
         
         assertNotNull("Expecting judge exe ", language.getActiveProgramExecuteCommandLine());
         
         assertEquals("Expecting team and judge exec to be identical ",language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());
         
         language.setJudgeProgramExecuteCommandLine("security wrapper");
         language.setUsingJudgeProgramExecuteCommandLine(true);
         
         assertNotEquals("Expecting team dn judge exec to be different",language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());
         
    }
    
    
    /**
     * Test {@link Language#isUsingJudgeProgramExecuteCommandLine()} and {@link Language#setJudgeProgramExecuteCommandLine(String)}.
     * <br>
     * Test Bug 903.
     *  
     * @throws Exception
     */
    public void testIsSameAsJudgeCommandLineAndFlag() throws Exception {
        
        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        Language language2 = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        
        assertFalse("language NOT expected to be equal",language.equals(language2));

        assertTrue("language expected to be the same",language.isSameAs(language2));

        assertFalse("Since setJudgeProgramExecuteCommandLine to be false", language.isUsingJudgeProgramExecuteCommandLine());

        assertEquals("Expecting team exec and judge exec to be same ", language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());

        /**
         * Expecting team and judge command lines equal becauase isUsingJudgeProgramExecuteCommandLine is false.
         */
        assertEquals("Expecting team exec and judge exec to be same ", language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());
        
        language.setJudgeProgramExecuteCommandLine(language2.getActiveProgramExecuteCommandLine());
        assertTrue("language expected to be the same",language.isSameAs(language2));

        assertEquals("Expecting team exec and judge exec NOT to be same ", language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());

        language.setUsingJudgeProgramExecuteCommandLine(true);
        
        assertFalse("language NOT expected to be the same",language.isSameAs(language2));
        
        language.setJudgeProgramExecuteCommandLine("security_wrapper ./{:basename}");
        
        System.out.println("exec "+language.getProgramExecuteCommandLine());
        System.out.println("J "+language.getActiveProgramExecuteCommandLine());
        
        assertNotEquals("Expecting team exec and judge exec NOT to be same ", language.getProgramExecuteCommandLine(), language.getActiveProgramExecuteCommandLine());
    }

   public void testSameAssetUsingJudgeProgramExecuteCommandLine() throws Exception {
        
        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        Language language2 = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        
        assertTrue("language expected to be equal",language.isSameAs(language2));
        language2.setUsingJudgeProgramExecuteCommandLine(true);
        assertFalse("language expected to be equal",language.isSameAs(language2));
    }
   
   public void testsetDisplayName() throws Exception {
       
       Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
       Language language2 = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
       
       assertTrue("language expected to be equal",language.isSameAs(language2));
       language2.setDisplayName("foo");
       assertFalse("language expected to be equal",language.isSameAs(language2));
       
   }
   
    public void testsetCompileCommandLine() throws Exception {

        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        Language language2 = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);

        assertTrue("language expected to be equal", language.isSameAs(language2));
        language2.setCompileCommandLine("foo");
        assertFalse("language expected to be equal", language.isSameAs(language2));

    }

}
