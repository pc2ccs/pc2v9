package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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

    private JLabel titleLabel = null;

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
        this.setSize(new java.awt.Dimension(449,278));

        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
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
            titleLabel = new JLabel();
            titleLabel.setText("Problems available to Auto Judge");
            titleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
            titleLabel.setPreferredSize(new java.awt.Dimension(255,36));
            titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(titleLabel, java.awt.BorderLayout.CENTER);
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
            flowLayout.setHgap(30);
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

    private ClientSettings getClientSettingsFromFields(ClientSettings checkSettings) {

        if (checkSettings == null) {
            checkSettings = new ClientSettings();
        }

        checkSettings.setAutoJudging(getEnableAutoJudgingCheckBox().isSelected());

        Filter filter = new Filter();

        int[] selectedIndex = getSelectedProblemsIndex ();
        for (int row : selectedIndex) {
            Problem problem = (Problem) getProblemListMCLB().getRowKey(row);
            if (problem != null) {
                if (problem.isComputerJudged()) {
                    filter.addProblem(problem);
                } else {
                    throw new InvalidFieldValue("Problem '" + problem.getDisplayName()+"' can not be auto judged\nOnly computer judged problems can be auto judged.");
                }
            }
        }
        
        checkSettings.setBalloonList(clientSettings.getBalloonList());

        checkSettings.setAutoJudgeFilter(filter);
        return checkSettings;
    }

    private int[] getSelectedProblemsIndex() {
        
        int numberSelected = 0;
        for (int i = 0; i < getProblemListMCLB().getRowCount(); i++) {
            JCheckBox jCheckBox = (JCheckBox) getProblemListMCLB().getRow(i)[0];
            if (jCheckBox.isSelected()){
                numberSelected ++;
            }
        }
        
        if (numberSelected > 0){
            int [] selected = new int[numberSelected];
            int count = 0;
            for (int i = 0; i < getProblemListMCLB().getRowCount(); i++) {
                JCheckBox jCheckBox = (JCheckBox) getProblemListMCLB().getRow(i)[0];
                if (jCheckBox.isSelected()){
                    selected[count] = i;
                    count ++;
                }
            }
            return selected;
        } else {
            return new int[0];
        }
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Auto Judge modified, save changes?", "Confirm Choice");

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
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setVgap(0);
            centerPanel = new JPanel();
            centerPanel.setLayout(borderLayout);
            centerPanel.setName("advancedEdit");
            centerPanel.add(getEnableAutoJudgingCheckBox(), java.awt.BorderLayout.SOUTH);
            centerPanel.add(getProblemListMCLB(), java.awt.BorderLayout.CENTER);
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
        }
        
        for (Problem problem : problems) {
            if (canBeAutoJudged(problem)) {
                boolean problemSelected = filter.isFilteringProblems() && filter.matchesProblem(problem.getElementId());
                Object[] row = buildProblemRow(problem, problemSelected);
                getProblemListMCLB().addRow(row, problem);
            }
        }
        
        getAddButton().setVisible(false);
        getUpdateButton().setVisible(true);
        
        populatingGUI = false;
    }
    
    /**
     * Can this problem be auto judged?.
     * @param problem
     * @return
     */
    private boolean canBeAutoJudged(Problem problem) {
        return problem.isComputerJudged() && problem.isValidatedProblem();
    }

    /**
     * Get the judging type.
     * 
     * @param problem
     * @return
     */
    public String getJudgingTypeName(Problem problem) {
        String judgingTypeName = "Manual";
        if (problem.isComputerJudged()) {
            judgingTypeName = "Computer";
            if (problem.isManualReview()) {
                judgingTypeName = "Computer+Manual";
                if (problem.isPrelimaryNotification()) {
                    judgingTypeName = "Computer+Manual/Notify";
                }
            }
        } else if (problem.isValidatedProblem()) {
            judgingTypeName = "Manual w/Val.";
        } else {
            judgingTypeName = "Manual";
        }
        return judgingTypeName;
    }
    /**
     * 
     * @param problem
     * @param problemSelected set checkbox if selected
     * @return
     */
    private Object[] buildProblemRow(Problem problem, boolean problemSelected) {
        
//        Object[] cols = { "Problem", "Judging Type" };

        int numberColumns = getProblemListMCLB().getColumnCount();
        Object[] c = new Object[numberColumns];
        
        JCheckBox jCheckBox = new JCheckBox(problem.getDisplayName());
        jCheckBox.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                enableUpdateButton();
            }
        });
        jCheckBox.setSelected(problemSelected);

        c[0] = jCheckBox;
        c[1] = getJudgingTypeName(problem);
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
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * This method initializes enableAutoJudgingCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getEnableAutoJudgingCheckBox() {
        if (enableAutoJudgingCheckBox == null) {
            enableAutoJudgingCheckBox = new JCheckBox();
            enableAutoJudgingCheckBox.setText("Enable Auto Judging");
            enableAutoJudgingCheckBox.setPreferredSize(new java.awt.Dimension(140,30));
            enableAutoJudgingCheckBox.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
            enableAutoJudgingCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
            problemListMCLB.setMultipleSelections(true);
            problemListMCLB.addListboxListener(new com.ibm.webrunner.j2mclb.event.ListboxListener() {
                public void rowDeselected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    enableUpdateButton();
                }
                public void rowSelected(com.ibm.webrunner.j2mclb.event.ListboxEvent e) {
                    enableUpdateButton();
                }

            });
            Object[] cols = { "Problem", "Judging Type" };

            problemListMCLB.addColumns(cols);
            problemListMCLB.getColumnInfo(0).setWidth(200);
            // SOMEDAY make autoSizeAllColumns work with JCheckBoxes
//            problemListMCLB.autoSizeAllColumns()

        }
        return problemListMCLB;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
