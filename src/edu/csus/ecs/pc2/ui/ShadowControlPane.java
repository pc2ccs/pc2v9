// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ShadowInformation;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.IShadowMonitorStatus;
import edu.csus.ecs.pc2.shadow.MockContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.RemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.ShadowController;
import edu.csus.ecs.pc2.shadow.ShadowController.SHADOW_CONTROLLER_STATUS;

import com.fasterxml.jackson.databind.ObjectMapper;

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

public class ShadowControlPane extends JPanePlugin implements IShadowMonitorStatus {

    private static final long serialVersionUID = 1;
    
    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;
    
    private static final String CCS_API_ENDPOINT = "/";

    private JPanel buttonPanel = null;

    private JButton startStopButton = null;
    
    private JButton testConnectionButton;

    private JPanel centerPanel = null;

    private boolean currentlyShadowing;

    private ShadowController shadowController;

    private ShadowSettingsPane shadowSettingsPane;

    private JButton updateButton;

    private JPanel lastEventIDPane;

    private JPanel shadowingOnOffStatusPane;
    
    private JScrollPane connectStatusPane;

    private JLabel shadowingStatusValueLabel;

    private JTextField lastEventTextfield;
    
    private ContestInformation savedContestInformation;
    
    private JButton compareRunsButton;

    private JButton compareScoreboardsButton;

    private JTextField lastRecordTextfield;
    
    private JTextField lastEventTimeTextField;
    
    private JTableCustomized connectStatusTable;
    
    private DefaultTableModel connectStatusTableModel;
    
    private int statusScrollBarMax = 0;
    
    private int numRecord = 0;
    
    private String lastToken = null;
    
    private SimpleDateFormat lastDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Lightish green for success
    private Color statusColorSuccess = new Color(128, 255, 128);
    // Lightish red for failure
    private Color statusColorFailure = new Color(255, 128, 128);

