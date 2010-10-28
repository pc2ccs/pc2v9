package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;
import com.ibm.webrunner.j2mclb.util.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IMessageListener;
import edu.csus.ecs.pc2.core.model.MessageEvent;

/**
 * Message Monitor Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MessageMonitorPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1485047964016484623L;

    private JPanel buttonPane = null;

    private JButton clearButton = null;

    private MCLB messageListBox = null;

    private int sequenceNumber = 1;

    private SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss MM-dd"); // @jve:decl-index=0:

    private int maxLines = 500;

    /**
     * This method initializes
     * 
     */
    public MessageMonitorPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(453, 214));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getMessageListBox(), BorderLayout.CENTER);

    }
    
    /**
     * This method initializes messageListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getMessageListBox() {
        if (messageListBox == null) {
            messageListBox = new MCLB();

            Object[] cols = { "Seq", "At", "Area", "Message" };

            messageListBox.addColumns(cols);

            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());
//            HeapSorter accountNameSorter = new HeapSorter();
//            accountNameSorter.setComparator(new AccountColumnComparator());

            int idx = 0;

            messageListBox.setColumnSorter(idx++, numericStringSorter, 1); // Sequence number
            messageListBox.setColumnSorter(idx++, sorter, 2); // At date/time
            messageListBox.setColumnSorter(idx++, sorter, 3); // Message Area
            messageListBox.setColumnSorter(idx++, sorter, 4); // Message

        }
        return messageListBox;
    }


    @Override
    public String getPluginTitle() {
        return "Message Monitor Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        inContest.addMessageListener(new MessageListenerImplementation());
    }

    public void addMessageRow(final MessageEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildMessageRow(event);
                messageListBox.addRow(objects, event);

                truncateTo(maxLines);
                messageListBox.autoSizeAllColumns();
                messageListBox.sort();
            }
        });
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getClearButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes clearButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    clearList();
                }
            });
        }
        return clearButton;
    }

    protected void clearList() {
        messageListBox.removeAllRows();
    }


    protected Object[] buildMessageRow(MessageEvent event) {

        // Object[] cols = { "Seq", "At", "Area", "Message" };

        int numberColumns = messageListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = Integer.toString(sequenceNumber);
        c[1] = formatter.format(new Date());
        c[2] = event.getArea().toString();
        c[3] = event.getMessage();

        return c;
    }


    private void truncateTo(int numLines) {
        TableModel tableModel = getMessageListBox().getModel();
        if (tableModel.getRowCount() > numLines) {
            int lastRow = tableModel.getRowCount();
            for (int i = lastRow; i >= numLines; i--) {
                tableModel.removeRow(i);
            }
        }
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class MessageListenerImplementation implements IMessageListener {

        public void messageAdded(MessageEvent event) {
            addMessageRow(event);
        }

        public void messageRemoved(MessageEvent event) {
            // ignored
        }

    }

} // @jve:decl-index=0:visual-constraint="10,10"
