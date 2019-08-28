// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ClipboardUtilities;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * Show information about the pc2 version and run time environment.
 * 
 * @author Douglas A. Lane, John Clevenger, PC^2 Team, pc2@ecs.csus.edu
 */
public class AboutPane extends JPanePlugin {
    
    private VersionInfo versionInfo = new VersionInfo();
    
    public AboutPane() {
        setLayout(new BorderLayout(0, 0));
        
        
        JPanel titleBarPanel = new JPanel();
        add(titleBarPanel, BorderLayout.NORTH);
        
        JLabel lblAboutPc = new JLabel("About PC^2");
        lblAboutPc.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleBarPanel.add(lblAboutPc);
        
        JPanel centerPanel = new JPanel();
        add(centerPanel);
        centerPanel.setLayout(new BorderLayout(0, 0));
        
        //create a panel to hold the PC2 graphical logo
        JPanel logoPanel = new JPanel();
        
        //create the logo image icon from the images folder
        ImageIcon pc2LogoImageIcon = getImageIconFromFile("images/PC2Logo135x135.png");

        if (pc2LogoImageIcon != null) {
            //add the logo image to the logo panel
            JLabel logoLabel = new JLabel(pc2LogoImageIcon);
            logoPanel.add(logoLabel);
        }
        
        //add the logoPanel to the center panel
        centerPanel.add(logoPanel, BorderLayout.WEST);
        
        JTextPane informationPane = new JTextPane();
        informationPane.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(informationPane);
        centerPanel.add(scrollPane);
        
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
                "OS              : "+osName+" " + osVer+" ("+osArch+")", //
                "Java Version    : "+javaVer, //
                "", //
                "Today's Date    : "+new Date(), //
                "",
                versionInfo.getSystemVersionInfo(), // 
                "",
                "Copyright \u00A9 1989,2019 PC2 Development Team: "
                        + "John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. ", //
                "PC^2 is licensed under the Eclipse Public License Version 2.0; "
                        + "see file LICENSE.TXT for details.",
                ""
                
        };
        
        return String.join("\n", lines);
    }

    /**
     * Returns an {@link ImageIcon} built from a specified file, or null if the file cannot be found.
     * Attempts to find the file in the current jar (pc2.jar); otherwise falls back to file system.
     * Based on code copied from {@link LoginFrame}.
     * 
     * @param inFileName the name of the file containing a graphical image
     * @return an ImageIcon for the file, or null if the file can't be found
     */
    private ImageIcon getImageIconFromFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon icon = null;
        // attempt to locate in jar
        URL iconURL = getClass().getResource("/"+inFileName);
        if (iconURL == null) {
            //didn't find the file in the jar; try the file system
            if (imgFile.exists()) {
                try {
                    iconURL = imgFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    iconURL = null;
                    StaticLog.log("AboutPane.getImageIconFromFile("+inFileName+")", e);
                }
            }
        }
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
        }
        
        return icon;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -78540930688713989L;
    
    @Override
    public String getPluginTitle() {
        return "About Information Pane";
    }
    
    public static void main (String [] args) {
        
        JFrame frame = new JFrame();
        AboutPane pane = new AboutPane();
        frame.getContentPane().add(pane);
        frame.setSize(600, 400);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
}
