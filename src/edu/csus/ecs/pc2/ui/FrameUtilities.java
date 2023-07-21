// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Methods to center frame, change cursor, etc. <br>
 * Contains method to change look and feel, set cursor state, center windows and a yes no dialog with cancel as default.
 * 
 * @author pc2@ecs.csus
 * @version $Id$
 */

// $HeadURL$
public final class FrameUtilities {
    
    public static final String PC2_LOGO_FILENAME = "PC2Logo.png";
    public static final String ICPC_BANNER_FILENAME = "ICPCWebMast_small.png";
    public static final String CSUS_LOGO_FILENAME = "csus_logo.png";
    public static final String ICPC_LOGO_FILENAME = "icpc-logo@1.5.png";


    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum HorizontalPosition {
        LEFT, RIGHT, CENTER, NO_CHANGE
    };

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    public enum VerticalPosition {
        TOP, CENTER, BOTTOM, NO_CHANGE
    }

    private FrameUtilities() {
        // Constructor required by CheckEclipse
    }

    /**
     * Set Native Look and Feel.
     * 
     */
    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
    }

    /**
     * Set Java Look and Feel.
     * 
     */
    public static void setJavaLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
    }

    /**
     * Center this frame/component on the screen.
     * 
     * @param component
     */
    public static void centerFrame(Component component) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        component.setLocation(screenDim.width / 2 - component.getSize().width / 2, screenDim.height / 2 - component.getSize().height / 2);
    }

    /**
     * Center frame/component at top of screen.
     * 
     * @param component
     */
    public static void centerFrameTop(Component component) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        component.setLocation(screenDim.width / 2 - component.getSize().width / 2, 20);
    }

    /**
     * 
     * Center frame/component over a parentFrame.  If parentFrame is null
     * centers across screen.  If the parent is smaller, match the x and/or y.
     * 
     * @param parentFrame
     * @param component
     */
    public static void centerFrameOver(Component parentFrame, Component component) {
        if (parentFrame == null) {
            centerFrame(component);
        } else {
            Rectangle parentRect = parentFrame.getBounds();
            Rectangle myRect = component.getBounds();
            int x, y;
            if (myRect.width > parentRect.width) {
                x = parentRect.x;
            } else {
                x = (parentRect.width - myRect.width)/2 + parentRect.x;
            }
            if (myRect.height > parentRect.height) {
                y = parentRect.y;
            } else {
                y = (parentRect.height - myRect.height)/2 + parentRect.y;
            }
            component.setBounds(x, y, myRect.width, myRect.height);
        }

    }
    /**
     * Center frame at top of screen.
     * 
     * @param component
     */
    public static void setFrameWindowWidth(Component component) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        component.setSize(new Dimension(screenDim.width, component.getHeight()));
    }

    /**
     * Display mouse Busy or Wait cusor (usually hourglass)
     * 
     * @param component
     */
    public static void waitCursor(Component component) {
        component.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
    }

    /**
     * Display mouse Default cursor (usually pointer)
     * 
     * @param component
     */
    public static void regularCursor(Component component) {
        component.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Puts this frame/component to right of input frame.
     * 
     * @param sourceComponent
     *            component relative to otherComponent
     * @param otherComponent
     *            component to be to the right of the sourceComponent
     */
    public static void windowToRight(Component sourceComponent, Component otherComponent) {
        int rightX = sourceComponent.getX() + sourceComponent.getWidth();
        otherComponent.setLocation(rightX, otherComponent.getY());
    }

    /**
     * Yes No Cancel dialog, default selection is Cancel.
     * 
     * Unlike showConfirmDialog, this dialog defaults to Cancel.
     * 
     * @see JOptionPane#showConfirmDialog(java.awt.Component, java.lang.Object, java.lang.String, int, int, javax.swing.Icon)
     * @param title
     * @param message
     * @return the result
     */
    public static int yesNoCancelDialog(Component parentFrame, String message, String title) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        Object[] options = { "Yes", "No", "Cancel" };

        int result = JOptionPane.showOptionDialog(parentFrame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        return result;
    }

    /**
     * Create this lovely window with full height of screen
     */
    public static void centerFrameFullScreenHeight(Component component) {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        Dimension currDim = component.getSize();

        int newHeight = screenDim.height - 120; // save some pixels on top and bottom

        component.setSize(currDim.getSize().width, newHeight);

        component.setLocation(screenDim.width / 2 - component.getSize().width / 2, screenDim.height / 2 - component.getSize().height / 2);

    }

    public static void setFramePosition(Component component, HorizontalPosition horizontalPosition, VerticalPosition verticalPosition) {

        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        int newX = component.getX();
        int newY = component.getY();
        
        if (verticalPosition == VerticalPosition.TOP){
            newY = 20;
        } else if (verticalPosition == VerticalPosition.BOTTOM){
            newY = screenDim.height - component.getSize().height - 20;
        } else if (verticalPosition == VerticalPosition.CENTER){
            newY = screenDim.height / 2 - component.getSize().height / 2;
        }

        if (horizontalPosition == HorizontalPosition.LEFT) {
            newX = 20;
        } else if (horizontalPosition == HorizontalPosition.RIGHT) {
            newX = screenDim.width - component.getSize().width - 20;
        } else if (horizontalPosition == HorizontalPosition.CENTER) {
            newX = screenDim.width / 2 - component.getSize().width / 2;
        } // else no change

        component.setLocation(newX, newY);
    }

    public static void showMessage(JFrame parentFrame, String strTitle, String displayString) {
        final JDialog dialog = new JDialog(parentFrame, strTitle, true);
        final JOptionPane optionPane = new JOptionPane(displayString, JOptionPane.INFORMATION_MESSAGE);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();

                if (dialog.isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                    // If you were going to check something
                    // before closing the window, you'd do
                    // it here.
                    dialog.setVisible(false);
                }
            }
        });
        dialog.setContentPane(optionPane);
        dialog.pack();
        centerFrameOver(parentFrame, dialog);
        dialog.setVisible(true);
    }
    
    /**
     * Set a PC^2 Frame title.
     * 
     * Form:
     * PC^2 moduleName [clockstate] ver#-build#
     * <br>
     * Example:  PC^2 Server (Site 1) [STOPPED] 9.1.4-1908 <br>
     * PC^2 TEAM 1 (Site 1) [STARTED] 9.1.4-1908 <br>
     * 
     * @param frame
     * @param moduleName
     * @param versionInfo
     */
    public static void setFrameTitle(Frame frame, String moduleName, boolean clockStarted, VersionInfo versionInfo) {

        String clockStateString = "STOPPED";
        if (clockStarted) {
            clockStateString = "RUNNING";
        }
        String versionNumber = versionInfo.getVersionNumber();
        String[] parts = versionNumber.split(" ");
        if (parts.length == 2) {
            versionNumber = parts[0];
        }

        frame.setTitle("PC^2 " + moduleName + " [" + clockStateString + "] " + versionNumber + "-" + versionInfo.getBuildNumber());
    }

    /**
     * Show message to user.
     * @param component
     * @param title 
     * @param message
     */
    public static void showMessage(Component component, String title, String message) {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Creates a frame with the input plugin.
     * 
     * @param plugin
     * @param contest
     * @param controller
     * @return
     */
    public static JFramePlugin createPluginFrame(JPanePlugin plugin, IInternalContest contest, IInternalController controller) {

        JFramePluginImpl frame = new JFramePluginImpl(plugin);
        frame.setContestAndController(contest, controller);
        return frame;
    }
    
    public static void viewFile(String filename, String title, Log log) {
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 View File (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }
    
    public static void updateRowHeights(JTable table) {
        try {
            for (int row = 0; row < table.getRowCount(); row++) {
                int rowHeight = table.getRowHeight();

                for (int column = 0; column < table.getColumnCount(); column++) {
                    Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }

                table.setRowHeight(row, rowHeight);
            }
        } catch (ClassCastException e) {
            // ignore this exception
            System.out.println("Ignore "+e.getMessage());
        }
    }
    
    /**
     * This method returns an {@link ImageIcon} for the image contained in the file whose name is specified.
     * It first attempts to find the file as a resource in the current jars; if not found there it falls back to looking
     * for the file in the file system.
     * If the file is found (in either place) then its checksum is verified before returning a result.
     * 
     * @param the name of the image file to be loaded
     * 
     * @return an ImageIcon for the image file, or null if the file cannot be found or if it fails checksum verification
     */
    public static ImageIcon loadAndVerifyImageFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon returnIcon = null;
        // attempt to locate image file in jar
        StaticLog.info("FrameUtilities.loadAndVerifyImageFile(): searching for image file '" + inFileName + "' in jar files");
        URL iconURL = FrameUtilities.class.getResource("/"+inFileName);
        if (iconURL == null) {
            //we didn't find the image file in the jar; look for it in the file system
            StaticLog.warning("FrameUtilities.loadAndVerifyImageFile(): didn't find image file '" + inFileName + "' in jar files; trying file system");
            if (imgFile.exists()) {
                try {
                    iconURL = imgFile.toURI().toURL();
                    StaticLog.info("FrameUtilities.loadAndVerifyImageFile(): found image file '" + imgFile.getName() + "' at URL '" + iconURL + "'");
                } catch (MalformedURLException e) {
                    iconURL = null;
                    StaticLog.log("FrameUtilities.loadAndVerifyImageFile("+inFileName+")", e);
                }
            }
        } else {
            //we found the image in the jar file
            StaticLog.info("FrameUtilities.loadAndVerifyImageFile(): found image file '" + inFileName + "' at URL '" + iconURL + "'");
        }
        if (iconURL != null) {
            //we found a URL to the image; verify that it has the correct checksum
            StaticLog.info("FrameUtilities.loadAndVerifyImageFile(): found image file '" + inFileName + "'; verifying checksum");
            if (verifyImage(inFileName, iconURL)) {
                //checksums match; return an ImageIcon for the image
                returnIcon = new ImageIcon(iconURL);
            } else {
                StaticLog.warning("FrameUtilities.loadAndVerifyImageFile(): " + inFileName+"("+iconURL.toString()+") checksum failed");
            }
        }
        return returnIcon;
    }

    /**
     * This method verifies that the file whose filename and corresponding URL are provided are legitimate --
     * that is, that the files have the expected SHA checksum values. It first reads the file from the 
     * specified URL, then uses the {@link MessageDigest} class to compute an SHA checksum for that file.
     * It then uses the given String filename to select the "correct" checksum for the file,
     * returning true if the checksums match, false otherwise.
     * 
     * @param inFileName the name of the file to be verified
     * @param url a URL pointing to an ImageIcon for the file
     * 
     * @return true if the SHA checksum for the image at the URL matches the expected checksum; false if not
     */
    private static boolean verifyImage(String inFileName, URL url) {
        
        // these are the real (correct) checksums for the specified files:
        
        //csus_logo.png (SHA1 = 3E1762112204E9032C45D57D14BB299F9D9ECD42)
        byte[] csuslogoChecksum =   {62, 23, 98, 17, 34, 4, -23, 3, 44, 69, -43, 125, 20, -69, 41, -97, -99, -98, -51, 66};
        
        //PC2Logo.png: (SHA1 = C0D5C36C310EC7092A74A651311FC9D7B987A27D)
        byte[] pc2logoChecksum =    {-64, -43, -61, 108, 49, 14, -57, 9, 42, 116, -90, 81, 49, 31, -55, -41, -71, -121, -94, 125};
        
        //ICPCWebMast_small.png (SHA1 = D5047FE7093E1DB3281F53A83BC02B743A9DA7A4
        byte[] icpcbannerChecksum = {-43, 4, 127, -25, 9, 62, 29, -77, 40, 31, 83, -88, 59, -64, 43, 116, 58, -99, -89, -92};
        
        //icpc_logo.png (SHA1 = 1BFEE495B8862445370FF2CB82884FD286D63C4B)
        byte[] icpclogoChecksum =   {27, -2, -28, -107, -72, -122, 36, 69, 55, 15, -14, -53, -126, -120, 79, -46, -122, -42, 60, 75};

        try {
            //compute the checksum for the image file whose URL was passed to us
            InputStream is = url.openStream();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            
//            //old code:
//            byte[] b = new byte[1024];
//            while(is.read(b) > 0) {
//                md.update(b);     <--this produces unpredictable results depending on timing of the read; this is why the old version needed multiple "SHA checksums"
//            }
            
            //new code 27March2020 (from Tim deBoer):
            byte[] b = new byte[1024];
            int n = is.read(b);
            while(n > 0) {
                md.update(b, 0, n);   //<--this version updates the digest with exactly (and ONLY) the NEW bytes read... (thanks Tim)
                n = is.read(b);
            }
            
            byte[] digested = md.digest();  //"digested" now holds the image checksum
            
            //find the appropriate "correctChecksum" for the current image file

            byte[] correctChecksum = { -1 };  //default to a nonsensical value (must have at least one byte to avoid index-out-of-range, below)
            
            if (inFileName.equals("images/" + CSUS_LOGO_FILENAME)) {
                correctChecksum = csuslogoChecksum;
            } else if (inFileName.equalsIgnoreCase("images/" + PC2_LOGO_FILENAME)) {
                correctChecksum = pc2logoChecksum;
            } else if (inFileName.equals("images/" + ICPC_BANNER_FILENAME)){
                correctChecksum = icpcbannerChecksum;
            } else if (inFileName.equals("images/" + ICPC_LOGO_FILENAME)) {
                correctChecksum = icpclogoChecksum;
            } else {
                //if we get here, the file we were given doesn't match any of the expected/known files we want to check; 
                // use the (nonsensical) default (above) which should cause the checksum verification (below) to fail
                StaticLog.warning("FrameUtilities.verifyImage(): unrecognized image file name: '" + inFileName +"'");
            }
            
            //if in debug mode, print out the calculated checksum values for the specified image
            if (edu.csus.ecs.pc2.core.Utilities.isDebugMode()) {
                System.out.println ();
                System.out.println (inFileName);
                System.out.print ("byte[] ChecksumX = {");
                 
                for (int i = 0; i < digested.length; i++) {
                    System.out.print(digested[i]);
                    if (i < digested.length -1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("};");
            }
            
            //count the number of byte in the calculated checksum which match the expected checksum
            int matchedBytes = 0;
            for (int i = 0; i < digested.length; i++) {
                if (digested[i] == correctChecksum[i]) {
                    matchedBytes++;
                } else {
                    break;
                }
            }
            
            return(matchedBytes == correctChecksum.length);
            
        } catch (IOException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        } catch (NoSuchAlgorithmException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        }
        
        return false;
    }
    


    private static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    /**
     * create a label with question mark and on click shows a message dialog.
     * 
     * @param messageTitle dialog title
     * @param messageLines dialog message lines
     * @return @return a What's this? label.
     */
    public static JLabel getWhatsThisLabel(String messageTitle, String[] messageLines) {
        return getToolTipLabel("<What's This?>", "What's This? (click for additional information)", messageTitle,
                String.join("\n", messageLines));
    }

    /**
     * create a label with question mark and on click shows a message dialog.
     * 
     * @param messageTitle dialog title
     * @param message      dialog message
     * @return a What's this JLabel
     */
    // TODO REFACTOR use a getWhatsThisLabel where other What's up labels, search for getIcon("OptionPane.questionIcon")
    public static JLabel getWhatsThisLabel(String messageTitle, String message) {
        return getToolTipLabel("<What's This?>", "What's This? (click for additional information)", messageTitle,
                message);
    }

    /**
     * create a label with question mark and on click shows a message dialog.
     * 
     * @param buttonName
     * @param toolTip
     * @param messageTitle
     * @param messageLines
     */
    public static JLabel getToolTipLabel(String buttonName, String toolTip, String messageTitle,
            String[] messageLines) {
        return getToolTipLabel(buttonName, toolTip, messageTitle, String.join("\n", messageLines));
    }

    /**
     * create a label with question mark and on click shows a message dialog.
     * 
     * @param buttonName   name for button
     * @param toolTip      tooltip for button
     * @param messageTitle
     * @param message
     */
    public static JLabel getToolTipLabel(String buttonName, String toolTip, String messageTitle, String message) {

        JLabel button = new JLabel(buttonName);

        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
            // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon

            button.setForeground(Color.blue);
        } else {
            Image image = ((ImageIcon) questionIcon).getImage();
            button = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
        }

        button.setToolTipText(toolTip);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                JOptionPane.showMessageDialog(null, message, messageTitle, JOptionPane.INFORMATION_MESSAGE, null);
            }
        });
        button.setBorder(new EmptyBorder(0, 15, 0, 0));
        return button;
    }
    
    /**
     * return list of stack trace elements that match pattern
     * 
     */
    public static List<String> fetchStackTraceElements(Throwable e, String pattern) {

        List<String> list = new ArrayList<String>();

        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {

            String className = stackTraceElement.getClassName();

            if (className.indexOf(pattern) != -1) {
                list.add("    at " + //
                        className + "." + //
                        stackTraceElement.getMethodName() + "(" + //
                        stackTraceElement.getFileName() + ":" + //
                        stackTraceElement.getLineNumber() + ")" //
                );
            }
        }

        return list;
    }

    /**
     * Provides information about exception with only pc2 code "csus" stack trace elements.
     * @param ex
     * @param delimiter
     * @return
     */
    public static String getExceptionMessageAndStackTrace(Exception ex, String delimiter) {

        StringBuffer buff = new StringBuffer();
        
        if (ex != null) {
            buff.append("Message: " + ex.getMessage());
            buff.append(Constants.NL);
            buff.append("Class: " + ex.getClass().getName());
            buff.append(Constants.NL);
            
            List<String> stackTraceLines = fetchStackTraceElements(ex, "csus");
            for (String string : stackTraceLines) {
                buff.append(string);
                buff.append(Constants.NL);
            }
        }

        return buff.toString();
    }

    /**
     * Shows users information about exception with only pc2 code "csus" stack trace elements. 
     * @param component
     * @param message
     * @param ex
     */
    public static void showExceptionMessage(Component component, final String message, Exception ex) {
        
        // TODO CI improve this  to create a dialog that allows copying of stack trace into clipboard
        // TODO CI improve this to create a dialog that allows copying all the dialog lines into clipboard

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message + Constants.NL + getExceptionMessageAndStackTrace(ex, Constants.NL));
            }
        });
    }

}
