package edu.csus.ecs.pc2.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.csus.ecs.pc2.core.ICountDownMessage;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.RomanNumeral;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Text Countdown timeer with optional program halt;
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TextCountDownMessage extends TimerTask implements ICountDownMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -1864153362739487599L;

    private static final long ONE_SECOND_IN_MS = 1000;

    private boolean exitOnClose = false;

    private int inputRemainingSeconds = 10;

    private Timer timer = null;

    private long endMilliSeconds;

    private String prefixToTime = "";

    public TextCountDownMessage() {

    }

    public void start(String prefixForCount, int seconds) {
        setRemainingSeconds(seconds);
        String remainSring = prefixToTime + new RomanNumeral(seconds).toString() + " seconds";
        output(remainSring);
        setPrefixToTime(prefixForCount);
        endMilliSeconds = new Date().getTime() + inputRemainingSeconds * ONE_SECOND_IN_MS;
        timer = new Timer();
        timer.scheduleAtFixedRate(this, 0, ONE_SECOND_IN_MS);
    }

    public void actionOnClose() {
        if (exitOnClose) {
            System.exit(4);
        } else {
            timer.cancel();
        }
    }

    public boolean isExitOnClose() {
        return exitOnClose;
    }

    public void setExitOnClose(boolean exitOnClose) {
        this.exitOnClose = exitOnClose;
    }

    public int getRemainingSeconds() {
        return inputRemainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        inputRemainingSeconds = remainingSeconds;
    }

    private void output(String message) {
        System.out.println(message);
    }

    public String getPrefixToTime() {
        return prefixToTime;
    }

    public void setPrefixToTime(String prefixToTime) {
        if (prefixToTime != null) {
            this.prefixToTime = prefixToTime;
        }
    }

    public void setTitle(String string) {
        // not needed in a Text based countdown
        
    }

    public void setVisible(boolean b) {
        // not needed in a Text based countdown
        
    }

    @Override
    public void run() {
        long remainingSeconds = (endMilliSeconds - new Date().getTime()) / ONE_SECOND_IN_MS;
        String message = null;
        
        if (remainingSeconds >= 1) {
            message = prefixToTime + new RomanNumeral(remainingSeconds).toString() + " seconds";
        } else {
            message = prefixToTime + "0 seconds";
        }

        output(message);

        if (remainingSeconds < 1) {
            actionOnClose();
        }
    }


    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        // unused
    }

    public String getPluginTitle() {
        return "Text Countdown timer";
    }
    
   
}
