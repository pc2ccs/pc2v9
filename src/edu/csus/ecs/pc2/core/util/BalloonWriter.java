/**
 * 
 */
package edu.csus.ecs.pc2.core.util;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Balloon;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
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

    private String contestTitle = "";

    private String message = "";

    /**
     * 
     */
    public BalloonWriter(Log aLog) {
        super();
        log = aLog;
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return java.lang.String
     * @param answer
     *            java.lang.String
     * @param riaf
     *            pc2.ex.RunInfoAndFiles
     * @param postscript
     *            java.lang.boolean
     */
    private String buildBalloonMessage(Balloon balloon, boolean postscript) {
        String answer = balloon.getAnswer();
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        StringBuffer message = new StringBuffer("");
        int x = 72; // 1 inch from the left edge
        int y = 72 * 10;

        if (postscript) {
            message.append("%!" + NL + NL);
            message.append("/Time-Roman findfont 18 scalefont setfont" + NL);
        }
        if (answer.equalsIgnoreCase("revoke") || answer.equalsIgnoreCase("take")) {
            message.append(print("There has been a change in judgement.", postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("Please ensure " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ")", postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("does not have a " + balloonSettings.getColor(balloon.getProblemId()) + " colored balloon.", postscript, x, y));
            message.append(NL + NL + NL);
            y -= 20 * 3;
        }
        if (!answer.equalsIgnoreCase("test")) {
            y = buildBalloonMessageRunInfo(message, balloon, postscript, y);
        }
        y = buildBalloonMessageSummary(message, balloon, postscript, y - 20);
        if (postscript) {
            message.append(NL + "showpage" + NL);
        }
        return message.toString();
    }

    /**
     * Insert the method's description here. Creation date: (5/1/2005 2:21:16 PM)
     * 
     * @return java.lang.int
     * @param message
     *            StringBuffer
     * @param status
     *            java.lang.String
     * @param riaf
     *            pc2.ex.RunInfoAndFiles
     * @param postscript
     *            boolean
     * @param y
     *            java.lang.int
     */
    private int buildBalloonMessageRunInfo(StringBuffer message, Balloon balloon, boolean postscript, int y) {
        String status = balloon.getAnswer();
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        try {
            int x = 72; // 1 inch from the left edge
            if (postscript) {
                message.append("/Time-Roman findfont 18 scalefont setfont" + NL);
            }
            String yesOrNo = "";
            if (status.equalsIgnoreCase("yes")) {
                yesOrNo = "YES";
            } else {
                yesOrNo = "NO";
            }
            message.append(print(yesOrNo + " for " + balloon.getClientId().getName(), postscript, x, y));
            message.append(NL);
            y -= 20;
            if (status.equalsIgnoreCase("yes")) {
                message.append(print("Color: " + getColor(balloonSettings, balloon.getProblemId()), postscript, x, y));
                message.append(NL);
                y -= 20;
            }
            message.append(print("Team: " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ")", postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("Site: " + balloon.getClientId().getSiteNumber() + " - " + balloon.getSiteTitle(), postscript, x, y));
            message.append(NL);
            y -= 20;
            message.append(print("Problem: " + balloon.getProblemTitle(), postscript, x, y));
            message.append(NL);
            y -= 20;
            if (balloon.getRun() != null) {
                // is null on a take
                message.append(print("Time: " + balloon.getRun().getElapsedMins(), postscript, x, y));
                message.append(NL);
                y -= 20;
                message.append(print("RunID: " + balloon.getRun().getNumber(), postscript, x, y));
                message.append(NL);
                y -= 20;
            }
            if (contestTitle.trim().length() > 0) {
                message.append(print("Contest Title: " + contestTitle.trim(), postscript, x, y));
                message.append(NL);
                y -= 20;
            }
            message.append(print("Current Date: " + new Date().toString(), postscript, x, y));
            message.append(NL);
            y -= 20 * 2;
        } catch (Exception e) {
            log.throwing(getClass().getName(), "buildBaloonMesageRunInfo for " + balloon.getRun().getNumber() + " error.", e);
        }

        return y;
    }

    /**
     * if status is "test" generates a message consisting of all the problem colors, otherwise should be a list of all the problems this user has solved.
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
                // if (postscript) {
                // message.append("/Time-Roman findfont 18 scalefont setfont" + NL);
                // message.append(X + " " + Y + " moveto" + NL);
                // }
                message.append(print("List of balloon colors:", postscript, x, y));
                message.append(NL);
                Problem[] problems = balloon.getProblems();
                for (int j = 0; j < problems.length; j++) {
                    sw.write(NL);
                    y -= 16 * 2;
                    sw.write(print("    " + getColor(balloonSettings, problems[j]) + " - " + problems[j].getDisplayName(), postscript, x, y));
                    sw.write(NL);
                }
                message.append(sw.toString());
            } else {
                int count = 0;
                int saveY = y;
                Problem[] problems = balloon.getProblems();
                for (int j = 0; j < problems.length; j++) {
                    sw.write(NL);
                    y -= 16 * 2;
                    sw.write(print("    " + getColor(balloonSettings, problems[j]) + " - " + problems[j].getDisplayName(), postscript, x, y));
                    sw.write(NL);
                    count++;
                }
                if (count == 1) {
                    message.append(NL + NL + NL);
                    message.append(print("Team now has " + count + " balloon:", postscript, x, saveY));
                    message.append(NL + sw.toString());
                } else {
                    if (count > 1) {
                        message.append(NL + NL + NL);
                        message.append(print("Team now has " + count + " balloons:", postscript, x, saveY));
                        message.append(NL + sw.toString());
                    } else { // count == 0
                        message.append(NL + NL + NL);
                        message.append(print("This team now has 0 (zero) balloons.", postscript, x, saveY));
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

    private String getColor(BalloonSettings balloonSettings, Problem problem) {
        return getColor(balloonSettings, problem.getElementId());
    }

    /**
     * Pretty print the "color".
     * 
     * @param balloonSettings
     * @param problemId
     * @return <undefined> or a configured color
     */
    String getColor(BalloonSettings balloonSettings, ElementId problemId) {
        String color = balloonSettings.getColor(problemId);
        if (color == null || color.trim().equals("")) {
            color = "<undefined>";
        }
        return color;
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
            StringWriter psString = new StringWriter(message.length() + 30);
            psString.write(x + " " + y + " moveto" + NL + "(");
            try {
                for (int i = 0; i < message.length(); i++) {
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
                log.throwing(getClass().getName(), "Unable to postscript quote(" + message + ")", e);
                return message;
            }
            psString.write(") show");
            return psString.toString();
        } else {
            return message;
        }
    }

    /**
     * @param balloon
     * @return will return true if a balloon was output (either by print or email).
     */
    public boolean sendBalloon(Balloon balloon) {
        boolean sentBalloon = false;
        BalloonSettings balloonSettings = balloon.getBalloonSettings();
        try {
            log.entering(getClass().getName(), "sendBalloon", balloon.getAnswer());
            if (balloon.getContestTitle() != null) {
                contestTitle = balloon.getContestTitle();
            }
            String message = buildBalloonMessage(balloon, false);
            String answer = balloon.getAnswer();

            if (balloonSettings.isEmailBalloons()) {
                if (balloonSettings.getEmailContact() != null && balloonSettings.getEmailContact().trim().length() > 0) {
                    if (balloonSettings.getMailServer() != null && balloonSettings.getMailServer().trim().length() > 0) {
                        String subject;
                        if (answer.equalsIgnoreCase("yes")) {
                            subject = "YES " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ") color " + balloonSettings.getColor(balloon.getProblemId());
                        } else if (answer.equalsIgnoreCase("no")) {
                            // TODO need problemTitle here
                            subject = "NO " + balloon.getClientId().getName() + " (" + balloon.getClientTitle() + ")" + " problem " + balloon.getProblemId();
                        } else if (answer.equalsIgnoreCase("test")) {
                            subject = "List of Balloon Colors";
                        } else {
                            subject = "Take away balloon from " + balloon.getClientId().getName() + " color " + balloonSettings.getColor(balloon.getProblemId());
                        }
                        sendBalloonByEmail(balloonSettings, subject, message);
                    } else {
                        // TODO cleanup this message
                        log.info("emailing enabled, but no mailServer set '"+balloonSettings.getMailServer()+"'");
                    }
                } else {
                    // TODO cleanup this message
                    log.info("emailing enabled, but no emailContest set");
                }
            }

            if (balloonSettings.isPrintBalloons()) {
                if (balloonSettings.getPrintDevice() != null && balloonSettings.getPrintDevice().trim().length() > 0) {
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
                    if (sendBalloonToLocalFile(message, balloonSettings.getPrintDevice().trim())) {
                        sentBalloon = true;
                    }
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
            log.exiting(getClass().getName(), "sendBalloon()", sentBalloon);
        }
        return sentBalloon;
    }

    private boolean sendBalloonByEmail(BalloonSettings balloonSettings, String subject, String message) {
        boolean success = false;
        try {
            String mailTo = balloonSettings.getEmailContact();
            log.entering(getClass().getName(), "sendBalloonByEmail() " + mailTo);
            Properties props = balloonSettings.getMailProperties();

            String host = (String) props.get(BalloonSettings.MAIL_HOST);
            String user = (String) props.get(BalloonSettings.MAIL_USER);
            String passwd = (String) props.get(BalloonSettings.MAIL_PASSWORD);
            String method = (String) props.get(BalloonSettings.MAIL_PROTOCOL);
            String from = (String) props.get(BalloonSettings.MAIL_FROM);
            StringBuffer fromName = new StringBuffer("Balloons");
            if (contestTitle.trim().length() > 0) {
                fromName.append(" - "+contestTitle.trim());
            }
            InternetAddress fromAddress = new InternetAddress(from, fromName.toString());

            // -1 means the default port
            int port = -1;
            try {
                port = Integer.parseInt((String)props.get("mail." + method + ".port"));
            } catch (Exception e) {
                log.info("Invalid method " + method);
                log.throwing("BalloonWriter", "sendBalloonByEmail()", e);
                port = -1;
                props.put(BalloonSettings.MAIL_PROTOCOL, "");
            }
            
            Session session = Session.getInstance(props, null);
            // XXX TODO, how to handle unknown keys (ala InstallCert)
            Transport tr = session.getTransport(method);
            tr.addTransportListener(new BalloonTransportListener());
            tr.connect(host, port, user, passwd);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(fromAddress);
            msg.setRecipients(Message.RecipientType.TO, mailTo);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.addHeader("X-PC2-Version", new VersionInfo().getVersionNumber());
            msg.setText(message);
            msg.saveChanges();
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();
            success = true;
            setMessage("successful");
        } catch (Exception e) {
            log.throwing(getClass().getName(), "sendBallonBalloonByEmail()", e);
            setMessage(e.getMessage());
        } finally {
            log.exiting(getClass().getName(), "sendBalloonByEmail()", success);
        }
        return success;
    }

    private void setMessage(String message) {
        this.message = message;
    }
    
    public String getLastStatus() {
        return message;
    }

    /**
     * This functions returns an array of recipients suitable for use by rcpt to.
     * 
     * @param mailTo
     * @return array of recipients for the mail message
     */
    String[] spliRecipients(String mailTo) {
        String[] results = null;
        if (mailTo != null) {
            int comma = mailTo.indexOf(',');
            if (comma == -1) {
                results = new String[1];
                results[0] = mailTo.trim();
            } else {
                // we have at least one comma, but how many receipients?
                StringTokenizer st = new StringTokenizer(mailTo, ",");
                results = new String[st.countTokens()];
                for (int i = 0; i < results.length; i++) {
                    results[i] = st.nextToken().trim();
                }
            }
        }
        return results;
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
    
    String printAddressArray(Address[] addresses) {
        String result="";
        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                if (i > 0) {
                    result = result+",";
                }
                result = result+addresses[i].toString();
            }
        }
        return result;
    }
    
    String printSubject(Message message, String method) {
        String subject="unknown";
        if (message != null) {
            try {
                subject = message.getSubject();
            } catch (MessagingException e) {
                log.throwing("BalloonTransportListener", method, e);
            }
        }
        return subject;
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class BalloonTransportListener implements TransportListener {

        public void messageDelivered(TransportEvent arg0) {
            String subject=printSubject(arg0.getMessage(), "messageDelivered");
            String sent = printAddressArray(arg0.getValidSentAddresses());
            log.info("Balloon "+subject+" delivered to "+sent);
        }

        public void messageNotDelivered(TransportEvent arg0) {
            String subject=printSubject(arg0.getMessage(), "messageNotDelivered");
            String unSent = printAddressArray(arg0.getValidUnsentAddresses());
            String invalid = printAddressArray(arg0.getInvalidAddresses());
            logErrors(subject,unSent, invalid);
        }

        public void messagePartiallyDelivered(TransportEvent arg0) {
            String subject=printSubject(arg0.getMessage(), "messagePartiallyDelivered");
            String sent = printAddressArray(arg0.getValidSentAddresses());
            String unSent = printAddressArray(arg0.getValidUnsentAddresses());
            String invalid = printAddressArray(arg0.getInvalidAddresses());
            if (sent.length() > 0) {
                log.info("Balloon "+subject+" partially delivered to "+sent);
            }
            logErrors(subject,unSent, invalid);
        }

        private void logErrors(String subject, String unSent, String invalid) {
            if (unSent.length() > 0) {
                log.warning("Balloon "+subject+" trouble with "+invalid+" also NOT delivered to "+unSent);
            } else {
                // invalid only
                log.warning("Balloon "+subject+" trouble with "+invalid);
            }

        }
    }
}