    // Status column for JTable notifications
    enum ShadowStatus {
        SUCCESS,
        FAILURE,
        INFO
    };

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
        this.setSize(new Dimension(800, 250));
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);

        setupConnectionStatusTable();
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
            buttonPanel.add(getStartStopButton(), null);
            buttonPanel.add(getCompareRunsButton(), null);
            buttonPanel.add(getCompareScoreboardsButton(), null);
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
     * This method initializes the startStopButton which starts or stops
     * shadowing operations.
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton();
            startStopButton.setText("Start Shadowing");
            startStopButton.setMnemonic(KeyEvent.VK_S);
            startStopButton.setToolTipText("Start shadowing operations on the specified remote CCS");
            startStopButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!currentlyShadowing) {
                        
                        if (!getContest().getContestTime().isContestRunning()) {
                            showErrorMessage("Contest clock STOPPED, cannot start shadowing", "Cannot start shadowing");
                            return;
                        }

                    	SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                startShadowing();
                            }
                        });

                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {

                                int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to stop shadowing?", "Stop Shadowing");

                                if (result == JOptionPane.YES_OPTION) {
                                    stopShadowing();
                                }
                            }
                        });

                   }

                }
            });
        }
        return startStopButton;
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
            shadowController = new ShadowController(this.getContest(), this.getController(), (IShadowMonitorStatus)this, lastToken) ;
            boolean success = shadowController.start();
            if (success) {
                currentlyShadowing = true;
                shadowingStatusValueLabel.setText("ON");
                getStartStopButton().setText("Stop Shadowing");
                getStartStopButton().setToolTipText("Stop shadowing operations");
                getController().getLog().info("Shadowing started");
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
     * Displays an error message dialog; also logs the Error Message.
     * @param message the message to be displayed and logged.
     * @param title the title to be put at the top of the error message dialog
     */
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        getController().getLog().log(Log.WARNING, message);
    }

    /**
     * Stops shadowing operations if running. 
     */
    protected void stopShadowing() {

        if (shadowController!=null) {
            shadowController.stop();
            currentlyShadowing = false;
            shadowingStatusValueLabel.setText("OFF");
            getStartStopButton().setText("Start Shadowing");
            getStartStopButton().setToolTipText("Start shadowing operations on the specified remote CCS");
            getController().getLog().info("Shadowing stopped");
            // save last token on server
            updateContestInformation();
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
            
           /*
             * We use a GridBagLayout instead of a FlowLayout since we want to make
             * the notification JTable resize as the window gets bigger so you can see more
             * entries.  FlowLayout doesn't work that way.  Being there are 3 other panes involved
             * GridBag seemed the way to go.  The GridBagLayout is one column wide by 4 rows high:
             * Row 0 - Indicator if shadowing is on or off pane
             * Row 1 - Shadow settings pane
             * Row 2 - Event ID information pane
             * Row 3 - Status table (Expands to fill window)
             */
            centerPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            
            // Each pane uses exactly one cell in the layout
            c.gridwidth = 1;
            c.gridheight = 1;
            // Since it's only 1 column wide, all cells start in the first column
            c.gridx = 0;
            
            c.fill = GridBagConstraints.NONE;
            c.gridy = 0;
            centerPanel.add(getShadowingOnOffStatusPane(), c);
            
            c.gridy = 1;
            centerPanel.add(getShadowSettingsPane(), c);
            
            // Fill horizontally or it will chop it off.
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridy = 2;
            // This is needed due to the way the pane is created.  We have to 
            // allow the height to expand a tiny bit, or it chops the pane off.
            c.weighty = 0.01;
            centerPanel.add(getLastEventIDPane(), c);
            
            // Fill both width and height as needed as display area expands
            c.fill = GridBagConstraints.BOTH;
            c.gridy = 3;
            c.weighty = 0.5;
            // Was bumping up against the edge, so leave some elbow room
            c.insets = new Insets(0, 20, 0, 20);
            centerPanel.add(getConnectStatusPane(), c);          
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
            
            JLabel lastEventDateLabel = new JLabel("Last Event Processed At:");
            lastEventDateLabel.setToolTipText("The time the last event was processed");
            lastEventIDPane.add(lastEventDateLabel);
            
            // 2022-09-27 23:02:03.009 (23 chars), but that's too many columns for our font
            lastEventTimeTextField = new JTextField("N/A", 16);
            lastEventTimeTextField.setEditable(false);
            lastEventIDPane.add(lastEventTimeTextField);
            
            JLabel lastEventIDLabel = new JLabel("Last Event ID:");
            lastEventIDLabel.setToolTipText("The ID of the last event already received; i.e., the \"since_id\" for events being requested");
            lastEventIDPane.add(lastEventIDLabel);
            
            lastEventTextfield = new JTextField(10);
            lastEventTextfield.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    enableButtons();
                }
            });
            lastEventTextfield.setHorizontalAlignment(JTextField.RIGHT);
            lastEventIDPane.add(lastEventTextfield);
            
            JLabel lastRecordLabel = new JLabel("Records Read:");
            lastRecordLabel.setToolTipText("The number of JSON event records read from the primary");
            lastEventIDPane.add(lastRecordLabel);
            
            lastRecordTextfield = new JTextField(10);
            lastRecordTextfield.setEditable(false);
            lastRecordTextfield.setHorizontalAlignment(JTextField.RIGHT);
            lastEventIDPane.add(lastRecordTextfield);
            
        }
        return lastEventIDPane;
    }

    /**
     * @return
     */
    private JScrollPane getConnectStatusPane() {
        if (connectStatusPane == null) {
            connectStatusPane = new JScrollPane(getConnectStatusTable());

            // make it so it always scrolls to the bottom of the pane
            connectStatusPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    if(statusScrollBarMax == e.getAdjustable().getMaximum()) {
                        return;
                    }
                    statusScrollBarMax = e.getAdjustable().getMaximum();
                    e.getAdjustable().setValue(statusScrollBarMax);  
                }
            });
        }
        
        return connectStatusPane;
    }
    
    private JTableCustomized getConnectStatusTable() {
    
        connectStatusTable = new JTableCustomized() {
            private static final long serialVersionUID = 1L;

            // override JTable's default renderer to set the background color based on the ShadowStatus
            // Essentially stolen from ShadowCompareScoreboardPane.  Thank you JohnC.
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                //default to normal background
                c.setBackground(getBackground());
              
                if(connectStatusTableModel != null) {
                    //map the specified row index number to the corresponding model row (index numbers can change due
                    // to sorting/scrolling; model row numbers never change).
                    int modelRow = convertRowIndexToModel(row);
                    ShadowStatus stat = (ShadowStatus)connectStatusTableModel.getValueAt(modelRow, 1);
                    switch(stat) {
                        case SUCCESS: c.setBackground(statusColorSuccess); break;
                        case FAILURE: c.setBackground(statusColorFailure); break;
                        case INFO: c.setBackground(getBackground()); break;
                    }
                }
                
                
                return(c);
            }
        };
        
        return(connectStatusTable);
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
            getStartStopButton().setEnabled(true);
            getTestConnectionButton().setEnabled(!currentlyShadowing);            
            
        } else {
            getUpdateButton().setEnabled(true);
            getStartStopButton().setEnabled(false);
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

    private void setupConnectionStatusTable() {

        Object[] columns = { "Time             ", "Status", "Description               " };
        connectStatusTable.removeAll();
        
        connectStatusTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        connectStatusTable.setModel(connectStatusTableModel);

        // Sorters
        TableRowSorter<DefaultTableModel> trs = new TableRowSorter<DefaultTableModel>(connectStatusTableModel);
        
        connectStatusTable.setRowSorter(trs);
        connectStatusTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        ArrayList<SortKey> sortList = new ArrayList<SortKey>();
        
        /*
         * Column headers left justified
         */
        ((DefaultTableCellRenderer)connectStatusTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        connectStatusTable.setRowHeight(connectStatusTable.getRowHeight() + VERT_PAD);
                     
        int idx = 0;

        // These are in sort order
        // Time
        sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
        // Description - sorting on description is not really useful.  If you want it someday, it's easily re-added.
//      sortList.add(new RowSorter.SortKey(idx++, SortOrder.ASCENDING));
        trs.setSortKeys(sortList);
        resizeColumnWidth(connectStatusTable);
    }
    
    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
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

        getStartStopButton().setEnabled(true);
        getUpdateButton().setEnabled(false);

        if (currentlyShadowing) {
            shadowingStatusValueLabel.setText("ON");
            getStartStopButton().setText("Stop shadowing");
            getStartStopButton().setToolTipText("Stop the currently active shadowing of the remote CCS");
        } else {
            shadowingStatusValueLabel.setText("OFF");
            getStartStopButton().setText("Start shadowing");
            getStartStopButton().setToolTipText("Start shadowing the currently specified remote CCS");
        }
        
        updateShadowSettingsPane(currentlyShadowing);
        lastToken = contestInformation.getLastShadowEventID();
        lastEventTextfield.setText(lastToken);
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

    private JButton getCompareRunsButton() {
        if (compareRunsButton == null) {
        	compareRunsButton = new JButton("Compare Runs");
        	compareRunsButton.setMnemonic(KeyEvent.VK_R);
        	compareRunsButton.setToolTipText("Display run comparison results");
        	compareRunsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (shadowController==null) {
                        showErrorMessage("No shadow controller available; cannot show runs comparison", "Missing Controller"); 
                    } else if (!ShadowController.SHADOW_CONTROLLER_STATUS.SC_RUNNING.equals(shadowController.getStatus())) {
                        showErrorMessage("Cannot compare runs, shadow not running","Shadow not running");
                    } else {
                        JFrame shadowCompareRunsFrame = new ShadowCompareRunsFrame(shadowController);
                        shadowCompareRunsFrame.setSize(600,700);
                        shadowCompareRunsFrame.setLocationRelativeTo(null); // centers frame
                        shadowCompareRunsFrame.setTitle("Shadow Run Comparison");
                        shadowCompareRunsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        shadowCompareRunsFrame.setVisible(true);
                    }
                    
                }
            });

        }
        return compareRunsButton;
    }
    
    private JButton getCompareScoreboardsButton() {
        if (compareScoreboardsButton == null) {
            compareScoreboardsButton = new JButton("Compare Scoreboards");
            compareScoreboardsButton.setMnemonic(KeyEvent.VK_S);
            compareScoreboardsButton.setToolTipText("Display scoreboard comparison results");
            compareScoreboardsButton.addActionListener(new java.awt.event.ActionListener() {
        	    
                public void actionPerformed(java.awt.event.ActionEvent e) {

                	SwingUtilities.invokeLater(new Runnable() {
                		
                		public void run() {
                		
                            if (shadowController==null) {
                                showErrorMessage("No shadow controller available; cannot show scoreboard comparison", "Missing Controller"); 
                            } else if (!ShadowController.SHADOW_CONTROLLER_STATUS.SC_RUNNING.equals(shadowController.getStatus())) {
                                showErrorMessage("Cannot compare scoreboard, shadow not running","Shadow not running");
                            } else {
                                JFrame shadowCompareScoreboardFrame = new ShadowCompareScoreboardFrame(shadowController);
                                shadowCompareScoreboardFrame.setSize(600,700);
                                shadowCompareScoreboardFrame.setLocationRelativeTo(null); // centers frame
                                shadowCompareScoreboardFrame.setTitle("Shadow Scoreboard Comparison");
                                shadowCompareScoreboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                shadowCompareScoreboardFrame.setVisible(true);
                            }
                		}
                	}); 
                };

            });

        }
        return compareScoreboardsButton;
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
                                        addConnectTableEntry(ShadowStatus.SUCCESS, "Test connection to remote CCS");
                                    } else {
                                        showErrorMessage("Connection to remote CCS failed", "Connection failed");
                                        addConnectTableEntry(ShadowStatus.FAILURE, "Test connection to remote CCS");
                                    }
                                    
                                    // Try to get the remote API version
                                    String infoStr = getRemoteAPIVersionInfo(remoteURLString, remoteLogin, remotePW);
                                    // infoStr is supposed to be non-null all the time, but let's be sure
                                    if(infoStr != null && !infoStr.isEmpty()) {
                                        addConnectTableEntry(ShadowStatus.INFO, infoStr);
                                        getController().getLog().info("Shadow EventFeed: " + infoStr);
                                    }
                                } catch (Exception e) {
                                    showErrorMessage("Exception attempting to test connection to remote system:\n" + e, "Exception in connecting");
                                    getController().getLog().log(Log.SEVERE, "Exception attempting to test connection to remote system: " + e.getMessage(), e);
                                    
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

    /**
     * Returns a Map containing the key/value elements in the specified JSON string.
     * This method uses the Jackson {@link ObjectMapper} to perform the conversion from the JSON
     * string to a Map.  Note that the ObjectMapper recurses for nested JSON elements, returning
     * a appropriate Object in the Map under the corresponding key string.
     * 
     * @param jsonString a JSON string to be converted to a Map
     * @return a Map mapping the keys in the JSON string to corresponding values, or null if the input
     *          String is null or if an exception occurs while converting the JSON to a Map.
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> getMap(String jsonString) {
        
        if (jsonString == null){
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private void addConnectTableEntry(ShadowStatus stat, String msg)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = new Object[3];
                
                try {
                    GregorianCalendar cal = new GregorianCalendar();
                    
                    lastDateFormat.setCalendar(cal);
                    objects[0] = lastDateFormat.format(cal.getTime());
                } catch(Exception e) {
                    objects[0] = "Unknown";
                }
                objects[1] = stat;
                if(msg == null || msg.isEmpty()) {
                    objects[2] = "<Empty Message>";
                } else {
                    objects[2] = msg;
                }
                connectStatusTableModel.addRow(objects);
                resizeColumnWidth(connectStatusTable);
            }
        });
        
    }
    
    /*
     * IShadowMonitorStatus implementaiton
     */
    /**
     * {@inheritDoc}
     */
    public void updateShadowLastToken(String token)
    {
        // if the value supplied is valid and different from what we last saw,
        // then we update the text fields
        if(token != null) {
            if(lastToken == null || !token.equals(lastToken)) {
                // TODO: Do we want to save the token to a file here in case we crash?
                //       Currently, token is only saved when the shadow is "stopped"
                // TODO: Do we want to "InvokeLater" these (simple) updates to text fields?
                lastToken = token;
                lastEventTextfield.setText(lastToken);
                try {
                    GregorianCalendar cal = new GregorianCalendar();
                    
                    lastDateFormat.setCalendar(cal);
                    lastEventTimeTextField.setText(lastDateFormat.format(cal.getTime()));
                } catch(Exception e) {
                    // Just ignore any exception from date formatter
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateShadowNumberofRecords(int nRec)
    {
        // if the number of records is different from what we last display and it's valid
        // update the instrumentation.
        if(nRec != numRecord && nRec >= 0) {
            // TODO: Do we want to "InvokeLater" this (simple) update to a text field?
            numRecord = nRec;
            // Save to file? Send to server contestinfo?
            lastRecordTextfield.setText(String.valueOf(numRecord));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectFailed(String token)
    {
        if(token == null || token.isEmpty()) {
            addConnectTableEntry(ShadowStatus.FAILURE, "Connect");
        } else {
            addConnectTableEntry(ShadowStatus.FAILURE, "Connection starting at token " + token);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectSucceeded(String token)
    {
        if(token == null || token.isEmpty()) {
            addConnectTableEntry(ShadowStatus.SUCCESS, "Connected");
        } else {
            addConnectTableEntry(ShadowStatus.SUCCESS, "Connected starting at token " + token);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectClosed(String msg)
    {
        if(msg == null || msg.isEmpty()) {
            msg = "Connection closed";
        }
        
        // Save last token on disconnect
        if(lastToken != null && !lastToken.isEmpty()) {
            updateContestInformation();
            msg += " at token " + lastToken;
        }
        addConnectTableEntry(ShadowStatus.INFO, msg);
    }
    
    /**
     * {@inheritDoc}
     */
    public void errorDisconnect(String errMsg)
    {
        if(errMsg == null || errMsg.isEmpty()) {
            errMsg = "Unexpected disconnect";
        }
        addConnectTableEntry(ShadowStatus.FAILURE, errMsg);
        
        // Save last token on disconnect
        if(lastToken != null && !lastToken.isEmpty()) {
            updateContestInformation();
        }
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
    
    /**
     * Returns a String containing the URL of the remote version endpoint, or null if the string can not be formed
     * from the supplied remoteURLString
     * This endpoint is distinctly different from the /contest/xxxx endpoints in that it is the same for all contests
     * provided by that server; it is contest independent.  Therefore, it does not appear as a /contest/ endpoint but a
     * server endpoint.
     * eg: https://judge.gehack.nl/api/contests/bapc2022 -> https://judge.gehack.nl/api
     * @return a String with the remote URL Version API endpoint
     */
    private String getRemoteAPIVersionURLString(String remoteURLString)
    {
        String remoteAPIVersionURLString = null;
    
        // API (Version) endpoint is right before /contests/ in the URL, so find that, if it's there
        int iApi = remoteURLString.lastIndexOf("/contests/");
        if(iApi != -1) {
            // eg: https://judge.gehack.nl/api/contests/bapc2022 -> https://judge.gehack.nl/api
            remoteAPIVersionURLString = remoteURLString.substring(0, iApi);
            if(remoteAPIVersionURLString.isEmpty()) {
                remoteAPIVersionURLString = null;
            }
        }
        return(remoteAPIVersionURLString);
    }
    
    /**
     * Returns a new IRemoteContestAPIAdapter object suitable for connecting to the VERSION api endpoint of the remote CCS
     * This endpoint is distinctly different from the /contest/xxxx endpoints in that it is the same for all contests
     * provided by that server; it is contest independent.  Therefore, it does not appear as a /contest/ endpoint but a
     * server endpoint.
     * eg: https://judge.gehack.nl/api/contests/bapc2022 -> https://judge.gehack.nl/api
     * @return a IRemoteContestAPIAdapter object
     */
    private IRemoteContestAPIAdapter createRemoteContestVersionAPIAdapter(String remoteURLString, String login, String password) throws MalformedURLException {

        boolean useMockAdapter = StringUtilities.getBooleanValue(IniFile.getValue("shadow.usemockcontestadapter"), false);
        
        String remoteAPIVersionURLString = getRemoteAPIVersionURLString(remoteURLString);
        if(remoteAPIVersionURLString != null) {
            // If we have a valid URL to try, let's do it.
            if (useMockAdapter)
            {
                return new MockContestAPIAdapter(new URL(remoteAPIVersionURLString), login, password);
            } else {
                return new RemoteContestAPIAdapter(new URL(remoteAPIVersionURLString), login, password);
            }
        }
        return(null);
    }

    /**
     * Returns a String suitable for display in a log indicating the CLICS API version supported by the remote.
     * A valid string will always be returned as follows:
     * Can not form API Version URL from xxxxx - If the remote URL string does not contain a contest URL path, eg. /contests/
     * No API version available at  - If the remote did not return valid information for the version API
     * API Version: N/A - A response was returned by the remote, but it did not contain version information (version)
     * API Version: 2022-07 by XXXXX - A valid version and provider was returned by the remote
     * API Version: 2022-07 (unknown provider) - A valid version was returned by the remote, but no provider (name)
     * No version API endpoint available for contest URL: xxxxx - An exception occurred due to a bad URL supplied for the contest(s)
     * @return a non-null String representing log-ready version info of the remote API
     */
    private String getRemoteAPIVersionInfo(String remoteURLString, String login, String password)
    {
        String infoStr;
        
        try {
            // get the special API adapter for version info
            IRemoteContestAPIAdapter remoteAPI = createRemoteContestVersionAPIAdapter(remoteURLString, login, password);
            if(remoteAPI != null) {
                Map<String, Object> map = getMap(remoteAPI.getRemoteJSON(CCS_API_ENDPOINT));
                if(map != null) {
                    // ex. {"version":"2022-07","version_url":"https://ccs-specs.icpc.io/2022-07/contest_api","name":"domjudge"}
                    String verstr = (String)map.get("version");
                    String provider = (String)map.get("name");
                    
                    infoStr = "API Version: ";
                    
                    // Try to make an intelligent looking string if stuff is missing
                    if(verstr == null || verstr.isEmpty()) {
                        infoStr += "N/A";
                    } else {
                        infoStr += verstr;
                    }
                    if(provider != null && !provider.isEmpty()) {
                        infoStr += " by " + provider;
                    } else {
                        infoStr += " (unknown provider)";
                    }
                } else {
                    // getRemoteAPIVersionURLString will always return non-null here for those wondering, or remoteAPI would be null!
                    infoStr = "No API version available at " + getRemoteAPIVersionURLString(remoteURLString);                                                   
                }
            } else {
                infoStr = "Can not form API Version URL from " + remoteURLString;
            }
        } catch(Exception e) {
            infoStr = "No version API endpoint available for contest URL: " + remoteURLString;
        }
        return(infoStr);
    }
}
