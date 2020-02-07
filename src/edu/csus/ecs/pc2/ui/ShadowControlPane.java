// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ShadowInformation;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.MockContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.RemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.ShadowController;
import edu.csus.ecs.pc2.shadow.ShadowController.SHADOW_CONTROLLER_STATUS;

/**
 * This class provides a GUI for configuring and starting Shadowing operations on a remote CCS.
 * 
 * The remote CCS must support the <A href="https://clics.ecs.baylor.edu/index.php?title=Contest_API">CLICS Contest API</a>. 
 * 
 * This class is a {@link JPanePlugin} which allows specifying the remote CCS URL/login/password, 
 * along with "last event id" (that is, the value for the "since_id" parameter on the CLICS event-feed endpoint).
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 */

// $HeadURL$
public class ShadowControlPane extends JPanePlugin {

    private static final long serialVersionUID = 1;

    private JPanel buttonPanel = null;

    private JButton startButton = null;

    private JButton stopButton = null;
    
    private JButton testConnectionButton;

    private JPanel centerPanel = null;

    private boolean currentlyShadowing;

    private ShadowController shadowController;

    private ShadowSettingsPane shadowSettingsPane;

    private JButton updateButton;

    private JPanel lastEventIDPane;

    private JPanel shadowingOnOffStatusPane;

    private JLabel shadowingStatusValueLabel;

    private JTextField lastEventTextfield;
    
    private ContestInformation savedContestInformation;
    private JButton compareButton;

    /**
     * Constructs a new ShadowControlPane using the specified Contest and Controller.
     * 
     * This constructor invokes the superclass ({@link JPanePlugin}) method
     * {@link JPanePlugin#setContestAndController(IInternalContest, IInternalController)} passing to it
     * the received {@link IInternalContest} and {@link IInternalController}, making it unnecessary for
     * the caller to explicitly invoke that method.
     * 
     * @param inContest the PC2 IInternalContest representing the local contest acting as the shadow
     * @param inController the PC2 IInternalController for the local contest acting as the shadow
     * 
     */
    public ShadowControlPane(IInternalContest inContest, IInternalController inController) {
        super();
        super.setContestAndController(inContest, inController);
        this.getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        initialize();
    }

    /**
     * This method initializes the ShadowControlPane.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(600, 250));
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);

        updateGUI();
    }

    @Override
    public String getPluginTitle() {
        return "Shadow Mode Control Pane";
    }

    /**
     * This method initializes the Button Panel containing the Start and Stop buttons
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new Dimension(35, 35));
            buttonPanel.add(getUpdateButton(), null);
            buttonPanel.add(getTestConnectionButton());
            buttonPanel.add(getStartButton(), null);
            buttonPanel.add(getCompareButton());
            buttonPanel.add(getStopButton(), null);
        }
        return buttonPanel;
    }

    /**
     * @return
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setMnemonic(KeyEvent.VK_S);
            updateButton.setToolTipText("Save the updated Remote CCS settings");
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
//                    System.out.println("Update pressed...");
                    updateContestInformation();
                    enableButtons();
                }
            });
        }
        return updateButton;
    }

    /**
     * This method initializes startButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartButton() {
        if (startButton == null) {
            startButton = new JButton();
            startButton.setText("Start Shadowing");
            startButton.setMnemonic(KeyEvent.VK_S);
            startButton.setToolTipText("Start shadowing operations on the specified remote CCS");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startShadowing();
                }
            });
        }
        return startButton;
    }

    /**
     * Starts a Shadow Controller (a facade which manages the Shadowing system classes).
     * 
     */
    private void startShadowing() {
 
        //the following was carried over from WebServerPane (from which this class was initially copied)

//        Properties properties = new Properties();
//
//        properties.put(WebServer.PORT_NUMBER_KEY, portTextField.getText());
//        properties.put(WebServer.CLICS_CONTEST_API_SERVICES_ENABLED_KEY, Boolean.toString(getChckbxClicsContestApi().isSelected()));
//        properties.put(WebServer.STARTTIME_SERVICE_ENABLED_KEY, Boolean.toString(getChckbxStarttime().isSelected()));
//        properties.put(WebServer.FETCH_RUN_SERVICE_ENABLED_KEY, Boolean.toString(getChckbxFetchRuns().isSelected()));
//
//        getWebServer().startWebServer(getContest(), getController(), properties);
        

        boolean shadowCheckboxEnabled = getShadowSettingsPane().getShadowModeCheckbox().isSelected();
        boolean shadowDataComplete = verifyShadowControls();
        
        if (shadowCheckboxEnabled && shadowDataComplete) {
            shadowController = new ShadowController(this.getContest(),this.getController()) ;
            boolean success = shadowController.start();
            if (success) {
                currentlyShadowing = true;
                shadowingStatusValueLabel.setText("ON");
            } else {
                handleStartFailure();
                showErrorMessage("Failed to start shadowing; check logs (bad URL or credentials? mismatched configs?)", "Cannot start Shadowing");
            }

        } else {
            showErrorMessage("Shadow Mode not enabled, or shadowing parameters not valid", "Cannot start Shadowing");
        }
        updateGUI();
    }

