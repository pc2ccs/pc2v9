package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.admin.GenerateAndMergePasswordPane;

/**
 * Generate Acccounts Frame.
 *  
 * @author pc2@ecs.csus.edu
 */
public class GenerateAccountsFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2545121434161546435L;

    /**
     * 
     */

    private IInternalContest contest;

    private IInternalController controller;

    private GenerateAccountsPane generateAccountsPane = null;
    
    private GenerateAndMergePasswordPane generateAndMergePasswordPane = null;
    
    /**
     * This method initializes
     * 
     */
    public GenerateAccountsFrame() {
        super();
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Accounts", null, getGenerateAccountsPane(), null);
        
        tabbedPane.addTab("Passwords", null, getGenerateAndMergePasswordPane(), null);
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549,349));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        
        this.setTitle("Generate Accounts");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getGenerateAccountsPane().setContestAndController(contest, controller);
        getGenerateAccountsPane().setParentFrame(this);
        
        getGenerateAndMergePasswordPane().setContestAndController(contest, controller);
        getGenerateAndMergePasswordPane().setParentFrame(this);
    }



    public String getPluginTitle() {
        return "Generate Accounts Frame";
    }

    public GenerateAndMergePasswordPane getGenerateAndMergePasswordPane() {
        if (generateAndMergePasswordPane == null) {
            generateAndMergePasswordPane = new GenerateAndMergePasswordPane();
        }
        return generateAndMergePasswordPane;
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
