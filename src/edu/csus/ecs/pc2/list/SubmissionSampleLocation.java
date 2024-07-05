// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.list;

import edu.csus.ecs.pc2.clics.CLICSJudgementType;

/**
 * A single entry of a judge's solution locations.
 *
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class SubmissionSampleLocation  implements Comparable<SubmissionSampleLocation> {
    // These are the folders defined by the clics spec for the location of judge's submissions
    // (Except for compile error - that is not in the clics spec)
    public static final String CLICS_SUBMISSION_LOCATION_AC = "accepted";
    public static final String CLICS_SUBMISSION_LOCATION_WA = "wrong_answer";
    public static final String CLICS_SUBMISSION_LOCATION_RTE = "run_time_error";
    public static final String CLICS_SUBMISSION_LOCATION_TLE = "time_limit_exceeded";
    public static final String CLICS_SUBMISSION_LOCATION_CE = "compile_error";

    public static final String [][] CLICS_SUBMISSION_TO_ACRONYM = {
        { CLICS_SUBMISSION_LOCATION_AC, CLICSJudgementType.CLICS_BIG5.AC.toString() },
        { CLICS_SUBMISSION_LOCATION_WA, CLICSJudgementType.CLICS_BIG5.WA.toString() },
        { CLICS_SUBMISSION_LOCATION_RTE, CLICSJudgementType.CLICS_BIG5.RTE.toString() },
        { CLICS_SUBMISSION_LOCATION_TLE, CLICSJudgementType.CLICS_BIG5.TLE.toString() },
        { CLICS_SUBMISSION_LOCATION_CE, CLICSJudgementType.CLICS_BIG5.CE.toString() }
    };

    private String title;
    private String shortDirectoryName;
    private String clicsAcronym;

    /**
     * A judges solution name and location.
     *
     * @param title title for directory, ex. Run Time Error
     * @param shortDirectoryName base directory name, ex. run_time_error
     * @param clicsAcronym Big-5 acronym AC/WA/RTE/TLE/CE
     */
    public SubmissionSampleLocation(String title, String shortDirectoryName, String clicsAcronym) {
        this.title = title;
        this.shortDirectoryName = shortDirectoryName;
        if(clicsAcronym == null || clicsAcronym.isEmpty()) {
            this.clicsAcronym = "NA";
        } else {
            this.clicsAcronym = clicsAcronym;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getShortDirectoryName() {
        return shortDirectoryName;
    }

    public String getCLICSAcronym() {
        return clicsAcronym;
    }

    @Override
    public String toString() {
        String strVal;

        if (title.length() > 0) {
            strVal = title + " ("+shortDirectoryName+")";
        } else {
            strVal = shortDirectoryName;
        }
        if(!clicsAcronym.isEmpty()) {
            strVal = strVal + " : " + clicsAcronym;
        }
        return strVal;
    }

    @Override
    public int compareTo(SubmissionSampleLocation o) {
        return o.toString().compareTo(toString());
    }

}
