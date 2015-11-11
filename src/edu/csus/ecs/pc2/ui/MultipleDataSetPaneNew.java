package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

import javax.swing.border.LineBorder;

/**
 * Multiple Test Data set UI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: MultipleDataSetPane.java 3226 2015-10-20 23:19:16Z boudreat $
 */

// TODO 917 make font larger

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/MultipleDataSetPane.java $
public class MultipleDataSetPaneNew extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5975163495479418935L;

    private JPanel listBoxScrollPaneHolderPanel = null;

    private TestCaseTableModel tableModel = new TestCaseTableModel();

    private JScrollPane testDataSetsListBoxScrollPane = null;

    private ProblemDataFiles problemDataFiles;
    private JButton btnDelete;
    private JButton btnImportFromCDP;
    private JButton btnLoad;

    private EditProblemPane editProblemPane = null;

    private Problem problem;

    private JTable testDataSetsListBox;
    private JPanel problemDataFilesPanel;
    private JPanel inputDataStoragePanel;
    private JRadioButton radioButtonCopyFilesIntoPC2;
    private JRadioButton radioButtonKeepDataFilesExternal;
    private JPanel teamReadsFromPanel;
    private JRadioButton radioButtonStdIn;
    private JRadioButton radioButtonFileInput;
    private JLabel labelTeamFileName;
    private JTextField textFieldteamFileName;
    private JCheckBox checkBoxJudgesHaveProvidedAnswerFiles;
    private JCheckBox checkBoxProblemRequiresInputData;

    private JPanel dataFilesButtonPanel;
    private JButton btnAddButton;
    private JLabel lblDataSetOperations;

    /**
     * This method initializes
     * 
     */
    public MultipleDataSetPaneNew() {
        super();
        setPreferredSize(new Dimension(800, 600));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(800, 460));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(getListBoxScrollPaneHolderPanel());
