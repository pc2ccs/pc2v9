package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;

/**
 * This class is a {@link JFrame} designed to display comparisons between
 * multiple sets of files, for example, multiple pairs of team/judge outputs
 * for different Test Cases.  It presents a {@link JList} for selecting the desired
 * pair of files, and displays the selected file pair in a {@link JSplitPane}
 * containing each file in a {@link JScrollPane}.  The contents of the
 * {@link JList} and the {@link JScrollPane}s are dynamically setable.
 * 
 * @author john
 *
 */

public class MultiFileComparator extends JFrame  {

    private static final String JUDGES_ANS_FILENAME = "judges.ans";

    private static final String TEAMS_OUT_FILENAME = "teams.out";

    private static final long serialVersionUID = 1L;

    private IInternalController controller;

    private IInternalContest contest;
    
    private Log log ;

    private JList<String> lstTestCases;

    private JList<String> lstTeamOutput;

    private JList<String> lstJudgesOutput;

    private int currentRunID;

    private JScrollPane scrollPaneTestCaseList;

    private JLabel lblTestCaseDataFile;

    private int[] currentTestCaseNums;

    private String[] currentTeamOutputFileNames;

    private String[] currentJudgesOutputFileNames;

    private String[] currentJudgesDataFileNames;

    private String comparatorCommand = ""; // internal is "", otherwise it is the command to invoke

    private Process process = null;
    
