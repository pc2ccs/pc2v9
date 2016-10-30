package edu.csus.ecs.pc2.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

public class ContestClockAllPaneTest extends TestCase {
    
    
   public static JFrame createFrame(String title, JPanel pane){
        JFrame frame = new JFrame();
        
        frame.setSize(new java.awt.Dimension(660, 800));
        frame.setTitle(title);
        frame.setContentPane(pane);
        FrameUtilities.centerFrame(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        return frame;
    }
    
    public static void main(String[] args) {

        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        Calendar cal = GregorianCalendar.getInstance();
        
        ContestInformation info = contest.getContestInformation();

        info.setScheduledStartDate(cal.getTime());

        int seconds = 10;
        cal.add(GregorianCalendar.MILLISECOND, seconds * 1000);
        info.setScheduledStartDate(cal.getTime());
        contest.updateContestInformation(info);
        
//        display.setScheduledStartTime(info);

        ContestClockAllPane pane = new ContestClockAllPane();
        pane.setContestAndController(contest, controller);
        
        
        JFrame frame = createFrame("Title", pane);
        frame.setVisible(true);

    }

}
