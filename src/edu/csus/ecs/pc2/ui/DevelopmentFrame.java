package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import java.awt.Dimension;

/**
 * Report Frame, shows development pane.
 * 
 * @author pc2@ecs.csus.edu
 */
public class DevelopmentFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3883500524581405587L;
    
    private DevelopmentPane developmentPane;
    
    /**
     * This method initializes
     * 
     */
    public DevelopmentFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(812, 362));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Developer Tools");
        this.setContentPane(getDevelopmentPane());

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        developmentPane.setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "Development Frame";
    }

    public DevelopmentPane getDevelopmentPane() {
        if (developmentPane == null) {
            developmentPane = new DevelopmentPane();
        }
        return developmentPane;
    }

    
} // @jve:decl-index=0:visual-constraint="10,10"
