package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Show Judgements, allow add and edit.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
public class JudgementsPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 36368747620026978L;

    private MCLB judgementListBox = null;

    private JPanel buttonsPane = null;

    private JButton addButton = null;

    private JPanel statusPanel = null;

    private JLabel messageLabel = null;

    /**
     * This method initializes
     * 
     */
    public JudgementsPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(584, 211));
        this.add(getButtonsPane(), java.awt.BorderLayout.SOUTH);
        this.add(getJudgementListBox(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Judgements Panel";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadJudgementList();
            }
        });
        
        showMessage("");
    }

    protected void updateGUIperPermissions() {
        // TODO Auto-generated method stub

    }

    protected void reloadJudgementList() {

        getJudgementListBox().removeAllRows();

        for (Judgement judgement : getContest().getJudgements()) {
            updateJudgementRow(judgement);
        }

    }

    /**
     * This method initializes JudgementListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getJudgementListBox() {
        if (judgementListBox == null) {
            judgementListBox = new MCLB();

            judgementListBox.add(getStatusPanel(), java.awt.BorderLayout.NORTH);
            Object[] cols = { "Judgement" };
            judgementListBox.addColumns(cols);
        }
        return judgementListBox;
    }

    /**
     * This method initializes buttonsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPane() {
        if (buttonsPane == null) {
            buttonsPane = new JPanel();
            buttonsPane.add(getAddButton(), null);
        }
        return buttonsPane;
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
        }
        return addButton;
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setPreferredSize(new java.awt.Dimension(20, 20));
            statusPanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return statusPanel;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    private void updateJudgementRow(final Judgement judgement) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildJudgementRow(judgement);
                int rowNumber = judgementListBox.getIndexByKey(judgement.getElementId());
                if (rowNumber == -1) {
                    judgementListBox.addRow(objects, judgement.getElementId());
                } else {
                    judgementListBox.replaceRow(objects, rowNumber);
                }
                judgementListBox.autoSizeAllColumns();
            }
        });
    }

    private Object[] buildJudgementRow(Judgement judgement) {

        // Object[] cols = { "Judgement" };

        try {
            int cols = judgementListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = judgement.toString();
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
        }
        return null;

    }

} // @jve:decl-index=0:visual-constraint="10,10"
