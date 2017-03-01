package edu.csus.ecs.pc2.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.csus.ecs.pc2.core.log.NullController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 *
 * @author Douglas A. Lane, PC^2 Team, &lt;pc2@ecs.csus.edu&gt;
 */
public class AutoStarterTest extends AbstractTestCase {
    
    private SampleContest sample = new SampleContest();
    
    public AutoStarterTest() {
        super();
        setDebugMode(true);
    }

    /**
     * print a message runnable.
     *
     * @author Douglas A. Lane, PC^2 Team, &lt;pc2@ecs.csus.edu&gt;
     */
    public class PrintMessgeRunnable implements Runnable {

        private String message;

        PrintMessgeRunnable(String message){
            this.message = message;
        }
        
        @Override
        public void run() {
            debugPrintln(new Date()+" message is "+message);
        }
        
    }
    
    public void testSimpleAutoStart() throws Exception {
        
         String dir = getOutputDataDirectory();
         ensureDirectory(dir);
//         String logname = dir + File.separator + getName() + ".log";
         String logname = getName() + ".log";
        
        AutoStarter autoStarter = createAutoStarter(logname);
        
        assertNull(autoStarter.getScheduledFutureStartTime());
        assertFalse (autoStarter.isAutoStartTaskRunning());

        ContestInformation info = sample.createStandardContest().getContestInformation();

//        debugPrintln("  Now = "+ AutoStarter.formatTime(new GregorianCalendar()));
        
        updateStartTime (info, (long) 100);

//        debugPrintln("S Date = "+ AutoStarter.formatTime(autoStarter.getScheduledFutureStartTime()));

        autoStarter.setStarter(new PrintMessgeRunnable(getName()));
        
        autoStarter.updateScheduleStartContestTask(info);
        
        assertNotNull(autoStarter.getScheduledFutureStartTime());
        assertTrue (autoStarter.isAutoStartTaskRunning());
        
    }
    
    
    /**
     * A class that prints a message when startAllContestTimes invoked.
     *
     * @author Douglas A. Lane, PC^2 Team, &lt;pc2@ecs.csus.edu&gt;
     */
    public class OverRideStarterController extends InternalController{
        
        public OverRideStarterController(IInternalContest contest) {
            super(contest);
        }

        @Override
        public void startAllContestTimes() {
            debugPrintln("Started All Contest Clocks");
        }
        
    }
    
    /**
     * Test whether invokes startAllContestTimes.
     * 
     * @throws Exception
     */
    public void testStarter() throws Exception {
        
        IInternalContest contest = sample.createStandardContest();
        IInternalController controller = new OverRideStarterController(contest);
        AutoStarter autoStarter = new AutoStarter(contest, controller);

        ContestInformation info = sample.createStandardContest().getContestInformation();

        updateStartTime (info, (long) 15);

        autoStarter.updateScheduleStartContestTask(info);

        assertNotNull(autoStarter.getScheduledFutureStartTime());
        assertTrue (autoStarter.isAutoStartTaskRunning());
      
        
    }

    public void testgetRemainingTimeMs() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        String dir = getOutputDataDirectory();
        ensureDirectory(dir);
        String logname = getName() + ".log";

        AutoStarter autoStarter = createAutoStarter(logname);
        ContestInformation info = sample.createStandardContest().getContestInformation();

        int startSecondsInFuture = 20;

        updateStartTime (info, (long) startSecondsInFuture);

        debugPrintln(new Date() +" started, start in "+startSecondsInFuture+" seconds. ");
        autoStarter.setStarter(new PrintMessgeRunnable(getName()));
        autoStarter.updateScheduleStartContestTask(info);

        assertNotNull(autoStarter.getScheduledFutureStartTime());
        assertTrue (autoStarter.isAutoStartTaskRunning());
       
//       debugPrintln("MS left "+autoStarter.getRemainingTimeMs());
       
       assertTrue("Expecting remaining time ", autoStarter.getRemainingTimeMs() > (startSecondsInFuture-1)*1000);
       
//       long remTime = autoStarter.getRemainingTimeMs();
       debugPrintln("Sleeping for "+startSecondsInFuture);
       sleepSecs(startSecondsInFuture);
    }

    /**
     * Update ContestInfo fields for auto starting.
     * 
     * @param info
     * @param secondsInFuture null - clear start time, else the number of seconds to start in the future.
     */
    private void updateStartTime(ContestInformation info, Long secondsInFuture) {
        
        if (secondsInFuture == null){
            info.setScheduledStartDate(null);
            info.setScheduledStartTime(null);
            info.setAutoStartContest(false);
        } else {
            GregorianCalendar greg = new GregorianCalendar();
            greg.add(GregorianCalendar.SECOND, secondsInFuture.intValue());
            info.setScheduledStartDate(greg.getTime());
            info.setScheduledStartTime(greg);
            info.setAutoStartContest(true);
            
        }
    }

    /**
     * Create a auto start with model and controller and log defined.
     * 
     * @param logFilename
     * @return
     */

    private AutoStarter createAutoStarter(String logFilename) {
        IInternalContest contest = sample.createStandardContest();
        IInternalController controller = new NullController(logFilename);
        AutoStarter autoStarter = new AutoStarter(contest, controller);
        return autoStarter;
    }
    
    public void testStartThenStopClear() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        String logname = getName() + ".log";
        AutoStarter autoStarter = createAutoStarter(logname);
        ContestInformation info = sample.createStandardContest().getContestInformation();

        int startSecondsInFuture = 60;
        updateStartTime (info, (long) startSecondsInFuture);

        debugPrintln(new Date() +" started, start in "+startSecondsInFuture+" seconds. ");
        
        autoStarter.setStarter(new PrintMessgeRunnable(getName()));
        autoStarter.updateScheduleStartContestTask(info);

        // test if started
        assertNotNull(autoStarter.getScheduledFutureStartTime());
        assertTrue (autoStarter.isAutoStartTaskRunning());
        
        debugPrint("start sleep");
        sleepSecs(startSecondsInFuture / 10);
        debugPrint("end sleep");
        
        // stop
        updateStartTime (info, null);
        autoStarter.updateScheduleStartContestTask(info);
        
        // test if stopped
        assertNull("Expecting scheduled start time to be null", autoStarter.getScheduledFutureStartTime());
        assertFalse(autoStarter.isAutoStartTaskRunning());
        
        updateStartTime (info, (long) startSecondsInFuture);
        autoStarter.updateScheduleStartContestTask(info);
        
        // test if started
        assertNotNull("Expected scheduled start time defined ", autoStarter.getScheduledFutureStartTime());
        assertTrue (autoStarter.isAutoStartTaskRunning());
    }
    
    public void testStartTimeBeforeNow() throws Exception {
        
        String logname = getName() + ".log";
        AutoStarter autoStarter = createAutoStarter(logname);
        ContestInformation info = sample.createStandardContest().getContestInformation();

        int startSecondsInFuture = -60; // in past
        updateStartTime (info, (long) startSecondsInFuture);

        debugPrintln(new Date() +" started, start in "+startSecondsInFuture+" seconds. ");
        autoStarter.updateScheduleStartContestTask(info);
        
        // test if stopped aka never started
        assertNull(autoStarter.getScheduledFutureStartTime());
        assertFalse(autoStarter.isAutoStartTaskRunning());
        
    }

    private void sleepSecs(long secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }

    public void nonTestGregTime() throws Exception {

        GregorianCalendar cal = new GregorianCalendar();
        debugPrintln(" time " + cal.getTime());
        String pattern = "YYY-MMM-dd MMM HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        debugPrintln("formatted "+format.format(cal.getTime()));
    }
}

