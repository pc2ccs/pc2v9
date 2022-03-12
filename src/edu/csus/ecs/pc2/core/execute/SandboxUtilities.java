// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * TODO add javadoc.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */

public class SandboxUtilities {

    /**
     * Write input sandbox properties
     * 
     * @param executeDirectory
     * @param run
     * @param sourceFileName - name of team's source file name, ex. Sumit.java
     * @param problem
     * @param language
     * @param judgesDataFile - name of judge's data file
     * @param memlimitM
     * @param timelimitMS
     * @return filename where properties written.
     * @throws IOException
     */
    public static String writeSandboxInputProperties(String executeDirectory, Run run, String sourceFileName, Problem problem, Language language, File judgesDataFile, int memlimitM, int timelimitMS) throws IOException {

        Properties properties = new Properties();

        properties.put(SandboxInput.RUNID_KEY, Integer.toString(run.getNumber()));

        if (memlimitM > 0) {
            properties.put(SandboxInput.MEMLIMIT_KEY, Integer.toString(memlimitM));
        }
        if (timelimitMS > 0) {
            properties.put(SandboxInput.TIMELIMIT_KEY, Integer.toString(timelimitMS));
        }

        properties.put(SandboxInput.LANGUAGE_KEY, language.getDisplayName());
        properties.put(SandboxInput.PROBLEM_LETTER_KEY, problem.getLetter());

        properties.put(SandboxInput.DATAFILENAME_KEY, judgesDataFile.getAbsoluteFile().toString());
        properties.put(SandboxInput.SOURCEFILENAME, sourceFileName);

        String outfilename = new SandboxInput().store(new File(executeDirectory), properties);

        return outfilename;
    }
}
