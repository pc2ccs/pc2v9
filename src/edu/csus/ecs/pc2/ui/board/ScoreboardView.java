package edu.csus.ecs.pc2.ui.board;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * This class is the default scoreboard view (frame).
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class ScoreboardView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8071477348056424178L;

    // TODO remove @SuppressWarnings for model
    @SuppressWarnings("unused")
    private IModel model;

    // TODO remove @SuppressWarnings for controller
    @SuppressWarnings("unused")
    private IController controller;

    /**
     * This method initializes
     * 
     */
    public ScoreboardView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(405, 227));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Scoreboard");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        setTitle("PC^2 " + model.getTitle() + " Build " + new VersionInfo().getBuildNumber());

        setVisible(true);
    }

    public String getPluginTitle() {
        return "Scoreboard View";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
