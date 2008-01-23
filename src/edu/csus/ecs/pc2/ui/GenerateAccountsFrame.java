package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class GenerateAccountsFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2545121434161546435L;

    /**
     * 
     */

    private IContest contest;

    private IController controller;

    private GenerateAccountsPane generateAccountsPane = null;

    /**
     * This method initializes
     * 
     */
    public GenerateAccountsFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549,349));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.setContentPane(getGenerateAccountsPane());
        this.setTitle("Generate Accounts");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getGenerateAccountsPane().setContestAndController(contest, controller);
        getGenerateAccountsPane().setParentFrame(this);
    }



    public String getPluginTitle() {
        return "Generate Accounts Frame";
    }



    public GenerateAccountsPane getGenerateAccountsPane() {
        if (generateAccountsPane == null) {
            generateAccountsPane = new GenerateAccountsPane();     
        }
        return generateAccountsPane;
    }

    public void setGenerateAccountsPane(GenerateAccountsPane generateAccountsPane) {
        this.generateAccountsPane = generateAccountsPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
