package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Balloon and Balloon Distribution Settings (per Site).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettings implements IElementObject {

    public static final String MAIL_PROTOCOL = "mail.transport.protocol";

    /**
     * Key for the default host name of the mail server.
     */
    public static final String MAIL_HOST = "mail.host";

    /**
     * Value of the return email address of the current user.
     */
    private static final String DEFAULT_MAIL_FROM = "nobody@ecs.csus.edu";

    /**
     * List of balloon colors per problem.
     */
    // key = Problem.getElementId(), value = color name, e.g. Blue
    private Hashtable<ElementId, String> colorList = new Hashtable<ElementId, String>();
    
    /**
     * List of RGB colors per problem.
     */
    // key = Problem.getElementId(), value = RGB color name, e.g. 0000FF
    private Hashtable<ElementId, String> colorListRGB = new Hashtable<ElementId, String>();

    private static final long serialVersionUID = 4208771943370594478L;

    /**
     * Default IANA assigned port for SMTP over SSL (TLS).
     */
    private static final int DEFAULT_PORT_SMTPS = 465;
    /**
     * Default IANA assigned port for mail.
     */
    private static final int DEFAULT_PORT_SMTP = 25;

    public static final String MAIL_USER = "mail.user";
    public static final String MAIL_PASSWORD = "mail.user.password";

    public static final Object MAIL_FROM = "mail.from";

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

    private Properties mailProperties = null;
    
    public BalloonSettings(String displayName, int siteNumber) {
        super();
        elementId = new ElementId(displayName);
        setSiteNumber(siteNumber);
        mailProperties = getDefaultMailProperties();
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

    public static Properties getDefaultMailProperties() {
        Properties defaultProperties = new Properties();
        defaultProperties.put("mail.debug", Boolean.toString(false));
        defaultProperties.put(MAIL_HOST, "");
        defaultProperties.put(MAIL_USER, "");
        defaultProperties.put(MAIL_PASSWORD, "");
        defaultProperties.put(MAIL_PROTOCOL, "smtp");
        defaultProperties.put("mail.smtp.auth", Boolean.toString(false));
        defaultProperties.put("mail.smtp.port", Integer.toString(DEFAULT_PORT_SMTP));
        defaultProperties.put("mail.smtp.starttls.enable",Boolean.toString(false));
        defaultProperties.put("mail.smtps.auth", Boolean.toString(false));
        defaultProperties.put("mail.smtps.port", Integer.toString(DEFAULT_PORT_SMTPS));
        defaultProperties.put(MAIL_FROM, DEFAULT_MAIL_FROM);
        defaultProperties.put("mail.smtp.ssl.trust", "*"); // trust everyone
        defaultProperties.put("mail.smtps.ssl.trust", "*"); // trust everyone
        return defaultProperties;
    }
    /*
     * for use with JavaMail API
     */
    public Properties getMailProperties() {
/* XXX these should be set via the ui now
        mailProperties = new Properties();
        mailProperties.put("mail.from", "pc2@ecs.csus.edu");
        // consider not adding this if getMailServer() is not set
        mailProperties.put("mail.host", getMailServer());
        // hardcode auth for now
        mailProperties.put("mail.smtps.auth", "true");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.debug", "true");
        mailProperties.put("mail.smtp.starttls.enable","true");
        // for use with digest-md5, the default gaia.ecs.csus.edu not working
//        mailProperties.put("mail.smtp.sasl.realm","ecs.csus.edu");
        // in the future might have other stuff including mail.user
         */
        if (mailProperties == null) {
            mailProperties = getDefaultMailProperties();
        }
        return mailProperties;
    }
    
    /**
     * @return Returns the mailServer.
     */
    public String getMailServer() {
        return (String)getMailProperties().get(MAIL_HOST);
    }

    /**
     * @param mailServer
     *            The mailServer to set.
     */
    public void setMailServer(String mailServer) {
        getMailProperties().put(MAIL_HOST, mailServer);
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
        colorListRGB = new Hashtable<ElementId, String>();
    }

    /**
     * Add balloon color for problem.
     * @param problem
     * @param colorName
     */
    public void addColor(Problem problem, String colorName) {
        addColor(problem.getElementId(), colorName);
    }
    
    /**
     * Add Balloon color and rgb color for problem.
     * 
     * @param problem
     * @param colorName
     * @param rgbColor
     */
    public void addColor(Problem problem, String colorName, String rgbColor) {
        addColor(problem.getElementId(), colorName, rgbColor);
    }

    private void addColor(ElementId id, String colorName) {
        colorList.put(id, colorName);
    }

    private void addColor(ElementId id, String colorName, String rgbColor) {
        colorList.put(id, colorName);
        colorListRGB.put(id, rgbColor);
    }

    /**
     * Update color in list.
     * @param problem
     * @param colorName
     */
    public void updateColor (Problem problem, String colorName){
        colorList.put(problem.getElementId(), colorName);
    }

    /**
     * @param id Problem eventId
     */
    public String getColor(ElementId id) {
        return colorList.get(id);
    }
    
    /**
     * Get list of problem ids where color has been set.
     * @return
     */
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
            if (! mailProperties.equals(balloonSettings.getMailProperties())) {
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

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BalloonSettings clone = new BalloonSettings("foo", getSiteNumber());
        clone.elementId = elementId;
        clone.colorList = cloneColorList();
        clone.colorListRGB = cloneColorRGBList();    
        clone.setBalloonClient(getBalloonClient());
        clone.setEmailBalloons(isEmailBalloons());
        clone.setEmailContact(new String(emailContact));
        clone.setIncludeNos(isIncludeNos());
        clone.setLinesPerPage(getLinesPerPage());
        clone.setMailServer(new String(mailServer));
        clone.setPostscriptCapable(isPostscriptCapable());
        clone.setPrintBalloons(isPrintBalloons());
        clone.setPrintDevice(new String(printDevice));
        
        Properties newMailProperties = new Properties();
        String[] keys = mailProperties.keySet().toArray(new String[mailProperties.size()]);
        for (String key : keys) {
            newMailProperties.put(key, new String((String)mailProperties.get(key)));
        }
        clone.setMailProperties(newMailProperties);
        return clone;
    }
    
    
    
    private Hashtable<ElementId,String> cloneColorRGBList() {
        Hashtable<ElementId,String> newHash=new Hashtable<ElementId,String>();
        Enumeration<ElementId> elementList=colorListRGB.keys();
        while (elementList.hasMoreElements()) {
            ElementId key = elementList.nextElement();
            newHash.put(key,colorListRGB.get(key));
        }
        return newHash;
    }
    
    private Hashtable<ElementId,String> cloneColorList() {
        Hashtable<ElementId,String> newHash=new Hashtable<ElementId,String>();
        Enumeration<ElementId> elementList=colorList.keys();
        while (elementList.hasMoreElements()) {
            ElementId key = elementList.nextElement();
            newHash.put(key,colorList.get(key));
        }
        return newHash;
    }
    public BalloonSettings copy(Site newSite) {
        // this is like the clone, but without the elementId foo
        BalloonSettings clone = new BalloonSettings(newSite.getDisplayName(), newSite.getSiteNumber());
        // TODO deep clone the colorList hashtable
        clone.colorList = cloneColorList();
        clone.setBalloonClient(getBalloonClient());
        clone.setEmailBalloons(isEmailBalloons());
        clone.setEmailContact(new String(emailContact));
        clone.setIncludeNos(isIncludeNos());
        clone.setLinesPerPage(getLinesPerPage());
        clone.setMailServer(new String(mailServer));
        clone.setPostscriptCapable(isPostscriptCapable());
        clone.setPrintBalloons(isPrintBalloons());
        clone.setPrintDevice(new String(printDevice));
        Properties newMailProperties = new Properties();
        String[] keys = mailProperties.keySet().toArray(new String[mailProperties.size()]);
        for (String key : keys) {
            newMailProperties.put(key, new String((String)mailProperties.get(key)));
        }
        clone.setMailProperties(newMailProperties);
        return clone;
    }

    /**
     * @param mailProperties the mailProperties to set
     */
    public void setMailProperties(Properties newMailProperties) {
        this.mailProperties = newMailProperties;
        setMailServer((String)newMailProperties.get(MAIL_HOST));
    }

    public String getColorRGB(Problem problem) {
        return getColorRGB(problem.getElementId());        
    }
    
    /**
     * @param id Problem eventId
     */
    public String getColorRGB(ElementId id) {
        return colorListRGB.get(id);
    }
}
