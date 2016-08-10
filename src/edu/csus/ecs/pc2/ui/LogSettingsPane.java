package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.ClipboardUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.LogLevels;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Log settings pane.
 * 
 *  Features like: changing logging level, adding Console logger, 
 *  opening log or copying log path into clipboard.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
// TOOD code a number of methods in LogSettingsPane 
public class LogSettingsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 7633765001374305766L;
    
    private ConsoleHandler consoleHandler = null;
    
    private JCheckBox chckbxAddConsoleLogger = new JCheckBox("Add Console logger");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public LogSettingsPane() {
        super();
        setLayout(new BorderLayout(0, 0));
        
        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        centerPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JPanel logLevelPane = new JPanel();
        centerPane.add(logLevelPane);
        
        JLabel logLevelLabel = new JLabel("Log Level");
        logLevelPane.add(logLevelLabel);
        
        JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(LogLevels.values()));
        comboBox.setSelectedIndex(3);
        logLevelPane.add(comboBox);
        
        
        JButton btnNewButton = new JButton("Set");
        btnNewButton.setToolTipText("Change Logging level");
        logLevelPane.add(btnNewButton);
        
        JPanel addConsole = new JPanel();
        centerPane.add(addConsole);
        
        
        chckbxAddConsoleLogger.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                toggleConsoleLogger();
            }
        });
        addConsole.add(chckbxAddConsoleLogger);
        
        JPanel bottomPane = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
        flowLayout.setHgap(125);
        add(bottomPane, BorderLayout.SOUTH);
        
        JButton editLogButton = new JButton("Edit Log");
        editLogButton.setToolTipText("Edit log file ");
        bottomPane.add(editLogButton);
        
        JButton copyLognameButton = new JButton("Copy Log Name");
        copyLognameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClipboardUtilities.put(getLogName());
            }
        });
        copyLognameButton.setToolTipText("Copy log filename into clipboard");
        bottomPane.add(copyLognameButton);
    }
    
    public void setLogLevel (Level level){
        
        // TODO CODE setLogLevel
        
        System.out.println("Logfilename: " + getLogName());
        
        Handler[] handlers = getController().getLog().getHandlers();
        for (Handler handler : handlers) {
            System.out.println("Handler " + handler.toString() + " level = " + handler.getLevel());
        }
    }
    
    
    public LogLevels getLogLevel (Handler handler){
        
        // TODO CODE setLogLevel
        
        return LogLevels.ALL;
    }

    
    protected void toggleConsoleLogger() {
        

        if (consoleHandler == null && chckbxAddConsoleLogger.isSelected()) {
            consoleHandler = new ConsoleHandler();
            getController().getLog().addHandler(consoleHandler);
            System.out.println("Added console handler/loggers");

        } else if (consoleHandler != null && !chckbxAddConsoleLogger.isSelected()) {
            getController().getLog().removeHandler(consoleHandler);
            consoleHandler = null;
            System.out.println("Removed console handler/loggers");
        }
    }

    private String getLogName() {
        
        // TODO return actual log name not name with pattern, SERVER0@site1-%u.log
        return Utilities.getCurrentDirectory() + File.separator + getController().getLog().getLogfilename();
    }

    @Override
    public String getPluginTitle() {
        return "Log Settings Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
     
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
