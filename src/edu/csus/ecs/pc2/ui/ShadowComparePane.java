// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;

/**
 * A plug-in pane which displays Shadow comparison results.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowComparePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    @Override
    public String getPluginTitle() {
        return "Shadow_Compare_Pane";
    }
    
    /**
     * Accepts a Mapping from Strings (which are submission IDs) to pairs of Judgements for that submission --
     * one judgement from the PC2 Shadow system, one taken from the CLICS event feed from the remote CCS --
     * and displays a table of those submissions and the corresponding judgements.
     * 
     * The received map is originally constructed (and passed to here) by {@link ShadowController#getJudgementComparison()).
     * 
     * @param map a Mapping of submission IDs to pairs of judgements
     */
    public ShadowComparePane(Map<String,ShadowJudgementPair> map) {
        
        //temporary hack to display some data
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Judgements");
        this.add(header);
        
        if (map != null && !map.keySet().isEmpty()) {
            
            String[] columnNames = { "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };
            Object[][] data = new Object[map.size()][4];
            int row = 0;
            for (String key : map.keySet()) {
                data[row][0] = key;
                data[row][1] = map.get(key).getPc2Judgement();
                data[row][2] = map.get(key).getRemoteCCSJudgement();
                if (data[row][1]!=null && data[row][2]!=null) {
                    data[row][3] = ((String)data[row][1]).equalsIgnoreCase((String)data[row][2]) ? "Y" : "N" ;
                } else {
                    data[row][3] = "---" ;                    
                }
                row++;
            }
            JTable results = new JTable(data, columnNames);
            results.setAutoCreateRowSorter(true);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            results.setDefaultRenderer(String.class, centerRenderer);

            this.add(new JScrollPane(results, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

        } else {
            JLabel label = new JLabel("Comparison map is null or empty");
            this.add(label);
        }
        
    }

}