//        this.add(getProblemDataFilesPanel());
        this.add(getDataFilesButtonPanel());
    }

    private JPanel getDataFilesButtonPanel() {
        if (dataFilesButtonPanel == null) {
            dataFilesButtonPanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) dataFilesButtonPanel.getLayout();
            flowLayout.setHgap(35);
            dataFilesButtonPanel.setPreferredSize(new Dimension(35, 35));
            dataFilesButtonPanel.add(getLblDataSetOperations());
            dataFilesButtonPanel.add(getBtnAddButton());
            
            JButton btnEditButton = new JButton("Edit");
            btnEditButton.setToolTipText("Edit the selected problem data set");
            dataFilesButtonPanel.add(btnEditButton);
            dataFilesButtonPanel.add(getBtnDelete());
            dataFilesButtonPanel.add(getBtnLoad());
            dataFilesButtonPanel.add(getBtnImportFromCDP());
        }
        return dataFilesButtonPanel;
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
        resizeColumnWidth(getTestDataSetsListBox());
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
    private JPanel getListBoxScrollPaneHolderPanel() {
        if (listBoxScrollPaneHolderPanel == null) {
            listBoxScrollPaneHolderPanel = new JPanel();
            listBoxScrollPaneHolderPanel.setBorder(new LineBorder(new Color(255, 0, 0), 1, true));
            listBoxScrollPaneHolderPanel.setLayout(new BorderLayout());
            listBoxScrollPaneHolderPanel.add(getTestDataSetsListBoxScrollPane(), BorderLayout.CENTER);
        }
        return listBoxScrollPaneHolderPanel;
    }


    /**
     * This method initializes testDataSetsListBoxScrollPane
     * 
     * @return A JScrollPane containing a JTable for holding the test data sets
     */
    private JScrollPane getTestDataSetsListBoxScrollPane() {
        if (testDataSetsListBoxScrollPane == null) {
            testDataSetsListBoxScrollPane = new JScrollPane();
            testDataSetsListBoxScrollPane.add(getTestDataSetsListBox());
            testDataSetsListBoxScrollPane.setViewportView(getTestDataSetsListBox());
            getTestDataSetsListBox().getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
            //set columns to different widths: leftmost fixed, others evenly divided
            //TODO:  the following code doesn't work because it relies on calling testDataSetsListBox.getSize() but the
            //  listbox won't have any size (returns 0) until setVisible() is called...
            TableColumn column = null;
            for (int i = 0; i < getTestDataSetsListBox().getColumnCount(); i++) {
                column = getTestDataSetsListBox().getColumnModel().getColumn(i);
                switch (column.getModelIndex()) {
                    case 0: 
                        column.setPreferredWidth(20);
                    default:
                        column.setPreferredWidth((int) ((testDataSetsListBox.getSize().getWidth()-20)/getTestDataSetsListBox().getColumnCount()-1));
                }
            } 
        }
        return testDataSetsListBoxScrollPane;
    }
    
    /**
     * Returns a JTable initialized with the TableModel defined in the class initialization clause.
     * @return a structured JTable
     */
    public JTable getTestDataSetsListBox() {
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

    private JButton getBtnImportFromCDP() {
        if (btnImportFromCDP == null) {
            btnImportFromCDP = new JButton("Import From CDP");
            btnImportFromCDP.setToolTipText("Refresh/Reload data sets from an ICPC Contest Data Package (CDP); see https://clics.ecs.baylor.edu/index.php/CDP");
            btnImportFromCDP.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reloadDataFiles();
                }
            });
        }
        return btnImportFromCDP;
    }

    /**
     * Load new data files.
     */
    protected void loadDataFiles() {

        /**
         * cdp config directory with problem subdirectories.
         */
        String baseDirectoryName = "workspace";
        
        /**
         * short name or base directory
         */
        String shortProblemName = getEditProblemPane().getShortNameTextfield().getText();
        
        if (shortProblemName == null || shortProblemName.trim().length() == 0){
            showMessage(this, "Must enter short problem name to load","Enter short problems name (on General tab)");
            return;
        }
        
        boolean externalFiles = false;
        
//        String problemFilesDirectory = baseDirectoryName + File.separator +  shortProblemName; 
        
        if (problem != null){
            externalFiles = problem.isUsingExternalDataFiles();
//            problemFilesDirectory = Utilities.getSecretDataPath(baseDirectoryName, problem);
            
        } else {
            problem = new Problem(shortProblemName);
        }
        
        // check for answer files
        String secretDirPath = Utilities.getSecretDataPath(baseDirectoryName, shortProblemName);
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
            btnLoad = new JButton("Load From Folder");
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
 
    @SuppressWarnings("unused")
    private JPanel getProblemDataFilesPanel() {
        if (problemDataFilesPanel == null) {
            problemDataFilesPanel = new JPanel();
            problemDataFilesPanel.setPreferredSize(new Dimension(700, 300));
            problemDataFilesPanel.setMinimumSize(new Dimension(700, 300));
            problemDataFilesPanel.setMaximumSize(new Dimension(700, 300));
            problemDataFilesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Problem Data Files", TitledBorder.LEADING, TitledBorder.TOP,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), null));
            problemDataFilesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            GroupLayout gl_problemDataFilesPanel = new GroupLayout(problemDataFilesPanel);
            gl_problemDataFilesPanel.setHorizontalGroup(
                gl_problemDataFilesPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_problemDataFilesPanel.createSequentialGroup()
                        .addGroup(gl_problemDataFilesPanel.createParallelGroup(Alignment.LEADING, false)
                            .addGroup(gl_problemDataFilesPanel.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_problemDataFilesPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getCheckBoxJudgesHaveProvidedAnswerFiles())
                                    .addComponent(getCheckBoxProblemRequiresInputData(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(gl_problemDataFilesPanel.createSequentialGroup()
                                .addGap(30)
                                .addGroup(gl_problemDataFilesPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(getTeamReadsFromPanel(), 0, 0, Short.MAX_VALUE)
                                    .addComponent(getInputDataStoragePanel(), GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())
            );
            gl_problemDataFilesPanel.setVerticalGroup(
                gl_problemDataFilesPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_problemDataFilesPanel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(getCheckBoxProblemRequiresInputData(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(9)
                        .addComponent(getTeamReadsFromPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18)
                        .addComponent(getInputDataStoragePanel(), GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                        .addGap(18)
                        .addComponent(getCheckBoxJudgesHaveProvidedAnswerFiles())
                        .addContainerGap(23, Short.MAX_VALUE))
            );
            problemDataFilesPanel.setLayout(gl_problemDataFilesPanel);
            
            problemDataFilesPanel.add(getCheckBoxProblemRequiresInputData());
            problemDataFilesPanel.add(getTeamReadsFromPanel());
            problemDataFilesPanel.add(getInputDataStoragePanel());
            problemDataFilesPanel.add(getCheckBoxJudgesHaveProvidedAnswerFiles());
        }
        return problemDataFilesPanel;
    }
    
    private JPanel getInputDataStoragePanel() {
        if (inputDataStoragePanel == null) {
            inputDataStoragePanel = new JPanel();
            inputDataStoragePanel.setPreferredSize(new Dimension(470, 80));
            inputDataStoragePanel.setMaximumSize(new Dimension(500, 200));
            inputDataStoragePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Input Data Storage", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("Dialog",
                    java.awt.Font.BOLD, 12), new Color(0, 0, 0)));
            inputDataStoragePanel.setAlignmentX(0.0f);
            inputDataStoragePanel.setLayout(new BoxLayout(inputDataStoragePanel, BoxLayout.Y_AXIS));
            inputDataStoragePanel.add(getRadioButtonCopyFilesIntoPC2());
            inputDataStoragePanel.add(getRadioButtonKeepDataFilesExternal());
        }
        return inputDataStoragePanel;
    }
    private JRadioButton getRadioButtonCopyFilesIntoPC2() {
        if (radioButtonCopyFilesIntoPC2 == null) {
            radioButtonCopyFilesIntoPC2 = new JRadioButton("Copy Data Files into PC2 (more efficient, but limited to 5MB total per problem)");
            radioButtonCopyFilesIntoPC2.setSelected(true);
            radioButtonCopyFilesIntoPC2.setPreferredSize(new Dimension(550, 30));
            radioButtonCopyFilesIntoPC2.setMinimumSize(new Dimension(550, 30));
            radioButtonCopyFilesIntoPC2.setMaximumSize(new Dimension(550, 30));
            radioButtonCopyFilesIntoPC2.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return radioButtonCopyFilesIntoPC2;
    }
    private JRadioButton getRadioButtonKeepDataFilesExternal() {
        if (radioButtonKeepDataFilesExternal == null) {
            radioButtonKeepDataFilesExternal = new JRadioButton("Keep Data Files external to PC2 (requires you to copy files to Judge's machines)");
            radioButtonKeepDataFilesExternal.setPreferredSize(new Dimension(550, 30));
            radioButtonKeepDataFilesExternal.setMinimumSize(new Dimension(550, 30));
            radioButtonKeepDataFilesExternal.setMaximumSize(new Dimension(550, 30));
            radioButtonKeepDataFilesExternal.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return radioButtonKeepDataFilesExternal;
    }
    
    //from EditProblemPaneNew:
    /**
     * This method initializes readsFromPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamReadsFromPanel() {
        if (teamReadsFromPanel == null) {
            FlowLayout fl_teamReadsFromPanel = new FlowLayout();
            fl_teamReadsFromPanel.setAlignment(FlowLayout.LEFT);
            fl_teamReadsFromPanel.setHgap(20);
            fl_teamReadsFromPanel.setVgap(0);
            teamReadsFromPanel = new JPanel();
            teamReadsFromPanel.setMinimumSize(new Dimension(500, 50));
            teamReadsFromPanel.setPreferredSize(new Dimension(500, 50));
            teamReadsFromPanel.setMaximumSize(new Dimension(500, 50));
            teamReadsFromPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            teamReadsFromPanel.setLayout(fl_teamReadsFromPanel);
            teamReadsFromPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Team Reads From", TitledBorder.LEADING, TitledBorder.TOP, 
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new Color(0, 0, 0)));
            
            teamReadsFromPanel.add(getRadioButtonStdIn(), null);
            
            Component verticalStrut = Box.createVerticalStrut(20);
            teamReadsFromPanel.add(verticalStrut);
            
            teamReadsFromPanel.add(getRadioButtonFileInput(), null);
            teamReadsFromPanel.add(getLabelTeamFileName());
            
            teamReadsFromPanel.add(getTeamFileNameTextField());
            
//            getTeamReadsFrombuttonGroup().setSelected(getRadioButtonStdIn().getModel(), true);
//            getValidatorChoiceButtonGroup().setSelected(getUseNOValidatatorRadioButton().getModel(), true);
        }
        return teamReadsFromPanel;
    }

//    private JPanel getTeamReadsFromPanel() {
//        if (teamReadsFromPanel == null) {
//          teamReadsFromPanel = new JPanel();
//          teamReadsFromPanel.setPreferredSize(new Dimension(500, 50));
//          teamReadsFromPanel.setMinimumSize(new Dimension(500, 50));
//          teamReadsFromPanel.setMaximumSize(new Dimension(500, 50));
//          teamReadsFromPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Team Reads From", TitledBorder.LEADING, TitledBorder.TOP, 
//                  new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), null));
//
//          teamReadsFromPanel.setAlignmentX(1.0f);
//          FlowLayout fl_teamReadsFromPanel = new FlowLayout();
//          fl_teamReadsFromPanel.setVgap(0);
//          fl_teamReadsFromPanel.setHgap(20);
//          fl_teamReadsFromPanel.setAlignment(FlowLayout.LEFT);
//          teamReadsFromPanel.setLayout(fl_teamReadsFromPanel);
//          teamReadsFromPanel.add(getRadioButtonStdIn());
//          teamReadsFromPanel.add(getRadioButtonFileInput());
//          teamReadsFromPanel.add(getLabelTeamFileName());
//          teamReadsFromPanel.add(getTeamFileNameTextField());
//        }
//        return teamReadsFromPanel;
//    }
    
    private JRadioButton getRadioButtonStdIn() {
        if (radioButtonStdIn == null) {
            radioButtonStdIn = new JRadioButton();
            radioButtonStdIn.setText("Stdin");
            radioButtonStdIn.setSelected(true);
        }
        return radioButtonStdIn;
    }

    private JRadioButton getRadioButtonFileInput() {
        if (radioButtonFileInput == null) {
            radioButtonFileInput = new JRadioButton();
            radioButtonFileInput.setText("File");
        }
        return radioButtonFileInput;
    }
    private JLabel getLabelTeamFileName() {
        if (labelTeamFileName == null) {
            labelTeamFileName = new JLabel("Name of file which teams open:");
            labelTeamFileName.setEnabled(false);
        }
        return labelTeamFileName;
    }
    private JTextField getTeamFileNameTextField() {
        if (textFieldteamFileName == null) {
            textFieldteamFileName = new JTextField();
            textFieldteamFileName.setToolTipText("Enter the name of the file which the problem statement specifies the team program should open and read");
            textFieldteamFileName.setEnabled(false);
            textFieldteamFileName.setColumns(15);
        }
        return textFieldteamFileName;
    }
    private JCheckBox getCheckBoxJudgesHaveProvidedAnswerFiles() {
        if (checkBoxJudgesHaveProvidedAnswerFiles == null) {
            checkBoxJudgesHaveProvidedAnswerFiles = new JCheckBox("Judges Have Provided Answer Files");
        }
        return checkBoxJudgesHaveProvidedAnswerFiles;
    }
    private JCheckBox getCheckBoxProblemRequiresInputData() {
        if (checkBoxProblemRequiresInputData == null) {
            checkBoxProblemRequiresInputData = new JCheckBox();
            checkBoxProblemRequiresInputData.setText("Problem Requires Input Data");
            checkBoxProblemRequiresInputData.setPreferredSize(new Dimension(200, 30));
            checkBoxProblemRequiresInputData.setMinimumSize(new Dimension(200, 30));
            checkBoxProblemRequiresInputData.setMaximumSize(new Dimension(200, 30));
            checkBoxProblemRequiresInputData.setBorder(new EmptyBorder(0, 10, 0, 0));
            checkBoxProblemRequiresInputData.setAlignmentX(1.0f);
        }
        return checkBoxProblemRequiresInputData;
    }
    private JButton getBtnAddButton() {
        if (btnAddButton == null) {
            btnAddButton = new JButton("Add");
            btnAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });
            btnAddButton.setToolTipText("Add a new data set to the problem");
        }
        return btnAddButton;
    }
    private JLabel getLblDataSetOperations() {
        if (lblDataSetOperations == null) {
            lblDataSetOperations = new JLabel("Data Set Operations:");
        }
        return lblDataSetOperations;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
