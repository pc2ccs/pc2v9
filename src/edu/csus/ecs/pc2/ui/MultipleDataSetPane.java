package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
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

    private JScrollPane listBoxScrollPane = null;

    private TestCaseTableModel tableModel = new TestCaseTableModel();

    private JTable testDataSetsListBox = null;

    private ProblemDataFiles problemDataFiles;
    private JButton btnDelete;
    private JButton btnLoad;

    private EditProblemPane editProblemPane = null;

    private Problem problem;
    private JPanel inputDataStoragePanel;
    
    /**
     * Copy Data Files into PC2 - aka Internal
     */
    private JRadioButton rdbtnCopyDataFiles;
    /**
     * Keep Data Files external to PC2 - aka external
     */
    private JRadioButton rdBtnKeepDataFilesExternal;
    private JPanel controlPanel;
    private JPanel buttonPanel;
    private Component horizontalStrut;
    private Component verticalStrut;
    private Component verticalStrut_1;
    private final ButtonGroup inputStorageButtonGroup = new ButtonGroup();

    private String loadDirectory;

    /**
     * This method initializes
     * 
     */
    public MultipleDataSetPane() {
        super();
        setMinimumSize(new Dimension(10, 500));
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(766, 526));
        this.add(getListBoxScrollPane(), BorderLayout.CENTER);
        add(getControlPanel(), BorderLayout.SOUTH);
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
        
        if (problem != null) {
            getRdBtnKeepDataFilesExternal().setSelected(problem.isUsingExternalDataFiles()); //button group will set the Copy button to the opposite
            getInputDataStoragePanel().setEnabled(false);
            getRdbtnCopyDataFiles().setEnabled(false);
            getRdBtnKeepDataFilesExternal().setEnabled(false);
        } 
     
        // TODO 917 re-add auto size columns
        resizeColumnWidth(getTestDataSetsListBox());
    }
    
    public void resizeColumnWidth(JTable table) {
        
//        final TableColumnModel columnModel = table.getColumnModel();
//        for (int column = 0; column < table.getColumnCount(); column++) {
//            int width = 50; // Min width
//            for (int row = 0; row < table.getRowCount(); row++) {
//                TableCellRenderer renderer = table.getCellRenderer(row, column);
//                Component comp = table.prepareRenderer(renderer, row, column);
//                width = Math.max(comp.getPreferredSize().width +1 , width);
//            }
//            columnModel.getColumn(column).setPreferredWidth(width);
//        }

//        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        for (int col=0; col<table.getColumnCount(); col++) {
            int width = 10;
            switch (col) {
                case 0: width = 100; break;
                case 1: width = 300; break;
                case 2: width = 300; break;
            }
            TableColumn column = table.getColumnModel().getColumn(col);
            column.setPreferredWidth(width);
        }
    

            
