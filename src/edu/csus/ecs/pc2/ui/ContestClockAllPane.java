package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;

/**
 * A Pane that shows all contest clock displays
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestClockAllPane extends JPanePlugin {

    private static final long serialVersionUID = 8645891083569276626L;

    private Log log;

    private boolean labelsAdded = false;

    private ContestClockDisplay contestClockDisplay = null;

    private JLabel label;

    private JLabel label_1;

    private JLabel label_2;

    private JLabel label_3;

    /**
     * This method initializes 
     * 
     */
    public ContestClockAllPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(518, 674));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel scheduledAndRemainingPane = new JPanel();
        add(scheduledAndRemainingPane);
        scheduledAndRemainingPane.setLayout(new BorderLayout(0, 0));
        
        label_3 = new JLabel();
        scheduledAndRemainingPane.add(label_3);
        label_3.setText("XX:XX:XX");
        label_3.setHorizontalAlignment(SwingConstants.CENTER);
        label_3.setFont(new Font("Dialog", Font.BOLD, 64));
        
        JLabel lblSchedAndRemain = new JLabel("Schedule then Remain");
        lblSchedAndRemain.setHorizontalAlignment(SwingConstants.CENTER);
        lblSchedAndRemain.setFont(new Font("Dialog", Font.PLAIN, 36));
        scheduledAndRemainingPane.add(lblSchedAndRemain, BorderLayout.NORTH);
        
        JPanel remainingTimePane = new JPanel();
        add(remainingTimePane);
        remainingTimePane.setLayout(new BorderLayout(0, 0));
        
        label_1 = new JLabel();
        label_1.setText("XX:XX:XX");
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        label_1.setFont(new Font("Dialog", Font.BOLD, 64));
        remainingTimePane.add(label_1);
        
        JLabel lblRemaining = new JLabel("Remaining");
        lblRemaining.setHorizontalAlignment(SwingConstants.CENTER);
        lblRemaining.setFont(new Font("Dialog", Font.PLAIN, 36));
        remainingTimePane.add(lblRemaining, BorderLayout.NORTH);
        
        JPanel scheduledTimePane = new JPanel();
        add(scheduledTimePane);
        scheduledTimePane.setLayout(new BorderLayout(0, 0));
        
        label_2 = new JLabel();
        label_2.setText("XX:XX:XX");
        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        label_2.setFont(new Font("Dialog", Font.BOLD, 64));
        scheduledTimePane.add(label_2);
        
        JLabel lblScheduleCountdown = new JLabel("Schedule Countdown");
        lblScheduleCountdown.setHorizontalAlignment(SwingConstants.CENTER);
        lblScheduleCountdown.setFont(new Font("Dialog", Font.PLAIN, 36));
        scheduledTimePane.add(lblScheduleCountdown, BorderLayout.NORTH);
        
        JPanel elapsedTimePane = new JPanel();
        add(elapsedTimePane);
        elapsedTimePane.setLayout(new BorderLayout(0, 0));
        
        label = new JLabel();
        label.setText("XX:XX:XX");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 64));
        elapsedTimePane.add(label, BorderLayout.CENTER);
        
        JLabel lblElapsed = new JLabel("Elapsed");
        lblElapsed.setHorizontalAlignment(SwingConstants.CENTER);
        lblElapsed.setFont(new Font("Dialog", Font.PLAIN, 36));
        elapsedTimePane.add(lblElapsed, BorderLayout.NORTH);
    }

    @Override
    public String getPluginTitle() {
        return "Contest Clock - All Countdowns Pane";
    }



    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        boolean isTeam = getContest().getClientId().getClientType().equals(ClientType.Type.TEAM);

        contestClockDisplay = new ContestClockDisplay(log, getContest().getContestTime(), getContest().getSiteNumber(), isTeam, null);
        contestClockDisplay.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });
    }

    private void populateGUI() {

        // only load lables one
        
        if (! labelsAdded){

            int siteNumber = getContest().getSiteNumber();
            
            contestClockDisplay.addLabeltoUpdateList(label, DisplayTimes.ELAPSED_TIME, siteNumber);
            
            contestClockDisplay.addLabeltoUpdateList(label_1, DisplayTimes.REMAINING_TIME, siteNumber);
            
            contestClockDisplay.addLabeltoUpdateList(label_2, DisplayTimes.TO_SCHEDULED_START_TIME, siteNumber);
            
            contestClockDisplay.addLabeltoUpdateList(label_3, DisplayTimes.SCHEDULED_THEN_REMAINING_TIME, siteNumber);
            
            labelsAdded = true;
            
        }
        
        
    }


    public void setClientFrame(JFrame frame) {
        contestClockDisplay.setClientFrame(frame);
    }
    

} //  @jve:decl-index=0:visual-constraint="10,10"
