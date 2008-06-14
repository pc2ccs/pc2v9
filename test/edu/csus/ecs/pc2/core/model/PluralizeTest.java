package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * Test for Pluralize
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PluralizeTest extends TestCase {

    public void testMain() {

        String[][] testCases = { 
                { "bird", "birds" }, 
                { "fry", "fries" }, 
                { "program", "programs" }, 
                { "hour", "hours" },
                { "minute", "minutes" },
                { "second", "seconds" },
                { "min", "mins" },
                { "sec", "secs" },
                { "boy", "boys" },
                { "ploy", "ploys" },
                {"brother-in-law","brothers-in-law"},
                {"child","children"},
                {"father-in-law","fathers-in law"},
                {"foot","feet"},
                {"louse","lice"},
                {"mailman","mailmen"},
                {"man","men"},
                {"mother-in-law","mothers-in-law"},
                {"mouse","mice"},
                {"ox","oxen"},
                {"secretary-general","secretaries general"},
                {"sister-in-law","sisters-in-law"},
                {"tooth","teeth"},
                {"woman","women"},
                {"calf","calves"},
                {"elf","elves"},
                {"half","halves"},
                {"knife","knives"},
                {"life","lives"},
                {"loaf","loaves"},
                {"shelf","shelves"},
                {"thief","thieves"},
                {"wife","wives"},
                
        };

        for (int i = 0; i < testCases.length; i++) {
            String before = testCases[i][0];
            String after = testCases[i][1];

            String singluar = Pluralize.simplePluralize(before, 1);
            String plural = Pluralize.pluralize(before, 2);

            assertTrue("Singular failed expected " + before + " got " + singluar, singluar.equals(before));
            assertTrue("Plural failed expected " + after + " got " + plural, plural.equals(after));
        }
    }
    
}
