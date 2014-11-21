package edu.csus.ecs.pc2.core.report;

/**
 * runs.tsv report.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunsTSVReport extends SubmissionsTSVReport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getPluginTitle() {
        return "runs.tsv";
    }

    @Override
    public String getReportTitle() {
        return "runs.tsv Report";
    }


}
