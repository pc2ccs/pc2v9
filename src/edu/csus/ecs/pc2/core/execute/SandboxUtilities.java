// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * 
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
     * @param problem
     * @param inputFile
     * @param memlimitK
     * @param timelimitMS
     * @return filename were properties written
     * @throws IOException
     */
    public static String writeSandboxInputProperties(String executeDirectory, Run run, Problem problem, Language language, File inputFile, int memlimitK, int timelimitMS) throws IOException {

        Properties properties = new Properties();

        properties.put(SandboxInput.RUNID_KEY, Integer.toString(run.getNumber()));

        properties.put(SandboxInput.MEMLIMIT_KEY, inputFile);
        properties.put(SandboxInput.TIMELIMIT_KEY, timelimitMS);

        properties.put(SandboxInput.LANGUAGE_KEY, language.getDisplayName());
        properties.put(SandboxInput.PROBLEM_LETTER_KEY, problem.getLetter());

        properties.put(SandboxInput.INPUTFILENAME_KEY, inputFile);

        String outfilename = new SandboxInput().store(new File(executeDirectory));

        return outfilename;
    }
}
