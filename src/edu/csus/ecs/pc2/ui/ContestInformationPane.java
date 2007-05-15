package edu.csus.ecs.pc2.ui;

import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Contest Information edit/update Pane.
 * 
 * Update contest title.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class ContestInformationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8408469113380938482L;

    private JPanel buttonPanel = null;

    private JPanel centerPane = null;

    private JButton updateButton = null;

    private JLabel contestTitleLabel = null;

    private JTextField contestTitleTextField = null;

    /**
     * This method initializes
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
        this.setSize(new java.awt.Dimension(533, 238));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);

    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getUpdateButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            contestTitleLabel = new JLabel();
            contestTitleLabel.setBounds(new java.awt.Rectangle(55, 21, 134, 27));
            contestTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            contestTitleLabel.setText("Contest Title");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(contestTitleLabel, null);
            centerPane.add(getContestTitleTextField(), null);
        }
        return centerPane;
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
            contestTitleTextField = new JTextField();
            contestTitleTextField.setBounds(new java.awt.Rectangle(204, 21, 287, 27));
        }
        return contestTitleTextField;
    }


    @Override
    public String getPluginTitle() {
        return "Contest Information Pane";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        populateGUI();

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContestInformation contestInformation = getContest().getContestInformation();
                getContestTitleTextField().setText(contestInformation.getContestTitle());
            }
        });
        
    }

    private void updateContestInformation() {
        ContestInformation contestInformation = getContest().getContestInformation();
        contestInformation.setContestTitle(getContestTitleTextField().getText());
        getController().updateContestInformation(contestInformation);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            populateGUI();

        }

        public void contestInformationChanged(ContestInformationEvent event) {
            populateGUI();

        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

    }

} // @jve:decl-index=0:visual-constraint="10,10"
