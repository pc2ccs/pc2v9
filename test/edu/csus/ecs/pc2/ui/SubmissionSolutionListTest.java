// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.util.List;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class SubmissionSolutionListTest extends AbstractTestCase {

    public void testgetAllDirectoryEntries() {

        String cdpDir = "samps/contests/tenprobs";
        cdpDir = "c:\\test\\current\\samps\\contests\\tenprobs";

        if (new File(cdpDir).isDirectory()) {

            List<String> entries = SubmissionSolutionList.getAllDirectoryEntries(cdpDir);
            assertNotNull(entries);
            assertEquals("Expecting entries for " + cdpDir, 71, entries.size());

            SubmissionSolutionList list = new SubmissionSolutionList(new File(cdpDir));

            assertNotNull(list);
            assertEquals("Expecting list items for " + cdpDir, 2, list.size());
        }
    }

}
