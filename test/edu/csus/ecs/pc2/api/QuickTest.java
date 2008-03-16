package edu.csus.ecs.pc2.api;

import java.util.Date;

/**
 * API Test - print rows.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class QuickTest {

    /**
     * Show runs
     * @param login
     * @param password
     */
    private void showRuns(String login, String password){
        if (password == null){
            password = login;
        }
        
        ServerConnection serverConnection = new ServerConnection();
        try {
            IContest contest = serverConnection.login(login, password);

            IRun[] runs = contest.getRuns();

            if (runs.length == 0) {
                info("No runs to view ");
            } else {
                info(runs.length+" runs.");
                for (IRun run : runs) {
                    info(" Fetching files for run " + run.getNumber()+" deleted="+run.isDeleted());
                    String[] names = run.getSourceCodeFileNames();
                    info(" fetched run, submitted names");
                    byte[][] contents = run.getSourceCodeFileContents();
                    for (int i = 0; i < names.length; i++) {
                        String name = names[i];
                        info(name + " " + contents[i].length);
                    }
                }
            }

            info("logoff");
            serverConnection.logoff();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(4);
    }
    
    /**
     * 
     * @param s
     */
    public void info(String s) {
        System.err.println(new Date() + " " +Thread.currentThread().getName() + " " + s);
        System.err.flush();
    }

    /**
     * Login as judge2 and print runs.
     * @param args
     */
    public static void main(String[] args) {
        new QuickTest().showRuns("judge2", null);
    }

}
