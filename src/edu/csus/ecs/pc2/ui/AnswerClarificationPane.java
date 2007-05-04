package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Add/Edit Clarification Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AnswerClarificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4185051421490716684L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton okButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Clarification clarification = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private Log log = null;

    private boolean populatingGUI = true;

    private PermissionList permissionList = new PermissionList();

    private JCheckBox sendToAllCheckBox = null;

    private JButton defaultAnswerButton = null;

    private JPanel infoPanel = null;

    private JLabel clarificationInfoLabel = null;

    private JPanel problemInfoPane = null;

    private JLabel problemTitleLabel = null;

    private JPanel clarificationPane = null;

    private JPanel questionPane = null;

    private JPanel answerPane = null;

    private JScrollPane answerScrollPane = null;

    private JScrollPane questionScrolPane = null;

    private JTextArea answerTextArea = null;

    private JTextArea questionTextArea = null;

    private JLabel problemNameLabel = null;

    private boolean fetchedFromServer = false;

    /**
     * This method initializes
     * 
     */
    public AnswerClarificationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(536, 269));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.EAST);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();

        initializePermissions();
        updateGUIperPermissions();

    }

    public String getPluginTitle() {
        return "Edit Clarification Pane";
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
            buttonPane.add(getOkButton(), null);
            buttonPane.add(getSendToAllCheckBox(), null);
            buttonPane.add(getDefaultAnswerButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    private Clarification getClarificationFromFields() {

        clarification.setAnswer(getAnswerTextArea().getText());
        clarification.setWhoJudgedItId(getContest().getClientId());
        clarification.setSendToAll(getSendToAllCheckBox().isSelected());
        return clarification;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("Ok");
            okButton.setEnabled(false);
            okButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateClarification();
                }
            });
        }
        return okButton;
    }

    private void cancelClarification() {

        enableUpdateButtons(false);
        Clarification newClarification = getClarificationFromFields();
        getController().cancelClarification(newClarification);

    }

    protected void updateClarification() {

        Clarification newClarification = getClarificationFromFields();

        enableUpdateButtons(false);

        getController().submitClarificationAnswer(newClarification);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
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

    public void handleCancelButton() {

        if (getOkButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("Clarification modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateClarification();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
            if (result == JOptionPane.NO_OPTION) {
                cancelClarification();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            cancelClarification();
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(final Clarification inClarification, boolean checkedOut) {

        this.clarification = inClarification;
        this.fetchedFromServer = checkedOut;

        if (checkedOut){
            showMessage("");
            FrameUtilities.regularCursor(this);
        }else{
            showMessage("Waiting for clarification...");
            FrameUtilities.waitCursor(this);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(clarification);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(Clarification theClarification) {

        populatingGUI = true;

        if (theClarification != null) {
            getOkButton().setVisible(true);

            ClientId id = theClarification.getSubmitter();
            String teamName = id.getName();

            clarificationInfoLabel.setText("Clarification " + theClarification.getNumber() + " (Site " + theClarification.getSiteNumber() + ") from " + teamName);

            problemNameLabel.setText(getContest().getProblem(clarification.getProblemId()).toString());
            
            getQuestionTextArea().setText(clarification.getQuestion());

        } else {
            getOkButton().setVisible(false);
            clarificationInfoLabel.setText("Could not get clarification " + +theClarification.getNumber() + " (Site " + theClarification.getSiteNumber() + ")");
            problemNameLabel.setText("");

        }
        
        getAnswerTextArea().setText("");
        getSendToAllCheckBox().setSelected(false);

        populatingGUI = false;

    }

    protected void enableUpdateButtons(boolean editedText) {
        
        if (fetchedFromServer){
            if (editedText) {
                cancelButton.setText("Cancel");
            } else {
                cancelButton.setText("Close");
            }

            okButton.setEnabled(editedText);
            defaultAnswerButton.setEnabled(false);
        } else  {
            okButton.setEnabled(editedText);
            defaultAnswerButton.setEnabled(true);

        }
    }

    /**
     * Enable or disable Update button based on comparison of clarification to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (clarification != null) {
            enableButton |= answerTextArea.getText().trim().length() > 0;
        }

        enableUpdateButtons(enableButton);
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            generalPane = new JPanel();
            generalPane.setLayout(new BorderLayout());
            generalPane.add(getInfoPanel(), java.awt.BorderLayout.NORTH);
            generalPane.add(getClarificationPane(), java.awt.BorderLayout.CENTER);
        }
        return generalPane;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    protected void extractClarification() {
        showMessage("Would have extracted clarification");
        // TODO code extract clarification
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    private void updateGUIperPermissions() {
        
        okButton.setEnabled(isAllowed(Permission.Type.ANSWER_CLARIFICATION));
        defaultAnswerButton.setEnabled(isAllowed(Permission.Type.ANSWER_CLARIFICATION));
    }

    /**
     * This method initializes sendToAllCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSendToAllCheckBox() {
        if (sendToAllCheckBox == null) {
            sendToAllCheckBox = new JCheckBox();
            sendToAllCheckBox.setText("Send to ALL teams");
            sendToAllCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return sendToAllCheckBox;
    }

    /**
     * This method initializes defaultAnswerButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDefaultAnswerButton() {
        if (defaultAnswerButton == null) {
            defaultAnswerButton = new JButton();
            defaultAnswerButton.setText("Default Answer");
            defaultAnswerButton.setToolTipText("No response, read problem statement");
            defaultAnswerButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    answerTextArea.setText("No response, read problem statement");
                    updateClarification();
                }
            });
        }
        return defaultAnswerButton;
    }

    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            clarificationInfoLabel = new JLabel();
            clarificationInfoLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            clarificationInfoLabel.setText("Clar Info");
            clarificationInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.setPreferredSize(new java.awt.Dimension(108, 60));
            infoPanel.add(clarificationInfoLabel, java.awt.BorderLayout.CENTER);
            infoPanel.add(getProblemInfoPane(), java.awt.BorderLayout.SOUTH);
        }
        return infoPanel;
    }

    /**
     * This method initializes problemInfoPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemInfoPane() {
        if (problemInfoPane == null) {
            problemNameLabel = new JLabel();
            problemNameLabel.setText("");
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(12);
            problemTitleLabel = new JLabel();
            problemTitleLabel.setText("Problem");
            problemTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            problemInfoPane = new JPanel();
            problemInfoPane.setPreferredSize(new java.awt.Dimension(108, 22));
            problemInfoPane.setLayout(gridLayout);
            problemInfoPane.add(problemTitleLabel, null);
            problemInfoPane.add(problemNameLabel, null);
        }
        return problemInfoPane;
    }

    /**
     * This method initializes clarificationPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClarificationPane() {
        if (clarificationPane == null) {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(2);
            gridLayout1.setColumns(1);
            clarificationPane = new JPanel();
            clarificationPane.setLayout(gridLayout1);
            clarificationPane.add(getQuestionPane(), null);
            clarificationPane.add(getAnswerPane(), null);
        }
        return clarificationPane;
    }

    /**
     * This method initializes questionPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getQuestionPane() {
        if (questionPane == null) {
            questionPane = new JPanel();
            questionPane.setLayout(new BorderLayout());
            questionPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Question", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            questionPane.add(getQuestionScrolPane(), java.awt.BorderLayout.CENTER);
        }
        return questionPane;
    }

    /**
     * This method initializes answerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerPane() {
        if (answerPane == null) {
            answerPane = new JPanel();
            answerPane.setLayout(new BorderLayout());
            answerPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            answerPane.add(getAnswerScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return answerPane;
    }

    /**
     * This method initializes answerScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getAnswerScrollPane() {
        if (answerScrollPane == null) {
            answerScrollPane = new JScrollPane();
            answerScrollPane.setViewportView(getAnswerTextArea());
        }
        return answerScrollPane;
    }

    /**
     * This method initializes questionScrolPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getQuestionScrolPane() {
        if (questionScrolPane == null) {
            questionScrolPane = new JScrollPane();
            questionScrolPane.setViewportView(getQuestionTextArea());
        }
        return questionScrolPane;
    }

    /**
     * This method initializes answerQuestionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getAnswerTextArea() {
        if (answerTextArea == null) {
            answerTextArea = new JTextArea();
            answerTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return answerTextArea;
    }

    /**
     * This method initializes questionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getQuestionTextArea() {
        if (questionTextArea == null) {
            questionTextArea = new JTextArea();
            questionTextArea.setEditable(false);
        }
        return questionTextArea;
    }
    
    

} // @jve:decl-index=0:visual-constraint="10,10"
