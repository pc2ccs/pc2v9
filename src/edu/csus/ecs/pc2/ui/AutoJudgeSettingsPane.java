package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Edit Auto Judge settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeSettingsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4704199226428040796L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private JPanel centerPanel = null;

    private ClientSettings clientSettings = null;

    private boolean populatingGUI = true;

    private JCheckBox enableAutoJudgingCheckBox = null;

    private MCLB problemListMCLB = null;

    /**
     * This method initializes
     * 
     */
    public AutoJudgeSettingsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(517, 204));

        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "Edit ClientSettings Pane";
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
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addClientSettings();
                }
            });
        }
        return addButton;
    }

    protected void addClientSettings() {

        ClientSettings newClientSettings = null;

        try {
            newClientSettings = getClientSettingsFromFields(null);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().addNewClientSettings(newClientSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private ClientSettings getClientSettingsFromFields(ClientSettings checkSettings) throws InvalidFieldValue {

        if (checkSettings == null) {
            checkSettings = new ClientSettings();
        }

        checkSettings.setAutoJudging(getEnableAutoJudgingCheckBox().isSelected());

        Filter filter = new Filter();

        int[] selectedIndex = problemListMCLB.getSelectedIndexes();
        for (int row : selectedIndex) {
            Problem problem = (Problem) problemListMCLB.getRowKey(row);
            if (problem != null) {
                filter.addProblem(problem);
            }
        }

        checkSettings.setAutoJudgeFilter(filter);
        return checkSettings;
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
                    updateClientSettings();
                }
            });
        }
        return updateButton;
    }

    protected void updateClientSettings() {

        ClientSettings newClientSettings = null;

        try {
            newClientSettings = getClientSettingsFromFields(clientSettings);
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().updateClientSettings(newClientSettings);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

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

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("Auto Judge modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addClientSettings();
                } else {
                    updateClientSettings();
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

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(null);
            centerPanel.setName("advancedEdit");
            centerPanel.add(getEnableAutoJudgingCheckBox(), null);
            centerPanel.add(getProblemListMCLB(), null);
        }
        return centerPanel;
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (clientSettings != null) {

            try {
                ClientSettings newClientSettings = getClientSettingsFromFields(null);

                enableButton |= (!enableAutoJudgingCheckBox.isSelected() == clientSettings.isAutoJudging());
                enableButton |= (!getProblemlist(newClientSettings.getAutoJudgeFilter()).equals(getProblemlist(clientSettings.getAutoJudgeFilter())));

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                StaticLog.getLog().log(Log.DEBUG, "Input Problem (but not saving) ", e);
                enableButton = true;
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public void setClientSettings(final ClientSettings clientSettings) {

        this.clientSettings = clientSettings;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getParentFrame().setTitle(clientSettings.getClientId() + " (Site " + clientSettings.getSiteNumber() + ") Auto Judge Settings");
                populateGUI(clientSettings);
                enableUpdateButtons(false);
            }
        });
    }

    /**
     * Return a list of comma delimited problem names.
     * 
     * <P>
     * returns "none selected" if no problems in filter <br>
     * returns "none active selected" if problems in the filter are all deactivated <br>
     * 
     * @param filter
     * @return
     */
    private String getProblemlist(Filter filter) {
        ElementId[] elementIds = filter.getProblemIdList();

        if (elementIds.length == 0) {
            return "(none selected)";
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Problem problem : getContest().getProblems()) {
            for (ElementId elementId : elementIds) {
                if (problem.getElementId().equals(elementId)) {
                    stringBuffer.append(problem.getDisplayName());
                    stringBuffer.append(", ");
                }
            }
        }

        if (stringBuffer.length() > 0) {
            // stringBuffer.length() - 2 used to strip off trailing ", "
            return new String(stringBuffer).substring(0, stringBuffer.length() - 2);
        } else {
            return "(none active selected)";
        }
    }

    private void populateGUI(ClientSettings clientSettings2) {

        populatingGUI = true;

        getEnableAutoJudgingCheckBox().setSelected(clientSettings2.isAutoJudging());

        Problem[] problems = getContest().getProblems();
        problemListMCLB.removeAllRows();

        Filter filter = clientSettings2.getAutoJudgeFilter();
        if (filter == null){
            filter = new Filter();
            filter.setUsingProblemFilter(true);
        }
            
        int rowNumber = 0;
        for (Problem problem : problems) {
            Object[] row = buildProblemRow(problem);
            problemListMCLB.addRow(row, problem);
            if (filter.matchesProblem(problem.getElementId())){
                problemListMCLB.selectRow(rowNumber);
            }
            rowNumber++;
        }
        
        if (clientSettings2 != null) {
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
        }

        populatingGUI = false;
    }

    private Object[] buildProblemRow(Problem problem) {
        int numberColumns = problemListMCLB.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = problem.getDisplayName();
        return c;
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        addButton.setEnabled(editedText);
        updateButton.setEnabled(editedText);
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes enableAutoJudgingCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getEnableAutoJudgingCheckBox() {
        if (enableAutoJudgingCheckBox == null) {
            enableAutoJudgingCheckBox = new JCheckBox();
            enableAutoJudgingCheckBox.setBounds(new java.awt.Rectangle(22, 15, 181, 18));
            enableAutoJudgingCheckBox.setText("Enable Auto Judging");
            enableAutoJudgingCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return enableAutoJudgingCheckBox;
    }

    /**
     * This method initializes problemListMCLB
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getProblemListMCLB() {
        if (problemListMCLB == null) {
            problemListMCLB = new MCLB();
            problemListMCLB.setBounds(new java.awt.Rectangle(206, 14, 290, 111));

            problemListMCLB.setMultipleSelections(true);
            problemListMCLB.addListboxListener(new com.ibm.webrunner.j2mclb.event.ListboxListener() {
                public void rowDeselected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    enableUpdateButton();
                }

                public void rowSelected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    enableUpdateButton();
                }

            });
            Object[] cols = { "Problem to Auto Judge" };
            problemListMCLB.addColumns(cols);
            problemListMCLB.autoSizeAllColumns();

        }
        return problemListMCLB;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
