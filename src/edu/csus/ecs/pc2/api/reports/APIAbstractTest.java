package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.implementation.ScrollyFrame;

/**
 * Class that supports test (print reports and other) classes.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class APIAbstractTest {

    /**
     * Run number
     */
    private int number = 0;

    private int siteNumber = 0;

    private String line = "";

    private ScrollyFrame scrollyFrame = null;

    private IContest contest = null;

    private ServerConnection serverConnection = null;

    public APIAbstractTest() {
    }

    /**
     * Print Test Output
     * 
     * @param contest
     * @param scrollyFrame
     */
    public abstract void printTest();

    /**
     * Get title for this test.
     * 
     * @return
     */
    public abstract String getTitle();

    public void println() {
        String s = "";
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

    public void print(String s) {
        line = line + s;

    }

    public void println(String s) {
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

    public void setAPISettings(ScrollyFrame frame, IContest inContest, ServerConnection inServerConnection) {
        this.scrollyFrame = frame;
        this.contest = inContest;
        this.serverConnection = inServerConnection;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    public IContest getContest() {
        return contest;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }
}
