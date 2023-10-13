// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ProblemsReportTest extends AbstractTestCase {

    /**
     * Test for the existence of a parent dir in a ProblemsReport.
     * 
     * @throws Exception
     */

    public void testProblemParentDir() throws Exception {

        ensureOutputDirectory(this.getName());
        ensureStaticLog();

        IInternalContest contest = loadContestFromSampleContest(null, "mini");
        contest.setClientId(new ClientId(1, Type.UNKNOWN, 0));

        IInternalController controller = new InternalController(contest);

        ProblemsReport report = new ProblemsReport();
        report.setContestAndController(contest, controller);

        String outFile = getOutputDataDirectory(this.getName()) + File.separator + "problemsReport.txt";

        report.createReportFile(outFile, new Filter());

        assertFileExists(outFile, "Problem output file ");
        //        editFile(outFile);

        String[] lines = Utilities.loadFile(outFile);

        for (String line : lines) {

            if (line.contains("judge data file ") || line.contains("judge ans. file ")) {
                String parentDir = findParentDir(line);
                assertNotNull("Expecting parent directory on line " + line, parentDir);
            }
        }
    }

    /**
     * Finds parent directory on line
     * @param line
     * @return null if parent directory not found, else the directory name
     */
    private String findParentDir(String line) {

        // judge data file 'sumit-samp-0.in' 0 bytes, External C:\repos\lane55fork\samps\contests\mini\config\sumit\data\sample SHA1 = -179-26-110-901122-99-64-52-55-6346-987397-62-6532
        // judge ans. file 'sumit-samp-1.ans' 0 bytes, External C:\repos\lane55fork\samps\contests\mini\config\sumit\data\sample SHA1 = 36115102-59842-4768-12185-65119-597312-125-108-74-2525

        // The parent directory is between Internal/EXternal and SHA1 strings

        /**
         * Start index for InternalExternal
         */
        int startIndex = line.indexOf("External");
        if (startIndex == -1) {
            startIndex = line.indexOf("Internal");
        }

        if (startIndex > 0) {
            startIndex += 8; // skip past Internal or External
            String parentDir = line.substring(startIndex).trim();
            /*
             * Start undex fr SH1
             */
            int startIndexTwo = parentDir.indexOf("SHA1");
            if (startIndexTwo > 0 && parentDir.trim().length() > 0) {
                if (!parentDir.trim().contentEquals("null")) {
                    // parent dir must not be the string 'null'
                    // parent directory is in front of SHA1
                    return parentDir.substring(0, startIndexTwo).trim();
                }
            }
        }

        return null;

    }
}
