/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.io.Serializable;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.model.RunResultFiles;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;

/**
 * This class is a JPanel (extension of JPanePlugin) designed to display the contents of a {@link RunResult} -- that is, the result of one execution of a submitted run from a team.
 * 
 * @author John
 * 
 */
public class RunResultsPane extends JPanePlugin implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2702736596302432093L;

    private JPanel topPanel = null;

    private JLabel runIDLabel = null;

    private JLabel problemIDLabel = null;

    private JLabel judgementLabel = null;

    private JPanel detailsPanel = null;

    private JPanel compilationPanel = null;

    private JPanel executionPanel = null;

    private JPanel validationPanel = null;

    /**
     * The run execution to be displayed
     */
    private RunResultFiles theRunResults;

    private JLabel compileSuccessLabel = null;

    private JLabel compileResultCodeLabel = null;

    private JButton showCompilerStdOutButton = null;

    private JButton showCompilerStdErrButton = null;

    private JLabel executionSuccessLabel = null;

    private JLabel executionResultCodeLabel = null;

    private JButton showExecutionStdOutButton = null;

    private JButton showExecutionStdErrButton = null;

    private JLabel validationSuccessLabel = null;

    private JLabel validationResultCodeLabel = null;

    private JButton showValidationStdOutButton = null;

    private JButton showValidationStdErrButton = null;

    private JLabel validationAnswerLabel = null;

    private JLabel compileTimeLabel = null;

    private JLabel executionTimeLabel = null;

    private JLabel validationTimeLabel = null;

    /**
     * Constructs an empty RunResultsPane. This constructor is intended for internal use and is only public to comply with the JavaBean specification; it should not be called by external code.
     */
    public RunResultsPane() {
        super();
        initialize();
    }

    /**
     * Constructs a RunResultsPane displaying the contents of the specified ExecutionData object.
     * 
     * @param executionData
     *            The object containing the data to be displayed.
     */
    public RunResultsPane(RunResultFiles rrf) {
        this();
        theRunResults = rrf;
        populatePane(theRunResults);
    }

    /**
     * This method populates this RunResultsPane from the specified RunResultsFile object, or assigns default values if the received RunResultsFile parameter is null.
     */
    public void populatePane(RunResultFiles runResults) {

        String runID = "RunID: <none>";
        String problemName = "Problem: <none>";
        String judgement = "Judgement: <none>";

        if (runResults != null) {
            runID = "RunID: " + runResults.getRunId().toString();
            problemName = "Problem: " + runResults.getProblemId().toString(); // TODO: need to convert "ID" to actual NAME string...
            judgement = "Judgement: " + runResults.getJudgementId().toString();
        }

        runIDLabel.setText(runID);
        problemIDLabel.setText(problemName);
        judgementLabel.setText(judgement);

        populateCompilerResults(runResults);
        populateExecutionResults(runResults);
        populateValidationResults(runResults);
    }

    /**
     * This method populates the Compiler result fields of the RunResultsPane from the received RunResultFile object. If the pane gets successfully populated, the Compiler "show output" buttons are
     * enabled.
     * 
     * @param runResults
     *            The RunResultFiles object containing the data to be used to populate the Compiler result fields.
     */
    private void populateCompilerResults(RunResultFiles runResults) {

        String successMsg = "Success: <undefined>";
        String resultCode = "Result code: <undefined>";
        String compileTime = "Compile time: <undefined>";

        if (runResults != null) {
            successMsg = "Successful: " + !runResults.failedInCompile();
            resultCode = "Result code: " + runResults.getCompileResultCode();
            compileTime = "Compile time(ms): " + runResults.getCompileTimeMS();
        }

        compileSuccessLabel.setText(successMsg);
        compileResultCodeLabel.setText(resultCode);
        compileTimeLabel.setText(compileTime);

        if (runResults != null) {
            showCompilerStdErrButton.setEnabled(true);
            showCompilerStdOutButton.setEnabled(true);
        } else {
            showCompilerStdErrButton.setEnabled(false);
            showCompilerStdOutButton.setEnabled(false);
        }
    }

    /**
     * This method populates the Execution result fields of the RunResultsPane from the received RunResultFile object. If the pane gets successfully populated, the Execution "show output" buttons are
     * enabled.
     * 
     * @param runResults
     *            The RunResultFiles object containing the data to be used to populate the Execution result fields.
     */
    private void populateExecutionResults(RunResultFiles runResults) {

        String successMsg = "Success: <undefined>";
        String resultCode = "Result code: <undefined>";
        String executeTime = "Execution time: <undefined>";

        if (runResults != null) {
            successMsg = "Successful: " + !runResults.failedInExecute();
            resultCode = "Result code: " + runResults.getExecutionResultCode();
            executeTime = "Execution time(ms): " + runResults.getExecuteTimeMS();
        }

        executionSuccessLabel.setText(successMsg);
        executionResultCodeLabel.setText(resultCode);
        executionTimeLabel.setText(executeTime);

        if (runResults != null) {
            showExecutionStdErrButton.setEnabled(true);
            showExecutionStdOutButton.setEnabled(true);
        } else {
            showExecutionStdErrButton.setEnabled(false);
            showExecutionStdOutButton.setEnabled(false);
        }

    }

    /**
     * This method populates the Validation result fields of the RunResultsPane from the received RunResultFile object. If the pane gets successfully populated, the Validation "show output" buttons
     * are enabled.
     * 
     * @param runResults
     *            The RunResultFiles object containing the data to be used to populate the Validation result fields.
     */
    private void populateValidationResults(RunResultFiles runResults) {

        String successMsg = "Success: <undefined>";
        String resultCode = "Result code: <undefined>";
        String validationTime = "Validation time: <undefined>";
        String validationAnswer = "Judgement: <undefined>";

        if (runResults != null) {
            successMsg = "Successful: " + !runResults.failedInValidating();
            resultCode = "Result code: " + runResults.getValidationResultCode();
            validationTime = "Validation time(ms): " + runResults.getValidateTimeMS();
            validationAnswer = "Judgement: " + getJudgement(runResults);
        }

        validationSuccessLabel.setText(successMsg);
        validationResultCodeLabel.setText(resultCode);
        validationTimeLabel.setText(validationTime);
        validationAnswerLabel.setText(validationAnswer);

        if (runResults != null) {
            showValidationStdErrButton.setEnabled(true);
            showValidationStdOutButton.setEnabled(true);
        } else {
            showValidationStdErrButton.setEnabled(false);
            showValidationStdOutButton.setEnabled(false);
        }

    }

    /**
     * Returns a String containing the judgement specified in the received RunResultFiles object.
     * 
     * @param runResult
     * @return
     */
    private String getJudgement(RunResultFiles runResult) {
        return runResult.getJudgementId().toString(); // TODO: map judgementID into judgement name (string)
    }

    /**
     * This method clears the RunResultsPane contents to the "default" (unknown) state.
     */
    public void clear() {
        populatePane(null);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        TitledBorder titledBorder1 = javax.swing.BorderFactory.createTitledBorder(null, "Run Results", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.red);
        titledBorder1.setTitleFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(616, 600));
        this.setMinimumSize(new java.awt.Dimension(600, 400));
        this.setPreferredSize(new java.awt.Dimension(600, 600));
        this.setBorder(titledBorder1);
        this.add(getTopPanel(), java.awt.BorderLayout.NORTH);
        this.add(getDetailsPanel(), java.awt.BorderLayout.CENTER);

    }

    /**
     * Return the title for this pane
     * 
     * @return A String giving the pane's title
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#getPluginTitle()
     */
    @Override
    public String getPluginTitle() {
        return "Run Results Pane";
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(30);
            judgementLabel = new JLabel();
            judgementLabel.setText("Judgement: <none>");
            judgementLabel.setPreferredSize(new java.awt.Dimension(139, 25));
            judgementLabel.setMaximumSize(new java.awt.Dimension(139, 25));
            judgementLabel.setMinimumSize(new java.awt.Dimension(139, 25));
            judgementLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            problemIDLabel = new JLabel();
            problemIDLabel.setText("Problem: <none>");
            problemIDLabel.setPreferredSize(new java.awt.Dimension(118, 25));
            problemIDLabel.setMaximumSize(new java.awt.Dimension(118, 25));
            problemIDLabel.setMinimumSize(new java.awt.Dimension(118, 25));
            problemIDLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            runIDLabel = new JLabel();
            runIDLabel.setText("Run ID: <none>");
            runIDLabel.setMinimumSize(new java.awt.Dimension(106, 25));
            runIDLabel.setMaximumSize(new java.awt.Dimension(106, 25));
            runIDLabel.setPreferredSize(new java.awt.Dimension(106, 25));
            runIDLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            topPanel = new JPanel();
            topPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 0));
            topPanel.setMinimumSize(new java.awt.Dimension(483, 50));
            topPanel.setPreferredSize(new java.awt.Dimension(483, 40));
            topPanel.setLayout(flowLayout);
            topPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
            topPanel.add(runIDLabel, null);
            topPanel.add(problemIDLabel, null);
            topPanel.add(judgementLabel, null);
        }
        return topPanel;
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDetailsPanel() {
        if (detailsPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(5);
            gridLayout.setColumns(3);
            detailsPanel = new JPanel();
            detailsPanel.setPreferredSize(new java.awt.Dimension(668, 500));
            detailsPanel.setLayout(gridLayout);
            detailsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
            detailsPanel.add(getCompilationPanel(), null);
            detailsPanel.add(getExecutionPanel(), null);
            detailsPanel.add(getValidationPanel(), null);
        }
        return detailsPanel;
    }

    /**
     * This method initializes compilationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCompilationPanel() {
        if (compilationPanel == null) {
            compileTimeLabel = new JLabel();
            compileTimeLabel.setText("Compile time (ms): <undefined>");
            compileTimeLabel.setMaximumSize(new java.awt.Dimension(180, 30));
            compileTimeLabel.setMinimumSize(new java.awt.Dimension(180, 30));
            compileTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            compileTimeLabel.setPreferredSize(new java.awt.Dimension(180, 30));
            compileResultCodeLabel = new JLabel();
            compileResultCodeLabel.setText("Result Code: <undefined>");
            compileResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            compileResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            compileResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            compileResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            compileSuccessLabel = new JLabel();
            compileSuccessLabel.setText("Successful: <undefined> ");
            compileSuccessLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            compileSuccessLabel.setAlignmentY(3.0F);
            compileSuccessLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            compileSuccessLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            compileSuccessLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            compileSuccessLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            compilationPanel = new JPanel();
            compilationPanel.setLayout(new BoxLayout(getCompilationPanel(), BoxLayout.Y_AXIS));
            compilationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Compilation ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.blue));
            compilationPanel.add(compileSuccessLabel, null);
            compilationPanel.add(compileResultCodeLabel, null);
            compilationPanel.add(compileTimeLabel, null);
            compilationPanel.add(getShowCompilerStdOutButton(), null);
            compilationPanel.add(getShowCompilerStdErrButton(), null);
        }
        return compilationPanel;
    }

    /**
     * This method initializes executionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExecutionPanel() {
        if (executionPanel == null) {
            executionTimeLabel = new JLabel();
            executionTimeLabel.setText("Execution time (ms): <undefined>");
            executionTimeLabel.setMinimumSize(new java.awt.Dimension(190, 30));
            executionTimeLabel.setMaximumSize(new java.awt.Dimension(190, 30));
            executionTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionTimeLabel.setPreferredSize(new java.awt.Dimension(190, 30));
            executionResultCodeLabel = new JLabel();
            executionResultCodeLabel.setText("Result Code: <undefined>");
            executionResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionSuccessLabel = new JLabel();
            executionSuccessLabel.setText("Successful: <undefined>");
            executionSuccessLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            executionSuccessLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            executionSuccessLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionSuccessLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            executionPanel = new JPanel();
            executionPanel.setLayout(new BoxLayout(getExecutionPanel(), BoxLayout.Y_AXIS));
            executionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Execution ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.blue));
            executionPanel.add(executionSuccessLabel, null);
            executionPanel.add(executionResultCodeLabel, null);
            executionPanel.add(executionTimeLabel, null);
            executionPanel.add(getShowExecutionStdOutButton(), null);
            executionPanel.add(getShowExecutionStdErrButton(), null);
        }
        return executionPanel;
    }

    /**
     * This method initializes validationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getValidationPanel() {
        if (validationPanel == null) {
            validationTimeLabel = new JLabel();
            validationTimeLabel.setText("Validation time (ms): <undefined>");
            validationTimeLabel.setMinimumSize(new java.awt.Dimension(200, 30));
            validationTimeLabel.setMaximumSize(new java.awt.Dimension(200, 30));
            validationTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationTimeLabel.setPreferredSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel = new JLabel();
            validationAnswerLabel.setText("Judgement: <undefined>");
            validationAnswerLabel.setMinimumSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel.setPreferredSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationAnswerLabel.setMaximumSize(new java.awt.Dimension(200, 30));
            validationResultCodeLabel = new JLabel();
            validationResultCodeLabel.setText("Result Code: <undefined>");
            validationResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            validationResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            validationResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            validationSuccessLabel = new JLabel();
            validationSuccessLabel.setText("Successful: <undefined>");
            validationSuccessLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            validationSuccessLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            validationSuccessLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationSuccessLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            TitledBorder titledBorder = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), " Validation",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.blue);
            titledBorder.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            validationPanel = new JPanel();
            validationPanel.setLayout(new BoxLayout(getValidationPanel(), BoxLayout.Y_AXIS));
            validationPanel.setBorder(titledBorder);
            validationPanel.add(validationSuccessLabel, null);
            validationPanel.add(validationResultCodeLabel, null);
            validationPanel.add(validationTimeLabel, null);
            validationPanel.add(validationAnswerLabel, null);
            validationPanel.add(getShowValidationStdOutButton(), null);
            validationPanel.add(getShowValidationStdErrButton(), null);
        }
        return validationPanel;
    }

    /**
     * This method initializes showStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowCompilerStdOutButton() {
        if (showCompilerStdOutButton == null) {
            showCompilerStdOutButton = new JButton();
            showCompilerStdOutButton.setText("Show Stdout");
            showCompilerStdOutButton.setEnabled(false);
            showCompilerStdOutButton.setMaximumSize(new java.awt.Dimension(106, 30));
        }
        return showCompilerStdOutButton;
    }

    /**
     * This method initializes showStdErrButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowCompilerStdErrButton() {
        if (showCompilerStdErrButton == null) {
            showCompilerStdErrButton = new JButton();
            showCompilerStdErrButton.setText("Show Stderr");
            showCompilerStdErrButton.setMinimumSize(new java.awt.Dimension(106, 26));
            showCompilerStdErrButton.setPreferredSize(new java.awt.Dimension(106, 26));
            showCompilerStdErrButton.setEnabled(false);
            showCompilerStdErrButton.setMaximumSize(new java.awt.Dimension(106, 30));
        }
        return showCompilerStdErrButton;
    }

    /**
     * This method initializes showExecutionStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowExecutionStdOutButton() {
        if (showExecutionStdOutButton == null) {
            showExecutionStdOutButton = new JButton();
            showExecutionStdOutButton.setMaximumSize(new java.awt.Dimension(106, 30));
            showExecutionStdOutButton.setPreferredSize(new java.awt.Dimension(106, 30));
            showExecutionStdOutButton.setText("Show Stdout");
            showExecutionStdOutButton.setEnabled(false);
            showExecutionStdOutButton.setMinimumSize(new java.awt.Dimension(106, 30));
        }
        return showExecutionStdOutButton;
    }

    /**
     * This method initializes showExecutionStdErrButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowExecutionStdErrButton() {
        if (showExecutionStdErrButton == null) {
            showExecutionStdErrButton = new JButton();
            showExecutionStdErrButton.setText("Show Stderr");
            showExecutionStdErrButton.setMinimumSize(new java.awt.Dimension(105, 30));
            showExecutionStdErrButton.setPreferredSize(new java.awt.Dimension(105, 30));
            showExecutionStdErrButton.setEnabled(false);
            showExecutionStdErrButton.setMaximumSize(new java.awt.Dimension(105, 30));
        }
        return showExecutionStdErrButton;
    }

    /**
     * This method initializes showValidationStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowValidationStdOutButton() {
        if (showValidationStdOutButton == null) {
            showValidationStdOutButton = new JButton();
            showValidationStdOutButton.setText("Show Stdout");
            showValidationStdOutButton.setMaximumSize(new java.awt.Dimension(106, 30));
            showValidationStdOutButton.setMinimumSize(new java.awt.Dimension(106, 30));
            showValidationStdOutButton.setEnabled(false);
            showValidationStdOutButton.setPreferredSize(new java.awt.Dimension(106, 30));
        }
        return showValidationStdOutButton;
    }

    /**
     * This method initializes showValidationStdErrButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowValidationStdErrButton() {
        if (showValidationStdErrButton == null) {
            showValidationStdErrButton = new JButton();
            showValidationStdErrButton.setText("Show Stderr");
            showValidationStdErrButton.setMinimumSize(new java.awt.Dimension(105, 30));
            showValidationStdErrButton.setEnabled(false);
            showValidationStdErrButton.setMaximumSize(new java.awt.Dimension(105, 30));
        }
        return showValidationStdErrButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