    public  MultiFileComparator() {
        super();
        setTitle("Test Case Outputs for Run ID:  xxx");
        setMinimumSize(new Dimension(650, 600));
        setPreferredSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        
        JPanel pnlSouthPanel = new JPanel();
        getContentPane().add(pnlSouthPanel, BorderLayout.SOUTH);
        
        final JCheckBox chckbxLockScrolling = new JCheckBox("Lock Scrolling");
        chckbxLockScrolling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Sorry; this function isn't implemented yet",
                        "Not Implemented", JOptionPane.INFORMATION_MESSAGE); 
                chckbxLockScrolling.setEnabled(false);
                chckbxLockScrolling.setSelected(false);
            }
        });
        pnlSouthPanel.add(chckbxLockScrolling);
        
        Component horizontalStrut_2 = Box.createHorizontalStrut(20);
        pnlSouthPanel.add(horizontalStrut_2);
        
        final JButton btnExportFiles = new JButton("Export");
        btnExportFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Sorry; this function isn't implemented yet",
                        "Not Implemented", JOptionPane.INFORMATION_MESSAGE); 
                btnExportFiles.setEnabled(false);
            }
        });
        pnlSouthPanel.add(btnExportFiles);
        
        Component horizontalStrut_3 = Box.createHorizontalStrut(20);
        pnlSouthPanel.add(horizontalStrut_3);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        pnlSouthPanel.add(btnClose);
        
        JPanel northPanel = new JPanel();
        northPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        getContentPane().add(northPanel, BorderLayout.NORTH);
        
        lblTestCaseDataFile = new JLabel("Data file for selected Test Case: <none selected>");
        lblTestCaseDataFile.setToolTipText("The Judge's data file associated with the currently selected Test Case");
        northPanel.add(lblTestCaseDataFile);
        
        JPanel westPanel = new JPanel();
        westPanel.setMinimumSize(new Dimension(80, 10));
        westPanel.setPreferredSize(new Dimension(100, 10));
        getContentPane().add(westPanel, BorderLayout.WEST);
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        
        JLabel lblTestCases = new JLabel("Test Cases");
        westPanel.add(lblTestCases);
        
        //create a scrollpane to hold the list of test cases
        scrollPaneTestCaseList = new JScrollPane();
        scrollPaneTestCaseList.setPreferredSize(new Dimension(0, 0));
        scrollPaneTestCaseList.setMinimumSize(new Dimension(0, 0));
        scrollPaneTestCaseList.setViewportBorder(new LineBorder(Color.blue));
        scrollPaneTestCaseList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTestCaseList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneTestCaseList.setWheelScrollingEnabled(true);
        westPanel.add(scrollPaneTestCaseList);

        //create a JList of Strings for showing the test cases
        lstTestCases = new JList<String>();
        lstTestCases.setBorder(new EmptyBorder(2, 5, 2, 5));
        lstTestCases.setBounds(new Rectangle(10, 0, 0, 0));
        
        //add a list listener that handles list selection by switching output views to the
        // newly-selected test case
        
        //XXX PROBLEM:  see the comments in method setTestCaseList(), below.
        //XXX Need to redesign this piece of logic...
        lstTestCases.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getSource() instanceof JList<?>) {
                    JList<?> source = (JList<?>)(e.getSource());
                    if (!source.getValueIsAdjusting()) {
                        ListModel<?> model = source.getModel();
                        int index = source.getSelectedIndex();
                        System.out.println ("MFC.lstTestCases.valueChanged(): Selected Index = " + index);
                        //if the list model was just loaded then there is no selected index; 
                        // force it to display the item at index 0
                        if (index == -1) {
                            index = 0;
                            source.setSelectedIndex(index);
                        } else {
                            int testCaseNum = new Integer((String)(model.getElementAt(index)));
                            System.out.println ("MFC.lstTestCases.valueChanged(): Test Case Number = " + testCaseNum);
                        }
                        updateViewsToSelectedTestCase(index);
                    }
                }
            }
        });

        //add default data to the test case list
        DefaultListModel<String> initialListData = new DefaultListModel<String>();
        initialListData.addElement("?");
        initialListData.addElement("?");
        initialListData.addElement("?");
        initialListData.addElement("...");
        lstTestCases.setModel(initialListData);
        
        //set various properties of the list
        lstTestCases.setToolTipText("List of selectable Test Cases");
        lstTestCases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //put the list in the scrollpane
        scrollPaneTestCaseList.setViewportView(lstTestCases);
        
        JLabel lblClickHint = new JLabel("(Click to Select)");
        westPanel.add(lblClickHint);
        
        
        JPanel pnlOutputViewHolder = new JPanel();
        getContentPane().add(pnlOutputViewHolder, BorderLayout.CENTER);
        pnlOutputViewHolder.setLayout(new BorderLayout(0, 0));
        
        JPanel pnlOutputViewLabels = new JPanel();
        pnlOutputViewHolder.add(pnlOutputViewLabels, BorderLayout.NORTH);
        
        JPanel pnlTeamOutputLabel = new JPanel();
        pnlOutputViewLabels.add(pnlTeamOutputLabel);
        
        JLabel lblTeamOutput = new JLabel("Team Output");
        pnlTeamOutputLabel.add(lblTeamOutput);
        
        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        horizontalStrut_1.setPreferredSize(new Dimension(100, 0));
        pnlOutputViewLabels.add(horizontalStrut_1);
        
        JPanel pnlJudgesOutputLabel = new JPanel();
        pnlOutputViewLabels.add(pnlJudgesOutputLabel);
        
        JLabel lblJudgesOutput = new JLabel("Judge's Output");
        pnlJudgesOutputLabel.add(lblJudgesOutput);
        
        JSplitPane splitPaneOutputViews = new JSplitPane();
        splitPaneOutputViews.setContinuousLayout(true);
        splitPaneOutputViews.setResizeWeight(0.5);
        splitPaneOutputViews.setToolTipText("Displays the outputs corresponding to the selected Test Case");
        pnlOutputViewHolder.add(splitPaneOutputViews, BorderLayout.CENTER);
        
        String [] noData = new String [] {"<no test case selected>"};
        lstTeamOutput = new JList<String>();
        lstTeamOutput.setListData(noData);
        splitPaneOutputViews.setLeftComponent(lstTeamOutput);
        
        lstJudgesOutput = new JList<String>();
        lstJudgesOutput.setListData(noData);
        splitPaneOutputViews.setRightComponent(lstJudgesOutput);
    }
    

    /**
     * Changes the Team Output file, Judge's Output file, and Data File name displays to
     * the values corresponding to the test case specified by the given index.
     * 
     * @param testCaseIndex - the index in the JList of the test case which should be displayed
     */
    private void updateViewsToSelectedTestCase(int testCaseIndex) {
        
        //make sure the test case number (index) points into the array of team/judge/data test cases
        if (testCaseIndex >= 0 && testCaseIndex < currentTeamOutputFileNames.length) {
            
            System.out.println("MFC.updateViewsToSelectedTestCase(): "
                    + "received testCaseIndex = " + testCaseIndex 
                    + "; list test case number = " + currentTestCaseNums[testCaseIndex]
                    + "; currentJudgesDataFileNames.length = " + currentJudgesDataFileNames.length);

            // update the data file name on the display
            if (currentJudgesDataFileNames != null) {
                lblTestCaseDataFile.setText("Data file for selected Test Case: '" 
                                        + currentJudgesDataFileNames[testCaseIndex] + "'");
            } else {
                //log error - bad judge's data file names
                if (getLog() != null) {
                    log.log(Log.WARNING, "MultiFileComparator.updateViewsToSelectedTestCase(): "
                            + "judge's data file names array is null!");
                } else {
                    System.err.println ("WARNING: MultiFileComparator.updateViewsToSelectedTestCase(): "
                            + "judge's data file names array is null!");
                }
            }
            
            // update the team output on the display            
            if (currentTeamOutputFileNames != null) {

                updateOutputDisplay(testCaseIndex, currentTeamOutputFileNames, lstTeamOutput);
                
            } else {
                //log error - bad team output file names
                if (getLog() != null) {
                    log.log(Log.WARNING, "MultiFileComparator.updateViewsToSelectedTestCase(): "
                               + "team output file names array is null!");
                } else {
                    System.err.println ("WARNING: MultiFileComparator.updateViewsToSelectedTestCase(): "
                            + "team output file names array is null!");
                }
            }
            
            // update the judge's output on the display            
            if (currentJudgesOutputFileNames != null) {

               updateOutputDisplay(testCaseIndex, currentJudgesOutputFileNames, lstJudgesOutput);
                
            } else {
               //log error - bad judge's output file names
                if (getLog() != null) {
                    log.log(Log.WARNING, "MultiFileComparator.updateViewsToSelectedTestCase(): "
                            + "judge's output file names array is null!");
                } else {
                    System.err.println ("WARNING: MultiFileComparator.updateViewsToSelectedTestCase(): "
                            + "judge's output file names array is null!");
                }

            }
            
        } else {
            
            //invalid test case number
            if (getLog() != null) {
                log.log(Log.WARNING, "MultiFileComparator.updateViewsToSelectedTestCase(): "
                        + "invalid test case number: "+ testCaseIndex);
            } else {
                System.err.println ("WARNING: MultiFileComparator.updateViewsToSelectedTestCase(): "
                        + "invalid test case number: "+ testCaseIndex);
            }
        }
    }

    /**
     * Returns the index in the list of current test cases of the specified
     * test case number, or -1 if the specified test case doesn't exist in the list.
     * This is useful for matching test case numbers with positions in the team and
     * judge output lists and the data file list.
     * 
     * @param testCaseNum - the number of a test case
     * 
     * @return the index of the specified test case in the list of test cases, or -1 if not found
     */
    private int indexOfTestCase(int testCaseNum) {
        int retVal = -1;
        if (currentTestCaseNums != null) {
            for (int i = 0; i < currentTestCaseNums.length; i++) {
                if (currentTestCaseNums[i] == testCaseNum) {
                    retVal = i;
                    break;
                }
            }
        }
        return retVal;
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;
    }
    
    /**
     * Loads this MultiFileComparator with the required information.
     * @param runID - the ID of the run for which test case info is being displayed
     * @param testCaseNums - an array of test case numbers
     * @param teamOutputFileNames - an array of team output file names corresponding 
     *                              to the specified test case numbers
     * @param judgesOutputFileNames - an array of judge's output file names corresponding 
     *                              to the specified test case numbers
     * @param judgesDataFileNames - an array of judge's data file names corresponding
     *                              to the specified test case numbers
     */
    public void setData(int runID, int [] testCaseNums, String [] teamOutputFileNames, String [] judgesOutputFileNames, String [] judgesDataFileNames) {
        this.setRunID(runID);
        this.setTestCaseList(testCaseNums);
        this.setOutputFileNames(teamOutputFileNames, judgesOutputFileNames);
        this.setJudgesDataFileNames(judgesDataFileNames);
        // this will highlight the 1st entry
        lstTestCases.setSelectedIndex(0);
        // this will show the files for the 1st entry (vs the last MFC files)
        updateViewsToSelectedTestCase(0);
        if (!comparatorCommand.equals("")) {
            // just kidding, we are going to prepare to launch an external command
            try {
                BufferedOutputStream outWriter = new BufferedOutputStream(new FileOutputStream(TEAMS_OUT_FILENAME));
                BufferedOutputStream ansWriter = new BufferedOutputStream(new FileOutputStream(JUDGES_ANS_FILENAME));
                int fileIndex = 0;
                for (int caseNum : testCaseNums) {
                    String msg = "TESTCASE " + caseNum + " BEGIN ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
                    msg = msg.substring(0,80) + "\n";
                    outWriter.write(msg.getBytes());
                    ansWriter.write(msg.getBytes());
                    int c = 0;
                    byte[] cbuf = new byte[32768];
                    if (judgesOutputFileNames != null && judgesOutputFileNames[fileIndex] != null  && new File(judgesOutputFileNames[fileIndex]).exists()) {
                        
                        BufferedInputStream ansReader = new BufferedInputStream(new FileInputStream(judgesOutputFileNames[fileIndex]));
                        c = ansReader.read(cbuf);
                        while (c != -1) {
                            ansWriter.write(cbuf, 0, c);
                            c = ansReader.read(cbuf);
                        }
                        ansReader.close();
                        ansReader = null;
                    }
                    if (teamOutputFileNames != null && teamOutputFileNames[fileIndex] != null && new File(teamOutputFileNames[fileIndex]).exists()) {
                        BufferedInputStream outReader = new BufferedInputStream(new FileInputStream(teamOutputFileNames[fileIndex]));
                        c = outReader.read(cbuf);
                        while (c != -1) {
                            outWriter.write(cbuf, 0, c);
                            c = outReader.read(cbuf);
                        }
                        outReader.close();
                        outReader = null;
                    }
                    msg = "TESTCASE " + caseNum + " END ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
                    msg = msg.substring(0,80) + "\n";
                    outWriter.write(msg.getBytes());
                    ansWriter.write(msg.getBytes());
                    fileIndex++;
                }
                outWriter.close();
                ansWriter.close();
                outWriter = null;
                ansWriter = null;
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
        }
    }
    
    private void setRunID(int runID) {
        currentRunID = runID;
        this.setTitle("Test Case Outputs for Run ID:  " + runID );
    }


    private void setJudgesDataFileNames(String[] judgesDataFileNames) {
        this.currentJudgesDataFileNames = judgesDataFileNames;
        
        //TODO: create a cache of the new judge data file names; load "<none selected>" into label 
        
    }


    /**
     * Modifies the Test Case List so it contains the specified Test Case Numbers.
     * Any previous data in the Test Case List is discarded.
     * Clients who call this method should also call {@link #setOutputFileNames()} so that
     * the test case list matches the output file names.
     * 
     * @param testCaseNums - the test case numbers which should go into the Test Case List,
     *                      replacing any current Test Case List data
     */
    private void setTestCaseList(int [] testCaseNums) {
        this.currentTestCaseNums = testCaseNums;
        
        if (testCaseNums == null || testCaseNums.length <= 0) {
            if (getLog() != null) {
                log.log(Log.WARNING, 
                    "MultiFileComparator.setTestCaseList() called with null or empty list");
            } else {
                System.err.println (
                    "Warning: MultiFileComparator.setTestCaseList() called with null or empty list");
            }
            
        }
        DefaultListModel<String> testCaseListModel =  new DefaultListModel<String>();
        for (int i=0; i<testCaseNums.length; i++) {
            testCaseListModel.addElement(String.valueOf(testCaseNums[i]));
        }
        
        //XXX Problem: setting the model CLEARS THE CURRENT SELECTION and then FIRES valueChanged(), 
        //XXX which invokes the valueChanged() method in the ListSelectionListener, 
        //XXX which calls getSelectedIndex() on the table -- but there is no longer any selected index
        //XXX because setting the model cleared the selection!  Result: index out of range in
        //XXX the list selection "valueChanged()" method (see above)
        lstTestCases.setModel(testCaseListModel);
        int listSize = testCaseListModel.getSize();
        if (listSize<10) {
            lstTestCases.setVisibleRowCount(listSize);
        } else {
            lstTestCases.setVisibleRowCount(10);
        }
        scrollPaneTestCaseList.setViewportView(lstTestCases);

    }
    
    /**
     * Updates the lists of team and judge output file names.
     * Any previous output file names are discarded.
     * Clients calling this method should also call {@link #setTestCaseList(int[])}
     * with an array of test case numbers; the array length should be the same as
     * the lengths of the arrays received by this method.
     * 
     * @param teamOutputFileNames - the names of the team output files for the test cases to be displayed
     * @param judgesOutputFileNames - the names of the judge's output files for the test cases to be displayed
     */
    private void setOutputFileNames(String [] teamOutputFileNames, String [] judgesOutputFileNames) {
        if (teamOutputFileNames==null || judgesOutputFileNames==null ||
                teamOutputFileNames.length != judgesOutputFileNames.length) {
            if (getLog() != null) {
                log.log(Log.WARNING, 
                   "MultiFileComparator.setOutputFileNames() called with invalid arrays (null or different lengths)");
            } else {
                System.err.println (
                   "Warning: MultiFileComparator.setOutputFileNames() called with invalid arrays (null or different lengths)");
            }
        }
        currentTeamOutputFileNames = teamOutputFileNames;
        
        //debug
        System.out.println ("MFC.setOutputFilenames(): team output file names:");
        for (int i=0; i<currentTeamOutputFileNames.length; i++) {
            System.out.println ("  '" + currentTeamOutputFileNames[i] + "'");
        }
        
        currentJudgesOutputFileNames = judgesOutputFileNames;
        
        //debug
        System.out.println ("MFC.setOutputFilenames(): judges output file names:");
        for (int i=0; i<currentJudgesOutputFileNames.length; i++) {
            System.out.println ("  '" + currentJudgesOutputFileNames[i] + "'");
        }
        
        //TODO: create a cache of the new team/judge files; load "<none selected>" into list models 
    }
    
    /* (non-Javadoc)
     * @see java.awt.Window#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean arg0) {
        if (comparatorCommand.equals("")) {
            super.setVisible(arg0);
        } else {
            if (arg0) {
                // execute process
                String[] env = null;
                try {
                    
                    process = Runtime.getRuntime().exec(comparatorCommand+" "+TEAMS_OUT_FILENAME+" "+JUDGES_ANS_FILENAME, env, new File("."));
                } catch (IOException e) {
                    log.warning("setVisible() "+e.getMessage());
                    JOptionPane.showMessageDialog(this, 
                            "System Error: "+e.getMessage(), 
                            "System Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // destroy process if it still exists
                if (process != null) {
                    process.destroy();
                }
            }
        }
    }


    /**
     * Returns the log defined by the current controller, or null if none.
     * @return - the current log, or null.
     */
    private Log getLog() {
        if (controller != null) {
            log = controller.getLog();
        } 
        return log ;
    }
    
    /**
     * Updates the specified (Team or Judge) output display JList to show the contents 
     * of the (Team or Judge) output file indicated by the specified list index.
     * 
     * @param listIndex - the index in the output file names array of the file to be displayed
     */
    private void updateOutputDisplay(int listIndex, String [] outputFileNameList, JList<String> outputDisplayList ) {
        
        BufferedReader fileHandle = null;
        
        //make sure the incoming data points to legit file info
        if (listIndex >= 0  &&  outputFileNameList != null && listIndex < outputFileNameList.length) {
            
            //get the "output" file name (the name of the problem output file that we are going to read)
             String fileName = outputFileNameList[listIndex];
             
             //read the file data into a Vector
             try {
                fileHandle = getFileHandle(fileName);
                String lineRead = fileHandle.readLine();
                Vector<String> v = new Vector<String>();
                while (lineRead != null) {
                    v.addElement(lineRead);
                    lineRead = new String();
                    lineRead = fileHandle.readLine();
                }
                //update the JList displaying the file data
                outputDisplayList.setListData(v);
                
            } catch (Exception e) {
                
                if (getLog() != null) {
                    log.log(Log.WARNING, "MultiFileComparator.updateOutputDisplay(): error processing '" 
                            + fileName + "'," + e.getMessage());
                } else {
                    System.err.println ("WARNING: MultiFileComparator.updateOutputDisplay(): error processing '"
                            + fileName + "'," + e.getMessage()); 
                }
            } finally {
                try {
                    if (fileHandle != null) {
                        fileHandle.close();
                    }
                } catch (Exception e) {
                    if (getLog() != null) {
                        log.log(Log.WARNING, "MultiFileComparator.updateOutputDisplay(): error closing fileHandle " 
                                + "for '" + fileName + "'," + e.getMessage());
                    } else {
                        System.err.println ("WARNING: MultiFileComparator.updateOutputDisplay(): error closing fileHandle "
                                + "for '" + fileName + "'," + e.getMessage());
                    }
                }
            }
        } else {
            //log error
            if (getLog() != null) {
                log.log(Log.WARNING, "MultiFileComparator.updateOutputDisplay(): bad list index ("
                        + listIndex + ") or bad output file names array");
            } else {
                System.err.println ("WARNING: MultiFileComparator.updateOutputDisplay(): bad list index ("
                        + listIndex + ") or bad output file names array"); 
            }

        }

    }
    
    /**
     * Returns a {@link BufferedReader} for the specified file.
     * @param fileName - the name of the file to wrap
     * @return a BufferedReader wrapping the specified file
     * @throws FileNotFoundException if the specified file can't be found
     */
    private BufferedReader getFileHandle(String fileName) throws FileNotFoundException {
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(fileName);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException fe) {
            // log error
            if (getLog() != null) {
                log.log(Log.SEVERE, "MultiFileComparator.getFileHandle(): unable to open '"
                        + fileName + "'; " + fe.getMessage() );
            } else {
                System.err.println ("ERROR: MultiFileComparator.getFileHandle(): unable to open '"
                        + fileName + "'; " + fe.getMessage());
            }
            throw fe;
        }

        return br;
    }


    /**
     * @return the comparatorCommand
     */
    public String getComparatorCommand() {
        return comparatorCommand;
    }


    /**
     * @param comparatorCommand the comparatorCommand to set
     */
    public void setComparatorCommand(String comparatorCommand) {
        // null is the same as ""
        if (comparatorCommand == null) {
            this.comparatorCommand = "";
        } else {
            this.comparatorCommand = comparatorCommand;
        }
    }


    /* (non-Javadoc)
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        if (!comparatorCommand.equals("")) {
            // destroy process if it still exists
            if (process  != null) {
                process.destroy();
                // TODO alas this doesn't seem to work with gvim.bat
            }
        }
        super.dispose();
    }

    
}
