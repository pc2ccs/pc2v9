// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import edu.csus.ecs.pc2.VersionInfo;
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
     * This method returns an {@link ImageIcon} for the image contained in the whose name is specified.
     * It first attempts to find the file in the current jar; if not found there it falls back to looking
     * for the file in the file system.
     * 
     * @return an ImageIcon for the file, or null if the file cannot be found in either location
     */
    public static ImageIcon loadImageIconFromFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon icon = null;
        // attempt to locate image file in jar
        URL iconURL = FrameUtilities.class.getResource("/"+inFileName);
        if (iconURL == null) {
            //we didn't find the image file in the jar; look for it in the file system
            if (imgFile.exists()) {
                try {
                    iconURL = imgFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    iconURL = null;
                    StaticLog.log("LoginFrame.loadImageIconFromFile("+inFileName+")", e);
                }
            }
        }
        if (iconURL != null) {
            //we found a URL to the image; verify that it has the correct checksum
            if (verifyImage(inFileName, iconURL)) {
                //checksums match; return an ImageIcon for the image
                icon = new ImageIcon(iconURL);
            } else {
                StaticLog.warning(inFileName+"("+iconURL.toString()+") checksum failed");
            }
        }
        return icon;
    }

    /**
     * This method verifies that the file whose filename and corresponding URL are provided are legitimate --
     * that is, that the files have the expected SHA checksum values. It first reads the file from the 
     * specified URL, then uses the {@link MessageDigest} class to compute an SHA checksum for that file.
     * It then uses the given String filename and uses that to select the "expected" checksum for the file,
     * returning true if the checksums match, false otherwise.
     * 
     * @param inFileName the name of the file to be verified
     * @param url a URL pointing to an ImageIcon for the file
     * 
     * @return true if the SHA checksum for the image at the URL matches the expected checksum; false if not
     */
    private static boolean verifyImage(String inFileName, URL url) {
        // these are the real (correct) checksums for the specified files, generated on a variety of platforms
        
        //CSUS Logo (images/csus_logo.png) checksums:
        
        //generated under Win8.1 w/ java 1.8.0_201; 
        // verified the same on Win10 w/ java 1.8.0_201 and on Ubuntu 18.04 w/ Java openjdk 11.0.4 2019-07-16
        byte[] csusChecksum = { -78, -82, -33, 125, 3, 20, 3, -51, 53, -82, -66, -19, -96, 82, 39, -92, 16, 52, 17, 127};

        // generated under Windows10 running java version "1.8.0_144" and ubuntu running "1.8.0_131":
        byte[] csusChecksum2 = { 98, 105, -19, -31, -71, -121, 109, -34, 64, 83, -78, -31, 49, -57, 57, 8, 35, -79, 13, -49};
        
        // these are the ibm jre checksums
        byte[] csusChecksum3 = {-46, -84, -66, 55, 82, -78, 124, 88, 68, -83, -128, -110, -19, -26, 92, -3, 76, -26, 21, 30};
        
        
        //ICPC banner (images/ICPCWebMast_small.png) checksums:

        // old icpc_logo.png checksums
//      byte[] icpcChecksum = {-116, -88, -24, 46, 99, 102, -94, -64, -28, -61, 51, 4, -52, -116, -23, 92, 51, -78, -90, -107};
//      byte[] icpcChecksum2 = { 70, -55, 53, -41, 127, 102, 30, 95, -55, -13, 11, -11, -31, -103, -107, -31, 119, 25, -98, 14};

        byte[] icpcChecksum3 = {41, 72, 104, 75, 73, 55, 55, 93, 32, 35, -6, -12, -96, -23, -3, -17, -119, 26, 81, -2};
        
        // this is the eclipse checksum
        byte[] icpcChecksum4 = {47, -56, 88, -115, 40, 20, 98, -6, 99, 49, -17, 37, 74, -77, 0, -74, 55, -100, 9, -118};
      
        // new 20180924 generated on win10 java9; 
        //  verified the same on Win8.1 w/ java 1.8.0_201 and on Ubuntu 18.04 w/ Java openjdk 11.0.4 2019-07-16
        byte[] icpcChecksum = {119, 107, 9, -52, 56, 121, 125, -115, -2, -40, 53, 86, 113, 4, 87, 42, 83, 118, 117, -2};
        
        // mac java8
        byte[] icpcChecksum2 = {-20, -110, 63, 117, -52, 4, -125, 31, 47, 92, 13, 97, 91, -28, -55, -28, 65, -106, 106, -24};
        
        
        //PC2 Logo (images/PC2Logo.png) checksums:
        
        //generated on Win10 w/ java 1.8.0_201; 
        // verified the same on Win8.1 w/ java 1.8.0_201 and on Ubuntu 18.04 w/ Java openjdk 11.0.4 2019-07-16
        byte[] pc2Checksum = {-58, -108, 63, 33, 72, -127, -38, 75, 78, 104, -102, 119, -128, 96, 11, -86, 100, -74, -109, 9};
        
        
        //an array to hold the checksum which is chosen from the above:
        byte[] verifyChecksum = { };
        
        try {
            //compute the checksum for the ImageIcon whose URL was passed to us
            int matchedBytes = 0;
            InputStream is = url.openStream();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            byte[] b = new byte[1024];
            while(is.read(b) > 0) {
                md.update(b);
            }
            byte[] digested = md.digest();  //"digested" now holds the image checksum
            
            //find the appropriate "verifyChecksum" for the current image file
            if (inFileName.equals("images/" + CSUS_LOGO_FILENAME)) {
                switch (digested[0]) {
                    case 98:
                        verifyChecksum = csusChecksum2;
                        break;
                    case -46:
                        verifyChecksum = csusChecksum3;
                        break;
                    default:
                        verifyChecksum = csusChecksum;
                        break;
                } 
            } else if (inFileName.equalsIgnoreCase("images/" + PC2_LOGO_FILENAME)) {
                switch (digested[0]) {
                    case -58:
                        verifyChecksum = pc2Checksum ;
                        break;
                    //TODO: add cases here for pc2checksums computed on other platforms
                    default:
                        verifyChecksum = pc2Checksum;
                        break;
                }
            } else if (inFileName.equals("images/" + ICPC_BANNER_FILENAME)){
                switch (digested[0]) {
                    case -20:
                        verifyChecksum = icpcChecksum2;
                        break;
                    case 41:
                        verifyChecksum = icpcChecksum3;
                        break;
                    case 47:
                        verifyChecksum = icpcChecksum4;
                        break;
                    default:
                        verifyChecksum = icpcChecksum;
                        break;
                } 
            } else {
                //if we get here, the file we were given doesn't match any of the expected/known files we want to check; 
                // default to the CSUS checksum, which should cause the checksum verification (below) to fail
                verifyChecksum = csusChecksum;
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
            for (int i = 0; i < digested.length; i++) {
                if (digested[i] == verifyChecksum[i]) {
                    matchedBytes++;
                } else {
                    break;
                }
            }
            
            return(matchedBytes == verifyChecksum.length);
            
        } catch (IOException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        } catch (NoSuchAlgorithmException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        }
        
        return false;
    }

}
