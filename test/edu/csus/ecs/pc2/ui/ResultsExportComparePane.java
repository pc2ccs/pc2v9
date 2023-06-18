// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.report.ResultsCompareReport;

/**
 * Results epxort and compare pane. 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsExportComparePane extends JPanePlugin {
    
    
    /**
     * 
     */
    private static final long serialVersionUID = -2726716271169661000L;
    private JTextField exportDirectoryTextField;
    private JTextField primaryCCSResultsDirectoryTtextFidld;
    
    public ResultsExportComparePane()
    {
        setBorder(new TitledBorder(null, "Export and Compare Contest Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BorderLayout(0, 0));
        
        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        
        JLabel lblNewLabel = new JLabel("Export Directory");
        centerPane.add(lblNewLabel);
        
        exportDirectoryTextField = new JTextField();
        centerPane.add(exportDirectoryTextField);
        exportDirectoryTextField.setColumns(10);
        
        JButton selectExportDirectoryButton = new JButton("...");
        selectExportDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO code select dir
                System.out.println("debug 22 TODO code select dir selectExportDirectoryButton");
            }
        });
        selectExportDirectoryButton.setToolTipText("Select export Directory");
        centerPane.add(selectExportDirectoryButton);
        
        JLabel lblNewLabel_1 = new JLabel("Result compare to directory");
        centerPane.add(lblNewLabel_1);
        
        primaryCCSResultsDirectoryTtextFidld = new JTextField();
        centerPane.add(primaryCCSResultsDirectoryTtextFidld);
        primaryCCSResultsDirectoryTtextFidld.setColumns(10);
        
        JButton selectPrimaryCCSResultsDirectoryButton = new JButton("...");
        selectPrimaryCCSResultsDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO code select dir
                System.out.println("debug 22 TODO code select dir selectPrimaryCCSResultsDirectoryButton");
            }
        });
        selectPrimaryCCSResultsDirectoryButton.setToolTipText("Select results directory to compare with");
        centerPane.add(selectPrimaryCCSResultsDirectoryButton);
        
        JButton exportResultsButton = new JButton("Export Results");
        exportResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO code select dir
                System.out.println("debug 22 TODO code select dir exportResultsButton");
                
            }
        });
        centerPane.add(exportResultsButton);
        
        JButton compartResultsButton = new JButton("Compare Result");
        compartResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                exportAndCompare();

            }
        });
        compartResultsButton.setToolTipText("Create Report to compare results");
        centerPane.add(compartResultsButton);
    }

    protected void exportAndCompare() {
        
        // TODO code export

        ResultsCompareReport report = new ResultsCompareReport();
        Utilities.viewReport(report, "Results Coparison", getContest(), getController(), true);
    }

    @Override
    public String getPluginTitle() {
        return "Results Export and Compare";
    }
}
