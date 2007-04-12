package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.MCLB;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Site;
import javax.swing.JButton;

/**
 * Contest Times Pane.
 * 
 * Shows contest times at all sites.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ContestTimesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8946167067842024295L;

    private JPanel contestTimeButtonPane = null;

    private MCLB contestTimeListBox = null;

    private JButton contestTimeRefreshButton = null;

    /**
     * This method initializes
     * 
     */
    public ContestTimesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getContestTimeListBox(), java.awt.BorderLayout.CENTER);
        this.add(getContestTimeButtonPane(), java.awt.BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method initializes contestTimeButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContestTimeButtonPane() {
        if (contestTimeButtonPane == null) {
            contestTimeButtonPane = new JPanel();
            contestTimeButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            contestTimeButtonPane.add(getContestTimeRefreshButton(), null);
        }
        return contestTimeButtonPane;
    }

    /**
     * This method initializes contestTimeListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getContestTimeListBox() {
        if (contestTimeListBox == null) {
            contestTimeListBox = new MCLB();

            Object[] cols = { "Site", "Running", "Remaining", "Elapsed", "Length" };

            contestTimeListBox.addColumns(cols);
            contestTimeListBox.autoSizeAllColumns();

        }
        return contestTimeListBox;
    }

    public void updateContestTimeRow(final ContestTime contestTime) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildContestTimeRow(contestTime.getSiteNumber(), contestTime);
                int rowNumber = contestTimeListBox.getIndexByKey(contestTime.getElementId());
                if (rowNumber == -1) {
                    contestTimeListBox.addRow(objects, contestTime.getElementId());
                } else {
                    contestTimeListBox.replaceRow(objects, rowNumber);
                }
                contestTimeListBox.autoSizeAllColumns();
                contestTimeListBox.sort();
            }
        });
    }

    protected Object[] buildContestTimeRow(int siteNumber, ContestTime contestTime) {

        // Object[] cols = { "Site", "Running", "Remaining", "Elapsed", "Length" };

        int numberColumns = contestTimeListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = "Site " + siteNumber;

        if (contestTime != null) {
            if (contestTime.isContestRunning()) {
                c[1] = "STARTED";
            } else {
                c[1] = "STOPPED";
            }

            c[2] = contestTime.getRemainingTimeStr();
            c[3] = contestTime.getElapsedTimeStr();
            c[4] = contestTime.getContestLengthStr();
        }

        return c;
    }

    private void reloadListBox() {

        contestTimeListBox.removeAllRows();
        Site[] sites = getModel().getSites();

        for (Site site : sites) {
            ContestTime contestTime = getModel().getContestTime(site.getSiteNumber());
            addContestTimeRow(site.getSiteNumber(), contestTime);
        }
    }

    private void addContestTimeRow(int siteNumber, ContestTime contestTime) {
        Object[] objects = buildContestTimeRow(siteNumber, contestTime);
        contestTimeListBox.addRow(objects, contestTime.getElementId());
        contestTimeListBox.autoSizeAllColumns();
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);

        getModel().addContestTimeListener(new ContestTimeListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    /**
     * ContestTime Listener
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // TODO Auto-generated method stub
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestStarted(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

        public void contestStopped(ContestTimeEvent event) {
            updateContestTimeRow(event.getContestTime());
        }

    }

    /**
     * This method initializes contestTimeRefreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getContestTimeRefreshButton() {
        if (contestTimeRefreshButton == null) {
            contestTimeRefreshButton = new JButton();
            contestTimeRefreshButton.setText("Refresh");
            contestTimeRefreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reloadListBox();
                }
            });
        }
        return contestTimeRefreshButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
