package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Pane for Plugin Frame (tabbed panes).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PluginPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4327701084703859112L;

    private JTabbedPane pluginTabbedPane = null;

    private JPanel infoPane = null;

    private JLabel infoLabel = null;

    /**
     * This method initializes
     * 
     */
    public PluginPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(498, 242));
        this.add(getPluginTabbedPane(), BorderLayout.CENTER);
        this.add(getInfoPane(), BorderLayout.NORTH);

        FrameUtilities.centerFrame(this);

    }

    public String getPluginTitle() {
        return "Plugin View";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        PluginLoadPane pluginLoadPane = new PluginLoadPane();
        pluginLoadPane.setParentTabbedPane(getPluginTabbedPane());
        getPluginTabbedPane().add(pluginLoadPane.getPluginTitle(), pluginLoadPane);
        pluginLoadPane.setContestAndController(inContest, inController);
    }

    /**
     * This method initializes pluginTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getPluginTabbedPane() {
        if (pluginTabbedPane == null) {
            pluginTabbedPane = new JTabbedPane();
        }
        return pluginTabbedPane;
    }

    /**
     * This method initializes infoPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPane() {
        if (infoPane == null) {
            infoLabel = new JLabel();
            infoLabel.setText("JLabel");
            infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            infoPane = new JPanel();
            infoPane.setLayout(new BorderLayout());
            infoPane.setPreferredSize(new Dimension(30, 30));
            infoPane.add(infoLabel, BorderLayout.CENTER);
        }
        return infoPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
