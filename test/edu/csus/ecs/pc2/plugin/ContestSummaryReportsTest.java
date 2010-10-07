package edu.csus.ecs.pc2.plugin;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.report.IReport;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestSummaryReportsTest extends TestCase {

    public static void main(String[] args) {
        ContestSummaryReports report = new ContestSummaryReports();

        IReport[] reports = report.getReportList();
        
//        Arrays.sort(reports, new ReportNameByComparator());

        for (IReport report2 : reports) {
            System.out.println(report2.getReportTitle());
//                    + " via " + report2.getClass().getName());
        }
    }
}
