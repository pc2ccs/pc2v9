package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class EditAccountFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2154916224530006165L;

    private IContest contest;

    private IController controller;

    private AccountPane accountPane = null;

    /**
     * This method initializes
     * 
     */
    public EditAccountFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549,349));
        this.setContentPane(getAccountPane());
        this.setTitle("New Account");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getAccountPane().setContestAndController(contest, controller);
        getAccountPane().setParentFrame(this);
    }

    public void setAccount(Account account) {
        if (account == null) {
            setTitle("Add New Account");
        } else {
            setTitle("Edit Account " + account.getClientId().getName() + " (Site " + account.getSiteNumber() + ")");
        }
        getAccountPane().setAccount(account);
    }

    public String getPluginTitle() {
        return "Edit Account Frame";
    }

    /**
     * This method initializes accountPane
     * 
     * @return edu.csus.ecs.pc2.ui.AccountPane
     */
    private AccountPane getAccountPane() {
        if (accountPane == null) {
            accountPane = new AccountPane();
        }
        return accountPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
