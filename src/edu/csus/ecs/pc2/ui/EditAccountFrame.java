package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import java.awt.Dimension;

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

    private IInternalContest contest;

    private IInternalController controller;

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
        this.setSize(new Dimension(550, 485));
        this.setPreferredSize(new Dimension(550, 485));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getAccountPane());
        this.setTitle("New Account");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
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

}  //  @jve:decl-index=0:visual-constraint="12,18"
