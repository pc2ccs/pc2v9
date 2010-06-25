package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Properties Frame.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PropertiesFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6224973493523534530L;

    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;  //  @jve:decl-index=0:

    private ViewPropertiesPane viewPropertiesPane = null;

    /**
     * This method initializes
     * 
     */
    public PropertiesFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(495, 269));
        this.setContentPane(getViewPropertiesPane());
        this.setTitle("My Properties");

        FrameUtilities.centerFrame(this);
    }

    public String getPluginTitle() {
        return "My Properties View Frame";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;

        setTitle("Properties for " + contest.getClientId());

        viewPropertiesPane.setParentFrame(this);
        viewPropertiesPane.setContestAndController(inContest, inController);
    }

    /**
     * This method initializes viewPropertiesPane
     * 
     * @return edu.csus.ecs.pc2.ui.ViewPropertiesPane
     */
    private ViewPropertiesPane getViewPropertiesPane() {
        if (viewPropertiesPane == null) {
            viewPropertiesPane = new ViewPropertiesPane();
        }
        return viewPropertiesPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
