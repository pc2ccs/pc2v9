package edu.csus.ecs.pc2.core.model;

/**
 * Contest Replay Setting.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReplaySetting implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -4581905363142199708L;

    // # load replay data on server
    // replay:
    // - title: Replay file for December
    // - file: replay/report.Extract_Replay_Runs.11.16.062.txt
    // - auto_start: yes
    // - iterations: 200
    // - start_at: 5
    // - site: 2

    private boolean autoStart = false;

    private int iterationCount = 1;

    private int startSequenceNumber = 1;

    private String loadFileName = "";

    private ElementId elementId = null;

    private String displayName;

    public ReplaySetting(String displayName) {
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(1);
    }

    public String getTitle() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public int getStartSequenceNumber() {
        return startSequenceNumber;
    }

    public void setStartSequenceNumber(int startSequenceNumber) {
        this.startSequenceNumber = startSequenceNumber;
    }

    public String getLoadFileName() {
        return loadFileName;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }

    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
