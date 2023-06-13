// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class LanguageUtilitiesTest extends AbstractTestCase {

    /**
     * Test for C++, Kotlin and C language display names.
     * @throws Exception
     */
    public void testmatchFirstLanguage() throws Exception {

        ensureStaticLog();
        IInternalContest contest = loadContestFromSampleContest(null, "mini");
        assertNotNull(contest);

        String[] data = {
                "java;Java", //
                "cpp;GNU C++", //
                "c++;GNU C++", //
                "kt;Kotlin", //
                "c;GNU C", //
        };

        // Add Kotlin/kt as a language
        Language kotlin = new Language("Kotlin");
        contest.addLanguage(kotlin);
        
        for (String string : data) {

            String[] fields = string.split(";");
            String input = fields[0];
            String expected = fields[1];

            Language lang = LanguageUtilities.matchFirstLanguage(contest, input);
            String actual = lang.getDisplayName();
            assertEquals("Expecting language name", expected, actual);
        }
    }
    
    
    /**
     * Test whether in contest C++ extension cc will find C++ languages.
     * 
     * @throws Exception
     */
    public void testmatchFirstLanguageForCC() throws Exception {

        ensureStaticLog();
        IInternalContest contest = loadContestFromSampleContest(null, "qanat");
        assertNotNull(contest);

        String cdpDir = getTestSampleContestDirectory("qanat");

        List<String> allCCSamples = FileUtilities.getAllFileEntries(cdpDir, ".cc");
        assertEquals("Expected .cc files under " + cdpDir, 8, allCCSamples.size());

        /**
         * Expected language display name.
         */
        String expected = "GNU C++";

        for (String input : allCCSamples) {

            String extension = LanguageUtilities.getExtension(input);
            Language lang = LanguageUtilities.matchFirstLanguage(contest, extension);
            assertNotNull("Expecting to match contest model Language", lang);
            String actual = lang.getDisplayName();
            assertEquals("Expecting language name", expected, actual);
        }
    }
}
