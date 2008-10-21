package edu.csus.ecs.pc2.api;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class APIAbstractTest {

    private String line = "";

    private ScrollyFrame scrollyFrame = null;

    @SuppressWarnings("unused")
    private IContest contest = null;

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
    abstract String getTitle();

    protected void println() {
        String s = "";
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

    protected void print(String s) {
        line = line + s;

    }

    protected void println(String s) {
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

    public void setScrollyFrame(ScrollyFrame frame, IContest inContest) {
        this.scrollyFrame = frame;
        this.contest = inContest;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    
    

}
