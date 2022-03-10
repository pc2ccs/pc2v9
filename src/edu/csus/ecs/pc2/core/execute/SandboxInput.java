// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Sandbox Input
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class SandboxInput {

    private static final String SANDBOX_PROPERTIES_FILENAME = "sandbox.properties";

    public static final String TIMELIMIT_KEY = "timelimit";

    public static final String INPUTFILENAME_KEY = "inputfilename";

    public static final String MEMLIMIT_KEY = "memlimit";

    public static final String LANGUAGE_KEY = "language";

    public static final String PROBLEM_LETTER_KEY = "problemLetter";

    public static final String RUNID_KEY = "runid";

    private Properties inputProperties = new Properties();

    private String outputPropertiesFilename = SANDBOX_PROPERTIES_FILENAME;

    /**
     * Store input sandbox properties.
     * 
     * @param outputDirectory
     *            the output directory, use execute directory
     * @return
     * @throws IOException
     */
    public String store(File outputDirectory) throws IOException {

        String filename = SANDBOX_PROPERTIES_FILENAME;
        if (outputDirectory != null) {
            filename = outputDirectory.getAbsolutePath() + File.pathSeparator + SANDBOX_PROPERTIES_FILENAME;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(filename, false);
        inputProperties.store(fileOutputStream, "PC^2 Sandbox Input properties on " + new Date());

        outputPropertiesFilename = filename;
        return outputPropertiesFilename;
    }

    /**
     * Output file written to by {@link #store(File)}
     * 
     * @return properties filename
     */
    public String getOutputPropertiesFilename() {
        return outputPropertiesFilename;
    }
}
