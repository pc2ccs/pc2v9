// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class LanguageUtilitiesTest extends AbstractTestCase {

    public void testname() throws Exception {

        IInternalContest contest = loadContestFromSampleContsts(null, "mini");
        assertNotNull(contest);

        String[] data = {
                "java;Java", //
                "cpp;GNU C++", //
                "c++;GNU C++", //
        };

        for (String string : data) {

            String[] fields = string.split(";");
            String input = fields[0];
            String expected = fields[1];

            Language lang = LanguageUtilities.matchFirstLanguage(contest, input);
            String actual = lang.getDisplayName();
            assertEquals("Expecting language name", expected, actual);
        }
    }

}
