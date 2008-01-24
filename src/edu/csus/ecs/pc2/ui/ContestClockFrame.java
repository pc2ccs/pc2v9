package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;

/**
 * A Frame that shows contest time(s).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestClockFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3280581230202123658L;

    private JPanel mainPanel1 = null;

    private JPanel buttonPane = null;

    private JButton closeButton = null;

    private ContestClockPane contestClockPane = null;

    /**
     * This method initializes
     * 
     */
    public ContestClockFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(474, 287));
        this.setTitle("InternalContest Clock");
        this.setContentPane(getMainPanel1());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

    }

    public void setContestAndController(IInternalContest model, IInternalController controller) {

        contestClockPane.setContestAndController(model, controller);

        contestClockPane.setClientFrame(this);

    }

    public String getPluginTitle() {
        return "InternalContest Clock";
    }

    /**
     * This method initializes mainPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel1() {
        if (mainPanel1 == null) {
            mainPanel1 = new JPanel();
            mainPanel1.setLayout(new BorderLayout());
            mainPanel1.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
            mainPanel1.add(getContestClockPane(), java.awt.BorderLayout.CENTER);
        }
        return mainPanel1;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes closeButton
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

    /**
     * This method initializes contestClockPane
     * 
     * @return edu.csus.ecs.pc2.ui.ContestClockPane
     */
    private ContestClockPane getContestClockPane() {
        if (contestClockPane == null) {
            contestClockPane = new ContestClockPane();
            contestClockPane.hideButtonPane();
        }
        return contestClockPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
