package edu.csus.ecs.pc2.ui;

import javax.swing.WindowConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * An implementation of {@link JFramePlugin}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JFramePluginImpl extends JFramePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 93354276312013972L;

    private JPanePlugin plugin;

    public JFramePluginImpl(JPanePlugin plugin) {
        this.plugin = plugin;
        setContentPane(plugin);
        setTitle(plugin.getPluginTitle());
        setBounds(new java.awt.Rectangle(0, 0, 500, 350));
        FrameUtilities.centerFrame(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        plugin.setContestAndController(inContest, inController);
        plugin.setParentFrame(this);
    }

    @Override
    public String getPluginTitle() {
        return "Plugin JFrame";
    }

}
