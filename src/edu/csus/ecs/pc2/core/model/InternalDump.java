package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.MultipleFileViewer;

/**
 * Internal Dump of  information.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class InternalDump {

    private String editorNameFullPath =   "/windows/vi.bat";

    private IContest contest;

    public InternalDump(IContest contest){
        this.contest = contest;
    }

    /**
     * view the file.
     * 
     * If editor not found will put up a multiplefileviewer.
     * 
     * @param dumpFileName
     */
    protected void viewFile(String dumpFileName) {
        String editorName = editorNameFullPath;
        File f = new File(editorName);
        if (!f.exists()) {
            Log log = new Log("viewFile");
            MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
            multipleFileViewer.addFilePane("Internal Dump", dumpFileName);
            FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
            multipleFileViewer.setVisible(true);
        } else {

            String command = editorName + " " + dumpFileName;
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to run command " + command + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate report file and view file in editor.
     */
    public void viewContestData() {
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String filename = "dump" + simpleDateFormat.format(new Date()) + ".log";
        
        if ( generateReportFile (filename) != null) {
            // show file to user
            
            viewFile (filename);
            
        }
    }

    /**
     * Generate report file.
     * 
     * @param filename
     *            output filename
     * @return the filename if successful output, null if not.
     */
    public String generateReportFile(String filename) {

        try {
            PrintWriter log = new PrintWriter(new FileOutputStream(filename, false), true);
            log.println(new VersionInfo().getSystemName());
            log.println("Date: " + new Date());
            log.println(new VersionInfo().getSystemVersionInfo());

            Vector<Account> allAccounts = new Vector<Account>();

            log.println();
            log.println("-- Accounts --");
            for (ClientType.Type ctype : ClientType.Type.values()) {
                if (contest.getAccounts(ctype).size() > 0) {
                    log.println("Accounts " + ctype.toString() + " there are " + contest.getAccounts(ctype).size());
                    Vector<Account> accounts = contest.getAccounts(ctype);
                    allAccounts.addAll(accounts);
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.elementAt(i);
                        log.println("   " + account + " Site " + account.getClientId().getSiteNumber() + " id=" + account.getElementId());
                    }
                }
            }

            Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
            Arrays.sort(accountList, new AccountComparator());
            
            log.println();
            log.println("-- "+accountList.length+" Accounts --");
            for (int i = 0; i < accountList.length; i++) {
                Account account = accountList[i];
                log.println("   " + account + " Site " + account.getClientId().getSiteNumber() + " id=" + account.getElementId());
            }


            // Sites
            log.println();
            log.println("-- " + contest.getSites().length + " sites --");
            Site[] sites = contest.getSites();
            Arrays.sort(sites, new SiteComparatorBySiteNumber());
            for (Site site1 : sites) {
                String hostName = site1.getConnectionInfo().getProperty(Site.IP_KEY);
                String portStr = site1.getConnectionInfo().getProperty(Site.PORT_KEY);

                log.println("Site " + site1.getSiteNumber() + " " + hostName + ":" + portStr + " " + site1.getDisplayName() + "/" + site1.getPassword() + " id=" + site1.getElementId());
            }

            // Problem
            log.println();
            log.println("-- " + contest.getProblems().length + " problems --");
            for (Problem problem : contest.getProblems()) {
                log.println("  Problem " + problem + " id=" + problem.getElementId());
            }

            // Language
            log.println();
            log.println("-- " + contest.getLanguages().length + " languages --");
            for (Language language : contest.getLanguages()) {
                log.println("  Language " + language + " id=" + language.getElementId());
            }

            // Runs
            log.println();
            Run[] runs = contest.getRuns();
            Arrays.sort(runs, new RunComparator());
            log.println("-- " + runs.length + " runs --");
            for (Run run : runs) {
                log.println("  Run " + run);
            }

            // Clarifications
            log.println();
            Clarification[] clarifications = contest.getClarifications();
            Arrays.sort(clarifications, new ClarificationComparator());
            log.println("-- " + clarifications.length + " clarifications --");
            for (Clarification clarification : clarifications) {
                log.println("  " + clarification);
            }

            // Contest Times
            log.println();
            ContestTime[] contestTimes = contest.getContestTimes();
            Arrays.sort(contestTimes, new ContestTimeComparator());
            log.println("-- " + contestTimes.length + " Contest Times --");
            for (ContestTime contestTime : contestTimes) {

                if (contest.getSiteNumber() == contestTime.getSiteNumber()) {
                    log.print("  * ");
                } else {
                    log.print("    ");
                }
                String state = "STOPPED";
                if (contestTime.isContestRunning()) {
                    state = "STARTED";
                }

                log.println("  Site " + contestTime.getSiteNumber() + " " + state + " " + contestTime.getElapsedTimeStr() + " " + contestTime.getRemainingTimeStr() + " "
                        + contestTime.getContestLengthStr());
            }

            // Logins
            log.println();
            log.println("-- Logins -- ");
            for (ClientType.Type ctype : ClientType.Type.values()) {

                ClientId[] clientIds = contest.getAllLoggedInClients(ctype);
                if (clientIds.length > 0) {

                    log.println("Logged in " + ctype.toString());

                    for (ClientId clientId : clientIds) {
                        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                        log.println("   " + clientId + " on " + connectionHandlerID);
                    }
                }
            }

            // Connections
            log.println();
            ConnectionHandlerID[] connectionHandlerIDs = contest.getConnectionHandleIDs();
            // Arrays.sort(connectionHandlerIDs, new ConnectionHanlderIDComparator());
            log.println("-- " + connectionHandlerIDs.length + " Connections --");
            for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
                log.println("  " + connectionHandlerID);
            }

            log.println();
            log.println("*end*");

            log.close();
            log = null;

            return filename;

        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
            e.printStackTrace();
        }

        return null;

    }

    public String getEditorNameFullPath() {
        return editorNameFullPath;
    }

    /**
     * Set the name of the editor.
     * 
     * This must be a fully qualified path to the editor. 
     * 
     * @param editorNameFullPath
     */
    public void setEditorNameFullPath(String editorNameFullPath) {
        this.editorNameFullPath = editorNameFullPath;
    }

}
