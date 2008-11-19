package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ICPCAccount;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class ICPCAccountFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6248957592340866836L;

    private IInternalContest contest;

    private IInternalController controller;

    private ICPCAccountPane icpcAccountPane = null;

    /**
     * This method initializes
     * 
     */
    public ICPCAccountFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(800, 1200));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getICPCAccountPane());
        this.setTitle("ICPC Accounts");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getICPCAccountPane().setContestAndController(contest, controller);
        getICPCAccountPane().setParentFrame(this);
        FrameUtilities.centerFrameFullScreenHeight(this);
    }

    public void setICPCAccounts(ICPCAccount[] icpcAccounts) {
        if (icpcAccounts == null) {
            setVisible(false);
        } else {
            getICPCAccountPane().setIcpcAccounts(icpcAccounts);
        }
    }

    public String getPluginTitle() {
        return "ICPC Account Frame";
    }

    /**
     * This method initializes languagePane
     * 
     * @return edu.csus.ecs.pc2.ui.LanguagePane
     */
    private ICPCAccountPane getICPCAccountPane() {
        if (icpcAccountPane == null) {
            icpcAccountPane = new ICPCAccountPane();
        }
        return icpcAccountPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
