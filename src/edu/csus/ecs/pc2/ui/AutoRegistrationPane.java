package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.ibm.webrunner.j2mclb.ListboxColumn;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IMessageListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.MessageEvent;
import edu.csus.ecs.pc2.core.model.MessageEvent.Area;
import edu.csus.ecs.pc2.core.packet.PacketType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Auto Registration Pane.
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

    private JTextField teamNameJTextField = null;

    private JPanel memberNamesPanel = null;

    private JPanel teamMemberTitle = null;

    private MCLB teamNameMCLB = null;

    private JPanel southPane;

    private JPanel buttonPaneTwo;

    private JButton btnRemoveMember;

    private JButton btnAddMember;

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
        this.add(getCenterFrame(), BorderLayout.CENTER);
        this.add(getTitlePane(), BorderLayout.NORTH);
        add(getSouthPane(), BorderLayout.SOUTH);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        inContest.addMessageListener(new MessageListenerImplementation());
        inContest.addLoginListener(new LoginListenerImplementation());
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
            centerFrame.setLayout(new BorderLayout());
            centerFrame.add(getMemberNamesPanel(), BorderLayout.CENTER);
            centerFrame.add(getTeamMemberTitle(), BorderLayout.NORTH);
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
            buttonPane.setPreferredSize(new Dimension(35, 35));
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
            registerButton.setMnemonic('R');
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

        if (getAccountNameTitle().getText().trim().length() == 0) {
            showMessage(getParentFrame(), "Enter missing information", "Enter a Team Name/title");
            return;
        }

        String teamName = getAccountNameTitle().getText().trim();

        String[] teamMemberNames = getTeamNamesFromFields();

        if (teamMemberNames.length == 0) {
            showMessage(getParentFrame(), "Enter missing information", "Enter a Team Member Name");
            return;
        }

        String delimit = PacketType.FIELD_DELIMIT;
        String names = StringUtilities.join(delimit, teamMemberNames);

        try {
            String autoRegisterInformation = teamName + delimit + names;
            getController().autoRegister(autoRegisterInformation);
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Unable to send auto registration ", e);
            showMessage(getParent(), "Unable to send auto registration", "Internal error " + e.getMessage());
        }
    }

    /**
     * Fetch names from frame.
     * 
     * @return
     */
    private String[] getTeamNamesFromFields() {

        MCLB mclb = getTeamNameMCLB();

        if (mclb.getRowCount() == 0) {
            return new String[0];
        }

        ArrayList<String> list = new ArrayList<String>();

        for (int row = 0; row < mclb.getRowCount(); row++) {
            JTextField jTextField = (JTextField) mclb.getRow(row)[0];
            String name = jTextField.getText();
            if (name != null && name.trim().length() > 0) {
                list.add(name);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
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

    /**
     * Hide this window.
     */
    protected void returnToParent() {
        getParentFrame().setVisible(false); // hide the parent frame, show the parent parent's (LoginFrame).
    }

    /**
     * This method initializes teamNameJTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAccountNameTitle() {
        if (teamNameJTextField == null) {
            teamNameJTextField = new JTextField();
        }
        return teamNameJTextField;
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
            Object[] cols = { "Name" };

            teamNameMCLB.addColumns(cols);
            teamNameMCLB.addRow(buildRow());

            int width = 500;
            ListboxColumn column = teamNameMCLB.getColumnInfo(0);
            column.setWidth(width);
        }
        return teamNameMCLB;
    }

    private Object[] buildRow() {
        // Object[] cols = { "Name"};
        Object[] objects = new Object[teamNameMCLB.getColumnCount()];
        objects[0] = createJTextField();
        return objects;
    }

    private Object createJTextField() {
        JTextField textField = new JTextField();
        textField.setText("");
        textField.setEditable(true);
        textField.requestFocus();
        return textField;
    }

    /**
     * Login Listener for auto reg.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            // not used
        }

        public void loginRemoved(LoginEvent event) {
            // not used
        }

        public void loginDenied(LoginEvent event) {
            String message = event.getMessage();
            showMessage(getParent(), "Not Allowed", message);
            returnToParent();
        }

        public void loginRefreshAll(LoginEvent event) {
            // not used
        }
    }

    /**
     * Message Listener for auto reg.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class MessageListenerImplementation implements IMessageListener {

        public void messageAdded(MessageEvent event) {
            if (Area.AUTOREG.equals(event.getArea())) {
                String accountMessage = event.getMessage();
                showAccountMessage(accountMessage);

            }
        }

        public void messageRemoved(MessageEvent event) {
            // not used
        }
    }

    /**
     * Parse message into parts and display to user.
     * 
     * @param accountMessage
     */
    public void showAccountMessage(String accountMessage) {

        String[] loginInfo = accountMessage.split(PacketType.FIELD_DELIMIT);

        String teamName = loginInfo[0];
        String login = loginInfo[1];
        String password = loginInfo[2];

        StringBuffer buffer = new StringBuffer() //
                .append("<HTML><FONT SIZE=+1> ") //
                .append("Automantic Registration Information<BR>") //
                .append("for team ") //
                .append(teamName) //
                .append("<BR><BR>") //
                .append("Login: ") //
                .append(login) //
                .append("<BR><BR>") //
                .append("Password: ") //
                .append(password) //
                .append("<BR><BR>") //
                .append("</FONT></HTML>");

        FrameUtilities.showMessage(getParentFrame(), "Automatic Registration Information", buffer.toString());

        returnToParent();
    }

    private JPanel getSouthPane() {
        if (southPane == null) {
            southPane = new JPanel();
            southPane.setName("southPane");
            southPane.setLayout(new BorderLayout(0, 0));
            southPane.add(getButtonPaneTwo());
            southPane.add(getButtonPane(), BorderLayout.SOUTH);
        }
        return southPane;
    }

    private JPanel getButtonPaneTwo() {
        if (buttonPaneTwo == null) {
            buttonPaneTwo = new JPanel();
            FlowLayout flowLayout = (FlowLayout) buttonPaneTwo.getLayout();
            flowLayout.setHgap(100);
            buttonPaneTwo.setPreferredSize(new Dimension(35, 35));
            buttonPaneTwo.add(getBtnAddMember());
            buttonPaneTwo.add(getBtnRemoveMember());
        }
        return buttonPaneTwo;
    }

    private JButton getBtnRemoveMember() {
        if (btnRemoveMember == null) {
            btnRemoveMember = new JButton("Remove Member");
            btnRemoveMember.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    int lastRow = teamNameMCLB.getRowCount();
                    if (lastRow > 1) {
                        teamNameMCLB.removeRow(lastRow - 1);
                    } else {
                        teamNameMCLB.removeRow(lastRow - 1);
                        teamNameMCLB.addRow(buildRow());
                    }
                }
            });
            btnRemoveMember.setMnemonic(KeyEvent.VK_E);
        }
        return btnRemoveMember;
    }

    private JButton getBtnAddMember() {
        if (btnAddMember == null) {
            btnAddMember = new JButton("Add Member");
            btnAddMember.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    teamNameMCLB.addRow(buildRow());
                }
            });
            btnAddMember.setMnemonic(KeyEvent.VK_A);
        }
        return btnAddMember;
    }

    public void resetFields() {
        teamNameMCLB.removeAllRows();
        teamNameMCLB.addRow(buildRow());
        teamNameJTextField.setText("");
    }
} // @jve:decl-index=0:visual-constraint="10,10"
