/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.model.RunResultFiles;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;

/**
 * This class is a JPanel (extension of JPanePlugin) designed to display the contents of 
 * a {@link RunResult} -- that is, the result of one execution of a submitted run from a team.
 * 
 * @author John
 * 
 */
public class RunResultsPane extends JPanePlugin implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2702736596302432093L;
    
    private final String defaultTitle = "Run Results";

    private JPanel compilationPanel = null;

    private JPanel executionPanel = null;

    private JPanel validationPanel = null;

    /**
     * The run execution to be displayed
     */
    private RunResultFiles theRunResults;

    private JLabel compileSuccessLabel = null;

    private JLabel compileResultCodeLabel = null;

    private JButton showCompilerOutputButton = null;

    private JLabel executionSuccessLabel = null;

    private JLabel executionResultCodeLabel = null;

    private JButton showExecutionOutputButton = null;

    private JLabel validationSuccessLabel = null;

    private JLabel validationResultCodeLabel = null;

    private JButton showValidationOutputButton = null;

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
        setTitle(defaultTitle);
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
        populatePane(theRunResults, defaultTitle);
    }

    /**
     * This method populates this RunResultsPane from the specified RunResultsFile object, or assigns default values if the received RunResultsFile parameter is null.
     */
    public void populatePane(RunResultFiles runResults, String title) {
        
        populateCompilerResults(runResults);
        populateExecutionResults(runResults);
        populateValidationResults(runResults);
        setTitle(title);
    }

    /**
     * This method sets the "Titled Border" label on the panel.
     */
    private void setTitle (String title) {
        ((TitledBorder)this.getBorder()).setTitle(title);
    }
    
    /**
     * This method populates the Compiler result fields of the RunResultsPane from the received RunResultFile object. If the pane gets successfully populated, the Compiler "show output" buttons are
     * enabled.
     * 
     * @param runResults
     *            The RunResultFiles object containing the data to be used to populate the Compiler result fields.
     */
    private void populateCompilerResults(RunResultFiles runResults) {

        String successMsg = "<HTML>Success: <FONT COLOR=BLUE>";
        String resultCode = "<HTML>Result code: <FONT COLOR=BLUE>";
        String compileTime = "<HTML>Compile time(ms): <FONT COLOR=BLUE>";

        if (runResults != null) {
            successMsg += !runResults.failedInCompile();
            resultCode +=  runResults.getCompileResultCode();
            compileTime += runResults.getCompileTimeMS();
        }
        successMsg += "</FONT></HTML>";
        resultCode += "</FONT></HTML>";
        compileTime += "</FONT></HTML>";

        
        compileSuccessLabel.setText(successMsg);
        compileResultCodeLabel.setText(resultCode);
        compileTimeLabel.setText(compileTime);

        if (runResults != null) {
            showCompilerOutputButton.setEnabled(true);
        } else {
            showCompilerOutputButton.setEnabled(false);
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

        String successMsg = "<HTML>Success: <FONT COLOR=BLUE>";
        String resultCode = "<HTML>Result code: <FONT COLOR=BLUE>";
        String executeTime = "<HTML>Execution time(ms): <FONT COLOR=BLUE>";

        if (runResults != null) {
            successMsg += !runResults.failedInExecute();
            resultCode +=  runResults.getExecutionResultCode();
            executeTime += runResults.getExecuteTimeMS();
        }

        successMsg += "</FONT></HTML>";
        resultCode += "</FONT></HTML>";
        executeTime += "</FONT></HTML>";

        executionSuccessLabel.setText(successMsg);
        executionResultCodeLabel.setText(resultCode);
        executionTimeLabel.setText(executeTime);

        if (runResults != null) {
            showExecutionOutputButton.setEnabled(true);
        } else {
            showExecutionOutputButton.setEnabled(false);
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

        String successMsg = "<HTML>Success: <FONT COLOR=BLUE>";
        String resultCode = "<HTML>Result code: <FONT COLOR=BLUE>";
        String validationTime = "<HTML>Validation time: <FONT COLOR=BLUE>";
        String validationAnswer = "<HTML>Judgement: <FONT COLOR=BLUE>";

        if (runResults != null) {
            successMsg += !runResults.failedInValidating();
            resultCode +=  runResults.getValidationResultCode();
            validationTime += runResults.getValidateTimeMS();
            validationAnswer += getJudgement(runResults);
        }
        successMsg += "</FONT></HTML>";
        resultCode += "</FONT></HTML>";
        validationTime += "</FONT></HTML>";
        validationAnswer += "</FONT></HTML>";

        validationSuccessLabel.setText(successMsg);
        validationResultCodeLabel.setText(resultCode);
        validationTimeLabel.setText(validationTime);
        validationAnswerLabel.setText(validationAnswer);

        if (runResults != null) {
            showValidationOutputButton.setEnabled(true);
        } else {
            showValidationOutputButton.setEnabled(false);
        }

    }

    /**
     * Returns a String containing the judgement specified in the received RunResultFiles object.
     * 
     * @param runResult
     * @return
     */
    private String getJudgement(RunResultFiles runResult) {
        try {
            return runResult.getJudgementId().toString(); // TODO: map judgementID into judgement name (string)
        } catch (Exception e) {
            return "";
        }
        
    }

    /**
     * This method clears the RunResultsPane contents to the "default" (unknown) state.
     */
    public void clear() {
        populatePane(null, defaultTitle);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(15);
        TitledBorder titledBorder1 = javax.swing.BorderFactory.createTitledBorder(null, "Run Results", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.red);
        titledBorder1.setTitleFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
        this.setLayout(flowLayout);
        this.setSize(new java.awt.Dimension(210,531));
        this.setMinimumSize(new java.awt.Dimension(600, 400));
        this.setPreferredSize(new java.awt.Dimension(200,600));
        this.setBorder(titledBorder1);
        this.add(getCompilationPanel(), null);
        this.add(getExecutionPanel(), null);
        this.add(getValidationPanel(), null);
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
     * This method initializes compilationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCompilationPanel() {
        if (compilationPanel == null) {
            compileTimeLabel = new JLabel();
            compileTimeLabel.setText("Compile time (ms): ");
            compileTimeLabel.setMaximumSize(new java.awt.Dimension(180, 30));
            compileTimeLabel.setMinimumSize(new java.awt.Dimension(180, 30));
            compileTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            compileTimeLabel.setPreferredSize(new java.awt.Dimension(180, 30));
            compileResultCodeLabel = new JLabel();
            compileResultCodeLabel.setText("Result Code: ");
            compileResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            compileResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            compileResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            compileResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            compileSuccessLabel = new JLabel();
            compileSuccessLabel.setText("Successful:  ");
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
            compilationPanel.setPreferredSize(new java.awt.Dimension(180,150));
            compilationPanel.add(compileSuccessLabel, null);
            compilationPanel.add(compileResultCodeLabel, null);
            compilationPanel.add(compileTimeLabel, null);
            compilationPanel.add(getShowCompilerOutputButton(), null);
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
            executionTimeLabel.setText("Execution time (ms): ");
            executionTimeLabel.setMinimumSize(new java.awt.Dimension(190, 30));
            executionTimeLabel.setMaximumSize(new java.awt.Dimension(190, 30));
            executionTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionTimeLabel.setPreferredSize(new java.awt.Dimension(190, 30));
            executionResultCodeLabel = new JLabel();
            executionResultCodeLabel.setText("Result Code: ");
            executionResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            executionResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionSuccessLabel = new JLabel();
            executionSuccessLabel.setText("Successful: ");
            executionSuccessLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            executionSuccessLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            executionSuccessLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            executionSuccessLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            executionPanel = new JPanel();
            executionPanel.setLayout(new BoxLayout(getExecutionPanel(), BoxLayout.Y_AXIS));
            executionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Execution ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 14), java.awt.Color.blue));
            executionPanel.setPreferredSize(new java.awt.Dimension(180,150));
            executionPanel.add(executionSuccessLabel, null);
            executionPanel.add(executionResultCodeLabel, null);
            executionPanel.add(executionTimeLabel, null);
            executionPanel.add(getShowExecutionOutputButton(), null);
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
            validationTimeLabel.setText("Validation time (ms): ");
            validationTimeLabel.setMinimumSize(new java.awt.Dimension(200, 30));
            validationTimeLabel.setMaximumSize(new java.awt.Dimension(200, 30));
            validationTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationTimeLabel.setPreferredSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel = new JLabel();
            validationAnswerLabel.setText("Judgement: ");
            validationAnswerLabel.setMinimumSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel.setPreferredSize(new java.awt.Dimension(200, 30));
            validationAnswerLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationAnswerLabel.setMaximumSize(new java.awt.Dimension(200, 30));
            validationResultCodeLabel = new JLabel();
            validationResultCodeLabel.setText("Result Code: ");
            validationResultCodeLabel.setMinimumSize(new java.awt.Dimension(150, 30));
            validationResultCodeLabel.setPreferredSize(new java.awt.Dimension(150, 30));
            validationResultCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            validationResultCodeLabel.setMaximumSize(new java.awt.Dimension(150, 30));
            validationSuccessLabel = new JLabel();
            validationSuccessLabel.setText("Successful: ");
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
            validationPanel.setPreferredSize(new java.awt.Dimension(180,180));
            validationPanel.setBorder(titledBorder);
            validationPanel.add(validationSuccessLabel, null);
            validationPanel.add(validationResultCodeLabel, null);
            validationPanel.add(validationTimeLabel, null);
            validationPanel.add(validationAnswerLabel, null);
            validationPanel.add(getShowValidationOutputButton(), null);
        }
        return validationPanel;
    }

    /**
     * This method initializes showStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowCompilerOutputButton() {
        if (showCompilerOutputButton == null) {
            showCompilerOutputButton = new JButton();
            showCompilerOutputButton.setText("Show");
            showCompilerOutputButton.setEnabled(false);
            showCompilerOutputButton.setPreferredSize(new java.awt.Dimension(130,30));
            showCompilerOutputButton.setMaximumSize(new java.awt.Dimension(106, 30));
            showCompilerOutputButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return showCompilerOutputButton;
    }

    /**
     * This method initializes showExecutionStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowExecutionOutputButton() {
        if (showExecutionOutputButton == null) {
            showExecutionOutputButton = new JButton();
            showExecutionOutputButton.setMaximumSize(new java.awt.Dimension(106, 30));
            showExecutionOutputButton.setPreferredSize(new java.awt.Dimension(120,30));
            showExecutionOutputButton.setText("Show");
            showExecutionOutputButton.setEnabled(false);
            showExecutionOutputButton.setMinimumSize(new java.awt.Dimension(106, 30));
            showExecutionOutputButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return showExecutionOutputButton;
    }

    /**
     * This method initializes showValidationStdOutButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowValidationOutputButton() {
        if (showValidationOutputButton == null) {
            showValidationOutputButton = new JButton();
            showValidationOutputButton.setText("Show");
            showValidationOutputButton.setMaximumSize(new java.awt.Dimension(106, 30));
            showValidationOutputButton.setMinimumSize(new java.awt.Dimension(106, 30));
            showValidationOutputButton.setEnabled(false);
            showValidationOutputButton.setPreferredSize(new java.awt.Dimension(120,30));
            showValidationOutputButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return showValidationOutputButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
