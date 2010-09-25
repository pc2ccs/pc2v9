package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.IStreamListener;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class is intended to register as a listener to the Log/LogWindowHandler and display the logs in a grid.
 * <br>
 * If {@link #setLog(Log)} is not used nor the constructor with log,
 * the log will be a window for
 * the {@link edu.csus.ecs.pc2.core.InternalController#getLog()}.
 * 
 * @version $Id$
 */

//$HeadURL$$

public class LogWindow extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3451023761556886609L;

    private int maxLines = 4000;

    private MCLB logMessageListbox = null;

    private IInternalController controller;

    @SuppressWarnings("unused")
    private IInternalContest contest;
    
    private Log log = null;

    private IStreamListener streamListener = null;
    
    public LogWindow (Log log){
        this();
        this.log = log;
    }
    
    public LogWindow() {
        super();
        initialize();
    }


    /**
     * A Stream listener.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    class StreamListener implements IStreamListener {
        
        public void messageAdded(final String inString) {
            // TODO figure out to do do this on the tablemodel instead of gui
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String[] logMessageFields = inString.split("[|]");
                    getLogMessageListbox().setUpdate(false);
                    
                    String [] rowData = getMessageRowData(logMessageFields);
             
                    getLogMessageListbox().insertRow(rowData, 0);
                    truncateTo(maxLines);
                    getLogMessageListbox().autoSizeAllColumns();
                    getLogMessageListbox().setUpdate(true);
                }
            });
        }
    }
    
    protected String[] getMessageRowData(String[] fields) {

        if (fields.length > 5) {
            // fill in reverse order because inserts rows at top
            for (int i = fields.length - 1; i > 4; i--) {
                String[] newRow = new String[5];
                newRow[0] = "";
                newRow[1] = "";
                newRow[2] = "";
                newRow[3] = "";
                newRow[4] = fields[i];
                return newRow;
            }

            // then print actual log message
            String[] row = { fields[0], fields[1], fields[2], fields[3], fields[4] };
            return row;
        } else {
            return fields;
        }
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(700, 300));
        this.setTitle("Log Viewer");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.add(getLogMessageListbox());
        centerFrameTopFullWidth(this);
   }

    /**
     * Center this frame on the screen.
     * 
     * @param aFrame
     */
    private void centerFrameTopFullWidth(JFrame aFrame) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int newFrameWidth = screenDim.width - 40;
        aFrame.setSize(newFrameWidth, aFrame.getHeight());
        aFrame.setLocation(screenDim.width / 2 - aFrame.getSize().width / 2, 40);
    }

    /**
     * @return MCLB
     */
    private MCLB getLogMessageListbox() {
        if (logMessageListbox == null) {
            logMessageListbox = new MCLB();
            Object[] cols = { "Date/Time", "Level", "Thread", "Method", "Message" };

            // String line = getDateString(date) + "|"
            // + level + "|"
            // + Thread.currentThread().getName() + "|"
            // + logRecord.getSourceMethodName() + "|"
            // + logRecord.getMessage();

            logMessageListbox.addColumns(cols);

            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // HeapSorter numericStringSorter = new HeapSorter();
            // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Date/Time
            // setColumnSorter (logMessageListbox, 0, sorter, 1);
            //
            // // Level
            // setColumnSorter (logMessageListbox, 1, sorter, 2);
            //
            // // Thread
            // setColumnSorter (logMessageListbox, 2, sorter, 3);
            //
            // // Method
            // setColumnSorter (logMessageListbox, 3, sorter, 4);
            //
            // // Message
            // setColumnSorter (logMessageListbox, 4, sorter, 5);
            //
            logMessageListbox.autoSizeAllColumns();
        }
        return logMessageListbox;
    }

    /**
     * Truncates the logMessageListBox data contest.
     * 
     * @param numLines
     *            Number of lines to remove from the listbox
     */
    private void truncateTo(int numLines) {
        TableModel tableModel = getLogMessageListbox().getModel();
        if (tableModel.getRowCount() > numLines) {
            int lastRow = tableModel.getRowCount();
            for (int i = lastRow; i >= numLines; i--) {
                tableModel.removeRow(i);
            }
        }
    }

    /**
     * @return number of lines listbox will be truncated to.
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * @param maxLines
     *            Number of lines listbox will be truncated to (defaults to 400).
     */
    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
       contest = inContest;
       controller = inController;
       if (controller == null) {
           System.err.println("controller is null");
       }
       if (controller.getLog() == null) {
           System.err.println("controller.getLog() is null");
       }
       if (log == null){
           log = controller.getLog();
       }
       if (controller.isUsingGUI()){
           streamListener = new StreamListener(); 
           log.getStreamHandler().addStreamListener(streamListener);
//       } else  -- no using GUI, comments go to log
       }
       
      
    }

    public String getPluginTitle() {
        return getTitle();
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
    
    public void dispose(){
        if (streamListener != null){
            log.getStreamHandler().removeStreamListener(streamListener);
            streamListener = null;
        }
    }

}
