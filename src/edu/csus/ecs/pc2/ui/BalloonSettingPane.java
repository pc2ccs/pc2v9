package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Add/Edit BalloonSettings Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingPane extends JPanePlugin {

    public final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    private static final long serialVersionUID = -1060536964672397704L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    /**
     * The input balloonSettings.
     */
    private BalloonSettings balloonSettings = null;

    @SuppressWarnings("unused")
    private Log log = null;

    @SuppressWarnings("unused")
    private boolean populatingGUI = true;

    /**
     * This method initializes
     * 
     */
    public BalloonSettingPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(539,306));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        // getContest().addProblemListener(new Proble)

        log = getController().getLog();
    }

    public String getPluginTitle() {
        return "Edit BalloonSettings Pane";
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
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
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
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    /**
     * Add BalloonSettings to the fields.
     * 
     */
    protected void addProblem() {

        BalloonSettings newBalloonSettings = null;
        try {
            newBalloonSettings = getProblemFromFields(null);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().addNewBalloonSettings(newBalloonSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {
//
//        if (populatingGUI) {
//            return;
//        }
//
//        boolean enableButton = false;
//
//        if (balloonSettings != null) {
//
//            try {
//                BalloonSettings changedProblem = getProblemFromFields(null);
//                
//                // TODO 
////                if (!balloonSettings.isSameAs(changedProblem)) {
////                    enableButton = true;
////                }
//
//            } catch (InvalidFieldValue e) {
//                // invalid field, but that is ok as they are entering data
//                // will be caught and reported when they hit update or add.
//                StaticLog.getLog().log(Log.DEBUG, "Input BalloonSettings (but not saving) ", e);
//                enableButton = true;
//            }
//
//        } else {
//            if (getAddButton().isVisible()) {
//                enableButton = true;
//            }
//        }
//
//        // TODO enable 
////        enableUpdateButtons(enableButton);
//
//    
    }

    /**
     * Create a BalloonSettings from the fields.
     * 
     * @param checkProblem
     * @return
     * @throws InvalidFieldValue
     */
    public BalloonSettings getProblemFromFields(BalloonSettings checkProblem) throws InvalidFieldValue {
        return null;
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
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateProblem();
                }
            });
        }
        return updateButton;
    }

    protected void updateProblem() {

        if (!validateProblemFields()) {
            // new balloonSettings is invalid, just return, message issued by validateProblemFields
            return;
        }

        BalloonSettings newBalloonSettings = null;

        try {
            newBalloonSettings = getProblemFromFields(balloonSettings);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().updateBalloonSettings(newBalloonSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Validate that all balloonSettings fields are ok.
     * 
     * @return
     */
    private boolean validateProblemFields() {
        
        return false; // TODO 
        
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
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("BalloonSettings modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addProblem();
                } else {
                    updateProblem();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public BalloonSettings getProblem() {
        return balloonSettings;
    }

    public void setBalloonSettings(final BalloonSettings balloonSettings) {

        this.balloonSettings = balloonSettings;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(balloonSettings);
                // enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    private void populateGUI(BalloonSettings inBalloonSettings) {

    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

}  //  @jve:decl-index=0:visual-constraint="28,22"
