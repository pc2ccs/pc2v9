package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
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
}
