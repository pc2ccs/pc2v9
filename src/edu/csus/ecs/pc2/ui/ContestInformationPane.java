// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.CommandVariableReplacer;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Contest Information edit/update Pane.
 * 
 * This pane displays and allows updating of Contest Information Settings.
 *   The pane uses a vertical BoxLayout to display a collection of settings sub-panes.  Each settings sub-pane is a singleton 
 *   which is constructed by a getter method.  Each getter returns a self-contained pane (including that each returned pane
 *   has a layout manager controlling how things are laid out within that pane and also has size and alignment
 *   constraints defining how the components within that pane are managed by the layout manager for the pane).
 *   Each sub-pane also has a CompoundBorder consisting of a {@link TitledBorder} compounded with a "margin border"
 *   (an {@link EmptyBorder} with {@link Insets}); this provides an offset for each sub-pane within the outer pane.
 *   
 *   Method {@link #initialize()}, which is invoked whenever this ContestInformationPane is instantiated, adds two 
 *   components to *this* pane:  a {@link JScrollPane} containing a "center pane"
 *   (returned by {@link #getCenterPane()}), plus a button bar.  The sub-panes displaying the Contest Information
 *   Settings are added to the center pane (within the scroll pane) in the method {@link #getCenterPane()}.
 *   
 *   Method {@link #initialize()} also invokes method {@link #populateGUI()}, which in turn invokes 
 *   {@link IInternalController.getContest().getContestInformation()} to obtain the current contest information settings from
 *   the server; it then uses the returned values to initialize the GUI display settings.
 *   
 *   Each active component in the pane (including its sub-panes) has a listener attached to it.  Whenever a component 
 *   is changed (key typed/release, checkbox checked, button pushed, etc.) it invokes method {@link #enableUpdateButton()}.
 *   This method (despite its name) doesn't actually necessarily *enable* the Update button; rather, it invokes {@link #getFromFields()}
 *   to obtain the data currently displayed in the GUI fields and compares it with the current contest information settings. 
 *   If they DIFFER then the Update and Cancel buttons are enabled. Subsequently pressing the Update button invokes 
 *   {@link #updateContestInformation()}, which (again) invokes {@link #getFromFields()} to fetch the GUI settings and then 
 *   invokes {@link IInternalController.updateContestInformation(contestInformation)} to save the new GUI information in the
 *   local controller (which presumably responds by saving it on the server).
 *    
 * Developer's Notes:
 * 
 *   To add a new sub-pane to this ContestInformationPane, define a getter method (e.g. <code>getNewPane()</code>)
 *   which returns the new pane as an instance of {@link JPanel}, and add a call <code>centerPane.add(getNewPane())</code>
 *   in method {@link #getCenterPane()}.
 *   
 *   To add a new {@link JComponent} to an *existing* sub-pane, first create an accessor which creates the new component
 *   (for example, <code>getNewComponent()</code>), then go to the getter method for the sub-pane to which the new component 
 *   is to be added (for example, {@link #getCCSTestModePane()}) and add to the body of that 
 *   method an "add" statement which calls the new getter  (for example, in the body of {@link #getCCSTestModePane()} you might add 
 *   <code>ccsTestModePane.add(getNewComponent()</code>).  Note that the new component could be either an individual component
 *   (such as a JLabel or JCheckBox) or a {@link JPanel} which itself contains sub-components.
 *   
 *   Note that you may (probably will) have to adjust the maximum, minimum, and preferred sizes of the pane to which the
 *   new component is being added in order to accommodate the new component in the layout.  Note also that you must include
 *   the necessary size and alignment attributes in any new component being added.
 *   
 *   Note also that if you add new information to the GUI, you must update {@link #getFromFields()} to fetch the new information from
 *   the GUI fields and save it, and you must update method  {@link ContestInformation#isSameAs(ContestInformation)} to include
 *   a check of the new information.
 *   
 * 
 * @author pc2@ecs.csus.edu
 */
public class ContestInformationPane extends JPanePlugin {

    private static final long serialVersionUID = -8408469113380938482L;

    private JPanel buttonPanel = null;

    private JPanel centerPane = null;

    private JButton updateButton = null;

    private JTextField contestTitleTextField = null;

    private JPanel teamInformationDisplaySettingsPane = null;

    private JRadioButton displayNoneRadioButton = null;

    private JRadioButton displayNumbersOnlyRadioButton = null;

    private JRadioButton displayNameAndNumberRadioButton = null;

    private JRadioButton displayAliasNameRadioButton = null;

    private JRadioButton displayNamesOnlyRadioButton = null;

    private ButtonGroup displayNameButtonGroup = null; // @jve:decl-index=0:visual-constraint="617,62"

    private JButton cancelButton = null;

    private JTextField judgesDefaultAnswerTextField = null;

    private JCheckBox jCheckBoxShowPreliminaryOnBoard = null;

    private JCheckBox jCheckBoxShowPreliminaryOnNotifications = null;

    private JCheckBox additionalRunStatusCheckBox = null;

    private ContestInformation savedContestInformation = null; // @jve:decl-index=0:

    private JLabel labelMaxOutputSize = null;

    private JTextField textfieldMaxOutputSizeInK = null;

    private JButton scoringPropertiesButton = null;

    private Properties savedScoringProperties = null; // @jve:decl-index=0:

    private Properties changedScoringProperties = null;

    private JCheckBox ccsTestModeCheckbox = null;

    private JTextField runSubmissionInterfaceCommandTextField = null;

    private JLabel runSubmissionInterfaceLabel = null;
    
    private JTextField startTimeTextField;

    private JLabel startTimeLabel;
    private JTextField contestFreezeLengthTextField;

    private JButton unfreezeScoreboardButton;

    private ShadowSettingsPane shadowSettingsPane;

    private Border lineBorderBlue2px = new LineBorder(Color.blue, 2, true) ; //blue, 2-pixel line, rounded corners
    private Border margin = new EmptyBorder(5,10,5,10); //top,left,bottom,right

    private JPanel judgeSettingsPane;

    private JPanel contestSettingsPane;

    private JPanel judgesDefaultAnswerPane;

    private JPanel judgingOptionsPane;

    private JPanel teamSettingsPane;

    private JLabel contestFreezeLengthLabel;

    private JPanel scheduledStartTimePane;

    private JPanel scoreboardFreezePane;

    private boolean scoreboardHasBeenUnfrozen = false;

    private JPanel remoteCCSSettingsPane;
    private Component horizontalStrut_2;

    private JPanel ccsTestModePane;
    private Component horizontalStrut_3;

    //set this true to display outlines around Contest Information Pane sections
    private boolean showPaneOutlines = true;

    private JPanel contestTitlePane;

    private JLabel contestTitleLabel;

    private JPanel runSubmissionCommandPane;
    private Component horizontalStrut;
    private Component horizontalStrut_1;
    private Component horizontalStrut_4;
    private Component horizontalStrut_5;

    private JTextField primaryCCSURLTextfield;

    private JTextField primaryCCSLoginTextfield;

    private JTextField primaryCCSPasswdTextfield;

    private JCheckBox shadowModeCheckbox;

    private JCheckBox allowMultipleTeamLoginsCheckbox;
    private boolean allowMultipleLoginsPerTeam = false;

    private Component rigidArea1;
    
//    private JTextField textfieldPrimaryCCSURL;
//
//    private JTextField textfieldPrimaryCCSLogin;
//
//    private JTextField textfieldPrimaryCCSPasswd;
    

    /**
     * This method initializes this Contest Information Pane
     * 
     */
    public ContestInformationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(900, 700));
        
        //put the center pane in a scrollpane so the user can access it without expanding the window
        JScrollPane sp = new JScrollPane(getCenterPane());
        this.add(sp,BorderLayout.CENTER);
        
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getUpdateButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes centerPane - the central pane containing the Contest Information Settings
     * and control components.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
                        
            centerPane = new JPanel();
            centerPane.setToolTipText("");

            centerPane.setLayout(new BoxLayout(centerPane,BoxLayout.Y_AXIS));
            
            //contents of the pane:
            
            centerPane.add(Box.createVerticalStrut(15));

            centerPane.add(getContestSettingsPane()) ;
            centerPane.add(Box.createVerticalStrut(15));
            
            centerPane.add(getJudgingSettingsPane(),null);
            centerPane.add(Box.createVerticalStrut(15));
           
            centerPane.add(getTeamSettingsPane());
            centerPane.add(Box.createVerticalStrut(15));
            
            centerPane.add(getRemoteCCSSettingsPane());
            centerPane.add(Box.createVerticalStrut(15));
            
        }
        return centerPane;
    }

    private Component getRemoteCCSSettingsPane() {
        if (remoteCCSSettingsPane == null) {
            
            remoteCCSSettingsPane = new JPanel();
            remoteCCSSettingsPane.setAlignmentX(LEFT_ALIGNMENT);
            remoteCCSSettingsPane.setMaximumSize(new Dimension(900, 250));
            remoteCCSSettingsPane.setMinimumSize(new Dimension(900, 250));
            remoteCCSSettingsPane.setPreferredSize(new Dimension(900,250));
           
            
            if (showPaneOutlines) {
                
                TitledBorder titleBorder = new TitledBorder("Remote CCS Settings ");
                titleBorder.setBorder(lineBorderBlue2px);

                remoteCCSSettingsPane.setBorder(new CompoundBorder(margin,titleBorder));
                
            } else {
                remoteCCSSettingsPane.setBorder(new EmptyBorder(2,2,2,2));
            }
            
            remoteCCSSettingsPane.setLayout(new BoxLayout(remoteCCSSettingsPane, BoxLayout.Y_AXIS));

            //the contents of the pane:
            
            remoteCCSSettingsPane.add(Box.createVerticalStrut(15));
            
            remoteCCSSettingsPane.add(getCCSTestModePane(),JComponent.LEFT_ALIGNMENT);
            remoteCCSSettingsPane.add(Box.createVerticalStrut(15));
            
            remoteCCSSettingsPane.add(getShadowSettingsPane(),JComponent.LEFT_ALIGNMENT);

        }
        return remoteCCSSettingsPane;
        
    }

    private JPanel getCCSTestModePane() {
        if (ccsTestModePane == null) {
            
            ccsTestModePane = new JPanel();

            ccsTestModePane.setLayout(new FlowLayout(FlowLayout.LEFT));
            ccsTestModePane.setPreferredSize(new Dimension(700, 80));
            ccsTestModePane.setMaximumSize(new Dimension(700, 80));
            ccsTestModePane.setMinimumSize(new Dimension(700, 80));
            
            TitledBorder tb = BorderFactory.createTitledBorder("CCS Test Mode");
            ccsTestModePane.setBorder(new CompoundBorder(margin,tb));
            ccsTestModePane.setAlignmentX(LEFT_ALIGNMENT); 
            
            //the contents of the pane:
            
            ccsTestModePane.add(getCcsTestModeCheckbox(), null);
            ccsTestModePane.add(getHorizontalStrut_2());
            
            ccsTestModePane.add(getRunSubmissionCommandPane(),null);
            
        }
        return ccsTestModePane;
        
    }


    private JPanel getRunSubmissionCommandPane() {
        if (runSubmissionCommandPane == null) {
            runSubmissionCommandPane = new JPanel();
            runSubmissionCommandPane.setMaximumSize(new Dimension(500, 20));
            
            runSubmissionInterfaceLabel = new JLabel();
            runSubmissionInterfaceLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
            runSubmissionInterfaceLabel.setText("Run Submission Command:  ");
            runSubmissionInterfaceLabel.setToolTipText("The command used to submit to a remote CCS");
            runSubmissionInterfaceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            
            //the contents of the pane:
            
            runSubmissionCommandPane.add(runSubmissionInterfaceLabel, null);
            runSubmissionCommandPane.add(getRunSubmissionInterfaceCommandTextField(), null);

        }
        return runSubmissionCommandPane;
    }

    private Component getScoreboardFreezePane() {
        if (scoreboardFreezePane == null) {
            
            scoreboardFreezePane = new JPanel();
            
            scoreboardFreezePane.add(getContestFreezeLengthLabel(),null);
            scoreboardFreezePane.add(getContestFreezeLengthtextField());

        }
        return scoreboardFreezePane;
    }

    private Component getScheduledStartTimePane() {
        if (scheduledStartTimePane == null) {
            scheduledStartTimePane = new JPanel();
            scheduledStartTimePane.add(getStartTimeLabel(), null);
            scheduledStartTimePane.add(getStartTimeTextField(), null);
        }
        return scheduledStartTimePane;
    }

    private JLabel getContestFreezeLengthLabel() {
        if (contestFreezeLengthLabel == null) {
            
            contestFreezeLengthLabel = new JLabel();
            contestFreezeLengthLabel.setText("Scoreboard Freeze Length (hh:mm:ss) ");
            contestFreezeLengthLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
            contestFreezeLengthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return contestFreezeLengthLabel;
    }

    private Component getContestSettingsPane() {
        if (contestSettingsPane == null) {
            contestSettingsPane = new JPanel();
            contestSettingsPane.setLayout(new FlowLayout(FlowLayout.LEFT));
            contestSettingsPane.setMinimumSize(new Dimension(700, 120));
            contestSettingsPane.setMaximumSize(new Dimension(700, 120));
            contestSettingsPane.setPreferredSize(new Dimension(700,120));
            contestSettingsPane.setAlignmentX(LEFT_ALIGNMENT);

            if (showPaneOutlines) {
                
                TitledBorder titleBorder = new TitledBorder("Contest Settings");
                titleBorder.setBorder(lineBorderBlue2px);
                
                contestSettingsPane.setBorder(new CompoundBorder(margin,titleBorder));
                
            } else {
                contestSettingsPane.setBorder(new EmptyBorder(2, 2, 2, 2));
            }

            // contents of the pane:

            contestSettingsPane.add(getContestTitlePane(), null);

            contestSettingsPane.add(getScheduledStartTimePane());

            contestSettingsPane.add(getScoreboardFreezePane());
            contestSettingsPane.add(getHorizontalStrut_3());

            contestSettingsPane.add(getUnfreezeScoreboardButton());

        }
        return contestSettingsPane;
    }

    private JPanel getContestTitlePane() {
        if (contestTitlePane == null) {
            
            contestTitlePane = new JPanel();
            
            contestTitlePane.add(getContestTitleLabel());
            contestTitlePane.add(getContestTitleTextField(), null);

        }
        return contestTitlePane;
    }

    private JLabel getContestTitleLabel() {
        
        if (contestTitleLabel == null) {
            
            contestTitleLabel = new JLabel("Contest title: ");
        }
        return contestTitleLabel;
    }

    /**
     * This method returns a JPanel containing the Contest Information settings
     * related to judging.
     * @return a JPanel
     */
    private JPanel getJudgingSettingsPane() {
        if (judgeSettingsPane == null) {
            
            judgeSettingsPane = new JPanel();
            
            judgeSettingsPane.setAlignmentX(LEFT_ALIGNMENT);
            judgeSettingsPane.setMaximumSize(new Dimension(750, 250));
            judgeSettingsPane.setMinimumSize(new Dimension(750, 250));
            judgeSettingsPane.setPreferredSize(new Dimension(750,250));

            if (showPaneOutlines) {
                
                TitledBorder titleBorder = new TitledBorder("Judging Settings");
                titleBorder.setBorder(lineBorderBlue2px);
                
                judgeSettingsPane.setBorder(new CompoundBorder(margin,titleBorder));
            } else {
                judgeSettingsPane.setBorder(new EmptyBorder(2,2,2,2));
            }
            
            judgeSettingsPane.setLayout(new FlowLayout((FlowLayout.LEFT)));
            
            //the contents of the pane:
            
            judgeSettingsPane.add(Box.createVerticalStrut(15));

            judgeSettingsPane.add(getTeamInformationDisplaySettingsPane(), LEFT_ALIGNMENT);
            
            judgeSettingsPane.add(getJudgesDefaultAnswerPane(),LEFT_ALIGNMENT);
            
            judgeSettingsPane.add(getJudgingOptionsPane(),LEFT_ALIGNMENT);
            
            judgeSettingsPane.add(Box.createHorizontalStrut(20));
            judgeSettingsPane.add(getScoringPropertiesButton(),LEFT_ALIGNMENT);

        }
        return judgeSettingsPane;
    }

    private Component getTeamSettingsPane() {
        if (teamSettingsPane == null ) {
            
            teamSettingsPane = new JPanel();
            teamSettingsPane.setMaximumSize(new Dimension(500, 100));
            teamSettingsPane.setPreferredSize(new Dimension(500,100));
            teamSettingsPane.setAlignmentX(LEFT_ALIGNMENT); 

            if (showPaneOutlines) {
                
                TitledBorder titleBorder = new TitledBorder("Team Settings");
                titleBorder.setBorder(lineBorderBlue2px);

                teamSettingsPane.setBorder(new CompoundBorder(margin,titleBorder));

            } else {
                teamSettingsPane.setBorder(new EmptyBorder(2,2,2,2));
            }
            
            teamSettingsPane.setLayout(new FlowLayout(FlowLayout.LEFT));

            //contents of the pane:
             
            teamSettingsPane.add(getMaxOutputSizeLabel(), null);
            teamSettingsPane.add(getMaxOutputSizeInKTextField(), null);
            teamSettingsPane.add(getRigidArea1());
            teamSettingsPane.add(getAllowMultipleTeamLoginsCheckbox(), null);
        }
        return teamSettingsPane;
    }

    private JLabel getMaxOutputSizeLabel() {
       if (labelMaxOutputSize == null) {
           
           labelMaxOutputSize = new JLabel();
           labelMaxOutputSize.setHorizontalAlignment(SwingConstants.RIGHT);
           labelMaxOutputSize.setBorder(new EmptyBorder(0,10,5,5));
           labelMaxOutputSize.setText("Maximum output size (in KB): ");
       }
        return labelMaxOutputSize ;
    }

    /**
     * This method initializes teamDisplaySettingPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamInformationDisplaySettingsPane() {
        if (teamInformationDisplaySettingsPane == null) {
            
            teamInformationDisplaySettingsPane = new JPanel();
            teamInformationDisplaySettingsPane.setMaximumSize(new Dimension(700, 200));
            teamInformationDisplaySettingsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            teamInformationDisplaySettingsPane.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            teamInformationDisplaySettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, 
                    "Team Information Displayed to Judges", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            
            //contents of pane:
            teamInformationDisplaySettingsPane.add(getDisplayNoneRadioButton(), null);
            teamInformationDisplaySettingsPane.add(getHorizontalStrut());
            teamInformationDisplaySettingsPane.add(getDisplayNumbersOnlyRadioButton(), null);
            teamInformationDisplaySettingsPane.add(getHorizontalStrut_1());
            teamInformationDisplaySettingsPane.add(getDisplayNamesOnlyRadioButton(), null);
            teamInformationDisplaySettingsPane.add(getHorizontalStrut_4());
            teamInformationDisplaySettingsPane.add(getDisplayNameAndNumberRadioButton(), null);
            teamInformationDisplaySettingsPane.add(getHorizontalStrut_5());
            teamInformationDisplaySettingsPane.add(getDisplayAliasNameRadioButton(), null);

        }
        return teamInformationDisplaySettingsPane;
    }

    private JPanel getJudgingOptionsPane() {
        if (judgingOptionsPane == null) {
            
            judgingOptionsPane = new JPanel();
            
            judgingOptionsPane.setLayout(new BoxLayout(judgingOptionsPane,BoxLayout.Y_AXIS));

            judgingOptionsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Judging Options", 
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            judgingOptionsPane.add(getJCheckBoxShowPreliminaryOnBoard(), LEFT_ALIGNMENT);
            judgingOptionsPane.add(getJCheckBoxShowPreliminaryOnNotifications(), LEFT_ALIGNMENT);
            judgingOptionsPane.add(getAdditionalRunStatusCheckBox(), LEFT_ALIGNMENT);

        }
        return judgingOptionsPane;
    }

    private JPanel getJudgesDefaultAnswerPane() {
        if (judgesDefaultAnswerPane == null) {
            
            judgesDefaultAnswerPane = new JPanel();
            judgesDefaultAnswerPane.setMaximumSize(new Dimension(500, 200));
            judgesDefaultAnswerPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            judgesDefaultAnswerPane.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            judgesDefaultAnswerPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Judge's Default Answer", 
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

            //the contents of the pane:
            
            judgesDefaultAnswerPane.add(getJudgesDefaultAnswerTextField(), null);

        }
        return judgesDefaultAnswerPane;
    }

    /**
     * Returns a ShadowSettingsPane containing the components comprising Shadow Mode configuration options.
     * Because the ShadowSettingsPane class does not add any listeners to its components (because it can't
     * know what listeners are desired by the surrounding class), this method adds a KeyListener to each
     * {@link JTextField} component on the ShadowSettingsPane, and adds an ActionListener to the Shadow Mode
     * checkbox on the ShadowSettingsPane.   All of these listeners do the same (one) thing: invoke
     * {@link #enableUpdateButton()}.
     * 
     * @return a ShadowSettingsPane containing Shadow Mode Settings components with listeners attached to them
     */
    private ShadowSettingsPane getShadowSettingsPane() {
        if (shadowSettingsPane == null) {
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



    private JButton getUnfreezeScoreboardButton() {
        if (unfreezeScoreboardButton == null) {
            
            unfreezeScoreboardButton = new JButton("Unfreeze Scoreboard");
            unfreezeScoreboardButton.setToolTipText("Unfreezing means the final results can be released to the public via the Contest API and public html");
            unfreezeScoreboardButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String message = "Unfreezing the scoreboard is permanent (cannot be undone);"
                            + "\nunfreezing means the final results are released for public viewing."
                            + "\n\nIf you are SURE you want to do this, click 'OK' then click 'Update'.";
                    int result = JOptionPane.showConfirmDialog(null, message, "Unfreezing Is Permanent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        scoreboardHasBeenUnfrozen = true;
                        enableUpdateButton();
                    }
                }
            });
        }
        return unfreezeScoreboardButton;
    }

    private JTextField getContestFreezeLengthtextField() {
        if (contestFreezeLengthTextField == null) {
            contestFreezeLengthTextField = new JTextField(8);
            contestFreezeLengthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return contestFreezeLengthTextField;
    }

    private JTextField getStartTimeTextField() {
        if (startTimeTextField == null) {
            startTimeTextField = new JTextField();
            startTimeTextField.setColumns(25);
            startTimeTextField.setEditable(false);
            startTimeTextField.setToolTipText("Use Contest Times tab \"Edit Start Schedule\" button to edit Start Time");
        }
        return startTimeTextField ;
    }

    private JLabel getStartTimeLabel() {
        if (startTimeLabel == null) {
            startTimeLabel = new JLabel("Scheduled Start Time:  ");
            startTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return startTimeLabel ;
    }

    private JCheckBox getShadowModeCheckbox() {
        
        if (shadowModeCheckbox==null) {
            shadowModeCheckbox = getShadowSettingsPane().getShadowModeCheckbox();
        }
        return shadowModeCheckbox;
    }

    private JTextField getPrimaryCCSURLTextfield() {
        
        if (primaryCCSURLTextfield==null) {
            primaryCCSURLTextfield = getShadowSettingsPane().getRemoteCCSURLTextfield() ;
        }
        return primaryCCSURLTextfield; 
    }

    private JTextField getPrimaryCCSLoginTextfield() {

        if (primaryCCSLoginTextfield == null) {
            primaryCCSLoginTextfield = getShadowSettingsPane().getRemoteCCSLoginTextfield();
        }
        return primaryCCSLoginTextfield;
    }

    private JTextField getPrimaryCCSPasswdTextfield() {
        
        if (primaryCCSPasswdTextfield==null) {
            primaryCCSPasswdTextfield = getShadowSettingsPane().getRemoteCCSPasswdTextfield() ;
        }
        return primaryCCSPasswdTextfield ;
    }


    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setToolTipText("Save settings");
            updateButton.setPreferredSize(new java.awt.Dimension(100, 26));
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                     updateContestInformation();
                }
            });
        }
        return updateButton;
    }

    /**
     * This method initializes contestTitleTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getContestTitleTextField() {
        if (contestTitleTextField == null) {
            contestTitleTextField = new JTextField(0); //'0' causes textfield to resize based on its data
            contestTitleTextField.setAlignmentX(LEFT_ALIGNMENT);
            contestTitleTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return contestTitleTextField;
    }

    @Override
    public String getPluginTitle() {
        return "Contest Information Pane";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        savedContestInformation = getContest().getContestInformation();
        populateGUI();

        setContestInformation(savedContestInformation);

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    /**
     * Returns a new ContestInformation object containing data fetched from this pane's fields.
     * @return a ContestInformation object
     */
    protected ContestInformation getFromFields() {
        ContestInformation newContestInformation = new ContestInformation();
        ContestInformation currentContestInformation = getContest().getContestInformation();
        
        //fill in the Contest URL
        if (currentContestInformation.getContestURL() != null) {
            newContestInformation.setContestURL(new String(currentContestInformation.getContestURL()));
        }
        
        //fill in the Contest Title
        newContestInformation.setContestTitle(getContestTitleTextField().getText());
        
        //fill in the Team Display mode
        if (getDisplayNoneRadioButton().isSelected()) {
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.NONE);
        } else if (getDisplayNameAndNumberRadioButton().isSelected()) {
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.NUMBERS_AND_NAME);
        } else if (getDisplayNumbersOnlyRadioButton().isSelected()) {
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);
        } else if (getDisplayNamesOnlyRadioButton().isSelected()) {
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.DISPLAY_NAME_ONLY);
        } else if (getDisplayAliasNameRadioButton().isSelected()) {
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.ALIAS);
        } else {
            // DEFAULT
            newContestInformation.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);
        }
        
        //fill in judging information
        newContestInformation.setJudgesDefaultAnswer(getJudgesDefaultAnswerTextField().getText());
        newContestInformation.setPreliminaryJudgementsTriggerNotifications(getJCheckBoxShowPreliminaryOnNotifications().isSelected());
        newContestInformation.setPreliminaryJudgementsUsedByBoard(getJCheckBoxShowPreliminaryOnBoard().isSelected());
        newContestInformation.setSendAdditionalRunStatusInformation(getAdditionalRunStatusCheckBox().isSelected());
        
        //fill in older Run Submission Interface (RSI) data
        newContestInformation.setRsiCommand(getRunSubmissionInterfaceCommandTextField().getText());
        newContestInformation.setCcsTestMode(getCcsTestModeCheckbox().isSelected());
        
        //fill in Shadow Mode information
        newContestInformation.setShadowMode(getShadowSettingsPane().getShadowModeCheckbox().isSelected());
        newContestInformation.setPrimaryCCS_URL(getShadowSettingsPane().getRemoteCCSURLTextfield().getText());
        newContestInformation.setPrimaryCCS_user_login(getShadowSettingsPane().getRemoteCCSLoginTextfield().getText());
        newContestInformation.setPrimaryCCS_user_pw(getShadowSettingsPane().getRemoteCCSPasswdTextfield().getText());
        
        //fill in additional field values
        String maxFileSizeString = "0" + getMaxOutputSizeInKTextField().getText();
        long maximumFileSize = Long.parseLong(maxFileSizeString);
        newContestInformation.setMaxFileSize(maximumFileSize * 1000);
        newContestInformation.setAllowMultipleLoginsPerTeam(allowMultipleLoginsPerTeam);

        //fill in values already saved, if any
        if (savedContestInformation != null) {
            newContestInformation.setJudgementNotificationsList(savedContestInformation.getJudgementNotificationsList());
                
            newContestInformation.setJudgeCDPBasePath(savedContestInformation.getJudgeCDPBasePath());
            newContestInformation.setScheduledStartDate(savedContestInformation.getScheduledStartDate());
            
            newContestInformation.setAdminCDPBasePath(savedContestInformation.getAdminCDPBasePath());
            newContestInformation.setContestShortName(savedContestInformation.getContestShortName());
            newContestInformation.setExternalYamlPath(savedContestInformation.getExternalYamlPath());
            
            newContestInformation.setFreezeTime(savedContestInformation.getFreezeTime());
            newContestInformation.setLastRunNumberSubmitted(savedContestInformation.getLastRunNumberSubmitted());
            newContestInformation.setAutoStartContest(savedContestInformation.isAutoStartContest());
        }

        newContestInformation.setScoringProperties(changedScoringProperties);
        
        newContestInformation.setFreezeTime(contestFreezeLengthTextField.getText());

        newContestInformation.setThawed(scoreboardHasBeenUnfrozen);
        
        return (newContestInformation);
    }

    protected void dumpProperties(String comment, Properties properties) {

        System.out.println("  Properties " + comment + " " + properties);
        if (properties == null) {
            return;
        }

        Set<Object> set = properties.keySet();

        String[] keys = (String[]) set.toArray(new String[set.size()]);

        Arrays.sort(keys);

        for (String key : keys) {
            System.out.println("     " + key + "='" + properties.get(key) + "'");
        }
    }

    protected void enableUpdateButton() {
        ContestInformation newChoice = getFromFields();

        if (getContest().getContestInformation().isSameAs(newChoice)) {
            setEnableButtons(false);
        } else {
            setEnableButtons(true);
        }
    }

    void setEnableButtons(boolean isEnabled) {
        getUpdateButton().setEnabled(isEnabled);
        getCancelButton().setEnabled(isEnabled);
    }

    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContestInformation contestInformation = getContest().getContestInformation();
                
                getContestTitleTextField().setText(contestInformation.getContestTitle());
                selectDisplayRadioButton();
                
                getJudgesDefaultAnswerTextField().setText(contestInformation.getJudgesDefaultAnswer());
                getJCheckBoxShowPreliminaryOnBoard().setSelected(contestInformation.isPreliminaryJudgementsUsedByBoard());
                getJCheckBoxShowPreliminaryOnNotifications().setSelected(contestInformation.isPreliminaryJudgementsTriggerNotifications());
                getAdditionalRunStatusCheckBox().setSelected(contestInformation.isSendAdditionalRunStatusInformation());
                
                getMaxOutputSizeInKTextField().setText((contestInformation.getMaxFileSize() / 1000) + "");
                getAllowMultipleTeamLoginsCheckbox().setSelected(contestInformation.isAllowMultipleLoginsPerTeam());
                getContestFreezeLengthtextField().setText(contestInformation.getFreezeTime());
                
                getCcsTestModeCheckbox().setSelected(contestInformation.isCcsTestMode());
                getRunSubmissionInterfaceCommandTextField().setText(contestInformation.getRsiCommand());
                if (contestInformation.getRsiCommand() == null || "".equals(contestInformation.getRsiCommand().trim())) {
                    String cmd = "# /usr/local/bin/sccsrs " + CommandVariableReplacer.OPTIONS + " " + CommandVariableReplacer.FILELIST;
                    getRunSubmissionInterfaceCommandTextField().setText(cmd);
                }
                
                getShadowModeCheckbox().setSelected(contestInformation.isShadowMode());

                getPrimaryCCSURLTextfield().setText(contestInformation.getPrimaryCCS_URL());
                getPrimaryCCSLoginTextfield().setText(contestInformation.getPrimaryCCS_user_login());
                getPrimaryCCSPasswdTextfield().setText(contestInformation.getPrimaryCCS_user_pw());
                
                //add the scheduled start time to the GUI
                GregorianCalendar cal = contestInformation.getScheduledStartTime();
                getStartTimeTextField().setText(getScheduledStartTimeStr(cal));   

                getUnfreezeScoreboardButton().setSelected(contestInformation.isUnfrozen());
                setContestInformation(contestInformation);
                setEnableButtons(false);
            }

        });

    }
    
    /**
     * Convert a GregorianCalendar date/time to a displayable string in yyyy-mm-dd:hh:mm form.
     */
    private String getScheduledStartTimeStr(GregorianCalendar cal) {
        
        String retString = "<undefined>";
        if (cal != null) {
            //extract fields from input and build string
            //TODO:  need to deal with the difference between displaying LOCAL time and storing UTC

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            fmt.setCalendar(cal);
            retString = fmt.format(cal.getTime());

            }

        return retString;
    }


    private void updateContestInformation() {
        ContestInformation contestInformation = getFromFields();
                
        getController().updateContestInformation(contestInformation);
    }


    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
             populateGUI();
            savedContestInformation = event.getContestInformation();
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            populateGUI();
            savedContestInformation = event.getContestInformation();

        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            populateGUI();
            savedContestInformation = getContest().getContestInformation();
            
        }
        
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }

    }


    private void selectDisplayRadioButton() {

        ContestInformation contestInformation = getContest().getContestInformation();
        if (contestInformation == null || contestInformation.getTeamDisplayMode() == null) {
            getDisplayNameButtonGroup().setSelected(getDisplayNamesOnlyRadioButton().getModel(), true);

        } else {
            switch (contestInformation.getTeamDisplayMode()) {
                case DISPLAY_NAME_ONLY:
                    getDisplayNameButtonGroup().setSelected(getDisplayNamesOnlyRadioButton().getModel(), true);
                    break;
                case LOGIN_NAME_ONLY:
                    getDisplayNameButtonGroup().setSelected(getDisplayNumbersOnlyRadioButton().getModel(), true);
                    break;
                case NUMBERS_AND_NAME:
                    getDisplayNameButtonGroup().setSelected(getDisplayNameAndNumberRadioButton().getModel(), true);
                    break;
                case ALIAS:
                    getDisplayNameButtonGroup().setSelected(getDisplayAliasNameRadioButton().getModel(), true);
                    break;
                case NONE:
                    getDisplayNameButtonGroup().setSelected(getDisplayNoneRadioButton().getModel(), true);
                    break;
                default:
                    break;
            }
        }
        // getDisplayNameButtonGroup().setSelected(getDisplayNoneButton(), false)

    }

    /**
     * This method initializes displayNoneButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNoneRadioButton() {
        if (displayNoneRadioButton == null) {
            displayNoneRadioButton = new JRadioButton();
            displayNoneRadioButton.setText("None");
            displayNoneRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNoneRadioButton;
    }

    /**
     * This method initializes displayNumbersOnlyRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNumbersOnlyRadioButton() {
        if (displayNumbersOnlyRadioButton == null) {
            displayNumbersOnlyRadioButton = new JRadioButton();
            displayNumbersOnlyRadioButton.setText("Show Numbers Only");
            displayNumbersOnlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNumbersOnlyRadioButton;
    }

    /**
     * This method initializes displayNameAndNumberRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNameAndNumberRadioButton() {
        if (displayNameAndNumberRadioButton == null) {
            displayNameAndNumberRadioButton = new JRadioButton();
            displayNameAndNumberRadioButton.setText("Show Number and Name");
            displayNameAndNumberRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNameAndNumberRadioButton;
    }

    /**
     * This method initializes displayAliasNameRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayAliasNameRadioButton() {
        if (displayAliasNameRadioButton == null) {
            displayAliasNameRadioButton = new JRadioButton();
            displayAliasNameRadioButton.setText("Show Alias");
            displayAliasNameRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayAliasNameRadioButton;
    }

    /**
     * This method initializes showNamesOnlyRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNamesOnlyRadioButton() {
        if (displayNamesOnlyRadioButton == null) {
            displayNamesOnlyRadioButton = new JRadioButton();
            displayNamesOnlyRadioButton.setText("Show Names only");
            displayNamesOnlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNamesOnlyRadioButton;
    }

    /**
     * This method initializes displayNameButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getDisplayNameButtonGroup() {
        if (displayNameButtonGroup == null) {
            displayNameButtonGroup = new ButtonGroup();
            displayNameButtonGroup.add(getDisplayNoneRadioButton());

            displayNameButtonGroup.add(getDisplayNamesOnlyRadioButton());
            displayNameButtonGroup.add(getDisplayNameAndNumberRadioButton());
            displayNameButtonGroup.add(getDisplayNumbersOnlyRadioButton());
            displayNameButtonGroup.add(getDisplayAliasNameRadioButton());
        }
        return displayNameButtonGroup;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setToolTipText("Discard changes");
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.setPreferredSize(new java.awt.Dimension(100, 26));
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    populateGUI();
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes JudgesDefaultAnswerTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJudgesDefaultAnswerTextField() {
        if (judgesDefaultAnswerTextField == null) {
            judgesDefaultAnswerTextField = new JTextField(40);
            judgesDefaultAnswerTextField.setText("");
//            judgesDefaultAnswerTextField.setSize(new Dimension(280, 29));
//            judgesDefaultAnswerTextField.setLocation(new Point(208, 214));
            judgesDefaultAnswerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgesDefaultAnswerTextField;
    }

    /**
     * This method initializes jCheckBoxShowPreliminaryOnBoard
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxShowPreliminaryOnBoard() {
        if (jCheckBoxShowPreliminaryOnBoard == null) {
            jCheckBoxShowPreliminaryOnBoard = new JCheckBox();
            jCheckBoxShowPreliminaryOnBoard.setHorizontalAlignment(SwingConstants.LEFT);
            jCheckBoxShowPreliminaryOnBoard.setAlignmentX( Component.LEFT_ALIGNMENT );
            jCheckBoxShowPreliminaryOnBoard.setBorder(new EmptyBorder(0, 15, 0, 0));
            jCheckBoxShowPreliminaryOnBoard.setMnemonic(KeyEvent.VK_UNDEFINED);
            jCheckBoxShowPreliminaryOnBoard.setText("Include Preliminary Judgements in Scoring Algorithm");
            jCheckBoxShowPreliminaryOnBoard.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return jCheckBoxShowPreliminaryOnBoard;
    }

    /**
     * This method initializes jCheckBoxShowPreliminaryOnNotifications
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxShowPreliminaryOnNotifications() {
        if (jCheckBoxShowPreliminaryOnNotifications == null) {
            jCheckBoxShowPreliminaryOnNotifications = new JCheckBox();
//            jCheckBoxShowPreliminaryOnNotifications.setBounds(new Rectangle(57, 364, 392, 21));
            jCheckBoxShowPreliminaryOnNotifications.setHorizontalAlignment(SwingConstants.LEFT);
            jCheckBoxShowPreliminaryOnNotifications.setBorder(new EmptyBorder(0, 15, 0, 0));

            jCheckBoxShowPreliminaryOnNotifications.setMnemonic(KeyEvent.VK_UNDEFINED);
            jCheckBoxShowPreliminaryOnNotifications.setText("Send Balloon Notifications for Preliminary Judgements");
            jCheckBoxShowPreliminaryOnNotifications.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return jCheckBoxShowPreliminaryOnNotifications;
    }

    /**
     * This method initializes additionalRunStatusCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getAdditionalRunStatusCheckBox() {
        if (additionalRunStatusCheckBox == null) {
            additionalRunStatusCheckBox = new JCheckBox();
            additionalRunStatusCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
            additionalRunStatusCheckBox.setBorder(new EmptyBorder(0, 15, 0, 0));

            additionalRunStatusCheckBox.setText("Send Additional Run Status Information");
            additionalRunStatusCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return additionalRunStatusCheckBox;
    }

    public ContestInformation getContestInformation() {
        return savedContestInformation;
    }

    public void setContestInformation(ContestInformation contestInformation) {
        this.savedContestInformation = contestInformation;
        savedScoringProperties = contestInformation.getScoringProperties();
        if (savedScoringProperties == null) {
            savedScoringProperties = DefaultScoringAlgorithm.getDefaultProperties();
        }
        changedScoringProperties = savedScoringProperties;
    }

    /**
     * This method initializes maxFieldSizeInKTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getMaxOutputSizeInKTextField() {
        if (textfieldMaxOutputSizeInK == null) {
            
            textfieldMaxOutputSizeInK = new JTextField(5);
            
            textfieldMaxOutputSizeInK.setDocument(new IntegerDocument());

            textfieldMaxOutputSizeInK.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return textfieldMaxOutputSizeInK;
    }
    
    private JCheckBox getAllowMultipleTeamLoginsCheckbox() {
        if (allowMultipleTeamLoginsCheckbox==null) {
            allowMultipleTeamLoginsCheckbox = new JCheckBox("Allow multiple logins per team", true);
            allowMultipleTeamLoginsCheckbox.addActionListener (new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    allowMultipleLoginsPerTeam = getAllowMultipleTeamLoginsCheckbox().isSelected();
                    enableUpdateButton();
                }
            });
            
        }
        return allowMultipleTeamLoginsCheckbox ;
    }

    /**
     * This method initializes scoringPropertiesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getScoringPropertiesButton() {
        if (scoringPropertiesButton == null) {
            
            scoringPropertiesButton = new JButton();
            scoringPropertiesButton.setHorizontalAlignment(SwingConstants.LEFT);
            scoringPropertiesButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

            scoringPropertiesButton.setAlignmentX(LEFT_ALIGNMENT);
            scoringPropertiesButton.setToolTipText("Edit Scoring Properties");
            scoringPropertiesButton.setMnemonic(KeyEvent.VK_S);
            scoringPropertiesButton.setText("Edit Scoring Properties");
            scoringPropertiesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showContestPropertiesEditor();
                }
            });
        }
        return scoringPropertiesButton;
    }

    protected void showContestPropertiesEditor() {

        PropertiesEditFrame propertiesEditFrame = new PropertiesEditFrame();
        propertiesEditFrame.setTitle("Edit Scoring Properties");
        propertiesEditFrame.setProperties(changedScoringProperties, new UpdateScoreProperties());
        propertiesEditFrame.setVisible(true);
    }

    /**
     * Update the edited properties.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class UpdateScoreProperties implements IPropertyUpdater {

        public void updateProperties(Properties properties) {
            changedScoringProperties = properties;
            enableUpdateButton();
        }
    }

    /**
     * This method initializes ccsTestModeCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCcsTestModeCheckbox() {
        if (ccsTestModeCheckbox == null) {
            
            ccsTestModeCheckbox = new JCheckBox();
            
            ccsTestModeCheckbox.setText("Enable CCS Test Mode");
            ccsTestModeCheckbox.setToolTipText("CCS Test Mode is used to allow PC2 to forward team submissions to a remote"
                    + " Contest Control System via the CLICS 'Run Submission Interface'");
            ccsTestModeCheckbox.setMnemonic(KeyEvent.VK_T);
            ccsTestModeCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return ccsTestModeCheckbox;
    }

    /**
     * This method initializes runSubmissionInterfaceCommandTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getRunSubmissionInterfaceCommandTextField() {
        if (runSubmissionInterfaceCommandTextField == null) {
            runSubmissionInterfaceCommandTextField = new JTextField();
            runSubmissionInterfaceCommandTextField.setMaximumSize(new Dimension(2147483647, 20));
            runSubmissionInterfaceCommandTextField.setText("");
            runSubmissionInterfaceCommandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return runSubmissionInterfaceCommandTextField;
    }

    private Component getHorizontalStrut_2() {
        if (horizontalStrut_2 == null) {
        	horizontalStrut_2 = Box.createHorizontalStrut(20);
        	horizontalStrut_2.setMaximumSize(new Dimension(20, 0));
        }
        return horizontalStrut_2;
    }

    private Component getHorizontalStrut_3() {
        if (horizontalStrut_3 == null) {
        	horizontalStrut_3 = Box.createHorizontalStrut(20);
        }
        return horizontalStrut_3;
    }

    private Component getHorizontalStrut() {
        if (horizontalStrut == null) {
        	horizontalStrut = Box.createHorizontalStrut(10);
        }
        return horizontalStrut;
    }
    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
        	horizontalStrut_1 = Box.createHorizontalStrut(10);
        }
        return horizontalStrut_1;
    }
    private Component getHorizontalStrut_4() {
        if (horizontalStrut_4 == null) {
        	horizontalStrut_4 = Box.createHorizontalStrut(10);
        }
        return horizontalStrut_4;
    }
    private Component getHorizontalStrut_5() {
        if (horizontalStrut_5 == null) {
        	horizontalStrut_5 = Box.createHorizontalStrut(10);
        }
        return horizontalStrut_5;
    }
    private Component getRigidArea1( ) {
        if (rigidArea1==null) {
            rigidArea1 = Box.createRigidArea(new Dimension(20,20));
        }
        return rigidArea1;
    }
}
