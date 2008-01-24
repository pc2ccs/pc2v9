package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalDump;

/**
 * Info Pane, internal developer test and info pane.
 * 
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public class InfoPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 9149317223518343589L;

    private JPanel centerPane = null;

    private JPanel buttonPanel = null;

    private JButton viewDumpButton = null;

    private JTextField editorTextBox = null;

    private JLabel viewCommandLabel = null;

    /**
     * This method initializes
     * 
     */
    public InfoPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(569, 221));
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        // getModel().addSiteListener(new SiteListenerImplementation());

        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        //
        // }
        // });
    }

    @Override
    public String getPluginTitle() {
        return "Info Pane";
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
        }
        return centerPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            viewCommandLabel = new JLabel();
            viewCommandLabel.setText("View Command");
            viewCommandLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new Dimension(45, 45));
            buttonPanel.add(viewCommandLabel, null);
            buttonPanel.add(getEditorTextBox(), null);
            buttonPanel.add(getViewDumpButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewDumpButton() {
        if (viewDumpButton == null) {
            viewDumpButton = new JButton();
            viewDumpButton.setText("View Dump");
            viewDumpButton.setMnemonic(java.awt.event.KeyEvent.VK_V);
            viewDumpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewDumpFile();
                }
            });
        }
        return viewDumpButton;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEditorTextBox() {
        if (editorTextBox == null) {
            editorTextBox = new JTextField();
            editorTextBox.setText("/windows/vi.bat");
            editorTextBox.setPreferredSize(new java.awt.Dimension(210,20));
            editorTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        viewDumpFile();
                    }
                }
            });
        }
        return editorTextBox;
    }

    protected void viewDumpFile() {
        InternalDump internalDump = new InternalDump(getContest());
        internalDump.setEditorNameFullPath(getEditorTextBox().getText());
        internalDump.viewContestData();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
