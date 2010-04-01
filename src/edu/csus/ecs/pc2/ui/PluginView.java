package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import javax.swing.WindowConstants;

/**
 * A JFrame which contains a pane to load/view plugins.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PluginView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4327701084703859112L;

    private PluginPane pluginPane = null;

    private JPanel mainPanel = null;

    /**
     * This method initializes
     * 
     */
    public PluginView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(517, 293));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getMainPanel());
        this.setTitle("Plugin View");

    }

    public String getPluginTitle() {
        return "Plugin View";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getPluginPane().setParentFrame(this);
        getPluginPane().setContestAndController(inContest, inController);
    }

    /**
     * This method initializes pluginPane
     * 
     * @return edu.csus.ecs.pc2.ui.PluginPane
     */
    private PluginPane getPluginPane() {
        if (pluginPane == null) {
            pluginPane = new PluginPane();
        }
        return pluginPane;
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getPluginPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
