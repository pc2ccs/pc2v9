package edu.csus.ecs.pc2.core.model;

import java.util.Hashtable;

/**
 * Balloon and Balloon Distribution Settings (per Site).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettings implements IElementObject {

    // TODO Add Balloon Color list

    private Hashtable<ElementId, String> colorList = new Hashtable<ElementId, String>();

    private static final long serialVersionUID = 4208771943370594478L;

    private ElementId elementId;

    /**
     * Who to email balloons to.
     */
    private String emailContact;

    /**
     * Where to write balloons to (ex: lpt1: or a fifo)
     */
    private String printDevice;

    /**
     * Whether the printDevice is capable.
     */
    private boolean postscriptCapable = false;

    /**
     * Our mail server (be mindful of relay filters)
     */
    private String mailServer;

    /**
     * Send a balloon for each no?
     */
    private boolean includeNos = false;

    /**
     * Send an email version of the balloons?
     */
    private boolean emailBalloons = false;

    /**
     * Write the balloons to a printDevice?
     */
    private boolean printBalloons = false;

    /**
     * How many lines per page before a page break, defaults to 66.
     */
    private int linesPerPage = 66;

    public BalloonSettings(String displayName, int siteNumber) {
        super();
        elementId = new ElementId(displayName);
        setSiteNumber(siteNumber);
    }

    /**
     * @return Returns the elementId.
     */
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

    /**
     * @return Returns the emailBalloons.
     */
    public boolean isEmailBalloons() {
        return emailBalloons;
    }

    /**
     * @param emailBalloons
     *            The emailBalloons to set.
     */
    public void setEmailBalloons(boolean emailBalloons) {
        this.emailBalloons = emailBalloons;
    }

    /**
     * @return Returns the emailContact.
     */
    public String getEmailContact() {
        return emailContact;
    }

    /**
     * @param emailContact
     *            The emailContact to set.
     */
    public void setEmailContact(String emailContact) {
        this.emailContact = emailContact;
    }

    /**
     * @return Returns the includeNos.
     */
    public boolean isIncludeNos() {
        return includeNos;
    }

    /**
     * @param includeNos
     *            The includeNos to set.
     */
    public void setIncludeNos(boolean includeNos) {
        this.includeNos = includeNos;
    }

    /**
     * @return Returns the mailServer.
     */
    public String getMailServer() {
        return mailServer;
    }

    /**
     * @param mailServer
     *            The mailServer to set.
     */
    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    /**
     * @return Returns the postscriptCapable.
     */
    public boolean isPostscriptCapable() {
        return postscriptCapable;
    }

    /**
     * @param postscriptCapable
     *            The postscriptCapable to set.
     */
    public void setPostscriptCapable(boolean postscriptCapable) {
        this.postscriptCapable = postscriptCapable;
    }

    /**
     * @return Returns the printBalloons.
     */
    public boolean isPrintBalloons() {
        return printBalloons;
    }

    /**
     * @param printBalloons
     *            The printBalloons to set.
     */
    public void setPrintBalloons(boolean printBalloons) {
        this.printBalloons = printBalloons;
    }

    /**
     * @return Returns the printDevice.
     */
    public String getPrintDevice() {
        return printDevice;
    }

    /**
     * @param printDevice
     *            The printDevice to set.
     */
    public void setPrintDevice(String printDevice) {
        this.printDevice = printDevice;
    }

    /**
     * @return Returns the linesPerPage.
     */
    public int getLinesPerPage() {
        return linesPerPage;
    }

    /**
     * @param linesPerPage
     *            The linesPerPage to set.
     */
    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    public String getColor(Problem problem) {
        return getColor(problem.getElementId());
    }

    /**
     * Clear existing list and add colors for problems.
     * 
     * @param problemList
     * @param colors
     */
    public void addColorList(Problem[] problemList, String[] colors) {
        clearList();
        for (int i = 0; i < problemList.length; i++) {
            addColor(problemList[i].getElementId(), colors[i]);
        }
    }

    private void clearList() {
        colorList = new Hashtable<ElementId, String>();
    }

    public void addColor(Problem problem, String colorName) {
        addColor(problem.getElementId(), colorName);
    }

    private void addColor(ElementId id, String colorName) {
        colorList.put(id, colorName);
    }

    public String getColor(ElementId id) {
        return colorList.get(id);
    }
}
