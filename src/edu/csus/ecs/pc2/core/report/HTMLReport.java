package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.IScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;

/**
 * Print list of profiles.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class HTMLReport implements IReport {

    /**
     * 
     */
    private static final long serialVersionUID = 808321237990590312L;

    private IInternalContest contest;

    private IInternalController controller;

    private Log log;

    private Filter filter = new Filter();

    /**
     * Name of style sheet directory
     */
    private String styleSheetDirectoryName = null;

    private IScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();
    
    private String reportDirectory = "reports";  //  @jve:decl-index=0:

    private String getDefaultSyleSheetDirectoryName() {

        String xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }
        return xslDir;
    }

    public String getStyleSheetDirectoryName() {
        if (styleSheetDirectoryName == null) {
            styleSheetDirectoryName = getDefaultSyleSheetDirectoryName();
        }
        return styleSheetDirectoryName;
    }

    private String createHTML(String xmlString, String xsltFileName) throws Exception {
        XSLTransformer xslTransformer = new XSLTransformer();
        File xslFile = new File(xsltFileName);
        String htmlString = xslTransformer.transformToString(xslFile, xmlString);
        return htmlString;
    }

    protected Properties getScoringProperties() {

        Properties properties = getContest().getContestInformation().getScoringProperties();
        if (properties == null) {
            properties = new Properties();
        }

        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String[] keys = (String[]) defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (!properties.containsKey(key)) {
                properties.put(key, defProperties.get(key));
            }
        }

        return properties;
    }

    public void writeReport(PrintWriter printWriter) throws Exception {

        String xslDir = getStyleSheetDirectoryName();

        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            throw new Exception("Unable to find XSLT source directory " + xslDir);
        }

        String outputDirectory = reportDirectory;
        if (isServer()) {
            outputDirectory = getContest().getProfile().getProfilePath() + File.separator + reportDirectory;
        }

        File file = new File(outputDirectory);
        if (!file.isDirectory()) {
            file.mkdirs();
        }

        String[] inputFiles = inputDir.list();
        for (int i = 0; i < inputFiles.length; i++) {
            String filename = inputFiles[i];
            if (filename.endsWith(".xsl")) {

                String xsltFileName = inputDir.getCanonicalPath() + File.separator + filename;

                String scoreboardXML;

                try {

                    Properties scoringProperties = getScoringProperties();
                    scoreboardXML = scoringAlgorithm.getStandings(getContest(), scoringProperties, log);
                    String html = createHTML(scoreboardXML, xsltFileName);

                    String name = filename.replaceAll(".xsl$", "");

                    String outputFilename = getReportFilename(this, name, "html");

                    String htmlFilename = outputDirectory + File.separator + outputFilename;

                    writeString(htmlFilename, html);

                    printWriter.println("Wrote file " + htmlFilename);
                    printWriter.println("  based on " + filename);
                    printWriter.println();

                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }
        }
    }

    // catFile
    // writeFile
    /**
     * Write the string to the file.
     * 
     * @param filename
     * @param html
     * @throws FileNotFoundException 
     */
    private void writeString(String filename, String html) throws FileNotFoundException {
        
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        printWriter.print(html);
        printWriter.close();
        printWriter = null;
    }

    public void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + Utilities.getL10nDateTime());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle() + " Report");

        printWriter.println();
        GregorianCalendar resumeTime = contest.getContestTime().getResumeTime();
        if (resumeTime == null) {
            printWriter.println("Contest date/time: never started");
        } else {
            printWriter.println("Contest date/time: " + resumeTime.getTime());

        }
    }
    
    /**
     * Creates a report name.
     * 
     * <pre>
     * Form: report.<report title>[.extraPart].yyMMdd HHmmss.SSS.<extension>
     * </p>
     * 
     * Will replace all spaces with underscores.
     * 
     * @param selectedReport
     * @param extraPart
     * @param extension
     * @return name of report with date/time
     */
    public String getReportFilename(IReport selectedReport, String extraPart, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        if (extraPart != null) {
            if (!extraPart.startsWith(".")) {
                extraPart = "." + extraPart;
            }
            reportName = reportName + extraPart;
        }

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + "." + extension;

    }

    public void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            try {
                printHeader(printWriter);

                writeReport(printWriter);

                printFooter(printWriter);

                printWriter.close();
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
        }
    }

    public String[] createReport(Filter inFilter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter inFilter) throws IOException {
        return Reports.notImplementedXML(this);
    }

    public String getReportTitle() {
        return "Standings Web Pages";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Standings Web Pages generator";
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public IInternalContest getContest() {
        return contest;
    }

    /**
     * Returns true if this client is a server.
     * 
     * @return true if logged in client is a server.
     */
    private boolean isServer() {
        return contest.getClientId() != null && isServer(contest.getClientId());
    }

    private boolean isServer(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SERVER);
    }
}
