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

    private Hashtable<ElementId, String> colorList = new Hashtable<ElementId, String>();

    private static final long serialVersionUID = 4208771943370594478L;

    private ElementId elementId;

    /**
     * Who to email balloons to.
     */
    private String emailContact = "";

    /**
     * Where to write balloons to (ex: lpt1: or a fifo)
     */
    private String printDevice = "";

    /**
     * Whether the printDevice is capable.
     */
    private boolean postscriptCapable = false;

    /**
     * Our mail server (be mindful of relay filters)
     */
    private String mailServer = "";

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

    private boolean balloonsEnabled = false;
    
    /**
     * How many lines per page before a page break, defaults to 66.
     */
    private int linesPerPage = 66;

    /**
     * Which client is responsible for this BalloonSettings.
     */
    private ClientId balloonClient = null;
    
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
        setBalloonsEnabled(isEmailBalloons() | isPrintBalloons());
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
        setBalloonsEnabled(isEmailBalloons() | isPrintBalloons());
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

    /**
     * Completely reset/erase list.
     *
     */
    private void clearList() {
        colorList = new Hashtable<ElementId, String>();
    }

    /**
     * Add balloon color for problem.
     * @param problem
     * @param colorName
     */
    public void addColor(Problem problem, String colorName) {
        addColor(problem.getElementId(), colorName);
    }

    private void addColor(ElementId id, String colorName) {
        colorList.put(id, colorName);
    }
    
    /**
     * Update color in list.
     * @param problem
     * @param colorName
     */
    public void updateColor (Problem problem, String colorName){
        colorList.put(problem.getElementId(), colorName);
    }

    public String getColor(ElementId id) {
        return colorList.get(id);
    }
    
    protected ElementId [] getProblemIDList() {
        return (ElementId[]) colorList.keySet().toArray(new ElementId[colorList.keySet().size()]);
    }
    
    /**
     * Compares string, handles if either string is null.
     * 
     * @param s1
     * @param s2
     * @return true if both null or equal, false otherwise
     */
    // TODO move this into a string utility class.
    private boolean stringSame (String s1, String s2){
        if (s1 == null && s2 == null) {
            return true;
        }
        
        if (s1 == null && s2 != null){
            return false;
        }
        
        return s1.equals(s2);
            
    }

    /**
     * Are classes the same?
     * 
     * @param balloonSettings
     * @return true if all fields are identical.
     */
    public boolean isSameAs(BalloonSettings balloonSettings) {

        try {
            if (balloonSettings == null){
                return false;
            }
            if (getSiteNumber() != balloonSettings.getSiteNumber()) {
                return false;
            }

            if (isEmailBalloons() != balloonSettings.isEmailBalloons()) {
                return false;
            }
            if (isPrintBalloons() != balloonSettings.isPrintBalloons()) {
                return false;
            }
            if (! stringSame(getPrintDevice(),balloonSettings.getPrintDevice())) {
                return false;
            }
            if (isPostscriptCapable() != balloonSettings.isPostscriptCapable()) {
                return false;
            }
            if (! stringSame(getEmailContact(),balloonSettings.getEmailContact())) {
                return false;
            }
            if (getLinesPerPage() != balloonSettings.getLinesPerPage()) {
                return false;
            }
            if (! stringSame(getMailServer(),balloonSettings.getMailServer())) {
                return false;
            }
            if (balloonClient == null) {
                if (balloonSettings.getBalloonClient() != null) {
                    return false;
                }
            } else {
                if (balloonSettings.getBalloonClient() == null) {
                    return false;
                } else {
                    if (!balloonClient.equals(balloonSettings.getBalloonClient())) {
                        return false;
                    }
                }
            }
            // If balloon color lists are different sizes, then false.
            if (balloonSettings.getProblemIDList().length != getProblemIDList().length){
                return false;
            }

            // Loop through colors
            for (ElementId problemId : getProblemIDList()) {
                String colorName = getColor(problemId);
                if (colorName == null) {
                    return false;
                }
                if (! colorName.equals(balloonSettings.getColor(problemId))){
                    return false;
                }
            }

            return true;
            
        } catch (Exception e) {
            // TODO Log to static exception Log
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return Returns the balloonsEnabled.
     */
    public boolean isBalloonsEnabled() {
        return balloonsEnabled;
    }

    /**
     * @param balloonsEnabled The balloonsEnabled to set.
     */
    private void setBalloonsEnabled(boolean balloonsEnabled) {
        this.balloonsEnabled = balloonsEnabled;
    }

    /**
     * @return Returns the balloonClient.
     */
    public ClientId getBalloonClient() {
        return balloonClient;
    }

    /**
     * @param balloonClient The balloonClient to set.
     */
    public void setBalloonClient(ClientId balloonClient) {
        this.balloonClient = balloonClient;
    }
    
    public boolean isMatchesBalloonClient(ClientId clientId) {
        if (balloonClient == null) {
            return clientId == null;
        }
        if (clientId == null) {
            return false;
        }
        return balloonClient.equals(clientId);
    }
}
