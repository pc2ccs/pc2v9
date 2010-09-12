package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;

/**
 * A Pane that shows contest time with a big display.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO Make sure that site title is displayed, not just Site N
// TODO test site change
// TODO test if sites are added or removed, does pull down update ?
// TODO test if both elapsed and remaining can be displayed
// FIXME add contest time listener

// $HeadURL$
public class ContestClockPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5750318173412410396L;

    private JPanel buttonPane = null;

    private JPanel messagePane = null;

    private JPanel centerPane = null;

    private JLabel siteNameLabel = null;

    private JLabel clockLabel = null;

    private JLabel messageLabel = null;

    private JComboBox siteSelectComboBox = null;

    private Log log;

    private int currentSiteNumber;

    private ContestClockDisplay contestClockDisplay = null;

    /**
     * This method initializes 
     * 
     */
    public ContestClockPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(446, 221));
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Contest Clock Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.add(getSiteSelectComboBox(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("JLabel");
            messagePane = new JPanel();
            messagePane.add(messageLabel, null);
        }
        return messagePane;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            clockLabel = new JLabel();
            clockLabel.setText("XX:XX:XX");
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 72));
            clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            siteNameLabel = new JLabel();
            siteNameLabel.setText("Site XXX YYYY ZZZZ");
            siteNameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 24));
            siteNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            centerPane = new JPanel();
            centerPane.setLayout(gridLayout);
            centerPane.add(clockLabel, null);
            centerPane.add(siteNameLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes siteSelectComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSiteSelectComboBox() {
        if (siteSelectComboBox == null) {
            siteSelectComboBox = new JComboBox();
            siteSelectComboBox.setPreferredSize(new java.awt.Dimension(300, 25));
            siteSelectComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    updateSiteLabelAndTime();
                }
            });
        }
        return siteSelectComboBox;
    }

    protected void updateSiteLabelAndTime() {
        int pickedSiteNumber = getSiteSelectComboBox().getSelectedIndex();

        showMessage("");
        if (pickedSiteNumber > -1) {
            // check to see if we have contest time for site
            pickedSiteNumber++;

            ContestTime contestTime = getContest().getContestTime(pickedSiteNumber);
            if (contestTime != null) {
                updateSiteLabelAndTime(pickedSiteNumber);
            } else {
                showMessage("Site " + pickedSiteNumber + " time not available");
            }
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        boolean isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);

        contestClockDisplay = new ContestClockDisplay(log, getContest().getContestTime(), getContest().getSiteNumber(), isTeam, null);
        contestClockDisplay.setContestAndController(inContest, inController);

        showMessage("");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });
    }

    private void populateGUI() {

        Site[] sites = getContest().getSites();
        Arrays.sort(sites,new SiteComparatorBySiteNumber());

        getSiteSelectComboBox().removeAllItems();

        for (Site site : sites) {
            getSiteSelectComboBox().addItem("Site " + site.getSiteNumber());
        }

        getSiteSelectComboBox().setSelectedIndex(getContest().getSiteNumber() - 1);

        int siteNumber = getContest().getSiteNumber();

        updateSiteLabelAndTime(siteNumber);

    }

    private void updateSiteLabelAndTime(int siteNumber) {
        siteNameLabel.setText("Site " + siteNumber);

        if (currentSiteNumber == 0) {
            // No label set 
            contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, siteNumber);
            currentSiteNumber = siteNumber;

        } else if (siteNumber != currentSiteNumber) {
            // changed from old site to new site

            contestClockDisplay.removeLabelFromUpdateList(clockLabel, currentSiteNumber);

            contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, siteNumber);
            currentSiteNumber = siteNumber;
        }

        ContestTime contestTime = getContest().getContestTime(currentSiteNumber);
        if (contestTime != null) {
            contestClockDisplay.fireClockStateChange(contestTime);
        }
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });
    }

    public void hideButtonPane() {
        getButtonPane().setVisible(false);
    }

    public void setClientFrame(JFrame frame) {
        contestClockDisplay.setClientFrame(frame);
    }

} //  @jve:decl-index=0:visual-constraint="10,10"
