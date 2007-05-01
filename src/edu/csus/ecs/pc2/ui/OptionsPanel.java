package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import javax.swing.JCheckBox;

/**
 * Options Pane, Show Log checkbox.
 * 
 * You must invoke {@link #setLogWindow(LogWindow)} for Show
 * Log checkbox to enable. 
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class OptionsPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7331492559860531233L;

    private LogWindow logWindow;

    private JCheckBox showLogWindowCheckbox = null;

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
        this.setSize(new java.awt.Dimension(453, 259));
        this.add(getShowLogWindowCheckbox(), null);

    }

    public void setModelAndController(IContest inModel, IController inController) {
        super.setModelAndController(inModel, inController);

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
        if (logWindow != null){
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

} // @jve:decl-index=0:visual-constraint="10,10"
