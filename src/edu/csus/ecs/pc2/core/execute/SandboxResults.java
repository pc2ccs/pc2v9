// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Sandbox Results loader.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class SandboxResults {

    /**
     * Sandbox results filename.
     */
    public static final String SANDBOX_RESULTS_FILENAME = "sandbox.results.properties";

    /**
     * Comment or more info from the sandbox
     */
    public static final String COMMENT_KEY = "comment";

    public static final String JUDGEMENT_ACRONYM_KEY = "judgementAcronym";

    public static final String RUNID_KEY = "runid";

    public static final String RUNTIME_KEY = "runtime";

    private Properties resultsProperties = new Properties();

    public SandboxResults() {
    }

    public SandboxResults(File propertiesFilename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(propertiesFilename);
        resultsProperties.load(fileInputStream);
    }

    /**
     * Return value for property from sandbox file.
     * 
     * @param key
     * @return null or key value.
     */
    public String getProperty(String key) {
        return resultsProperties.getProperty(key);
    }

    private void setComment(String comment) {
        resultsProperties.put(COMMENT_KEY, comment);
    }

    public String getComment() {
        String comment = "";
        if (resultsProperties.get(COMMENT_KEY) != null) {
            comment = (String) resultsProperties.get(COMMENT_KEY);
        }
        return comment;
    }

    public int getExitCode() {
        Object obj = resultsProperties.get("exitcode");
        if (obj == null) {
            return 0;
        } else {
            try {
                return Integer.parseInt(obj.toString());
            } catch (Exception e) {
                setComment("Invalid exit code " + e.getMessage());
                return 0;
            }
        }
    }
    
    public Properties getProperties() {
        return resultsProperties;
    }

}
