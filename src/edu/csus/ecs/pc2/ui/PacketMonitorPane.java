package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Packet Monitor Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketMonitorPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1276113801345035959L;

    private JPanel buttonPane = null;

    private JButton detailsButton = null;

    private JButton reportButton = null;

    /**
     * This method initializes
     * 
     */
    public PacketMonitorPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(662, 169));
        this.add(getButtonPane(), BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        return "Packet Monitor";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(40, 40));
            buttonPane.add(getDetailsButton(), null);
            buttonPane.add(getReportButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes detailsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDetailsButton() {
        if (detailsButton == null) {
            detailsButton = new JButton();
            detailsButton.setText("Details");
            detailsButton.setMnemonic(KeyEvent.VK_D);
            detailsButton.setToolTipText("Show Details about Selected Packet");
            detailsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printDetails();
                }
            });
        }
        return detailsButton;
    }

    protected void printDetails() {

        JOptionPane.showMessageDialog(this, "Show Details");
        // FIXME code printDetails

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        String moduleInfo = inContest.getTitle();

        getParentFrame().setTitle(getParentFrame().getTitle() + " " + moduleInfo);

    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printReport();
                }
            });
        }
        return reportButton;
    }

    protected void printReport() {
        JOptionPane.showMessageDialog(this, "Show Reports");
        // FIXME code printReport
    }

} // @jve:decl-index=0:visual-constraint="10,10"
