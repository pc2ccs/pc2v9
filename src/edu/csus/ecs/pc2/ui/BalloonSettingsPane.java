package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * View Balloon Settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel balloonSettingsButtonPane = null;

    private MCLB balloonSettingsListBox = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private Log log = null;

    private EditBalloonSettingsFrame editBalloonSettingsFrame = null;

    /**
     * This method initializes
     * 
     */
    public BalloonSettingsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getProblemListBox(), java.awt.BorderLayout.CENTER);
        this.add(getProblemButtonPane(), java.awt.BorderLayout.SOUTH);

        editBalloonSettingsFrame = new EditBalloonSettingsFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Problems Pane";
    }

    /**
     * This method initializes balloonSettingsButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemButtonPane() {
        if (balloonSettingsButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            balloonSettingsButtonPane = new JPanel();
            balloonSettingsButtonPane.setLayout(flowLayout);
            balloonSettingsButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            balloonSettingsButtonPane.add(getAddButton(), null);
            balloonSettingsButtonPane.add(getEditButton(), null);
        }
        return balloonSettingsButtonPane;
    }

    /**
     * This method initializes balloonSettingsListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getProblemListBox() {
        if (balloonSettingsListBox == null) {
            balloonSettingsListBox = new MCLB();

            Object[] cols = { "BalloonSettings Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "SVTJ", "Validator" };
            balloonSettingsListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the balloonSettingss are is to NOT sort them. Later we can add a sorter per ProblemDisplayList somehow.
             */

            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // // HeapSorter numericStringSorter = new HeapSorter();
            // // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Display Name
            // balloonSettingsListBox.setColumnSorter(0, sorter, 1);
            // // Compiler Command Line
            // balloonSettingsListBox.setColumnSorter(1, sorter, 2);
            // // Exe Name
            // balloonSettingsListBox.setColumnSorter(2, sorter, 3);
            // // Execute Command Line
            // balloonSettingsListBox.setColumnSorter(3, sorter, 4);
            balloonSettingsListBox.autoSizeAllColumns();

        }
        return balloonSettingsListBox;
    }

    public void updateBalloonSettingRow(final BalloonSettings balloonSettings) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildBalloonSettingsRow(balloonSettings);
                int rowNumber = balloonSettingsListBox.getIndexByKey(balloonSettings.getElementId());
                if (rowNumber == -1) {
                    balloonSettingsListBox.addRow(objects, balloonSettings.getElementId());
                } else {
                    balloonSettingsListBox.replaceRow(objects, rowNumber);
                }
                balloonSettingsListBox.autoSizeAllColumns();
                // balloonSettingsListBox.sort();
            }
        });
    }

    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    protected Object[] buildBalloonSettingsRow(BalloonSettings balloonSettings) {
        // TODO 
        return null;
        
//        // Object[] cols = { "BalloonSettings Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "SVTJ", "Validator" };
//
//        int numberColumns = balloonSettingsListBox.getColumnCount();
//        Object[] c = new String[numberColumns];
//
//        c[0] = balloonSettings.getDisplayName();
//        c[1] = balloonSettings.getDataFileName();
//        String inputMethod = "";
//        if (balloonSettings.isReadInputDataFromSTDIN()) {
//            inputMethod = "STDIN";
//        } else {
//            inputMethod = "File I/O";
//        }
//        c[2] = inputMethod;
//        c[3] = balloonSettings.getAnswerFileName();
//        c[4] = Integer.toString(balloonSettings.getTimeOutInSeconds());
//        c[5] = yesNoString(balloonSettings.isShowValidationToJudges());
//        c[6] = balloonSettings.getValidatorProgramName();
//
//        return c;
    }

    private void reloadListBox() {
        balloonSettingsListBox.removeAllRows();
        BalloonSettings[] balloonSettingsArray = getContest().getBalloonSettings();

        for (BalloonSettings balloonSettings : balloonSettingsArray) {
            addProblemRow(balloonSettings);
        }
    }

    private void addProblemRow(BalloonSettings balloonSettings) {
        Object[] objects = buildBalloonSettingsRow(balloonSettings);
        balloonSettingsListBox.addRow(objects, balloonSettings.getElementId());
        balloonSettingsListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        editBalloonSettingsFrame.setContestAndController(inContest, inController);

        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    protected void addProblem() {
        editBalloonSettingsFrame.setBalloonSettings(null);
        editBalloonSettingsFrame.setVisible(true);
    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedProblem();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedProblem() {

        int selectedIndex = balloonSettingsListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a balloonSettings to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) balloonSettingsListBox.getKeys()[selectedIndex];
            BalloonSettings balloonSettingsToEdit = getContest().getBalloonSettings(elementId);

            editBalloonSettingsFrame.setBalloonSettings(balloonSettingsToEdit);
            editBalloonSettingsFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit balloonSettings, check log");
        }
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateBalloonSettingRow(event.getBalloonSettings());
                }
            });
        }

        public void balloonSettingsChanged(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateBalloonSettingRow(event.getBalloonSettings());
                }
            });
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            // TODO remove balloon setting
            log.info("debug BalloonSettings REMOVED  " + event.getBalloonSettings());
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
