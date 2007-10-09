/**
 * 
 */
package edu.csus.ecs.pc2.core.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Balloon;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonWriter {
    private Object printingLock = new Object();

    private Log log;

    private static final String NL = System.getProperty("line.separator");

    private String myHostName = "";

    /**
     * 
     */
    public BalloonWriter(Log aLog) {
        super();
        log = aLog;
        try {
            InetAddress address = InetAddress.getLocalHost();
            if (address != null) {
                myHostName = address.getHostName();
            } else {
                log.fine("getLocalHost() returned null");
                myHostName = "localhost";
            }
        } catch (UnknownHostException e) {
            log.fine("getLostHost threw a UnknownHostException");
            myHostName = "localhost";
        }
    }
    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     * @param answer java.lang.String
     * @param riaf pc2.ex.RunInfoAndFiles
     * @param postscript java.lang.boolean
     */
    private String buildBalloonMessage(Balloon balloon, boolean postscript) {
        String answer = balloon.getAnswer();
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        StringBuffer message= new StringBuffer("");
        int x=72;   // 1 inch from the left edge
        int y=72*10;
        
        if (postscript) {
            message.append("%!"+NL+NL);
            message.append("/Time-Roman findfont 18 scalefont setfont"+NL);
        }
        if (answer.equalsIgnoreCase("revoke")) {
            message.append(print("There has been a change in judgement.",postscript,x,y));
            message.append(NL);
            y -= 20;
            message.append(print("Please ensure "+balloon.getClientId().getName()+" ("+balloon.getClientTitle()+ ")",postscript,x,y));
            message.append(NL);
            y -= 20;
            message.append(print("does not have a "+balloonSettings.getColor(balloon.getProblemId())+" colored balloon.",postscript, x, y));
            message.append(NL+NL+NL);
            y -= 20*3;
        }
        y = buildBalloonMessageRunInfo(message, balloon, postscript, y);
        y = buildBalloonMessageSummary(message, balloon, postscript, y-20);
        if (postscript) {
            message.append(NL+"showpage"+NL);
        }
        return message.toString();
    }
    /**
     * Insert the method's description here.
     * Creation date: (5/1/2005 2:21:16 PM)
     * @return java.lang.int
     * @param message StringBuffer
     * @param status java.lang.String
     * @param riaf pc2.ex.RunInfoAndFiles
     * @param postscript boolean
     * @param y java.lang.int
     */
    private int buildBalloonMessageRunInfo(StringBuffer message, Balloon balloon, boolean postscript, int y) {
        String status = balloon.getAnswer();
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        try {
            int x=72;   // 1 inch from the left edge
            if (postscript) {
                message.append("/Time-Roman findfont 18 scalefont setfont"+NL);
            }
            String yesOrNo = "";
            if (status.equalsIgnoreCase("yes")){
                yesOrNo="YES";
            } else {
                yesOrNo="NO";
            }
            message.append(print(yesOrNo+" for "+balloon.getClientId().getName(),postscript,x,y));
            message.append(NL);
            y -= 20;
            if (status.equalsIgnoreCase("yes")) {
                message.append(print("Color: "+balloonSettings.getColor(balloon.getProblemId()),postscript,x,y));
                message.append(NL);
                y -= 20;
            }
            message.append(print("Team: "+balloon.getClientId().getName()+" ("+balloon.getClientTitle()+")",postscript,x,y));
            message.append(NL);
            y -= 20;
            message.append(print("Problem: "+balloon.getProblemTitle(),postscript,x,y));
            message.append(NL);
            y -= 20;
            message.append(print("Time: "+balloon.getRun().getElapsedMins(),postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("RunID: "+balloon.getRun().getNumber(),postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("Current Date: "+new Date().toString(),postscript, x, y));
            message.append(NL);
            y -= 20*2;
        } catch (Exception e) {
            log.throwing(getClass().getName(),"buildBaloonMesageRunInfo for "+balloon.getRun().getNumber()+" error.",e);                    
        }

        return y;
    }
    /**
     * if status is "test" generates a message consisting of all the problem colors,
     * otherwise should be a list of all the problems this user has solved.
     *
     * @param message
     * @param status
     * @param riaf
     * @param postscript
     * @param y
     * @return updated y
     */
    private int buildBalloonMessageSummary(StringBuffer message, Balloon balloon, boolean postscript, int y) {
        String status = balloon.getAnswer();
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        try {
            int x = 72; // 1 inch from the left edge
            if (postscript) {
                message.append("/Time-Roman findfont 14 scalefont setfont" + NL);
            }
            StringWriter sw = new StringWriter();
            if (status.equalsIgnoreCase("test")) {
                //if (postscript) {
                    //message.append("/Time-Roman findfont 18 scalefont setfont" + NL);
                    //message.append(X + " " + Y + " moveto" + NL);
                //}
                message.append(print("List of balloon colors:", postscript, x, y));
                message.append(NL);
                Problem[] problems = balloon.getProblems();
                for (int j = 0; j < problems.length; j++) {
                    sw.write(NL);
                    y -= 16*2;
                    sw.write(print("    " + balloonSettings.getColor(problems[j]) + " - " + problems[j].getDisplayName(), postscript, x, y));
                    sw.write(NL);
                }
                message.append(sw.toString());
            } else {
                int count = 0;
                int saveY = y;
                    Problem[] problems = balloon.getProblems();
                    for (int j = 0; j < problems.length; j++) {
                        sw.write(NL);
                        y -= 16*2;
                        sw.write(print("    " + balloonSettings.getColor(problems[j]) + " - " + problems[j].getDisplayName(), postscript, x, y));
                        sw.write(NL);
                        count++;
                    }
                    if (count == 1) {
                        message.append(NL + NL + NL);
                        message.append(print("Team now has " + count + " balloon:",postscript,x,saveY));
                        message.append(NL + sw.toString());
                    } else {
                        if (count > 1) {
                            message.append(NL + NL + NL);
                            message.append(print("Team now has " + count + " balloons:",postscript,x,saveY));
                            message.append(NL + sw.toString());
                        } else { // count == 0
                            message.append(NL + NL + NL);
                            message.append(print("This team now has 0 (zero) balloons.",postscript,x,saveY));
                            message.append(NL);
                        }
                    }
                    sw.close();
                    sw = null;
            }
        } catch (Exception e) {
            log.throwing(getClass().getName(), "buildBaloonMesageSummary for " + balloon.getRun() + " error collecting list of balloons", e);
        }
        return y;
    }

    /**
     * Returns message if postscript = false Otherwise a Postscript string that does a moveto X Y followed by a show of the quoted version of message
     * 
     * @return java.lang.String
     * @param message
     *            java.lang.String
     * @param postscript
     *            boolean
     * @param X
     *            int
     * @param Y
     *            int
     */
    private String print(String message, boolean postscript, int x, int y) {
        if (message == null) {
            return null;
        }
        if (postscript) {
            StringWriter psString=new StringWriter(message.length()+30);
            psString.write(x+" "+y+" moveto"+NL+"(");
            try {
                for (int i = 0; i < message.length(); i++){
                    char ch = message.charAt(i);
                    switch (ch) {
                        case '(':
                            psString.write("\\(");
                            break;
                        case ')':
                            psString.write("\\)");
                            break;
                        default:
                            psString.write(ch);
                    }
                }
            } catch (Exception e) {
                log.throwing(getClass().getName(), "Unable to postscript quote("+message+")",e);
                return message;
            }
            psString.write(") show");
            return psString.toString();
        } else {
            return message;
        }
    }

    public void sendBalloon(Balloon balloon) {
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        try {
            log.entering(getClass().getName(), "sendBalloon", balloon.getAnswer());
            String message = buildBalloonMessage(balloon, false);
            String answer = balloon.getAnswer();

            if (balloonSettings.isEmailBalloons()) {
                if (balloonSettings.getEmailContact() != null && balloonSettings.getEmailContact().trim().length() > 0) {
                    String to = balloonSettings.getEmailContact().trim();
                    if (balloonSettings.getMailServer() != null && balloonSettings.getMailServer().trim().length() > 0) {
                        InetAddress mailServer;
                        try {
                            mailServer=InetAddress.getByName(balloonSettings.getMailServer());
                        } catch (UnknownHostException uhe) {
                            log.throwing(getClass().getName(), "sendBalloons smtpServerString=" + balloonSettings.getMailServer(), uhe);
                            mailServer = null;
                        }

                        String emailMessage = "";
                        String date = Utilities.getRFC2822DateTime();
                        if (answer.equalsIgnoreCase("yes")) {
                            String subject = "Subject: YES " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ") color " + balloonSettings.getColor(balloon.getProblemId()) + NL;
                            emailMessage = subject + "Date: " + date + NL + NL
                                    + message;
                        } else if (answer.equalsIgnoreCase("no")) {
                            // TODO need problemTitle here
                            String subject = "Subject: NO " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ")" + " problem " + balloon.getProblemId() + NL;
                            emailMessage = subject + "Date: " + date + NL + NL + message;
                        } else {
                            String subject = "Subject: Take away balloon from " + balloon.getClientId().getName() + " color " + balloonSettings.getColor(balloon.getProblemId()) + NL;
                            emailMessage = subject + "Date: " + date + NL + NL + message;
                        }
    
                        if (mailServer != null) {
                            sendBalloonByEmail(emailMessage, mailServer, to);
                        }
                        emailMessage = null;
                    } else {
                        // TODO cleanup this message
                        log.info("emailing enabled, but no mailserver set");
                    }
                } else {
                    // TODO cleanup this message
                    log.info("emailing enabled, but no emailContest set");
                }
            }

            if (balloonSettings.isPrintBalloons()) {
                if (balloonSettings.getPrintDevice() != null && balloonSettings.getPrintDevice().trim().length() > 0 ) {
                    if (balloonSettings.isPostscriptCapable()) {
                        // then rebuild message as postscript
                        message = buildBalloonMessage(balloon, true);
                    } else {
                        int lines = 66;
                        if (balloonSettings.getLinesPerPage() > 0) {
                            lines = balloonSettings.getLinesPerPage();
                        }
                        // yuck, kludge to get number of lines in message
                        int index = message.indexOf(NL, 0);
                        int count = 0;
                        while (index != -1) {
                            count++;
                            index = message.indexOf(NL, index + 1);
                        }
                        if (count < lines) {
                            StringWriter sw = new StringWriter(66);
                            for (int i = count; i < lines; i++) {
                                sw.write(NL);
                            }
                            message = message + sw.toString();
                            sw.close();
                            sw = null;
                        }
                        // or we could just rely on a formfeed
                        // message = message + '\f';
                    }
                    sendBalloonToLocalFile(message, balloonSettings.getPrintDevice().trim());
                } else {
                    // TODO cleanup this message
                    log.info("printing enabled, but no printDevice set");
                }
            }

            message = null;
        } catch (Exception e) {
            log.throwing(getClass().getName(), "sendBalloon()", e);
        } finally {
            if (log == null) {
                System.err.println("Log is null");
            }
            if (getClass() == null) {
                System.err.println("getClass is null");
            }
            log.exiting(getClass().getName(), "sendBalloon()");
        }
    }

    private boolean sendBalloonByEmail(String message, InetAddress host, String mailTo) {
        boolean success = false;
        try {
            log.entering(getClass().getName(), "sendBalloonByEmail()", host.toString());
            Socket socket = new Socket(host, 25);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            int arraySize = 200;
            byte[] b = new byte[arraySize];
            boolean error = false;
            String mess = "";
            for (int dummy = 0; dummy < 1; dummy++) { /* just so we can break out */
                mess = "helo " + myHostName + NL;
                os.write(mess.getBytes());
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                mess = "mail from: pc2@ecs.csus.edu" + NL;
                os.write(mess.getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                mess = "rcpt to: " + mailTo + NL;
                os.write(mess.getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                mess = "rcpt to: " + mailTo + NL;
                os.write(mess.getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                mess = "data" + NL;
                os.write(mess.getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                // String mess="helo "+myHostName+NL+
                // "mail from: pc2@ecs.csus.edu"+NL+
                // "rcpt to: "+to+NL+
                // "data"+NL+
                mess = "From: pc2@ecs.csus.edu" + NL + "To: " + mailTo + NL;
                os.write(mess.getBytes());
                message = message + "." + NL;
                os.write(message.getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
                os.write(new String("quit" + NL).getBytes());
                b = new byte[arraySize];
                is.read(b);
                if (b[0] == '4' || b[0] == '5') {
                    // todo: handle error
                    log.info("ERROR: sendBalloon I said=\"" + mess + "\" server said=\"" + new String(b).trim() + "\"");
                    error = true;
                    break;
                }
            }
            socket.close();
            if (!error) {
                success = true;
            }
        } catch (Exception e) {
            log.throwing(getClass().getName(), "sendBallonBalloonByEmail()", e);
        } finally {
            log.exiting(getClass().getName(), "sendBalloonByEmail()", success);
        }
        return success;
    }

    private boolean sendBalloonToLocalFile(String message, String fileName) {
        boolean success = false;
        try {
            log.entering(getClass().getName(), "sendBalloonToLocalFile", fileName);
            log.finest("Obtaining lock to print");
            synchronized (printingLock) {
                FileOutputStream fout = new FileOutputStream(fileName);
                log.finest("Printing");
                PrintWriter pout = new PrintWriter(fout, true);
                pout.print(message);
                pout.close();
            }
            log.finest("Printing done, lock released");
            success = true;
        } catch (Exception e) {
            log.throwing(getClass().getName(), "sendBallonBalloonToLocalFile()", e);
        } finally {
            log.exiting(getClass().getName(), "sendBalloontoLocalFile", success);
        }
        return success;
    }
}
