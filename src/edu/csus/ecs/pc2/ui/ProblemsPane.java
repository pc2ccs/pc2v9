package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Problem;

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
        this.add(getProblemListBox(), java.awt.BorderLayout.CENTER);
        this.add(getProblemButtonPane(), java.awt.BorderLayout.SOUTH);

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
            problemButtonPane = new JPanel();
            problemButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
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

            Object[] cols = { "Problem Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "Validator" };
            problemListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());

            // Display Name
            problemListBox.setColumnSorter(0, sorter, 1);
            // Compiler Command Line
            problemListBox.setColumnSorter(1, sorter, 2);
            // Exe Name
            problemListBox.setColumnSorter(2, sorter, 3);
            // Execute Command Line
            problemListBox.setColumnSorter(3, sorter, 4);

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
                problemListBox.sort();
            }
        });
    }

    protected Object[] buildProblemRow(Problem problem) {

        // Object[] cols = { "Problem Name", "Data File", "Input Method", "Answer File", "Run Time Limit", "Validator" };

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
        c[5] = problem.getValidatorProgramName();

        return c;
    }

    private void reloadListBox() {
        problemListBox.removeAllRows();
        Problem[] problems = getModel().getProblems();

        for (Problem problem : problems) {
            addProblemRow(problem);
        }
    }

    private void addProblemRow(Problem problem) {
        Object[] objects = buildProblemRow(problem);
        problemListBox.addRow(objects, problem.getElementId());
        problemListBox.autoSizeAllColumns();
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
