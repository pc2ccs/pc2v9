package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeam;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Balloon;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.util.BalloonHandler;
import edu.csus.ecs.pc2.core.util.BalloonWriter;

/**
 * Balloon Grid for ScoreboardView.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

//$HeadURL$
public class BalloonPane extends JPanePlugin {
    /**
     * 
     */
    private static final long serialVersionUID = 3041019296688422631L;

    private static final int EMAIL_COLUMN = 2;

    private static final int FILE_PRINT_COLUMN = 1;
    
    private BalloonHandler balloonHandler = new BalloonHandler();

    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            loadBalloonSettings();
            BalloonSettings balloonSettings = event.getBalloonSettings();
            if (balloonSettings != null && balloonSettings.isBalloonsEnabled() && balloonSettings.isMatchesBalloonClient(getContest().getClientId())) {
                recomputeBalloons(balloonSettings.getSiteNumber());
            }
            
        }

        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            // we do not know exactly what changed, so just make sure we
            // are uptodate
            loadBalloonSettings();
            BalloonSettings balloonSettings = event.getBalloonSettings();
            if (balloonSettings != null && balloonSettings.isBalloonsEnabled() && balloonSettings.isMatchesBalloonClient(getContest().getClientId())) {
                recomputeBalloons(balloonSettings.getSiteNumber());
            }
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            loadBalloonSettings();
        }

    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class RunListenerImplementation implements IRunListener{

        public void runAdded(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            if (event.getAction().equals(Action.CHANGED)){
                Run run = event.getRun();
                ClientId who = run.getSubmitter();
                BalloonSettings balloonSettings = getContest().getBalloonSettings(Integer.valueOf(who.getSiteNumber()));
                if (balloonSettings == null) {
                    return;
                }
                if (!balloonSettings.isBalloonsEnabled()) {
                    return;
                }
                // TODO are we allowed to send this balloon (based on time?)
                ElementId what = run.getProblemId();
                String key = balloonHandler.getBalloonKey(who, what);
                if (balloonHandler.hasBalloonBeenSent(key)) {
                    // yes -> recomputeBalloonStatus(who, what);
                    recomputeBalloonStatus(who, what);
                } else { // have not processed any balloons for this key
                    
                    if (balloonHandler.shouldSendBalloon(run)){
                        
                        if (sendBalloon(balloonHandler.buildBalloon("yes", who, what, run))) {
                            sentBalloonFor(key, who, what);
                        }
                    } // else we do not send balloons for deleted/new/no
                }
            }
        }


        public void runRemoved(RunEvent event) {
            Run run = event.getRun();
            ClientId who = run.getSubmitter();
            ElementId what = run.getProblemId();
            String key = balloonHandler.getBalloonKey(who, what);
            if (balloonHandler.hasBalloonBeenSent(key)) {
                // will revoke if this was the only yes
                recomputeBalloonStatus(who, what);
            }
        }
        
    }

    private Log log;
    
    
   private BalloonWriter balloonWriter;

    private JLabel messageLabel;

    private JPanel messagePane;

    private JPanel buttonPane;

    private JButton testButton = null;

    private MCLB testMCLB = null;

    private JCheckBox testEmailAllCheckBox = null; // @jve:decl-index=0:visual-constraint="584,57"

    private JCheckBox testPrintAllCheckBox = null; // @jve:decl-index=0:visual-constraint="579,93"

    private boolean oldEmailAll;

    private boolean oldPrintAll;
    
    /**
     * 
     */
    public BalloonPane() {
        super();
        initialize();
    }
    
    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(539, 511));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getTestMCLB(), java.awt.BorderLayout.CENTER);
    }

  
    
    private void addTestRow(int siteID) {

        try {
            JCheckBox emailCheckBox = new JCheckBox("email");
            JCheckBox printCheckBox = new JCheckBox("print");
            emailCheckBox.setBackground(getTestMCLB().getBackground());
            printCheckBox.setBackground(getTestMCLB().getBackground());
            Object[] row = new Object[getTestMCLB().getColumnCount()];
            if (siteID == 0) {
                // Default for sites
                row[0] = "";
                getTestEmailAllCheckBox().setBackground(getTestMCLB().getBackground());
                getTestPrintAllCheckBox().setBackground(getTestMCLB().getBackground());
                row[FILE_PRINT_COLUMN]=getTestPrintAllCheckBox();
                row[EMAIL_COLUMN]=getTestEmailAllCheckBox();
            } else {
                if (getContest().getSite(siteID) == null) {
                    log.info("addTestRow called for Invalid site ("+siteID+")"); 
                    return;
                }
                BalloonSettings settings = getContest().getBalloonSettings(siteID);
                if (settings == null) {
                    log.fine("addTestRow no balloonSettings for site "+siteID);
                    return;
                }
                if (settings.isMatchesBalloonClient(getContest().getClientId())) {
                    row[0] = getContest().getSite(siteID).getDisplayName();
                    if (settings.isEmailBalloons() && settings.getEmailContact().trim().length() > 0) {
                        emailCheckBox.setText(settings.getEmailContact());
                        emailCheckBox.setSelected(getTestEmailAllCheckBox().isSelected());
                        // TODO:  set preferredsize big enough to fit the text
                        emailCheckBox.setPreferredSize(new java.awt.Dimension(300,0));
                    } else {
                        emailCheckBox.setText("N/A");
                        emailCheckBox.setEnabled(false);
                    }
                    if (settings.isPrintBalloons() && settings.getPrintDevice().trim().length() > 0) {
                        printCheckBox.setText(settings.getPrintDevice());
                        // replicate default setting
                        printCheckBox.setSelected(getTestPrintAllCheckBox().isSelected());
                        // TODO:  set preferredsize big enough to fit the text
                        printCheckBox.setPreferredSize(new java.awt.Dimension(200,0));
                    } else {
                        printCheckBox.setText("N/A");
                        printCheckBox.setEnabled(false);
                    }
                    row[FILE_PRINT_COLUMN] = printCheckBox;
                    row[EMAIL_COLUMN] = emailCheckBox;
                } else {
                    log.fine("addTestRow not the balloon client for site "+siteID);
                    return;
                }
            }
            getTestMCLB().addRow(row);
        } catch (Exception e) {
            log.info("Problem adding test balloon row for site "+siteID);
            log.throwing(getClass().getName(), "addTestRow("+siteID+")", e);
        }   
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getTestButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#getPluginTitle()
     */
    @Override
    public String getPluginTitle() {
        return "Balloon Handler";
    }

    /**
     * This method initializes testButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getTestButton() {
        if (testButton == null) {
            testButton = new JButton();
            testButton.setText("Test");
            testButton.setToolTipText("Run the selected tests");
            testButton.setPreferredSize(new java.awt.Dimension(150, 26));
            testButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    runSelectedTests();
                }
            });
        }
        return testButton;
    }

    protected void runSelectedTests() {
        try {
            int siteTested = 0;
            for (int i = 1; i < getTestMCLB().getRowCount() ; i++){
                Object[] row=getTestMCLB().getRow(i);
                final int site = i;
                final boolean testEmail = ((JCheckBox)row[EMAIL_COLUMN]).isSelected();
                final boolean testPrint = ((JCheckBox)row[FILE_PRINT_COLUMN]).isSelected();
                if (testEmail || testPrint) {
                    // get it off the AWT thread
                    BalloonSettings settings = (BalloonSettings)getContest().getBalloonSettings(site).clone();
                    settings.setEmailBalloons(testEmail);
                    settings.setPrintBalloons(testPrint);
                    Balloon balloon = new Balloon(settings, null, "Test", null, "all problems", "test", null);
                    balloon.setProblems(getContest().getProblems());
                    final Balloon testBalloon = balloon;
                    Thread testASite = new Thread() {
                        public void run() {
                            balloonWriter.sendBalloon(testBalloon);
                        }
                    };
                    testASite.start();
                    siteTested++;
                }
            }
            if (siteTested == 0) {
                messageLabel.setText("No tests selected.");
            } else {
                messageLabel.setText("Last test sent at "+new Date());
            }
        } catch(Exception e) {
            log.info("Problem testing sites");
            log.throwing(getClass().getName(), "runSelectedTests", e);
        }
    }

    /**
     * This method initializes testEmailAllCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getTestEmailAllCheckBox() {
        if (testEmailAllCheckBox == null) {
            testEmailAllCheckBox = new JCheckBox();
            testEmailAllCheckBox.setText("Select All");
            testEmailAllCheckBox.setSize(new Dimension(300,15));
            testEmailAllCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    boolean changed=false;
                    if (getTestEmailAllCheckBox().isSelected() && !oldEmailAll) {
                        changed = true;
                        oldEmailAll = true;
                    } else {
                        if (!getTestEmailAllCheckBox().isSelected() && oldEmailAll) {
                            changed = true;
                            oldEmailAll = false;
                        }
                    }
                    if (changed) {
                        for(int i = 1; i < getTestMCLB().getRowCount(); i++) {
                            Object[] o=getTestMCLB().getRow(i);
                            if (o != null && o[EMAIL_COLUMN] instanceof JCheckBox) {
                                JCheckBox box = (JCheckBox)o[EMAIL_COLUMN];
                                if (!box.getText().equalsIgnoreCase("N/A")) {
                                    box.setSelected(getTestEmailAllCheckBox().isSelected());
                                }
                            }
                        }
                    }
                    return;
                }
            });
        }
        return testEmailAllCheckBox;
    }

    /**
     * This method initializes testPrintAllCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getTestPrintAllCheckBox() {
        if (testPrintAllCheckBox == null) {
            testPrintAllCheckBox = new JCheckBox();
            testPrintAllCheckBox.setText("Select All");
            testPrintAllCheckBox.setSize(new Dimension(300,15));
            testPrintAllCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    // TODO: invoke this on a GPU (in case N/A's are changed)
                    boolean changed=false;
                    if (getTestPrintAllCheckBox().isSelected() && !oldPrintAll) {
                        changed = true;
                        oldPrintAll = true;
                    } else {
                        if (!getTestPrintAllCheckBox().isSelected() && oldPrintAll) {
                            changed = true;
                            oldPrintAll = false;
                        }
                    }
                    if (changed) {
                        for(int i = 1; i < getTestMCLB().getRowCount(); i++) {
                            Object[] o=getTestMCLB().getRow(i);
                            if (o != null && o[FILE_PRINT_COLUMN] instanceof JCheckBox) {
                                JCheckBox box = (JCheckBox)o[FILE_PRINT_COLUMN];
                                if (!box.getText().equalsIgnoreCase("N/A")) {
                                    box.setSelected(getTestPrintAllCheckBox().isSelected());
                                }
                            }
                        }
                    }
                }
            });
        }
        return testPrintAllCheckBox;
    }

    /**
     * This method initializes testMCLB
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getTestMCLB() {
        if (testMCLB == null) {
            testMCLB = new MCLB();
            Object[] cols = { "Site", "", "" };
            cols[EMAIL_COLUMN] = "Test Email";
            cols[FILE_PRINT_COLUMN] = "Test Print";
            testMCLB.addColumns(cols);
        }
        return testMCLB;
    }
    

    
    void loadBalloonSettings() {

        balloonHandler.reloadBalloonSettings();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }
    
    void reloadListBox() {
        getTestMCLB().removeAllRows();
        addTestRow(0);
        for (Site site : getContest().getSites()) {
            addTestRow(site.getSiteNumber());
        }
        getTestMCLB().autoSizeAllColumns();
    }

    /**
     * This method retrieves the current ClientSettings from the contest,
     * updates the BalloonList, and calls the controller to update the ClientSetttings.
     */
    void saveClientSettings() {
        // TODO we should have some sort of locking here, or
        //  otherwise some sort of automatic updates
        ClientSettings clientSettings = getContest().getClientSettings();
        clientSettings.setBalloonList(balloonHandler.getBalloonDeliveryList());
        getController().updateClientSettings(clientSettings);
    }
    

    /**
     * Should only be called if a balloon has been sent out for this user and problem.
     * Will issue a revoke if required.
     * @param who
     * @param problemId
     */
    void recomputeBalloonStatus(ClientId who, ElementId problemId) {
        if (!balloonHandler.isRunSolved(who, problemId)) {
            if (takeBalloon(balloonHandler.buildBalloon("take", who, problemId, null))) {
                tookBalloonFrom(balloonHandler.getBalloonKey(who, problemId));
            }
        }
    }

    private void recomputeBalloons(int siteNumber) {
        BalloonSettings balloonSettings = balloonHandler.getBalloonSettingsHash().get(Integer.valueOf(siteNumber));
        if (balloonSettings == null) {
            return;
        }
        if (!balloonSettings.isBalloonsEnabled()) {
            return;
        }
       RunComparatorByTeam runComparatorByTeam = new RunComparatorByTeam();
        TreeMap<Run, Run> runTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);
        Run[] runs = getContest().getRuns();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (run.getSiteNumber() != siteNumber) {
                continue;
            }
            runTreeMap.put(run, run);
        }
        Collection<Run> runColl = runTreeMap.values();
        Iterator<Run> runIterator = runColl.iterator();

        Hashtable<String, Long> goodBalloons = new Hashtable<String,Long>();
        while (runIterator.hasNext()) {
            Object o = runIterator.next();
            Run run = (Run) o;
            if (!run.isDeleted() && run.isJudged() && run.isSolved()) {
                // there should be a yes for this run
                String key = balloonHandler.getBalloonKey(run.getSubmitter(), run.getProblemId()); 
                goodBalloons.put(key, Long.valueOf(0));
                if (!balloonHandler.hasBalloonBeenSent(key)) { // no balloon has been sent
                    if (run.isSendToTeams()) {
                        if (sendBalloon(balloonHandler.buildBalloon("yes", run.getSubmitter(), run.getProblemId(), run))) {
                            sentBalloonFor(key, run.getSubmitter(), run.getProblemId());
                        } else {
                            // TODO error sending balloon
                            log.info("Problem sending balloon to " + run.getSubmitter().getTripletKey() + " for " + run.getProblemId());
                        }
                    } else {
                        log.info("Run not sent to team, not sending balloon to "+ run.getSubmitter().getTripletKey() + " for " + run.getProblemId());
                    }
                }
            }
        }
        
        
        Enumeration<String> balloonEnum = balloonHandler.getBalloonDeliveryInfoKeys();
        while (balloonEnum.hasMoreElements()) {
            String key = balloonEnum.nextElement();
            if (!goodBalloons.containsKey(key)) {
                // pull apart key into ClientId & ElementId
                BalloonDeliveryInfo balloonDeliveryInfo = balloonHandler.getBalloonDeliveryInfo(key);
                ClientId clientId = balloonDeliveryInfo.getClientId();
                // sentBalloons includes all sites, filter for this site
                if (clientId.getSiteNumber() == siteNumber) {
                    ElementId problemId = balloonDeliveryInfo.getProblemId();
                    if (takeBalloon(balloonHandler.buildBalloon("take", clientId, problemId, null))) {
                        tookBalloonFrom(key);
                    }
                }
            }
        }

    }
  
    /**
     * Send a balloon notification.
     * @param balloon
     * @return
     */
    boolean sendBalloon(Balloon balloon) {
        log.finest("send a balloon to "+balloon.getClientId().getTripletKey()+ " for "+balloon.getProblemTitle());
        // TODO fire some event to notify others, or just do it ourselves
        return balloonWriter.sendBalloon(balloon);
    }

    /**
     * Update balloon delivery info.
     * @param key
     * @param clientId
     * @param problemId
     */
    private void sentBalloonFor(String key, ClientId clientId, ElementId problemId) {
        // record the time the balloon was sent, may be useful later
        balloonHandler.updateDeliveryInfo(key, new BalloonDeliveryInfo(clientId, problemId, Calendar.getInstance().getTime().getTime()));
        // TODO consider batching these updates
        saveClientSettings();
    }

    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.ui.UIPlugin#setContestAndController(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController)
     */
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        log = getController().getLog();
        
        balloonHandler.setContestAndController(inContest, inController);
        
        loadBalloonSettings();
        balloonWriter = new BalloonWriter(log);
        balloonHandler.setBalloonDeliveryList(getContest().getClientSettings().getBalloonList());
        Site[] sites = inContest.getSites();
        // TODO put this on a separate thread?
        // TODO #2 recompute after or before listeners, seems there is a timing window either way...
        for (int i = 0; i < sites.length; i++) {
            recomputeBalloons(sites[i].getSiteNumber());
        }

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    boolean takeBalloon(Balloon balloon) {
        log.finest("take a balloon away from "+balloon.getClientId().getTripletKey()+ " for "+balloon.getProblemTitle());
        // TODO fire some event to notify others
        return balloonWriter.sendBalloon(balloon);
    }

    private void tookBalloonFrom(String balloonKey) {
        balloonHandler.removeBalloonDelivery(balloonKey);
        // TODO consider batching these updates
        saveClientSettings();
    }
}
