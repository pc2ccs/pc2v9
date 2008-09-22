package edu.csus.ecs.pc2.api;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;

/**
 * A test of the Server Connection class, prompts for use and password to login.
 * 
 * main method will run a program to test ServerConnection, see usage (--help option).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnectionTest {

    public void testLogin(String login, String password) {

        ServerConnection serverConnection = new ServerConnection();
        try {
            IContest contest = serverConnection.login(login, password);
            System.out.println("PASSED Test - Logged in as " + contest.getMyClient().getLoginName());
        } catch (LoginFailureException e) {
            System.out.println("Could not login because " + e.getMessage());
        }
        try {
            serverConnection.logoff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 0) {

            // Get login and password from user

            String login = JOptionPane.showInputDialog("Enter login name", "team4");
            if (login == null || login.trim().length() < 1) {
                System.out.println("No login specified, exiting");
                System.exit(4);
            }

            String password = JOptionPane.showInputDialog("Enter password", login);

            new ServerConnectionTest().testLogin(login, password);

        } else {
            if (args[0].equalsIgnoreCase("--help")) {
                System.out.println("ServerConnectionTest [--help] [login]");
                System.out.println();
                System.out.println("If no parameters passed will prompt for login and password");
                System.out.println();
                System.out.println("login - login and password for test login ");
                System.out.println();
                System.out.println("When passes test prints: PASSED Test ");
            } else {
                new ServerConnectionTest().testLogin(args[0], args[0]);
            }
        }

    }

}
