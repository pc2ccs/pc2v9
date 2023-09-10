// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;

import edu.csus.ecs.pc2.core.imports.clics.FileComparison;

/**
 * Utilities for file comparison
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilities {

    public static FileComparison createJSONFileComparison(String tsvFileName, String sourceDir, String targetDir) {

        String firstFilename = sourceDir + File.separator + tsvFileName;
        String secondFilename = targetDir + File.separator + tsvFileName;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        // TODO 760 code comparison

        return fileComparison;
    }

    public static FileComparison createTSVFileComparison(String jsonFilename, String sourceDir, String targetDir) {

        String firstFilename = sourceDir + File.separator + jsonFilename;
        String secondFilename = targetDir + File.separator + jsonFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        // TODO 760 Code comparison

        return fileComparison;
    }

}
