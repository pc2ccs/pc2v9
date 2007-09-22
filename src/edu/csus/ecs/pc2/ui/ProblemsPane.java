package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;

/**
 * View Problems.
 * 
 * @author pc2@ecs.csus.edu
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

    private JButton editButton = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private Log log = null;

    private EditProblemFrame editProblemFrame = null;

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
        this.add(getProblemListBox(), java.awt.BorderLayout.CENTER);
        this.add(getProblemButtonPane(), java.awt.BorderLayout.SOUTH);

        editProblemFrame = new EditProblemFrame();

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
            problemButtonPane.add(getEditButton(), null);
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

            Object[] cols = { "Problem Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "SVTJ", "Validator" };
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
        // Object[] cols = { "Problem Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "SVTJ", "Validator" };

        int numberColumns = problemListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = problem.getDisplayName();
        c[1] = problem.getDataFileName();
        String inputMethod = "";
        if (problem.isReadInputDataFromSTDIN()) {
            inputMethod = "STDIN";
        } else {
            inputMethod = "File I/O";
        }
        c[2] = inputMethod;
        c[3] = problem.getAnswerFileName();
        c[4] = Integer.toString(problem.getTimeOutInSeconds());
        c[5] = yesNoString(problem.isShowValidationToJudges());
        c[6] = problem.getValidatorProgramName();

        return c;
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

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        editProblemFrame.setContestAndController(inContest, inController);

        getContest().addProblemListener(new ProblemListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
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
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    protected void addProblem() {
        editProblemFrame.setProblem(null);
        editProblemFrame.setVisible(true);
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

            editProblemFrame.setProblem(problemToEdit);
            editProblemFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit problem, check log");
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

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
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
            log.info("debug Problem REMOVED  " + event.getProblem());
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
