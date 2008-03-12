package edu.csus.ecs.pc2.api;

import java.util.Date;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class QuickTest {

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
                info(" Fetching files for run "+runs[0].getNumber());
                String[] names = runs[0].getSourceCodeFileNames();
                info( " fetched run, submitted names");
                
                for (String name : names){
                    info(name);
                }
            }

            info("logoff");
            serverConnection.logoff();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(4);
    }
    public void info(String s) {
        System.err.println(new Date() + " " +Thread.currentThread().getName() + " " + s);
        System.err.flush();
    }

    public static void main(String[] args) {
        
        new QuickTest().showRuns("judge2", null);
        
    }

}
