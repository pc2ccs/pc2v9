package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Options Pane, Show Log checkbox.
 * 
 * You must invoke {@link #setLogWindow(LogWindow)} for Show Log checkbox to enable.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class OptionsPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7331492559860531233L;

    private LogWindow logWindow;

    private JCheckBox showLogWindowCheckbox = null;

    private ReportFrame reportFrame;

    private JPanel contentPane = null;

    /**
     * This method initializes
     * 
     */
    public OptionsPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(453, 259));
        this.add(getContentPane(), java.awt.BorderLayout.CENTER);

    }

    protected void showReportFrame() {
        if (reportFrame == null) {
            reportFrame = new ReportFrame();
            reportFrame.setContestAndController(getContest(), getController());
        }
        FrameUtilities.setFramePosition(reportFrame, HorizontalPosition.RIGHT, VerticalPosition.CENTER);
        reportFrame.setVisible(true);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

    }

    @Override
    public String getPluginTitle() {
        return "Options Pane";
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }

    /**
     * Sets log window, enables Show Log checkbox.
     * 
     * @param logWindow
     */
    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
        if (logWindow != null) {
            getShowLogWindowCheckbox().setEnabled(true);
        }
    }

    /**
     * This method initializes showLogWindowCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowLogWindowCheckbox() {
        if (showLogWindowCheckbox == null) {
            showLogWindowCheckbox = new JCheckBox();
            showLogWindowCheckbox.setText("Show Log");
            showLogWindowCheckbox.setEnabled(false);
            showLogWindowCheckbox.setMnemonic(java.awt.event.KeyEvent.VK_L);
            showLogWindowCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showLog(showLogWindowCheckbox.isSelected());
                }
            });
        }
        return showLogWindowCheckbox;
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    /**
     * This method initializes contentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPane() {
        if (contentPane == null) {
            contentPane = new JPanel();
            contentPane.add(getShowLogWindowCheckbox(), null);
            contentPane.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                        showReportFrame();
                    }
                }
            });
        }
        return contentPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
