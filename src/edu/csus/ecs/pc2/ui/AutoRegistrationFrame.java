package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoRegistrationFrame extends JFramePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7129836991961579846L;

    private AutoRegistrationPane autoRegistrationPane = null;

    /**
     * This method initializes
     * 
     */
    public AutoRegistrationFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(477, 284));
        this.setContentPane(getAutoRegistrationPane());

        getAutoRegistrationPane().setParentFrame(this);
        FrameUtilities.centerFrame(this);
    }

    @Override
    public String getPluginTitle() {
        return "Auto Registration Frame";
    }

    /**
     * This method initializes autoRegistrationPane
     * 
     * @return edu.csus.ecs.pc2.ui.AutoRegistrationPane
     */
    private AutoRegistrationPane getAutoRegistrationPane() {
        if (autoRegistrationPane == null) {
            autoRegistrationPane = new AutoRegistrationPane();
        }
        return autoRegistrationPane;
    }

    public void processCancel() {
        setVisible(false);
        getParentFrame().setVisible(true);
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (getParentFrame() != null) {
            getParentFrame().setVisible(! b);
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
