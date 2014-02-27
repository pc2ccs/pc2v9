package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.JPluginPaneNameComparator;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Pane to allow user to choose and open/start plugin window or pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PluginLoadPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1303011559658754807L;

    private JComboBox<PluginWrapper> pluginComboBox = null;

    private JButton openNewPluginButton = null;

    private JButton addPluginInNewWindow = null;

    private JTabbedPane parentTabbedPane = null;

    public JTabbedPane getParentTabbedPane() {
        return parentTabbedPane;
    }

    public void setParentTabbedPane(JTabbedPane parentTabbedPane) {
        this.parentTabbedPane = parentTabbedPane;
    }

    /**
     * This method initializes
     * 
     */
    public PluginLoadPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(null);
        this.setSize(new Dimension(536, 240));
        this.add(getPluginComboBox(), null);
        this.add(getOpenNewPluginButton(), null);
        this.add(getAddPluginInNewWindow(), null);

    }

    @Override
    public String getPluginTitle() {
        return "Plugin Load";
    }

    /**
     * This method initializes pluginComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<PluginWrapper> getPluginComboBox() {
        if (pluginComboBox == null) {
            pluginComboBox = new JComboBox<PluginWrapper>();
            pluginComboBox.setBounds(new Rectangle(41, 57, 458, 33));
        }
        return pluginComboBox;
    }

    /**
     * This method initializes openNewPluginButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOpenNewPluginButton() {
        if (openNewPluginButton == null) {
            openNewPluginButton = new JButton();
            openNewPluginButton.setBounds(new Rectangle(41, 147, 134, 36));
            openNewPluginButton.setText("Add Tab");
            openNewPluginButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewTab();
                }
            });
        }
        return openNewPluginButton;
    }

    protected void addNewTab() {

        if (parentTabbedPane != null) {

            PluginWrapper wrapper = (PluginWrapper) getPluginComboBox().getSelectedItem();
            getController().register(wrapper.getPlugin());
            wrapper.getPlugin().setContestAndController(getContest(), getController());
            parentTabbedPane.add(wrapper.getPlugin(), wrapper.getPlugin().getPluginTitle());
        } else {
            JOptionPane.showMessageDialog(this, "Programming error: no tab specified");
        }
    }

    /**
     * This method initializes addPluginInNewWindow
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddPluginInNewWindow() {
        if (addPluginInNewWindow == null) {
            addPluginInNewWindow = new JButton();
            addPluginInNewWindow.setBounds(new Rectangle(317, 147, 182, 36));
            addPluginInNewWindow.setText("Open in new Window");
            addPluginInNewWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewPluginWindow();
                }
            });
        }
        return addPluginInNewWindow;
    }

    protected void addNewPluginWindow() {
        PluginWrapper wrapper = (PluginWrapper) getPluginComboBox().getSelectedItem();
        JPanePlugin plugin = wrapper.getPlugin();
        JFramePlugin frame = FrameUtilities.createPluginFrame(plugin, getContest(), getController());
        frame.setVisible(true);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class PluginWrapper {

        private JPanePlugin plugin = null;

        public PluginWrapper(JPanePlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String toString() {
            return plugin.getPluginTitle();
        }

        public JPanePlugin getPlugin() {
            return plugin;
        }
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        loadPluginList();
    }

    private JPanePlugin[] getPluginList() {
        Vector<JPanePlugin> plugins = new Vector<JPanePlugin>();

        plugins.add(new EventFeedServerPane());
        plugins.add(new AccountsPane());
        plugins.add(new BalloonColorListPane());
        plugins.add(new ClarificationsPane());
        plugins.add(new ConnectionsPane());
        plugins.add(new ContestClockPane());
        plugins.add(new ContestTimesPane());
        plugins.add(new GroupsPane());
        plugins.add(new InfoPane());
        plugins.add(new JudgementsPane());
        plugins.add(new LanguagesPane());
        plugins.add(new LoadContestPane());
        plugins.add(new LoginsPane());
        plugins.add(new OptionsPane());
        plugins.add(new PacketExplorerPane());
        plugins.add(new PlaybackPane());
        plugins.add(new ProblemsPane());
        plugins.add(new ProfilesPane());
        plugins.add(new ReportPane());
        plugins.add(new RunsPane());
        plugins.add(new SitesPane());
        // plugins.add(new StandingsHTMLPane());
        plugins.add(new StandingsPane());
        plugins.add(new SubmissionBiffPane());
        plugins.add(new TeamStatusPane());
        plugins.add(new ViewPropertiesPane());
        plugins.add(new PacketMonitorPane());
        plugins.add(new MessageMonitorPane());
        plugins.add(new EventFeedServerPane());
        plugins.add(new AutoJudgesPane());

        JPanePlugin[] pluginList = (JPanePlugin[]) plugins.toArray(new JPanePlugin[plugins.size()]);

        Arrays.sort(pluginList, new JPluginPaneNameComparator());

        return pluginList;
    }

    private void loadPluginList() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pluginComboBox.removeAllItems();
                for (JPanePlugin plugin : getPluginList()) {
                    PluginWrapper wrapper = new PluginWrapper(plugin);
                    pluginComboBox.addItem(wrapper);
                }
            }
        });
    }
} // @jve:decl-index=0:visual-constraint="10,10"
