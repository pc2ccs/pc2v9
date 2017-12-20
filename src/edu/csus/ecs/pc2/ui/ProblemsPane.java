package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.Alignment;

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
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.cellRenderer.MCLBInputValidationStatusCellRenderer;

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

    private JButton reportButton = null;

    private JPanel centerPanel;

    private JButton setJudgesDataPathButton;

    private EditJudgesDataFilePathFrame editCDPPathFrame;

    private JButton runInputValidatorsButton;

    private RunInputValidatorsFrame runInputValidatorsFrame;

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
        this.setSize(new Dimension(665, 229));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        add(getCenterPanel(), BorderLayout.CENTER);
        this.add(getProblemButtonPane(), java.awt.BorderLayout.SOUTH);

        editProblemFrame = new EditProblemFrame();
        runInputValidatorsFrame = new RunInputValidatorsFrame();
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
            problemButtonPane.add(getSetJudgesDataPathButton());
            problemButtonPane.add(getRunInputValidatorsButton());
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

            Object[] cols = { "Problem Name", "# Test Cases", "Input Method", "Judging Type", "Short", "Time Limit", "Input Validation", "I.V. Command", "Output Validator", "O.V. Command", };
            problemListBox.addColumns(cols);

            problemListBox.getColumnInfo(1).setAlignment(Alignment.CENTER);
            problemListBox.getColumnInfo(2).setAlignment(Alignment.CENTER);
            problemListBox.getColumnInfo(3).setAlignment(Alignment.CENTER);
            problemListBox.getColumnInfo(4).setAlignment(Alignment.CENTER);
            problemListBox.getColumnInfo(5).setAlignment(Alignment.CENTER);
            problemListBox.getColumnInfo(6).setAlignment(Alignment.CENTER);

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

    public void updateRunInputValidatorsProblemRow(final Problem problem) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // build a row of InputValidatorResults for the specified problem
                Object[] rowData = buildRunInputValidatorsResultsProblemRow(problem);

                // add the row to the InputValidatorResults table model in the RunInputValidators pane
                ((AllProblemsInputValidationResultsTableModel) (getRunInputValidatorsFrame().getRunInputValidatorsPane().getInputValidatorResultsTable().getModel())).addRow(rowData);

                // tell the table to update
                ((AllProblemsInputValidationResultsTableModel) (getRunInputValidatorsFrame().getRunInputValidatorsPane().getInputValidatorResultsTable().getModel())).fireTableDataChanged();
            }
        });

    }

    @SuppressWarnings("unused")
    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    protected Object[] buildProblemRow(Problem problem) {
        // Object[] cols = { "Problem Name", "Data File", "Answer File", "Input Method", "Judging Type", "short", "Time Limit", "SVTJ", "Validator" };
        // Object[] cols = { "Problem Name", "Data File", "Answer File", "Input Method", "Judging Type", "short", "Time Limit", "SVTJ", "Validator Prog", "Validator Command Line" };
        // Object[] cols = { "Problem Name", "Data File", "Answer File", "Input Method", "Judging Type", "Short", "Time Limit", "SVTJ", "Output Validator", "O.V. Command", "Input Validation", "I.V.
        // Command" };
        // Object[] cols = { "Problem Name", "# Test Cases", "Input Method", "Judging Type", "Short", "Time Limit", "Input Validation", "I.V. Command", "Output Validator", "O.V. Command", };

        int numberColumns = problemListBox.getColumnCount();
        Object[] c = new Object[numberColumns];
        int i = 0;

        // problem name
        String name = problem.getDisplayName();
        if (!problem.isActive()) {
            name = "[HIDDEN] " + name;
        }
        c[i++] = name;

        // num test cases
        // c[i++] = new MCLBCenteredStringCellRenderer(new Integer(problem.getNumberTestCases()).toString());
        c[i++] = new Integer(problem.getNumberTestCases()).toString();

        // input method
        String inputMethod = "";
        if (problem.isReadInputDataFromSTDIN()) {
            inputMethod = "STDIN";
        } else if (problem.getDataFileName() != null) {
            inputMethod = "File I/O";
        } else {
            inputMethod = "(none)";
        }
        // c[i++] = new MCLBCenteredStringCellRenderer(inputMethod);
        c[i++] = inputMethod;

        // judging type
        String judgingType = "";
        if (problem.isComputerJudged()) {
            judgingType = "Computer";
            if (problem.isManualReview()) {
                judgingType = "Computer+Manual";
                if (problem.isPrelimaryNotification()) {
                    judgingType = "Computer+Manual/Notify";
                }
            }
        } else if (problem.isValidatedProblem()) {
            judgingType = "Manual w/Val.";
        } else {
            judgingType = "Manual";
        }
        // c[i++] = new MCLBCenteredStringCellRenderer(judgingType);
        c[i++] = judgingType;

        // problem short name
        // c[i++] = new MCLBCenteredStringCellRenderer(problem.getShortName());
        c[i++] = problem.getShortName();

        // problem time limit
        // c[i++] = new MCLBCenteredStringCellRenderer(Integer.toString(problem.getTimeOutInSeconds()));
        c[i++] = Integer.toString(problem.getTimeOutInSeconds());

        // input validation status
        if (problem.isProblemHasInputValidator()) {
            c[i++] = new MCLBInputValidationStatusCellRenderer(problem.getInputValidationStatus());
        } else {
            // c[i++] = new MCLBCenteredStringCellRenderer("<none>");
            c[i++] = "<none>";
        }

        // input validator command line
        String inputValidatorCommandLine = "";
        if (problem.isProblemHasInputValidator()) {
            inputValidatorCommandLine = problem.getInputValidatorCommandLine();
        }
        c[i++] = inputValidatorCommandLine;

        // output validator program
        String validatorProgramName = "<none>";
        if (problem.isValidatedProblem()) {
            validatorProgramName = problem.getValidatorProgramName();
        }
        c[i++] = validatorProgramName;

        // output validator command line
        String validatorCommandLine = "";
        if (problem.isValidatedProblem()) {
            validatorCommandLine = problem.getValidatorCommandLine();
        }
        c[i++] = validatorCommandLine;

        return c;
    }

    private Object[] buildRunInputValidatorsResultsProblemRow(Problem problem) {
        // from AllProblemsInputValidationResultsTableModel:
        // String[] colNames = { "Select", "Problem", "Overall Result", "Pass/Fail Count", "Failed Files...", "Input Validator", "I.V. Command" };

        int numberColumns = ((AllProblemsInputValidationResultsTableModel) (getRunInputValidatorsFrame().getRunInputValidatorsPane().getInputValidatorResultsTable().getModel())).getColumnCount();

        Object[] c = new Object[numberColumns];
        int i = 0;

        c[i++] = new Boolean(true);

        c[i++] = problem.getShortName();

        c[i++] = problem.getInputValidationStatus();

        c[i++] = getPassFailCount(problem);

        c[i++] = "<unknown>";

        if (problem.isProblemHasInputValidator()) {
            c[i++] = problem.getInputValidatorProgramName();
        } else {
            c[i++] = "<none>";
        }

        c[i++] = problem.getInputValidatorCommandLine();

        return c;
    }

    private String getPassFailCount(Problem problem) {
        String retStr = "";
        if (problem.getInputValidationStatus() == InputValidationStatus.NOT_TESTED || problem.getInputValidationStatus() == InputValidationStatus.ERROR) {
            retStr = "N/A";
        } else {
            retStr = "?/?";
        }

        return retStr;
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
                getController().addNewProblem(newProblem, newProblemDataFiles);
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to clone problem, check log (" + e.getMessage() + ")");
        }
    }

    private String promptForProblemName(String problemName) {
        String s = (String) JOptionPane.showInputDialog(this, "Enter new name:\n", "Copying problem '" + problemName + "'", JOptionPane.QUESTION_MESSAGE, null, null, null);

        return s;
    }

    private void reloadListBox() {
        problemListBox.removeAllRows();
        Problem[] problems = getContest().getProblems();

        for (Problem problem : problems) {
            addProblemRow(problem);
        }

        if (problems.length > 0) {
            getRunInputValidatorsButton().setEnabled(true);
        } else {
            getRunInputValidatorsButton().setEnabled(false);
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
        runInputValidatorsFrame.setContestAndController(inContest, inController);

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

        // boolean enabled = !StringUtilities.stringSame(savedJudgeCDPLocation, judgeCDPLocationTextField.getText());

    }

    private void updateGUIperPermissions() {
        getAddButton().setVisible(isAllowed(Permission.Type.ADD_PROBLEM));
        getEditButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
        getCopyButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
        getSetJudgesDataPathButton().setVisible(isAllowed(Permission.Type.EDIT_PROBLEM));
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
        // bring up the ui, let the user add/cancel the copied problem
        editProblemFrame.setProblem(null);
        editProblemFrame.setVisible(true);

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

        // showStackTrace();
        // System.out.println ("Begin ProblemsPane.editSelectedProblem()...");

        int selectedIndex = problemListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a problem to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) problemListBox.getKeys()[selectedIndex];
            Problem problemToEdit = getContest().getProblem(elementId);

            ProblemDataFiles newProblemDataFiles = getController().getProblemDataFiles(problemToEdit);

            // System.out.println ("ProblemDataFiles = " + newProblemDataFiles);

            // bring up the ui, let the user add/cancel the copied problem
            editProblemFrame.setProblemCopy(problemToEdit, newProblemDataFiles);
            if (problemToEdit.isUsingExternalDataFiles()) {
                editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName() + " (" + problemToEdit.getExternalDataFileLocation() + ")");
            } else {
                editProblemFrame.setTitle("Edit Problem " + problemToEdit.getDisplayName());
            }
            editProblemFrame.setVisible(true);

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit problem, check log (" + e.getMessage() + ")");
        }

        // System.out.println ("End ProblemsPane.editSelectedProblem()...");

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
            // SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            // refreshJudgesCDPField();
            // }
            // });

        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            // SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            // refreshJudgesCDPField();
            // }
            // });
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
                    updateRunInputValidatorsProblemRow(event.getProblem());
                    getRunInputValidatorsButton().setEnabled(true);
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
        // refreshJudgesCDPField();
    }

    // private void refreshJudgesCDPField() {
    //
    // // Assumption - this is run on the Swing Thread
    // try {
    // savedJudgeCDPLocation = getJudgeCDPLocation();
    // judgeCDPLocationTextField.setText(savedJudgeCDPLocation);
    // enableUpdateButtons();
    // } catch (Exception e) {
    // getLog().log(Log.WARNING, "Problem fetching judges CDP Location ", e);
    // }
    //
    // }

    // private String getJudgeCDPLocation() {
    //
    // String value = null;
    //
    // ContestInformation info = getContest().getContestInformation();
    // if (info != null) {
    // value = info.getJudgeCDPBasePath();
    // }
    // if (value == null){
    // value = "";
    // }
    // return value;
    // }

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
        Utilities.viewReport(new ProblemsReport(), "Problems Report ", getContest(), getController());
    }

    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout(0, 0));
            centerPanel.add(getProblemListBox(), BorderLayout.CENTER);
        }
        return centerPanel;
    }

    // private void updateContestInformation() {
    // ContestInformation contestInformation = getFromFields();
    // getController().updateContestInformation(contestInformation);
    // }
    //
    // private ContestInformation getFromFields() {
    //
    // ContestInformation newInfo = getContest().getContestInformation();
    // return newInfo;
    // }

    private JButton getSetJudgesDataPathButton() {
        if (setJudgesDataPathButton == null) {
            setJudgesDataPathButton = new JButton("Set Judge's Data Path");
            setJudgesDataPathButton.setMnemonic(KeyEvent.VK_S);
            setJudgesDataPathButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editJudgesDataFilePath();
                }
            });
            setJudgesDataPathButton.setToolTipText("Specify a path to external data files on the Judge machines");
        }
        return setJudgesDataPathButton;
    }

    /**
     * Displays a frame which allows the user to edit the currently-defined CDP paths on both the Admin machine and the Judge's machines.
     */
    private void editJudgesDataFilePath() {
        getEditCDPFrame().setContestAndController(getContest(), getController());
        getEditCDPFrame().loadCurrentCDPPathsIntoGUI();
        getEditCDPFrame().setVisible(true);
    }

    /**
     * Returns a singleton instance of the Frame used to edit the CDP path(s).
     * 
     * @return the EditCDPPathFrame
     */
    private EditJudgesDataFilePathFrame getEditCDPFrame() {
        if (editCDPPathFrame == null) {
            editCDPPathFrame = new EditJudgesDataFilePathFrame();
        }
        return editCDPPathFrame;
    }

    /**
     * Returns a singleton instance of the Frame used to display the results of running the input validators.
     * 
     * @return a RunInputValidatorFrame
     */
    private RunInputValidatorsFrame getRunInputValidatorsFrame() {
        if (runInputValidatorsFrame == null) {
            runInputValidatorsFrame = new RunInputValidatorsFrame();
        }
        return runInputValidatorsFrame;
    }

    /**
     * Displays the class, method, and line number of the method that called this method, along with the same information for the method that called THAT method.
     */
    @SuppressWarnings("unused")
    private void showStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String callingClassName = stackTraceElements[2].getClassName();
        String callingMethodName = stackTraceElements[2].getMethodName();
        int callingMethodLineNumber = stackTraceElements[2].getLineNumber();
        System.out.println("\nIn " + callingClassName + "." + callingMethodName + "(line " + callingMethodLineNumber + ")");

        callingClassName = stackTraceElements[3].getClassName();
        callingMethodName = stackTraceElements[3].getMethodName();
        callingMethodLineNumber = stackTraceElements[3].getLineNumber();
        System.out.println("called from " + callingClassName + "." + callingMethodName + "(line " + callingMethodLineNumber + ")");

    }

    private JButton getRunInputValidatorsButton() {
        if (runInputValidatorsButton == null) {
            runInputValidatorsButton = new JButton("Run Input Validators...");
            runInputValidatorsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    runInputValidators();
                }
            });
            runInputValidatorsButton.setEnabled(false);
            runInputValidatorsButton.setVisible(false); // hide for Master branch until fully working
            runInputValidatorsButton.setToolTipText("Execute the Input Validator for each problem against the data files defined for that problem");
        }
        return runInputValidatorsButton;
    }

    private void runInputValidators() {
        getRunInputValidatorsFrame().setContestAndController(getContest(), getController());
        getRunInputValidatorsFrame().setVisible(true);
    }

}
