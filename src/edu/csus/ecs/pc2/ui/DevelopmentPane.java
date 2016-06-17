package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Developer tools pane.
 * 
 * @author pc2@ecs.csus.edu
 */

public class DevelopmentPane extends JPanePlugin {

    private static final long serialVersionUID = -6902750741500529883L;

    private PluginPane pluginPane = new PluginPane();

    private ReportPane reportPane = new ReportPane();

    private OptionsPane optionsPane = new OptionsPane();

    public DevelopmentPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane);

        tabbedPane.addTab("Plugins", null, pluginPane, null);

        tabbedPane.addTab("Reports", null, reportPane, null);

        tabbedPane.addTab("Options", null, optionsPane, null);
    }

    @Override
    public String getPluginTitle() {
        return "Development Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        pluginPane.setContestAndController(inContest, inController);
        reportPane.setContestAndController(inContest, inController);
        optionsPane.setContestAndController(inContest, inController);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
