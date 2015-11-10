package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * View Problems.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel problemButtonPane = null;

    private MCLB problemListBox = null;

    private JButton addButton = null;

    private JButton copyButton = null;

    private JButton editButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private Log log = null;

    private EditProblemFrame editProblemFrame = null;
    
    private EditProblemFrameNew editProblemFrameNew = null;

    private JButton reportButton = null;
    private JPanel centerPanel;

    private JButton setCDPPathButton;

    private EditCDPPathFrame editCDPPathFrame;
    
    /**
     * This method initializes
     * 
     */
    public ProblemsPane() {
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
        add(getCenterPanel(), BorderLayout.CENTER);
        this.add(getProblemButtonPane(), java.awt.BorderLayout.SOUTH);

        editProblemFrame = new EditProblemFrame();
        
        editProblemFrameNew = new EditProblemFrameNew();

    }

    @Override
    public String getPluginTitle() {
        return "Problems Pane";
    }

    /**
     * This method initializes problemButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemButtonPane() {
        if (problemButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            problemButtonPane = new JPanel();
            problemButtonPane.setLayout(flowLayout);
            problemButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            problemButtonPane.add(getAddButton(), null);
            problemButtonPane.add(getCopyButton(), null);
            problemButtonPane.add(getEditButton(), null);
            problemButtonPane.add(getReportButton(), null);
            problemButtonPane.add(getSetCDPPathButton());
        }
        return problemButtonPane;
    }

    /**
     * This method initializes problemListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getProblemListBox() {
        if (problemListBox == null) {
            problemListBox = new MCLB();

            Object[] cols = { "Problem Name", "Data File", "Answer File", "Input Method", "Judging Type", "short", "Time Limit", "SVTJ", "Validator" };
            problemListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the problems are is to NOT sort them. Later we can add a sorter per ProblemDisplayList somehow.
             */

            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // // HeapSorter numericStringSorter = new HeapSorter();
            // // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Display Name
            // problemListBox.setColumnSorter(0, sorter, 1);
            // // Compiler Command Line
            // problemListBox.setColumnSorter(1, sorter, 2);
            // // Exe Name
            // problemListBox.setColumnSorter(2, sorter, 3);
            // // Execute Command Line
            // problemListBox.setColumnSorter(3, sorter, 4);
            problemListBox.autoSizeAllColumns();

        }
        return problemListBox;
    }

    public void updateProblemRow(final Problem problem) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildProblemRow(problem);
                int rowNumber = problemListBox.getIndexByKey(problem.getElementId());
                if (rowNumber == -1) {
                    problemListBox.addRow(objects, problem.getElementId());
                } else {
                    problemListBox.replaceRow(objects, rowNumber);
                }
                problemListBox.autoSizeAllColumns();
                // problemListBox.sort();
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

    protected Object[] buildProblemRow(Problem problem) {
        // Object[] cols = { "Problem Name", "Data File", "Answer File", "Input Method", "Judging Type", "short", "Time Limit", "SVTJ", "Validator" };

        int numberColumns = problemListBox.getColumnCount();
        Object[] c = new String[numberColumns];
        int i = 0;

        String name = problem.getDisplayName();
        if (! problem.isActive()){
            name = "[HIDDEN] "+name;
        }
        c[i++] = name;
        c[i++] = problem.getDataFileName();
        c[i++] = problem.getAnswerFileName();
        String inputMethod = "";
        if (problem.isReadInputDataFromSTDIN()) {
            inputMethod = "STDIN";
        } else if (problem.getDataFileName() != null) {
            inputMethod = "File I/O";
        } else {
            inputMethod = "(none)";
        }
        c[i++] = inputMethod;
        String judgingType = "";
        if (problem.isComputerJudged()){
            judgingType = "Computer";
            if (problem.isManualReview()){
                judgingType = "Computer+Manual";
                if (problem.isPrelimaryNotification()){
                    judgingType = "Computer+Manual/Notify";
                }
            }
        } else if (problem.isValidatedProblem()){
            judgingType = "Manual w/Val.";
        } else {
            judgingType = "Manual";
        }
        c[i++] = judgingType;
        c[i++] = problem.getShortName();
        c[i++] = Integer.toString(problem.getTimeOutInSeconds());
        c[i++] = yesNoString(problem.isShowValidationToJudges());
        String validatorName = problem.getValidatorProgramName();
        if (problem.isValidatedProblem()) {
            if (! problem.isUsingPC2Validator()) {
                validatorName = problem.getValidatorCommandLine();
            }
        }
        
        c[i++] = validatorName;

        return c;
    }

    protected void copySelectedProblem() {
        int selectedIndex = problemListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a problem to copy");
            return;
        }

        try {
            // would be nice to select the new row, but this will do
            problemListBox.deselectAllRows();
            ElementId elementId = (ElementId) problemListBox.getKeys()[selectedIndex];
            Problem sourceProblem = getContest().getProblem(elementId);
            String newProblemName = promptForProblemName(sourceProblem.getDisplayName());
            if (newProblemName == null || newProblemName.trim().length() == 0) {
                showMessage("Copy Aborted.");
            } else {
                Problem newProblem = sourceProblem.copy(newProblemName);
                ProblemDataFiles pdf = getController().getProblemDataFiles(sourceProblem);
                ProblemDataFiles newProblemDataFiles = null;
                if (pdf != null) {
                    newProblemDataFiles = pdf.copy(newProblem);
                }
                
                // just bring up the ui, let the user add/cancel the copied problem
                editProblemFrame.setProblemCopy(newProblem, newProblemDataFiles);
                editProblemFrame.setVisible(true);

            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to clone problem, check log ("+e.getMessage()+")");
        }
    }
 
    private String promptForProblemName(String problemName) {
        String s = (String)JOptionPane.showInputDialog(
                this,
                "Enter new name:\n",
                "Copying problem '"+problemName+"'",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null);

        return s;
    }

    private void reloadListBox() {
        problemListBox.removeAllRows();
        Problem[] problems = getContest().getProblems();

        for (Problem problem : problems) {
            addProblemRow(problem);
        }
    }

    private void addProblemRow(Problem problem) {
        Object[] objects = buildProblemRow(problem);
        problemListBox.addRow(objects, problem.getElementId());
        problemListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        editProblemFrame.setContestAndController(inContest, inController);
        editProblemFrameNew.setContestAndController(inContest, inController);

        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                populateGUI();
                enableUpdateButtons();
            }
        });
    }
    
    
    protected void enableUpdateButtons() {

//        boolean enabled = !StringUtilities.stringSame(savedJudgeCDPLocation, judgeCDPLocationTextField.getText());

    }

    private void updateGUIperPermissions() {
        getAddButton().setVisible(isAllowed(Permission.Type.ADD_PROBLEM));
        getEditButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
        getCopyButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
        getSetCDPPathButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
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
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.setToolTipText("Add new Problem definition");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    protected void addProblem() {
        if (Utilities.isDebugMode()) {
            int result = FrameUtilities.yesNoCancelDialog(this, "Do you want to use  EditProblemFrameNew ? ", "TEMPORARY DEBUGGING THING. debug 22");

            if (result == JOptionPane.YES_OPTION) {
                editProblemFrameNew.setProblem(null);
                editProblemFrameNew.setVisible(true);
            } else {
                editProblemFrame.setProblem(null);
                editProblemFrame.setVisible(true);
            }
        } else {
            // just bring up the ui, let the user add/cancel the copied problem
            editProblemFrame.setProblem(null);
            editProblemFrame.setVisible(true);
        }
    }

    /**
     * This method initializes copyButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCopyButton() {
        if (copyButton == null) {
            copyButton = new JButton();
            copyButton.setText("Copy");
            copyButton.setMnemonic(KeyEvent.VK_C);
            copyButton.setToolTipText("Copy settings from an existing problem to a new problem");
            copyButton.setActionCommand("Copy");
            copyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    copySelectedProblem();
                }
            });
        }
        return copyButton;
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
            editButton.setMnemonic(KeyEvent.VK_E);
            editButton.setToolTipText("Edit existing Problem definition");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedProblem();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedProblem() {

        int selectedIndex = problemListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a problem to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) problemListBox.getKeys()[selectedIndex];
            Problem problemToEdit = getContest().getProblem(elementId);

            ProblemDataFiles newProblemDataFiles = getController().getProblemDataFiles(problemToEdit);
            
            if (Utilities.isDebugMode()) {

                int result = FrameUtilities.yesNoCancelDialog(this, "Do you want to use  EditProblemFrameNew ? ", "TEMPORARY DEBUGGING THING. debug 22");

                if (result == JOptionPane.YES_OPTION) {
                    editProblemFrameNew.setProblemCopy(problemToEdit, newProblemDataFiles);
                    if (problemToEdit.isUsingExternalDataFiles()) {
                        editProblemFrameNew.setTitle("Edit Problem " + problemToEdit.getDisplayName()+" ("+problemToEdit.getExternalDataFileLocation()+")");
                    } else {
                        editProblemFrameNew.setTitle("Edit Problem " + problemToEdit.getDisplayName());
                    }
                    editProblemFrameNew.setVisible(true);
                } else {
                    editProblemFrame.setProblemCopy(problemToEdit, newProblemDataFiles);
                    if (problemToEdit.isUsingExternalDataFiles()) {
                        editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName()+" ("+problemToEdit.getExternalDataFileLocation()+")");
                    } else {
                        editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName());
                    }
                    editProblemFrame.setVisible(true);
                }
            } else {
                // just bring up the ui, let the user add/cancel the copied problem
                editProblemFrame.setProblemCopy(problemToEdit, newProblemDataFiles);
                if (problemToEdit.isUsingExternalDataFiles()) {
                    editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName()+" ("+problemToEdit.getExternalDataFileLocation()+")");
                } else {
                    editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName());
                }
                editProblemFrame.setVisible(true);
            }
            
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit problem, check log ("+e.getMessage()+")");
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
        JOptionPane.showMessageDialog(this, string, "Problems pane message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 
     * @author ICPC
     *
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {
        
        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // ignore
            
        }
        
        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignore
            
        }
        
        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    refreshJudgesCDPField();
//                }
//            });
            
        }
        
        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    refreshJudgesCDPField();
//                }
//            });
        }
        
        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            // ignore
            
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateProblemRow(event.getProblem());
                }
            });
        }

        public void problemChanged(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateProblemRow(event.getProblem());
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    populateGUI();
                }
            });
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    populateGUI();
                }
            }); 
        }
    }
    
    protected void populateGUI() {
        
        // Assumption - this is run on the Swing Thread
        
        reloadListBox();
//        refreshJudgesCDPField();
    }
    
//    private void refreshJudgesCDPField() {
//
//        // Assumption - this is run on the Swing Thread
//        try {
//            savedJudgeCDPLocation = getJudgeCDPLocation();
//            judgeCDPLocationTextField.setText(savedJudgeCDPLocation);
//            enableUpdateButtons();
//        } catch (Exception e) {
//            getLog().log(Log.WARNING, "Problem fetching judges CDP Location ", e);
//        }
//
//    }

//    private String getJudgeCDPLocation() {
//
//        String value = null;
//
//        ContestInformation info = getContest().getContestInformation();
//        if (info != null) {
//            value = info.getJudgeCDPBasePath();
//        }
//        if (value == null){
//            value = "";
//        }
//        return value;
//    }

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.setToolTipText("View Problems Report");
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewReports();
                }
            });
        }
        return reportButton;
    }

    protected void viewReports() {
        Utilities.viewReport(new ProblemsReport() , "Problems Report ", getContest(), getController());
    }

    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout(0, 0));
            centerPanel.add(getProblemListBox(), BorderLayout.CENTER);
        }
        return centerPanel;
    }
    
//    private void updateContestInformation() {
//        ContestInformation contestInformation = getFromFields();
//        getController().updateContestInformation(contestInformation);
//    }
//
//    private ContestInformation getFromFields() {
//        
//        ContestInformation newInfo = getContest().getContestInformation();
//        return newInfo;
//    }
    
    private JButton getSetCDPPathButton() {
        if (setCDPPathButton == null) {
        	setCDPPathButton = new JButton("Set CDP Path");
        	setCDPPathButton.setMnemonic(KeyEvent.VK_S);            
        	setCDPPathButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
                    editCDPPath();
        	    }
        	});
        	setCDPPathButton.setToolTipText("Specify a path to the root of a Contest Data Package (see https://clics.ecs.baylor.edu/index.php/CDP) ");
        }
        return setCDPPathButton;
    }
    
    /**
     * Displays a frame which allows the user to edit the currently-defined CDP paths
     * on both the Admin machine and the Judge's machines.
     */
    private void editCDPPath() {
        getEditCDPFrame().setContestAndController(getContest(), getController());
        getEditCDPFrame().loadCurrentCDPPathsIntoGUI();
        getEditCDPFrame().setVisible(true);
    }

    /** Returns a singleton instance of the Frame used to edit the CDP path(s).
     * 
     * @return the EditCDPPathFrame
     */
    private EditCDPPathFrame getEditCDPFrame() {
        if (editCDPPathFrame == null) {
            editCDPPathFrame = new EditCDPPathFrame();
        }
        return editCDPPathFrame ;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