    /**
     * This method is invoked when a call to ShadowController.start() returns false (failure in starting shadowing).
     * 
     */
    private void handleStartFailure() {
        
        SHADOW_CONTROLLER_STATUS failureStatus = shadowController.getStatus();
        
        String failureReason = failureStatus.getLabel();
        
        showErrorMessage(failureReason, "Shadow Controller Failed To Start");
        
    }

    /**
     * Checks all the components on the ShadowModePane, returns true if they all have sane values
     * (meaning, they all have values which will work for starting shadowing); false otherwise.
     * 
     * Specifically, this means that in order for "true" to be returned, ALL of the following must be true:
     * <pre>
     *   - the "Enable Shadow Mode" checkbox is checked (selected)
     *   - the RemoteCCS textfields for URL, Login, and Password are ALL non-null and not the empty string
     * </pre>
     * 
     * @return an indication of whether the GUI controls are set for shadowing to start
     */
    private boolean verifyShadowControls() {
        
        ShadowSettingsPane shadowPane = getShadowSettingsPane();
        if (shadowPane==null) {
            return false;
        }
        if (!shadowPane.getShadowModeCheckbox().isSelected()) {
            return false;
        }
        if (shadowPane.getRemoteCCSURLTextfield()==null || "".equals(shadowPane.getRemoteCCSURLTextfield().getText().trim())) {
            return false;
        }
        if (shadowPane.getRemoteCCSLoginTextfield()==null || "".equals(shadowPane.getRemoteCCSLoginTextfield().getText().trim())) {
            return false;
        }
        if (shadowPane.getRemoteCCSPasswdTextfield()==null || "".equals(shadowPane.getRemoteCCSPasswdTextfield().getText().trim())) {
            return false;
        }

        return true;
    }

