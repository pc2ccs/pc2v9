package edu.csus.ecs.pc2.core.log;

import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A handler for displaying log records in a window.
 *
 * This handler presents the user with a window and as log entries are added they are formatted.
 *
 * <P>
 * from Core Java[tm] 2, Volume I--Fundamentals, 7th Edition
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class LogWindowHandler extends StreamHandler {

    public static final String SVN_ID = "$Id$";

    private int maxLines = 400;

    private JFrame frame;

    private MCLB logMessageListbox = null;

    /**
     *
     */
    public LogWindowHandler() {
        super();
        setupWindow("Log Viewer");
    }

    /**
     *
     */
    public LogWindowHandler(String title) {
        super();
        setupWindow(title);
    }

    /**
     * Center this frame on the screen.
     *
     * @param aFrame
     */
    private void centerFrameTopFullWidth(JFrame aFrame) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        int newFrameWidth = screenDim.width - 40;
        frame.setSize(newFrameWidth, frame.getHeight());
        frame.setLocation(screenDim.width / 2 - frame.getSize().width / 2, 40);
    }

    /**
     * Creates a default window with specified title.
     *
     * @param title
     *            of window
     */
    private void setupWindow(String title) {
        frame = new JFrame();
        frame.setSize(700, 300);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        centerFrameTopFullWidth(frame);

        frame.add(getMCLB());
        frame.setTitle(title);
        frame.setVisible(false);
        setOutputStream(new OutputStream() {
            @SuppressWarnings("unused")
            public void write(int b) {
            } // not called

            public void write(byte[] b, int off, int len) {
                String inString = new String(b, off, len);
                final String[] logMessageFields = inString.split("[|]");
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (logMessageFields.length > 5) {
                            // fill in reverse order because inserts rows at top
                            for (int i = logMessageFields.length - 1; i > 4; i--) {
                                Object[] newRow = new Object[5];
                                newRow[0] = "";
                                newRow[1] = "";
                                newRow[2] = "";
                                newRow[3] = "";
                                newRow[4] = logMessageFields[i];
                                logMessageListbox.insertRow(newRow, 0);
                            }

                            // then print actual log message
                            Object[] row = { logMessageFields[0],
                                    logMessageFields[1], logMessageFields[2],
                                    logMessageFields[3], logMessageFields[4] };
                            logMessageListbox.insertRow(row, 0);
                        } else {
                            logMessageListbox.insertRow(logMessageFields, 0);
                        }
                        logMessageListbox.autoSizeAllColumns();
                        truncateTo(maxLines);
                    }
                });
            }
        });
    }

    @Override
    public synchronized void publish(LogRecord arg0) {
        // added null check
        if (frame == null) {
            return;
        }
        super.publish(arg0);
        super.flush();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.logging.Handler#close()
     */
    @Override
    public synchronized void close() {
        // System.err.println("close invoked for LogWindowHandler");
        // close the underriding stream
        super.close();
        // now deal with the frame
        if (frame != null) {
            frame.setVisible(false);
            frame = null;
        }
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    /**
     * @return MCLB
     */
    private MCLB getMCLB() {
        if (logMessageListbox == null) {
            logMessageListbox = new MCLB();
            Object[] cols = { "Date/Time", "Level", "Thread", "Method",
                    "Message" };

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
     * Truncates the logMessageListBox.
     *
     * @param numLines
     *            Number of lines to remove from the listbox
     */
    private void truncateTo(int numLines) {
        if (logMessageListbox.getRowCount() > numLines) {
            logMessageListbox.removeRows(numLines, logMessageListbox
                    .getRowCount());
        }
    }

    // /**
    // * Set sorter for column in listbox.
    // *
    // * @param listBox MCLB
    // * @param columnNumber the column to apply the sort to.
    // * @param sorter the sorter
    // * @param sortRank which column will be sorted first, second, etc.
    // */
    // private void setColumnSorter(MCLB listBox, int columnNumber, HeapSorter
    // sorter, int sortRank)
    // {
    // listBox.getColumnInfo(columnNumber).setSorter(sorter);
    // listBox.getColumnInfo(columnNumber).getSorter().setSortOrder(sortRank);
    // }

    /**
     * @param title
     *            of window
     */
    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }

    /**
     * Is the log window visible.
     *
     * @return boolean
     */
    public boolean isVisible() {
        return frame.isVisible();
    }

}
