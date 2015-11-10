package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Multiple Test Data set UI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO 917 make font larger

// $HeadURL$
public class MultipleDataSetPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5975163495479418935L;

    private JPanel centerPane = null;

    private TestCaseTableModel tableModel = new TestCaseTableModel();

    private JTable testDataSetsListBox = null;

    private ProblemDataFiles problemDataFiles;
    private JButton btnDelete;
    private JButton btnReload;
    private JButton btnLoad;

    private EditProblemPane editProblemPane = null;

    private Problem problem;

    /**
     * This method initializes
     * 
     */
    public MultipleDataSetPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(440, 229));
        this.add(getCenterPane(), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setHgap(45);
        buttonPanel.setPreferredSize(new Dimension(35, 35));
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(getBtnLoad());
        buttonPanel.add(getBtnDelete());
        buttonPanel.add(getBtnReload());
    }

    @Override
    public String getPluginTitle() {
        return "Mulitple Data Set Pane";
    }

    /**
     * Clone data files and populate in pane.
     * 
     * @param aProblemDataFiles
     * @throws CloneNotSupportedException
     */
    public void setProblemDataFiles(Problem aProblem, ProblemDataFiles aProblemDataFiles) throws CloneNotSupportedException {
        this.problem = aProblem;
        if (aProblem != null){
            setProblemDataFiles(aProblemDataFiles.copy(aProblem));
        } else {
            clearDataFiles();
        }
    }

    public void setProblemDataFiles(ProblemDataFiles datafiles) {
        this.problemDataFiles = datafiles;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });
    }
    
    protected void populateUI() {

        tableModel.setFiles(problemDataFiles);
        tableModel.fireTableDataChanged();
     
        // TODO 917 re-add auto size columns
        resizeColumnWidth(testDataSetsListBox);
    }
    
    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void dump(ProblemDataFiles problemDataFiles2, String string) {
        Utilities.dump(problemDataFiles2, string);
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getTestDataSetsListBox(), BorderLayout.CENTER);
        }
        return centerPane;
    }


    /**
     * This method initializes testDataSetsListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private JTable getTestDataSetsListBox() {
        if (testDataSetsListBox == null) {
            testDataSetsListBox = new JTable(tableModel);

        }
        return testDataSetsListBox;
    }
    
    public ProblemDataFiles getProblemDataFiles () {
        dump(tableModel.getFiles(), "populateUI debug 22 C");
        return tableModel.getFiles();
    }

    public void clearDataFiles() {
        this.problemDataFiles = null;
        
        tableModel.setFiles(null);
        tableModel.fireTableDataChanged();
    }
    
    /**
     * Compares current set of data sets to input datafiles.
     * 
     */
    boolean hasChanged(ProblemDataFiles originalFiles) {

        if (originalFiles == null && problemDataFiles == null) {
            return true;
        }

        if (originalFiles == null || problemDataFiles == null) {
            return false;
        }

        int comp = compare(originalFiles.getJudgesDataFiles(), problemDataFiles.getJudgesDataFiles());
        if (comp != 0) {
            return false;
        }

        comp = compare(originalFiles.getJudgesAnswerFiles(), problemDataFiles.getJudgesAnswerFiles());
        if (comp != 0) {
            return false;
        }
        
        System.out.println("debug 22 Are problemId's identical ?" + //
                problemDataFiles.getProblemId().equals(originalFiles.getProblemId()));

        return true;

    }

    /**
     * Compare serializedfile arrays.
     * 
     * @param listOne
     * @param listTwo
     * @return 0 if identical, non-zero if different, returns 2 if either input are null. 
     */
    private int compare(SerializedFile[] listOne, SerializedFile[] listTwo) {
        
        if (listOne == null || listTwo == null){
            return 2;
        }

        if (listOne.length != listTwo.length) {
            return listTwo.length - listOne.length;
        } else {
            for (int i = 0; i < listTwo.length; i++) {
                if (!listOne[i].equals(listTwo[i])) {
                    return 1;
                }
            }

        }
        return 0;
    }

    
    private JButton getBtnDelete() {
        if (btnDelete == null) {
            btnDelete = new JButton("Delete");
            btnDelete.setToolTipText("Delete selected data sets");
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int rowNumber = testDataSetsListBox.getSelectedRow();
                    if (rowNumber != -1){
                        removeRow (rowNumber);
                    }
                }
            });
        }
        return btnDelete;
    }

    protected void removeRow(int rowNumber) {

        if (tableModel.getRowCount() == 1) {
            editProblemPane.setJudgingTestSetOne(tableModel.getFiles());
        }

        tableModel.removeRow(rowNumber);
        
        // TODO 917 if row one is deleted, update the data and answer file on the General Tab 
        // Warn if they delete row one ??
    }

    private JButton getBtnReload() {
        if (btnReload == null) {
            btnReload = new JButton("Re-Load");
            btnReload.setToolTipText("Refresh/Reload data sets from disk");
            btnReload.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reloadDataFiles();
                }
            });
        }
        return btnReload;
    }
    
    private void showMessage(String string, String title) {
        JOptionPane.showMessageDialog(null, string, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showMessage(String string) {
        showMessage(string, "Note");
    }


    /**
     * Load new data files.
     */
    protected void loadDataFiles() {
        
        /**
         * short name or base directory
         */
        String shortProblemName = getEditProblemPane().getShortNameTextfield().getText();
        
        if (shortProblemName == null || shortProblemName.trim().length() == 0){
            showMessage(this, "Must enter short problem name to load","Enter short problems name (on General tab)");
            return;
        }

        ContestInformation info = getContest().getContestInformation();
        
        String adminPath = info.getAdminCDPBasePath();
        
        if (adminPath == null){
            showMessage("Cannot load - Set Admin CDP path first");
            return;
        }
        
        if (!Utilities.isDirThere(adminPath)){
            showMessage("Cannot load - Admin CDP path directory missing: "+adminPath);
            return;
        }
        
        /**
         * cdp config directory with problem subdirectories.
         */
        String baseDirectoryName = adminPath;
        
        boolean externalFiles = false;
        if (problem != null) {
            externalFiles = problem.isUsingExternalDataFiles();
        } else {
            problem = new Problem(shortProblemName);
        }
        
        // check for answer files
        String secretDirPath = Utilities.getSecretDataPath(baseDirectoryName, shortProblemName);
        
        if (! Utilities.isDirThere(secretDirPath)){
            secretDirPath = baseDirectoryName + File.separator + shortProblemName;
        }

        if (! Utilities.isDirThere(secretDirPath)){
            showMessage(this, "Cannot read/find test data set directory"+secretDirPath,"No such directory");
            return;
        }
        
        String[] inputFileNames = Utilities.getFileNames(secretDirPath, ".ans");
        
        if (inputFileNames.length == 0){
            System.out.println("debug 22 "+"No .ans files found in "+secretDirPath);
            showMessage(this, "No answer files found", "No .ans files found in "+secretDirPath);
            return;
        }
        
        dump(problemDataFiles, "debug 22 before load");
        problemDataFiles = loadDataFiles(problem, problemDataFiles, secretDirPath, ".in", ".ans", externalFiles);
        dump(problemDataFiles, "debug 22 after load");
        
        populateUI();
        
        // Populate general data and answer files too
        editProblemPane.setJudgingTestSetOne(tableModel.getFiles());
        getEditProblemPane().enableUpdateButton();
    }

    private JButton getBtnLoad() {
        if (btnLoad == null) {
            btnLoad = new JButton("Load");
            btnLoad.setToolTipText("Load data sets from directory");

            btnLoad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadDataFiles();
                }
            });
        }
        return btnLoad;
    }

    /**
     * Refresh/Reload the already loaded data sets.
     */
    protected void reloadDataFiles() {

        // TODO 917 reload/refresh data files.
        showMessage(this, "Not implemented Yet", "reloadDataFiles not implemented, yet");

        // load the data and answer file on the General Tab
        editProblemPane.setJudgingTestSetOne(tableModel.getFiles());

    }

    public void setParentPane(EditProblemPane pane) {
        editProblemPane = pane;
    }
    
    
    public ProblemDataFiles loadDataFiles(Problem aProblem, ProblemDataFiles files, String dataFileBaseDirectory, String dataExtension, String answerExtension, boolean externalDataFiles) {

        if (files == null) {
            files = new ProblemDataFiles(aProblem);
        } else {
            /**
             * A check. It makes no sense to update an existing ProblemDataFiles for a different Problem.
             */
            if (aProblem != null && !files.getProblemId().equals(aProblem.getElementId())) {
                throw new RuntimeException("problem and data files are not for the same problem " + aProblem.getElementId() + " vs " + files.getProblemId());
            }
        }

        String[] inputFileNames = Utilities.getFileNames(dataFileBaseDirectory, dataExtension);

        String[] answerFileNames = Utilities.getFileNames(dataFileBaseDirectory, answerExtension);

        if (inputFileNames.length == 0) {
            throw new RuntimeException("No input files with extension " + dataExtension + " in "+dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new RuntimeException("No answer  files with extension " + answerExtension+ " in "+dataFileBaseDirectory);
        }

        if (answerFileNames.length != inputFileNames.length) {
            throw new RuntimeException("Miss match expecting same " + dataExtension + " and " + answerExtension + " files (" + inputFileNames.length + " vs " + answerFileNames.length);
        }

        SerializedFile[] inputFiles = Utilities.createSerializedFiles(dataFileBaseDirectory, inputFileNames, externalDataFiles);
        SerializedFile[] answertFiles = Utilities.createSerializedFiles(dataFileBaseDirectory, answerFileNames, externalDataFiles);
        files.setJudgesDataFiles(inputFiles);
        files.setJudgesAnswerFiles(answertFiles);

        return files;
    }
    
    public EditProblemPane getEditProblemPane() {
        return editProblemPane;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
