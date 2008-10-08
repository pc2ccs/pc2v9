package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;

/**
 * View List (Grid) of colors by problem(x) and sites(y)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonColorListPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7483784815760107250L;

    private JPanel balloonColorListButtonPane = null;

    private MCLB balloonColorListBox = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    /**
     * This method initializes
     * 
     */
    public BalloonColorListPane() {
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
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getBalloonColorListBox(), java.awt.BorderLayout.CENTER);
        this.add(getBalloonColorListButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    @Override
    public String getPluginTitle() {
        return "BalloonColorList Pane";
    }

    /**
     * This method initializes balloonSettingsButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBalloonColorListButtonPane() {
        if (balloonColorListButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            balloonColorListButtonPane = new JPanel();
            balloonColorListButtonPane.setLayout(flowLayout);
            balloonColorListButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
        }
        return balloonColorListButtonPane;
    }

    /**
     * This method initializes balloonSettingsListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getBalloonColorListBox() {
        if (balloonColorListBox == null) {
            balloonColorListBox = new MCLB();

//            Object[] cols = new Object[getContest().getSites().length+1];
//
//            cols[0] = "Problem";
//            Site[] sites = getContest().getSites();
//            Arrays.sort(sites, new SiteComparatorBySiteNumber());
//            int i = 1;
//            for (Site site : sites) {
//                cols[i] = site.getDisplayName();
//                i++;
//            }
//            balloonColorListBox.addColumns(cols);
//            balloonColorListBox.autoSizeAllColumns();

        }
        return balloonColorListBox;
    }

    private String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    protected Object[] buildBalloonSettingsRow(BalloonSettings balloonSettings) {
//        Object[] cols = { "Site", "Print", "E-mail", "Printer", "Sent To", "Mail Server" , "Balloon Client"};
        int numberColumns = getBalloonColorListBox().getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = "Site " + balloonSettings.getSiteNumber();
        c[1] = yesNoString(balloonSettings.isPrintBalloons());
        c[2] = yesNoString(balloonSettings.isEmailBalloons());
        c[3] = balloonSettings.getPrintDevice();
        c[4] = balloonSettings.getEmailContact();
        c[5] = balloonSettings.getMailServer();
        // it is an error if we get here and balloonClient is not set
        c[6] = balloonSettings.getBalloonClient().toString();

        return c;
    }

    private void reloadListBox() {

        getBalloonColorListBox().removeAllColumns();
        Object[] cols = new Object[getContest().getSites().length+1];

        cols[0] = "Problem";
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        int i = 1;
        for (Site site : sites) {
            cols[i] = site.getDisplayName();
            i++;
        }
        balloonColorListBox.addColumns(cols);

//        getBalloonColorListBox().removeAllRows();
        // TODO get this in problem order...
        Problem[] problems = getContest().getProblems();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Problem problem : problems) {
            addBalloonColorRow(problem, sites);
        }
        balloonColorListBox.autoSizeAllColumns();
    }

    private void addBalloonColorRow(Problem problem, Site[] sites) {
        Object[] row = buildBalloonColorRow(problem, sites);
        getBalloonColorListBox().addRow(row, problem);
    }

    private Object[] buildBalloonColorRow(Problem problem, Site[] sites) {
        int numberColumns = getBalloonColorListBox().getColumnCount();
        Object[] c = new String[numberColumns];
        c[0] = problem.getDisplayName();
        int i = 1;
        for (Site site : sites) {
            String color = "undefined";
            BalloonSettings settings = getContest().getBalloonSettings(site.getSiteNumber());
            if (settings == null) {
                color = "n/a";
            } else {
                color = settings.getColor(problem);
                if (color == null || color.trim().length() == 0) {
                    color = "undefined";
                }
            }
            c[i] = color;
            i++;
        }
        return c;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addSiteListener(new SiteListenerImplementation());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }


    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    /**
     * Balloon Settings Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    private class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void balloonSettingsChanged(final BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void problemChanged(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
        
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    private class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteRemoved(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteChanged(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteLoggedOn(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteLoggedOff(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
