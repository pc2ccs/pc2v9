package edu.csus.ecs.pc2.ui.board;


import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.ui.UIPlugin;
/**
 * This class is the default scoreboard view (frame).
 * @author pc2@ecs.csus.edu
 *
 */
//$HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/ClarificationsPane.java $
public class ScoreboardView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8071477348056424178L;
    private IModel model;
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
        this.setSize(new java.awt.Dimension(500,209));
        this.setTitle("Scoreboard");
			
	}

	public void setModelAndController(IModel model, IController controller) {
        this.model = model;
        this.controller = controller;
    }

    public String getPluginTitle() {
        return "ScoreboardView";
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
