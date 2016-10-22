package edu.csus.ecs.pc2.core.report;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.list.ReportNameByComparator;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ReportTest.java 276 2013-01-20 23:55:48Z laned $
 */
public class ReportsTest extends AbstractTestCase {

    
    /**
     * Tests whether all reports have implemented XML output.
     * 
     * @throws Exception
     */
    public void testXMLReportsExists() throws Exception {

        ensureOutputDirectory();

        IReport[] reports = Reports.getReports();

        Filter filter = new Filter();

        Arrays.sort(reports, new ReportNameByComparator());

        IInternalContest contest = new InternalContest();
        contest.setClientId(new ClientId(1, Type.UNKNOWN, 0));

        contest = createMockContest();

        IStorage storage = new FileStorage(getDataDirectory());
        contest.setStorage(storage);
        IInternalController controller = new InternalController(contest);

        testReports(reports, contest, controller, filter);

        ContestReport report = new ContestReport();
        report.setContestAndController(contest, controller);
        
        testReport(report, contest, controller, filter);

    }
    
    public void testSingleReport() throws Exception {

        ensureOutputDirectory();

        IReport[] reports = Reports.getReports();

        Filter filter = new Filter();

        Arrays.sort(reports, new ReportNameByComparator());

        IInternalContest contest = new InternalContest();
        contest.setClientId(new ClientId(1, Type.UNKNOWN, 0));

        contest = createMockContest();

        IStorage storage = new FileStorage(getDataDirectory());
        contest.setStorage(storage);
        IInternalController controller = new InternalController(contest);

        IReport report = new ContestReport();
        report.setContestAndController(contest, controller);
        
        testReport(report, contest, controller, filter);
    }

    /**
     * Test a single report.
     * 
     * @param report
     * @param contest
     * @param controller
     * @param filter
     */
    private void testReport(IReport report, IInternalContest contest, IInternalController controller, Filter filter) {
        IReport[] reports = { report };
        testReports(reports, contest, controller, filter);
    }

    /**
     * Test a list of reports.
     * 
     * @param reports
     * @param contest
     * @param controller
     * @param filter
     */
    private void testReports(IReport[] reports, IInternalContest contest, IInternalController controller, Filter filter) {

        int failures = 0;
        int numberOfReports = reports.length;
        int notImplementedCount = 0;

        for (IReport iReport : reports) {

            try {

                iReport.setContestAndController(contest, controller);
                String xmlString = iReport.createReportXML(filter);
                
                if (xmlString == null || xmlString.length() == 0) {
                    System.err.println("Failure " + iReport.getReportTitle() + " no output created ");
                    failures++;
                } else {
                    Exception ex2 = isValidXML(iReport, filter);
                    if (ex2 != null) {
                        throw ex2;
                    }
                }

            } catch (Exception e) {

                boolean notImplemented = e.getMessage().indexOf("Not implement") > -1;
                if (notImplemented) {
                    notImplementedCount++;
                }
                failures++;
                System.err.println("Failure " + failures+ ": "+ iReport.getReportTitle() + " " + e.getMessage());
            }
        }

        if (notImplementedCount > 0) {
            System.err.println(notImplementedCount + " reports not implemented ");
        }
        if (failures > 0) {
            System.err.println(numberOfReports - failures + " reports passed (of " + numberOfReports + ")");
        }
        if (failures == numberOfReports) {
            System.err.println("ALL " + numberOfReports + " reports failed");
            fail("ALL " + numberOfReports + " reports failed");
        }

        if (failures > 0) {
            fail("There were " + failures + " report creation failures");
        }

//        System.err.println("Saving files under " + getDataDirectory());

    }

    private IInternalContest createMockContest() {
        return new SampleContest().createContest(1, 5, 12, 12, true);
    }
    
    /**
     * 
     * @author ICPC
     *
     */
    protected class ReportTitleComparator implements Comparator<IReport>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -987432712228255571L;

