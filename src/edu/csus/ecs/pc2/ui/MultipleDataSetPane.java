package edu.csus.ecs.pc2.ui;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

import javax.swing.JLabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    private JPanel buttonPanel;

    private Component horizontalStrut;

    private final ButtonGroup inputStorageButtonGroup = new ButtonGroup();

    private String loadDirectory;

    private Component verticalStrut;

    private Component verticalStrut_1;

    private Component verticalStrut_2;
    private JLabel lblWhatsThis;

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
        this.setSize(new Dimension(766, 526));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getVerticalStrut_2());
        add(getInputDataStoragePanel());
        add(getVerticalStrut());
        this.add(getListBoxScrollPane());
        add(getVerticalStrut_1());
        add(getButtonPanel());
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
        if (aProblem != null) {
            setProblemDataFiles(aProblemDataFiles.copy(aProblem));
        } else {
            getRdbtnCopyDataFiles().setSelected(true);
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

    private void enableInputDataStoragePanel(boolean enable) {
        getInputDataStoragePanel().setEnabled(enable);
        getRdbtnCopyDataFiles().setEnabled(enable);
        getRdBtnKeepDataFilesExternal().setEnabled(enable);
    }

    protected void populateUI() {

        tableModel.setFiles(problemDataFiles);
        tableModel.fireTableDataChanged();

        if (problem != null) {
            getRdBtnKeepDataFilesExternal().setSelected(problem.isUsingExternalDataFiles()); // button group will set the Copy button to the opposite
            boolean enable = true;
            if (problemDataFiles != null) {
                // only disable inputDataStoragePanel if we have data
                SerializedFile[] dataFiles = problemDataFiles.getJudgesDataFiles();
                SerializedFile[] answerFiles = problemDataFiles.getJudgesAnswerFiles();
                // disable the ability to switch if we have any files loaded already
                if (dataFiles != null && dataFiles.length > 0) {
                    enable = false;
                }
                if (answerFiles != null && answerFiles.length > 0) {
                    enable = false;
                }
            }
            enableInputDataStoragePanel(enable);
        }

        // TODO 917 re-add auto size columns
        resizeColumnWidth(getTestDataSetsListBox());
    }

    public void resizeColumnWidth(JTable table) {

        // final TableColumnModel columnModel = table.getColumnModel();
        // for (int column = 0; column < table.getColumnCount(); column++) {
        // int width = 50; // Min width
        // for (int row = 0; row < table.getRowCount(); row++) {
        // TableCellRenderer renderer = table.getCellRenderer(row, column);
        // Component comp = table.prepareRenderer(renderer, row, column);
        // width = Math.max(comp.getPreferredSize().width +1 , width);
        // }
        // columnModel.getColumn(column).setPreferredWidth(width);
        // }

        // table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        for (int col = 0; col < table.getColumnCount(); col++) {
            int width = 10;
            switch (col) {
                case 0:
                    width = 100;
                    break;
                case 1:
                    width = 300;
                    break;
                case 2:
                    width = 300;
                    break;
                default:
                    System.err.println("MultipleDataSetPane.resizeColumnWidthUnhandled col " + col);
            }
            TableColumn column = table.getColumnModel().getColumn(col);
            column.setPreferredWidth(width);
        }

        // table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        //
        // for (int column = 0; column < table.getColumnCount(); column++)
        // {
        // TableColumn tableColumn = table.getColumnModel().getColumn(column);
        // int preferredWidth = tableColumn.getMinWidth();
        // int maxWidth = tableColumn.getMaxWidth();
        //
        // for (int row = 0; row < table.getRowCount(); row++)
        // {
        // TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        // Component c = table.prepareRenderer(cellRenderer, row, column);
        // int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
        // preferredWidth = Math.max(preferredWidth, width);
        //
        // // We've exceeded the maximum width, no need to check other rows
        //
        // if (preferredWidth >= maxWidth)
        // {
        // preferredWidth = maxWidth;
        // break;
        // }
        // }
        //
        // tableColumn.setPreferredWidth( preferredWidth );
        // }
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

            // construct a new JTable
            testDataSetsListBox = new JTable(tableModel);

            // insert a renderer that will center cell contents
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < testDataSetsListBox.getColumnCount(); i++) {
                testDataSetsListBox.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            // testDataSetsListBox.setDefaultRenderer(String.class, centerRenderer);

            // also center column headers (which use a different CellRenderer)
            ((DefaultTableCellRenderer) testDataSetsListBox.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            // change the header font
            JTableHeader header = testDataSetsListBox.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
        }
        return testDataSetsListBox;
    }

    public ProblemDataFiles getProblemDataFiles() {
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

        if (listOne == null || listTwo == null) {
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
                    if (rowNumber != -1) {
                        removeRow(rowNumber);
                    }
                    getEditProblemPane().enableUpdateButton();
                }
            });
        }
        return btnDelete;
    }

    /**
     * Remove the specified ROW from the table model holding Test Cases. Note that while Test Cases are numbered starting from 1, ROW NUMBERS start from 0!
     * 
     * @param rowNumber
     *            - the row to remove, where the first row is row 0
     */
    protected void removeRow(int rowNumber) {

        if (tableModel.getRowCount() == 1) {
            // this was the last row we are deleting
            editProblemPane.setJudgingTestSetOne(null);
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

        if (shortProblemName == null || shortProblemName.trim().length() == 0) {
            showMessage(this, "Missing Problem Short Name", "You must define a 'Short Name' for the problem (on the 'General' tab) before you can load data files.");
            return;
        }

        String baseDirectoryName = selectDirectory("Select directory where data files are located");

        if (baseDirectoryName == null) {
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
            throw new RuntimeException("Mismatch: expecting the same number of  '" + dataExtension + "'  and  '" + answerExtension + "'  files in " + dataFileBaseDirectory + "\n (found "
                    + inputFileNames.length + "  '" + dataExtension + "'  files vs. " + answerFileNames.length + "  '" + answerExtension + "'  files)");
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
            inputDataStoragePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Choose storage option before loading data files:", TitledBorder.LEADING, TitledBorder.TOP,
                    new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new Color(0, 0, 0)));
            inputDataStoragePanel.setAlignmentX(0.0f);
            inputDataStoragePanel.setLayout(new BoxLayout((Container) inputDataStoragePanel, BoxLayout.Y_AXIS));
            inputDataStoragePanel.add(getRdbtnCopyDataFiles());
            inputDataStoragePanel.add(getRdBtnKeepDataFilesExternal());
            inputDataStoragePanel.add(getLblWhatsThis());
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
        // TODO *** need a MODAL dialog to pop up...
        ContestInformation ci = getContest().getContestInformation();
        String judgeCDPBasePath = ci.getJudgeCDPBasePath();
        // no need to verify if already set
        if (judgeCDPBasePath == null || judgeCDPBasePath.trim().equals("")) {
            EditJudgesDataFilePathFrame frame = new EditJudgesDataFilePathFrame();
            frame.setContestAndController(getContest(), getController());
            frame.loadCurrentCDPPathsIntoGUI();
            frame.setVisible(true);
        }
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setPreferredSize(new Dimension(10, 50));
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

    public void setLoadDirectory(String externalDataFileLocation) {
        loadDirectory = externalDataFileLocation;
    }

    public String getLoadDirectory() {
        return loadDirectory;
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

    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
            verticalStrut_2 = Box.createVerticalStrut(20);
        }
        return verticalStrut_2;
    }
    
    private JLabel getLblWhatsThis() {
        if (lblWhatsThis == null) {
            lblWhatsThis = new JLabel("<What's This?>");
            lblWhatsThis.setForeground(Color.blue);
            lblWhatsThis.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisMessage, "Internal vs. External Files", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThis.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThis;
    }
    
    private String whatsThisMessage = "PC2 has two different ways of handling data files: Internal and External.  "
            + "\n\n'Internal' means that PC2 will load the file data internally into PC2 memory.  "
            + "\nThe advantage of this is that it allows PC2 to automatically transmit the file data to a Judge each time the Judge requests to judge a submission."
            + "\nThe drawback of this approach is that it does not work well for large data files, because it requires substantial system memory and large"
            + "\namounts of network traffic."
            + "\n\n'External' means that PC2 keeps track of the file names, but that the files themselves remain outside the PC2 system and hence do not use "
            + "\nsystem memory or require network bandwidth for transmission.  "
            + "\nThe advantage of this approach is that it allows the use of arbitrarily large data files for a contest problem."
            + "\nThe drawback of this approach is that it requires you to insure that a copy of the data files is placed on the Judge's machines.  This "
            + "\nmust be done externally to the PC2 system, and the files must be placed at the exact location specified by the value "
            + "\nof the 'Judges Data Path' setting (see the 'Set Judge's Data Path' button on the Contest Configuration->Problems tab)."
            + "\n\nYou must choose which type of data storage you want to use for each contest problem prior to loading the data for that problem.  ";
} // @jve:decl-index=0:visual-constraint="10,10"
