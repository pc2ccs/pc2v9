package edu.csus.ecs.pc2.ui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ClipboardUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Show information about the pc2 version and run time environment.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class AboutPane extends JPanePlugin {
    
    // TODO Bug 1466  Add to ServerView tab
    // TODO Bug 1466  Add to TeamView tab
    // TODO Bug 1466  Add to Judge tab
    // TODO Bug 1466  Add to ScoreboardView tab
    
    // TODO Bug 1466  Add to ServicesView tab
    
    private VersionInfo versionInfo = new VersionInfo();
    
    public AboutPane() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel centerPanel = new JPanel();
        add(centerPanel);
        centerPanel.setLayout(new BorderLayout(0, 0));
        
        JTextPane informationPane = new JTextPane();
        informationPane.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(informationPane);
        centerPanel.add(scrollPane);
        
        
        JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);
        
        JLabel lblAboutPc = new JLabel("About PC^2");
        lblAboutPc.setFont(new Font("Tahoma", Font.BOLD, 16));
        panel.add(lblAboutPc);
        
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        JButton btnCopy = new JButton("Copy");
        btnCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                pasteInfoIntoClipboard();
            }
        });
        btnCopy.setToolTipText("Copy Version Infomation into Clipboard");
        buttonPanel.add(btnCopy);
        
        informationPane.setText(getVersionInfoLines());
    }

    protected void pasteInfoIntoClipboard() {
        String info = getVersionInfoLines();
        ClipboardUtilities.put(info);
    }

    private String getVersionInfoLines() {
        
        String javaVer = System.getProperty("java.version", "?");
        String osName = System.getProperty("os.name", "?");
        String osArch = System.getProperty("os.arch", "?");
        String osVer = System.getProperty("os.version", "?");
        
        String [] lines = {
                //
                "Application     : "+versionInfo.getSystemName(), //
                "Version         : "+versionInfo.getVersionNumber(), //
                "Build Date      : "+versionInfo.getVersionDate(), //;
                "Build Number    : "+versionInfo.getBuildNumber(), //
                "", //
                "os              : "+osName+" " + osVer+" ("+osArch+")", //
                "Java Version    : "+javaVer, //
                "", //
                "Date            : "+new Date(), //
                "",
                versionInfo.getSystemVersionInfo(), // 
        };
        
        return String.join("\n", lines);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -78540930688713989L;
    
    @Override
    public String getPluginTitle() {
        return "About Information Pane";
    }
}
