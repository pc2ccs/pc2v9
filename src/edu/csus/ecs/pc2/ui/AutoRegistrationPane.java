package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoRegistrationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3571201525487468354L;

    private JPanel centerFrame = null;

    private JPanel buttonPane = null;

    private JPanel titlePane = null;

    private JLabel titleLabel = null;

    private JButton registerButton = null;

    private JButton cancelButton = null;

    private JTextField accountNameTitle = null;

    private JPanel memberNamesPanel = null;

    private JPanel teamMemberTitle = null;

    private MCLB teamNameMCLB = null;

    /**
     * This method initializes
     * 
     */
    public AutoRegistrationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(451, 281));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getCenterFrame(), BorderLayout.CENTER);
        this.add(getTitlePane(), BorderLayout.NORTH);

    }

    @Override
    public String getPluginTitle() {
        return "Auto Registration Pane";
    }

    /**
     * This method initializes centerFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterFrame() {
        if (centerFrame == null) {
            centerFrame = new JPanel();
            centerFrame.setLayout(null);
            centerFrame.add(getMemberNamesPanel(), null);
            centerFrame.add(getTeamMemberTitle(), null);
        }
        return centerFrame;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(40, 40));
            buttonPane.add(getRegisterButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes titlePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePane() {
        if (titlePane == null) {
            titleLabel = new JLabel();
            titleLabel.setText("Registration ");
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titlePane = new JPanel();
            titlePane.setLayout(new BorderLayout());
            titlePane.setPreferredSize(new Dimension(45, 45));
            titlePane.add(titleLabel, BorderLayout.CENTER);
        }
        return titlePane;
    }

    /**
     * This method initializes registerButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRegisterButton() {
        if (registerButton == null) {
            registerButton = new JButton();
            registerButton.setText("Register");
            registerButton.setMnemonic(KeyEvent.VK_R);
            registerButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sendRegistrationRequest();
                }
            });
        }
        return registerButton;
    }

    protected void sendRegistrationRequest() {
        // TODO BUG 572 - do registration button
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
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    returnToParent();
                }
            });
        }
        return cancelButton;
    }

    protected void returnToParent() {
        getParentFrame().setVisible(true);
    }

    /**
     * This method initializes accountNameTitle
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAccountNameTitle() {
        if (accountNameTitle == null) {
            accountNameTitle = new JTextField();
        }
        return accountNameTitle;
    }

    /**
     * This method initializes memberNamesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMemberNamesPanel() {
        if (memberNamesPanel == null) {
            memberNamesPanel = new JPanel();
            memberNamesPanel.setLayout(new BorderLayout());
            memberNamesPanel.setBounds(new Rectangle(29, 67, 370, 109));
            memberNamesPanel.setBorder(BorderFactory.createTitledBorder(null, "Team Member Names", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            memberNamesPanel.add(getTeamNameMCLB(), BorderLayout.CENTER);
        }
        return memberNamesPanel;
    }

    /**
     * This method initializes teamMemberTitle
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamMemberTitle() {
        if (teamMemberTitle == null) {
            teamMemberTitle = new JPanel();
            teamMemberTitle.setLayout(new BorderLayout());
            teamMemberTitle.setBounds(new Rectangle(27, 9, 389, 52));
            teamMemberTitle.setBorder(BorderFactory.createTitledBorder(null, "Team Name", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                    new Color(51, 51, 51)));
            teamMemberTitle.add(getAccountNameTitle(), BorderLayout.CENTER);
        }
        return teamMemberTitle;
    }

    /**
     * This method initializes teamNameMCLB
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getTeamNameMCLB() {
        if (teamNameMCLB == null) {
            teamNameMCLB = new MCLB();
        }
        return teamNameMCLB;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
