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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.shadow.ShadowController;

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

    private JPanel centerPanel = null;

    private boolean currentlyShadowing;

    private ShadowController shadowController;

    private ShadowSettingsPane shadowSettingsPane;

    private JButton updateButton;

    private JPanel lastEventIDPane;

    private JPanel shadowingOnOffStatusPane;

    private JLabel shadowingStatusValueLabel;

    private JTextField lastEventTextfield;

    /**
     * Constructs a new ShadowControlPane, <I>relying on the caller to also call method 
     * {@link #setContestAndController(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController)}
     * prior to doing any shadowing operations</i>.
     * 
     */
    public ShadowControlPane() {
        super();
        initialize();
    }

    /**
     * This method initializes the ShadowControlPane.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(505, 250));
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
            buttonPanel.add(getStartButton(), null);
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
                    System.out.println("Update pressed...");
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
            boolean started = shadowController.start();
            if (started) {
                currentlyShadowing = true;
                shadowingStatusValueLabel.setText("ON");
            } else {
                showErrorMessage("Failed to start shadowing; check logs (bad URL?)", "Cannot start Shadowing");
            }

        } else {
            showErrorMessage("Shadow Mode not enabled, or shadowing parameters not valid", "Cannot start Shadowing");
        }
        updateGUI();
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

//    /**
//     * Displays a message in a simple dialog format.
//     * @param string the message to be displayed
//     */
//    private void showMessage(String string) {
//        JOptionPane.showMessageDialog(this, string);
//    }
    
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
                    enableUpdateButton();
                }
            };
            shadowSettingsPane.getRemoteCCSURLTextfield().addKeyListener(keyListener);
            shadowSettingsPane.getRemoteCCSLoginTextfield().addKeyListener(keyListener);
            shadowSettingsPane.getRemoteCCSPasswdTextfield().addKeyListener(keyListener);

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enableUpdateButton();
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
                    enableUpdateButton();
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

    private void enableUpdateButton() {
        System.out.println ("EnableUpdateButton() called; needs to check for changes and save them");
        
    }


    /**
     * Updates the state of the GUI to correspond to the saved state.
     */
    private void updateGUI() {


        getStartButton().setEnabled(!currentlyShadowing);
        getStopButton().setEnabled(currentlyShadowing);

        if (currentlyShadowing) {
            shadowingStatusValueLabel.setText("ON");
        } else {
            shadowingStatusValueLabel.setText("OFF");
        }
        
        updateShadowSettingsPaneSettings(currentlyShadowing);
    }

    private void updateShadowSettingsPaneSettings(boolean currentlyShadowing) {
        // if Shadowing is currently on, do not allow these settings to be changed
        shadowSettingsPane.getRemoteCCSURLTextfield().setEditable(!currentlyShadowing);
        shadowSettingsPane.getRemoteCCSLoginTextfield().setEditable(!currentlyShadowing);
        shadowSettingsPane.getRemoteCCSPasswdTextfield().setEditable(!currentlyShadowing);
        lastEventTextfield.setEditable(!currentlyShadowing);
    }


}