        public int compare(IReport report1, IReport report2) {

            return report1.getReportTitle().compareTo(report2.getReportTitle());
        }
    }

    protected void printReportTitles() {

        IReport[] listOfReports = Reports.getReports();

        Arrays.sort(listOfReports, new ReportTitleComparator());

        for (IReport report : listOfReports) {
            System.out.println(report.getReportTitle());
        }

    }

    public String getFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    /**
     * Tests that titles have not changed in order or name
     */
    public void testReportTitles(){

        String [] reportTitles = {
                "Accounts Report", // 
                "Balloon Summary Report", // 
                "All Reports Report", // 
                "Contest Settings Report", // 
                "Contest XML Report", // 
                "Contest Analysis Report", // 
                "Solutions By Problem Report", // 
                "Submissions by Language Report", // 
                "Fastest Solutions Summary Report", // 
                "Fastest Solutions Per Problem Report", // 
                "Standings XML Report", // 
                "Logins Report", // 
                "Profiles Report", // 
                "Plugins Report", // 
                "Runs Report", // 
                "Clarifications Report", // 
                "Problems Report", // 
                "Languages Report", // 
                "Judgements Report", // 
                "Runs grouped by team Report", // 
                "Notification Settings Report", // 
                "Client Settings Report", // 
                "Groups Report", // 
                "Evaluations Report", // 
                "Runs Report (Version 8)", // 
                "Run 5 field Report", // 
                "Permissions Report", // 
                "Balloon Delivery Report", // 
                "Extract Replay Runs", // 
                "Run Notifications Sent Report", // 
                "Judgement Notifications Report", // 
                "Profile Clone Settings Report", // 
                "Sites Report", // 
                "Unused 2011 Event Feed XML Report", // 
                "Notifications XML Report", // 
                "Finalize-Certify Report", // 
                "Internal Dump Report", // 
                "Passwords Report", // 
                "accounts.tsv Report (team and judges)", // 
                "accounts.tsv Report (all accounts)", // 
                "runs.tsv", // 
                "JSON Standings", // 
                "Unused 2013 Event Feed XML Report", // 
                "userdata.tsv", // 
                "groups.tsv", // 
                "teams.tsv", // 
                "scoreboard.tsv", // 
                "submissions.tsv", //
                "ICPC Tools Event Feed Report", //
                "Auto Judging Settings Report", //
                "Judging Analysis Report", //
                "JSON 2016 Scoreboard", //
                "Contest Data Package Report/Export", //
        };

        IReport [] reportList = Reports.getReports();

        int idx = 0;
        for (IReport iReport : reportList) {
            String name = iReport.getPluginTitle();
            
            if (idx < reportTitles.length){
                assertEquals("Expected name for report "+(idx+1)+" "+iReport.getClass().getName(), reportTitles[idx], name);
            } else {
                System.out.println("Missing \""+name+"\", //");
                fail("Missing report named: "+name);
            }
            idx ++;
        }
    }
    
    /**
     * Ensure that all create XML create XML output.
     * 
     * @throws Exception
     */
    public void testCreateXMLEmptyContest() throws Exception {
        xmlContest(new InternalContest());
    }
    
    public void testCreateXMLStandardContest() throws Exception {
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        xmlContest(contest );
    }
    
    public void testCreateXMLSampleContest() throws Exception {
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(4, 4, 102, 22, true);
        xmlContest(contest );
    }

    
    public void xmlContest(IInternalContest contest) throws Exception {
        
        Exception firstException = null;
                
        IReport [] reportList = Reports.getReports();
        

        for (IReport report : reportList) {
            String className = getShortClassName(report.getClass());

            try {
                String xml = report.createReportXML(null);
                if (xml == null){
                    
                    System.out.println("     null returned for createReportXML in  "+className);
                }
//                assertNotNull("null returned for createReportXML in  "+className, xml);
                
            } catch (Exception ex) {
                if (ex != null && firstException == null){
                    firstException = ex;
                }
                
                if (ex != null) {
                    System.out.println("Exception in " + className + " (" + report.getReportTitle() + ") " + ex.getMessage());
                }
            }
             
        }
        
        if (firstException != null){
            throw firstException;
        }
        
    }

    private String getShortClassName(Class<? extends IReport> class1) {
        return class1.getName().replaceAll(".*[.]", "");
    }

}
