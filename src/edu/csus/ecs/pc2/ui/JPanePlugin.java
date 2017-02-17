package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.NoteList;
import edu.csus.ecs.pc2.core.NoteMessage;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Base class for UIPlugin panes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class JPanePlugin extends JPanel implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3600350449535614012L;
    
    private DevelopmentFrame developmentFrame = null;
    
    /**
     * Is this logged in as a server module? 
     * 
     * @return true if this module is a server, false otherwise
     */
    public boolean isServer() {
        return getContest().getClientId() != null && isServer(getContest().getClientId());
    }
    

    /**
     * Is the client a server ?
     * @param clientId
     */
    private boolean isServer(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SERVER);
    }

    private IInternalController controller;

    private IInternalContest contest;
    
    private JFrame parentFrame = null;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() > 1 && e.isControlDown()) {
                    showDevelopmentFrame();
                }
            }
        });
    }
    
    protected void showDevelopmentFrame() {
        
        if (isValidPassword()){

            StaticLog.info("Dev password matches, frame displayed");
            
            if (developmentFrame == null) {
                developmentFrame = new DevelopmentFrame();
                developmentFrame.setContestAndController(getContest(), getController());
                FrameUtilities.centerFrame(developmentFrame);

            }
            if (developmentFrame != null) {
                developmentFrame.setVisible(true);
            }
        } else {
            
            StaticLog.info("Dev password did not match");
        }
    }


    /**
     * Prompt for password in GUI.
     * @return true if matches override.
     */
    private boolean isValidPassword() {

        boolean validPassword = false;

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter password:");
        JPasswordField passwordField = new JPasswordField(32);
        panel.add(label);
        panel.add(passwordField);

        String[] options = new String[] { "OK", "Cancel" };
        int option = JOptionPane.showOptionDialog(this, panel, "Authorization Required", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (option == JOptionPane.YES_OPTION) {
            char[] passchars = passwordField.getPassword();
            String password = new String(passchars);
            validPassword = InternalController.matchOverride(password);
        }

        label = null;
        panel = null;

        return validPassword;
    }

    public abstract String getPluginTitle();

    public IInternalController getController() {
        return controller;
    }

    public void setController(IInternalController controller) {
        this.controller = controller;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
    }


    public JFrame getParentFrame() {
        return parentFrame;
    }


    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * Get filename based on the report title.
     *  
     * @param selectedReport
     * @return
     */

    /**
     * Create and view via GUI the report file.
     * 
     * @param report the IReport to be viewed
     * @param filter a Filter to be applied to the Report
     * @param log the Log to be used for logging 
     * @throws IOException if the Report Directory path cannot be found
     */
    public void createAndViewReportFile(IReport report, Filter filter, Log log) throws IOException {

        ReportPane reportPane = new ReportPane();
        reportPane.setContestAndController(contest, controller);

        String filename = reportPane.getFileName(report, "txt");
        
        File reportDirectoryFile = new File(reportPane.getReportDirectory());
        if (reportDirectoryFile.exists()) {
            if (reportDirectoryFile.isDirectory()) {
                filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
            }
        } else {
            if (reportDirectoryFile.mkdirs()) {
                filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
            }
        }

        reportPane.createReportFile(report, false, filename, filter);

        String title = report.getReportTitle();

        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }
    
    public void logException(String message, Exception ex){
        controller.getLog().log(Log.WARNING, message,ex);
        if (Utilities.isDebugMode()) {
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Output notelist to log.
     * 
     * @param noteList the Notelist to be output to the log
     */
    public void logNoteList(NoteList noteList) {

        for (NoteMessage noteMessage : noteList.getAll()) {

            String message = noteMessage.getComment();
            String prefix = "";

            String filename = noteMessage.getFilename();
            if (filename != null && filename.equals(NoteList.NO_FILENAME)) {
                prefix = filename + " " + noteMessage.getLineNumber();
                if (noteMessage.getColumnNumber() != 0) {
                    prefix += " col " + noteMessage.getColumnNumber();
                }
                message = prefix + " " + message;
            }

            switch (noteMessage.getType()) {
                case ERROR:
                    controller.getLog().log(Log.SEVERE, message);
                    break;
                case WARNING:
                    controller.getLog().log(Log.WARNING, message);
                    break;
                default:
                    controller.getLog().log(Log.INFO, message);
                    break;
            }
        }
    }
    
    /**
     * Load permission list.
     * 
     * @see #isAllowed(edu.csus.ecs.pc2.core.security.Permission.Type)
     */
    public void initializePermissions() {
        
        // TODO 6361 remove this method  everywhere.
        
    }

    public boolean isAllowed(Permission.Type type) {
        return getContest().isAllowed(type);
    }
        
    public Log getLog(){
        return getController().getLog();
    }
    
    /**
     * Show message to user.
     * 
     * @param component the parent component for the JOptionPane used to display the message
     * @param title the title for the JOptionPane dialog
     * @param message the message to be displayed in the JOptionPane message dialog
     */
    public void showMessage(Component component, String title, String message) {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show message to user.
     * 
     * @param message
     */
    // TODO cleanup - uncomment and use this for all messages
//    private void showMessage(String message) {
//        JOptionPane.showMessageDialog(null, message, "Information Message", JOptionPane.INFORMATION_MESSAGE);
//    }

    /**
     * Show message to user.
     * 
     * @param frame parent frame
     * @param strTitle title
     * @param message message
     */
    public void showMessage(JFrame frame, String strTitle, String message) {
        final JDialog dialog = new JDialog(frame, strTitle, true);
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
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
        FrameUtilities.centerFrameOver(parentFrame, dialog);
        dialog.setVisible(true);
    }

}
