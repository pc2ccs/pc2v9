// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        
        JLabel lblAboutPc = new JLabel("<html>About PC<sup>2</sup>");
        lblAboutPc.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleBarPanel.add(lblAboutPc);
        
        JPanel centerPanel = new JPanel();
        add(centerPanel);
        centerPanel.setLayout(new BorderLayout(0, 0));
        
        //create a panel to hold the PC2 graphical logo
        JPanel logoPanel = new JPanel();
        
        //get the PC2 logo image icon (from either the .jar or the file system)
        ImageIcon pc2LogoImageIcon = FrameUtilities.loadAndVerifyImageFile("images/" + FrameUtilities.PC2_LOGO_FILENAME);
        
        //scale the image to properly fit in the About pane
        int width = pc2LogoImageIcon.getIconWidth();
        int height = pc2LogoImageIcon.getIconHeight();
        int targetWidth = 135;
        int targetHeight = (int) ((double)targetWidth * height / width) ;
        ImageIcon targetImageIcon = new ImageIcon(pc2LogoImageIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH));

        if (targetImageIcon != null) {
            //add the logo image to the logo panel
            JLabel logoLabel = new JLabel(targetImageIcon);
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
