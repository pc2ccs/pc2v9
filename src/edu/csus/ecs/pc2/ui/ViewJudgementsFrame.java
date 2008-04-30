package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;

/**
 * View Judgement Frame.
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class ViewJudgementsFrame extends JFrame implements UIPlugin {
    
    /**
     * 
     */
    private static final long serialVersionUID = 3591229492456973289L;

    private IInternalContest contest;

    private IInternalController controller;

    private Run run = null;

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private ViewJudgementsPane viewJudgementsPane = null;

    private JButton closeButton = null;

    /**
     * This method initializes
     * 
     */
    public ViewJudgementsFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(700,329));
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Run Judgements");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        viewJudgementsPane.setContestAndController(contest, controller);

        contest.addRunListener(new RunListenerImplementation());
    }

    public void setRun(Run theRun) {
        if (theRun == null) {
            setTitle("View Run Judgments");
        } else {
            setTitle("Run Judgements for " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            run = theRun;
        }
        viewJudgementsPane.setRun(theRun);
    }

    public String getPluginTitle() {
        return "Edit Run Frame";
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            if (run != null) {
                if (event.getRun().getElementId().equals(run.getElementId())) {

                    viewJudgementsPane.setRun(event.getRun());
                    
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getViewJudgementsPane(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPanel.add(getCloseButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes viewJudgementsPane
     * 
     * @return edu.csus.ecs.pc2.ui.ViewJudgementsPane
     */
    private ViewJudgementsPane getViewJudgementsPane() {
        if (viewJudgementsPane == null) {
            viewJudgementsPane = new ViewJudgementsPane();
        }
        return viewJudgementsPane;
    }

    /**
     * This method initializes Close
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return closeButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