//        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
//
//        for (int column = 0; column < table.getColumnCount(); column++)
//        {
//            TableColumn tableColumn = table.getColumnModel().getColumn(column);
//            int preferredWidth = tableColumn.getMinWidth();
//            int maxWidth = tableColumn.getMaxWidth();
//    
//            for (int row = 0; row < table.getRowCount(); row++)
//            {
//                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
//                Component c = table.prepareRenderer(cellRenderer, row, column);
//                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
//                preferredWidth = Math.max(preferredWidth, width);
//    
//                //  We've exceeded the maximum width, no need to check other rows
//    
//                if (preferredWidth >= maxWidth)
//                {
//                    preferredWidth = maxWidth;
//                    break;
//                }
//            }
//    
//            tableColumn.setPreferredWidth( preferredWidth );
//        }
    }
                       

    private void dump(ProblemDataFiles problemDataFiles2, String string) {
        Utilities.dump(problemDataFiles2, string);
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JScrollPane getListBoxScrollPane() {
        if (listBoxScrollPane == null) {
            listBoxScrollPane = new JScrollPane(getTestDataSetsListBox());
            listBoxScrollPane.setBorder(new LineBorder(Color.RED));
        }
        return listBoxScrollPane;
    }


    /**
     * This method initializes testDataSetsListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private JTable getTestDataSetsListBox() {
        if (testDataSetsListBox == null) {
            
            //construct a new JTable
            testDataSetsListBox = new JTable(tableModel);
            
            //insert a renderer that will center cell contents
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            
            for (int i=0; i<testDataSetsListBox.getColumnCount(); i++) {
                testDataSetsListBox.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
            }
//            testDataSetsListBox.setDefaultRenderer(String.class, centerRenderer);
            
            //also center column headers (which use a different CellRenderer)
            ((DefaultTableCellRenderer)testDataSetsListBox.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            
            //change the header font
            JTableHeader header = testDataSetsListBox.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
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
            return false;
        }

        if (originalFiles == null || problemDataFiles == null) {
            return true;
        }

        int comp = compare(originalFiles.getJudgesDataFiles(), problemDataFiles.getJudgesDataFiles());
        if (comp != 0) {
            return true;
        }

        comp = compare(originalFiles.getJudgesAnswerFiles(), problemDataFiles.getJudgesAnswerFiles());
        if (comp != 0) {
            return true;
        }
        
        System.out.println("debug 22 Are problemId's identical ?" + //
                problemDataFiles.getProblemId().equals(originalFiles.getProblemId()));

        return false;
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

    public boolean isUsingExternalDataFiles() {
        return rdBtnKeepDataFilesExternal.isSelected();
    }
   
    private String selectDirectory(String dialogTitle) {

        String directory = null;

        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                directory = chooser.getSelectedFile().toString();
                loadDirectory = directory;
            }
        } catch (Exception e) {
            getController().getLog().log(Log.INFO, "Error getting selected file, try again.", e);
        }
        chooser = null;
        return directory;
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
            showMessage(this, "Missing Problem Short Name", "You must define a 'Short Name' for the problem (on the 'General' tab) before you can load data files.");
            return;
        }

        String baseDirectoryName = selectDirectory("Select directory where data files are located");
        
        if (baseDirectoryName == null){
            return;
        }
        
        boolean externalFiles = isUsingExternalDataFiles();
        if (problem == null) {
            problem = new Problem(shortProblemName);
        }
        problem.setUsingExternalDataFiles(externalFiles);
                
        dump(problemDataFiles, "debug 22 before load");
        try {
            problemDataFiles = loadDataFiles(problem, problemDataFiles, baseDirectoryName, ".in", ".ans", externalFiles);
        } catch (Exception e) {
            getController().getLog().log(Log.INFO, e.getMessage(), e);
            showMessage(this, "Import Failed", e.getMessage());
        }
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

    public void setParentPane(EditProblemPane pane) {
        editProblemPane = pane;
    }
    
    
    public ProblemDataFiles loadDataFiles(Problem aProblem, ProblemDataFiles files, String dataFileBaseDirectory, String dataExtension, String answerExtension, boolean externalDataFiles) {

        if (files == null) {
            files = new ProblemDataFiles(aProblem);
        } else {
            /**
             * A sanity check. It makes no sense to update an existing ProblemDataFiles for a different Problem.
             */
            if (aProblem != null && !files.getProblemId().equals(aProblem.getElementId())) {
                throw new RuntimeException("problem and data files are not for the same problem " + aProblem.getElementId() + " vs " + files.getProblemId());
            }
        }

        String[] inputFileNames = Utilities.getFileNames(dataFileBaseDirectory, dataExtension);

        String[] answerFileNames = Utilities.getFileNames(dataFileBaseDirectory, answerExtension);

        if (inputFileNames.length == 0) {
            throw new RuntimeException("No input data files with required  '" + dataExtension + "'  extension found in " + dataFileBaseDirectory);
        }

        if (answerFileNames.length == 0) {
            throw new RuntimeException("No Judge's answer files with required  '" + answerExtension + "'  extension found in " + dataFileBaseDirectory);
        }
        
        if (answerFileNames.length != inputFileNames.length) {
            throw new RuntimeException("Mismatch: expecting the same number of  '" + dataExtension + "'  and  '" + answerExtension + "'  files in " + dataFileBaseDirectory
                    + "\n (found " + inputFileNames.length + "  '" + dataExtension +  "'  files vs. " + answerFileNames.length + "  '" + answerExtension + "'  files)");
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
    
    public JPanel getInputDataStoragePanel() {
        if (inputDataStoragePanel == null) {
        	inputDataStoragePanel = new JPanel();
        	inputDataStoragePanel.setPreferredSize(new Dimension(470, 100));
        	inputDataStoragePanel.setMaximumSize(new Dimension(500, 200));
        	inputDataStoragePanel.setBorder(new TitledBorder(null, "Input Data Storage", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), null));
        	inputDataStoragePanel.setAlignmentX(0.0f);
        	inputDataStoragePanel.setLayout(new BoxLayout((Container) inputDataStoragePanel, BoxLayout.Y_AXIS));
        	inputDataStoragePanel.add(getRdbtnCopyDataFiles());
        	inputDataStoragePanel.add(getRdBtnKeepDataFilesExternal());
        }
        return inputDataStoragePanel;
    }
    public JRadioButton getRdbtnCopyDataFiles() {
        if (rdbtnCopyDataFiles == null) {
        	rdbtnCopyDataFiles = new JRadioButton("Copy Data Files into PC2 (more efficient, but limited to 5MB total per problem)");
        	inputStorageButtonGroup.add(rdbtnCopyDataFiles);
        	rdbtnCopyDataFiles.setSelected(true);
        	rdbtnCopyDataFiles.setPreferredSize(new Dimension(550, 30));
        	rdbtnCopyDataFiles.setMinimumSize(new Dimension(550, 30));
        	rdbtnCopyDataFiles.setMaximumSize(new Dimension(550, 30));
        	rdbtnCopyDataFiles.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return rdbtnCopyDataFiles;
    }
    public JRadioButton getRdBtnKeepDataFilesExternal() {
        if (rdBtnKeepDataFilesExternal == null) {
        	rdBtnKeepDataFilesExternal = new JRadioButton("Keep Data Files external to PC2 (requires you to copy files to Judge's machines)");
        	rdBtnKeepDataFilesExternal.addItemListener(new ItemListener() {
        	    public void itemStateChanged(ItemEvent e) {
                    if (rdBtnKeepDataFilesExternal.isSelected()) {
                        verifyJudgeDataPathIsSet();       	        
                    }
        	    }
        	});
        	inputStorageButtonGroup.add(rdBtnKeepDataFilesExternal);
        	rdBtnKeepDataFilesExternal.setPreferredSize(new Dimension(550, 30));
        	rdBtnKeepDataFilesExternal.setMinimumSize(new Dimension(550, 30));
        	rdBtnKeepDataFilesExternal.setMaximumSize(new Dimension(550, 30));
        	rdBtnKeepDataFilesExternal.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return rdBtnKeepDataFilesExternal;
    }
    
    protected void verifyJudgeDataPathIsSet() {
        //TODO *** need a MODAL dialog to pop up...
        EditJudgesDataFilePathFrame frame = new EditJudgesDataFilePathFrame();
        frame.setVisible(true);    
    }

    private JPanel getControlPanel() {
        if (controlPanel == null) {
        	controlPanel = new JPanel();
        	controlPanel.setPreferredSize(new Dimension(10, 200));
        	controlPanel.setMinimumSize(new Dimension(10, 200));
        	controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        	controlPanel.add(getVerticalStrut_1());
        	controlPanel.add(getInputDataStoragePanel());
        	controlPanel.add(getVerticalStrut());
        	controlPanel.add(getButtonPanel());
        }
        return controlPanel;
    }
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
        	buttonPanel = new JPanel();
        	buttonPanel.setPreferredSize(new Dimension(10, 30));
        	buttonPanel.add(getBtnLoad());
        	buttonPanel.add(getHorizontalStrut());
        	buttonPanel.add(getBtnDelete());
        }
        return buttonPanel;
    }
    private Component getHorizontalStrut() {
        if (horizontalStrut == null) {
        	horizontalStrut = Box.createHorizontalStrut(20);
        	horizontalStrut.setPreferredSize(new Dimension(30, 0));
        	horizontalStrut.setMinimumSize(new Dimension(30, 0));
        }
        return horizontalStrut;
    }
    private Component getVerticalStrut() {
        if (verticalStrut == null) {
        	verticalStrut = Box.createVerticalStrut(20);
        }
        return verticalStrut;
    }
    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
        	verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }

    public void setLoadDirectory(String externalDataFileLocation) {
        loadDirectory = externalDataFileLocation;
    }
    
    public String getLoadDirectory() {
        return loadDirectory;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