    /**
     * Displays a message in a simple dialog format.
     * @param string the message to be displayed
     */
    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }
    
    /**
     * Displays an error message dialog.
     * @param message the message to be displayed
     * @param title the title to be put at the top of the error message dialog
     */
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method initializes stopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setText("Stop Shadowing");
            stopButton.setMnemonic(KeyEvent.VK_T);
            stopButton.setToolTipText("Stop shadowing operations");
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopShadowing();
                }
            });
        }
        return stopButton;
    }

    /**
     * Stops shadowing operations if running. 
     */
    protected void stopShadowing() {

        if (shadowController!=null) {
            shadowController.stop();
            currentlyShadowing = false;
            shadowingStatusValueLabel.setText("OFF");

        }
        updateGUI();
    }

    /**
     * This method initializes centerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            
            centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            centerPanel.add(getShadowingOnOffStatusPane());
            centerPanel.add(getShadowSettingsPane());
            centerPanel.add(getLastEventIDPane());

            
        }
        return centerPanel;
    }


    /**
     * Constructs a new {@link ShadowSettingsPane} if none exists.
     * Construction includes adding keylisteners and actionlisteners to the ShadowSettingsPane
     * components.
     * 
     * @return a ShadowSettingsPane with listeners attached to its active components
     */
    private ShadowSettingsPane getShadowSettingsPane() {
        if (shadowSettingsPane==null) {
            shadowSettingsPane = new ShadowSettingsPane();
            
            KeyListener keyListener = new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableButtons();
                }
            };
            shadowSettingsPane.getRemoteCCSURLTextfield().addKeyListener(keyListener);
            shadowSettingsPane.getRemoteCCSLoginTextfield().addKeyListener(keyListener);
            shadowSettingsPane.getRemoteCCSPasswdTextfield().addKeyListener(keyListener);

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enableButtons();
                }
            };
            shadowSettingsPane.getShadowModeCheckbox().addActionListener(actionListener);

        }
        return shadowSettingsPane;
    }

    /**
     * @return
     */
    private JPanel getLastEventIDPane() {
        if (lastEventIDPane==null) {
            lastEventIDPane = new JPanel();
            
            lastEventIDPane.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            JLabel lastEventIDLabel = new JLabel("Last Event ID:");
            lastEventIDLabel.setToolTipText("The ID of the last event already received; i.e., the \"since_id\" for events being requested");
            lastEventIDPane.add(lastEventIDLabel);
            
            lastEventTextfield = new JTextField(10);
            lastEventTextfield.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    enableButtons();
                }
            });
            lastEventIDPane.add(lastEventTextfield);
        }
        return lastEventIDPane;
    }

    /**
     * @return
     */
    private JPanel getShadowingOnOffStatusPane() {
        if (shadowingOnOffStatusPane==null) {
            shadowingOnOffStatusPane = new JPanel();
            
            shadowingOnOffStatusPane.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            JLabel shadowingStatusLabel = new JLabel();
            shadowingStatusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            shadowingStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            shadowingStatusLabel.setText("Shadowing is currently: ");
            shadowingOnOffStatusPane.add(shadowingStatusLabel);
            
            shadowingStatusValueLabel = new JLabel();
            shadowingStatusValueLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            shadowingStatusValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            shadowingStatusValueLabel.setText("UNDEFINED");  
            shadowingOnOffStatusPane.add(shadowingStatusValueLabel);

        }
        return shadowingOnOffStatusPane;
    }

    private void enableButtons() {
//        System.out.println ("EnableButtons() called");

        ShadowInformation newChoice = getFromFields();

        if (getCurrentShadowInformation(getContest().getContestInformation()).isSameAs(newChoice)) {
            getUpdateButton().setEnabled(false);
            getStartButton().setEnabled(!currentlyShadowing);
            getStopButton().setEnabled(currentlyShadowing);
            getTestConnectionButton().setEnabled(!currentlyShadowing);            
            
        } else {
            getUpdateButton().setEnabled(true);
            getStartButton().setEnabled(false);
            getStopButton().setEnabled(false);
            getTestConnectionButton().setEnabled(false);
        }

    }


    /**
     * @param contestInformation
     * @return
     */
    private ShadowInformation getCurrentShadowInformation(ContestInformation contestInformation) {
        
        ShadowInformation newShadowInfo = new ShadowInformation();
        
        newShadowInfo.setShadowModeEnabled(contestInformation.isShadowMode());
        newShadowInfo.setRemoteCCSURL(contestInformation.getPrimaryCCS_URL());
        newShadowInfo.setRemoteCCSLogin(contestInformation.getPrimaryCCS_user_login());
        newShadowInfo.setRemoteCCSPassword(contestInformation.getPrimaryCCS_user_pw());
        newShadowInfo.setLastEventID(contestInformation.getLastShadowEventID());
        return newShadowInfo;
    }

    /**
     * Updates the GUI to correspond to the current state.
     */
    private void updateGUI() {

        ContestInformation contestInformation = getContest().getContestInformation();

//        System.out.println ("UpdateGUI(): got the following shadow info:");
//        System.out.println ("   Shadow Enabled: " + contestInformation.isShadowMode() 
//                          + "\n              URL: " + contestInformation.getPrimaryCCS_URL()
//                          + "\n            login: " + contestInformation.getPrimaryCCS_user_login()
//                          + "\n           passwd: " + contestInformation.getPrimaryCCS_user_pw()
//                          + "\n        lastEvent: " + contestInformation.getLastShadowEventID() );
//        

        getStartButton().setEnabled(!currentlyShadowing);
        getStopButton().setEnabled(currentlyShadowing);
        getUpdateButton().setEnabled(false);

        if (currentlyShadowing) {
            shadowingStatusValueLabel.setText("ON");
        } else {
            shadowingStatusValueLabel.setText("OFF");
        }
        
        updateShadowSettingsPane(currentlyShadowing);
        lastEventTextfield.setText(contestInformation.getLastShadowEventID());
    }

    private void updateShadowSettingsPane(boolean currentlyShadowing) {
        
        ContestInformation contestInformation = getContest().getContestInformation();

        getShadowSettingsPane().getShadowModeCheckbox().setSelected(contestInformation.isShadowMode());
        getShadowSettingsPane().getRemoteCCSURLTextfield().setText(contestInformation.getPrimaryCCS_URL());
        getShadowSettingsPane().getRemoteCCSLoginTextfield().setText(contestInformation.getPrimaryCCS_user_login());
        getShadowSettingsPane().getRemoteCCSPasswdTextfield().setText(contestInformation.getPrimaryCCS_user_pw());

        // if Shadowing is currently on, do not allow these settings to be changed
        getShadowSettingsPane().getRemoteCCSURLTextfield().setEditable(!currentlyShadowing);
        getShadowSettingsPane().getRemoteCCSLoginTextfield().setEditable(!currentlyShadowing);
        getShadowSettingsPane().getRemoteCCSPasswdTextfield().setEditable(!currentlyShadowing);
        lastEventTextfield.setEditable(!currentlyShadowing);
    }

    /**
     * Returns a new ShadowInformation object containing data fetched from this pane's fields.
     * @return a ShadowInformation object
     */
    protected ShadowInformation getFromFields() {
        
        ShadowInformation newShadowInformation = new ShadowInformation();                
        
        //fill in Shadow Mode information from this pane
        newShadowInformation.setShadowModeEnabled(getShadowSettingsPane().getShadowModeCheckbox().isSelected());
        newShadowInformation.setRemoteCCSURL(getShadowSettingsPane().getRemoteCCSURLTextfield().getText());
        newShadowInformation.setRemoteCCSLogin(getShadowSettingsPane().getRemoteCCSLoginTextfield().getText());
        newShadowInformation.setRemoteCCSPassword(getShadowSettingsPane().getRemoteCCSPasswdTextfield().getText());
        newShadowInformation.setLastEventID(lastEventTextfield.getText());
   
        return (newShadowInformation);
    }

    /**
     * Updates the current {@link ContestInformation} on the server with the current shadow settings
     * in this GUI pane.
     * 
     */
    private void updateContestInformation() {
        ShadowInformation shadowInfo = getFromFields();
        
//        System.out.println ("UpdateContestInformation(): got the following shadow info:");
//        System.out.println ("   Shadow Enabled: " + shadowInfo.isShadowModeEnabled()
//                          + "\n              URL: " + shadowInfo.getRemoteCCSURL()
//                          + "\n            login: " + shadowInfo.getRemoteCCSLogin()
//                          + "\n           passwd: " + shadowInfo.getRemoteCCSPassword()
//                          + "\n        lastEvent: " + shadowInfo.getLastEventID());
//        
//        System.out.println ("UpdateContestInformation(): savedContestInformation contains the following shadow info:");
//        System.out.println ("   Shadow Enabled: " + savedContestInformation.isShadowMode()
//                          + "\n              URL: " + shadowInfo.getRemoteCCSURL()
//                          + "\n            login: " + shadowInfo.getRemoteCCSLogin()
//                          + "\n           passwd: " + shadowInfo.getRemoteCCSPassword()
//                          + "\n        lastEvent: " + shadowInfo.getLastEventID());
        
        ContestInformation contestInfo = getContest().getContestInformation();
        
        contestInfo.setShadowMode(shadowInfo.isShadowModeEnabled());
        contestInfo.setPrimaryCCS_URL(shadowInfo.getRemoteCCSURL());
        contestInfo.setPrimaryCCS_user_login(shadowInfo.getRemoteCCSLogin());
        contestInfo.setPrimaryCCS_user_pw(shadowInfo.getRemoteCCSPassword());
        contestInfo.setLastShadowEventID(shadowInfo.getLastEventID());
        
        getController().updateContestInformation(contestInfo);
    }

    class ContestInformationListenerImplementation implements IContestInformationListener {



        public void contestInformationAdded(ContestInformationEvent event) {
//            System.out.println ("contestInformationAdded listener: event = " + event);
            savedContestInformation = event.getContestInformation();
            updateGUI();
        }

        public void contestInformationChanged(ContestInformationEvent event) {
//            System.out.println ("contestInformationChanged listener: event = " + event);
           savedContestInformation = event.getContestInformation();
            updateGUI();
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
//            System.out.println ("contestInformationRefreshAll listener: event = " + contestInformationEvent);
            savedContestInformation = contestInformationEvent.getContestInformation();
            updateGUI();
        }
        
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }

    }

    private JButton getCompareButton() {
        if (compareButton == null) {
        	compareButton = new JButton("Compare");
        	compareButton.setMnemonic(KeyEvent.VK_C);
        	compareButton.setToolTipText("Display comparison results");
        	compareButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (shadowController==null) {
                        showErrorMessage("No shadow controller available; cannot show comparison", "Missing Controller"); 
                    } else {
                        JFrame shadowCompareFrame = new ShadowCompareFrame(shadowController.getJudgementComparisonInfo());
                        shadowCompareFrame.setLocationRelativeTo(null); // centers frame
                        shadowCompareFrame.setSize(400,500);
                        shadowCompareFrame.setTitle("Shadow Comparison");
                        shadowCompareFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        shadowCompareFrame.setVisible(true);
                    }
                    
                }
            });

        }
        return compareButton;
    }
    
    private JButton getTestConnectionButton() {
        if (testConnectionButton == null) {
            testConnectionButton = new JButton("Test Connection");
            testConnectionButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            
                               IRemoteContestAPIAdapter remoteContestAPIAdapter = null;
                                try {
                                    ShadowInformation shadowInfo = getCurrentShadowInformation(getContest().getContestInformation());
                                    String remoteURLString = shadowInfo.getRemoteCCSURL();
                                    URL remoteURL = new URL(remoteURLString);
                                    String remoteLogin = shadowInfo.getRemoteCCSLogin();
                                    String remotePW = shadowInfo.getRemoteCCSPassword();
                                    remoteContestAPIAdapter = createRemoteContestAPIAdapter(remoteURL, remoteLogin, remotePW);
                                    boolean isConnected = remoteContestAPIAdapter.testConnection();
                                    if (isConnected) {
                                        showMessage ("Connection to remote CCS is successful");
                                    } else {
                                        showErrorMessage("Connection to remote CCS failed", "Connection failed");
                                    }

                                } catch (Exception e) {
                                    showErrorMessage("Exception attempting to connect to remote system:\n" + e, 
                                                        "Exception in connecting");
                                } finally {
                                    if (remoteContestAPIAdapter != null) {
                                        remoteContestAPIAdapter = null;
                                    }
                                }
                        }
                    });

                    
                }
            });
        }
        return testConnectionButton;
    }
    
    private IRemoteContestAPIAdapter createRemoteContestAPIAdapter(URL url, String login, String password) {

        boolean useMockAdapter = StringUtilities.getBooleanValue(IniFile.getValue("shadow.usemockcontestadapter"), false);
        if (useMockAdapter)
        {
            return new MockContestAPIAdapter(url, login, password);
        } else {
            return new RemoteContestAPIAdapter(url, login, password);
        }
    }

}
