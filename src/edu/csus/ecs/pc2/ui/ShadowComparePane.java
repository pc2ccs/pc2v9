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
    
    public ShadowComparePane(Map<String,ShadowJudgementPair> map) {
        
        //temporary hack to display some data
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Judgements");
        this.add(header);
        
        if (map != null && !map.keySet().isEmpty()) {
            
            String[] columnNames = { "Submission ID", "PC2", "Remote CCS" };
            Object[][] data = new Object[map.size()][3];
            int row = 0;
            for (String key : map.keySet()) {
                data[row][0] = key;
                data[row][1] = map.get(key).getPc2Judgement();
                data[row][2] = map.get(key).getRemoteCCSJudgement();
                row++;
            }
            JTable results = new JTable(data, columnNames);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            results.setDefaultRenderer(String.class, centerRenderer);

            this.add(new JScrollPane(results, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

//            for (String submission : map.keySet()) {
//                ShadowJudgementPair pair = map.get(submission);
//                String pc2Judgement = pair.getPc2Judgement();
//                String remoteJudgement = pair.getRemoteCCSJudgement();
//                JLabel label = new JLabel("Submission: " + submission + "   PC2: " + pc2Judgement + "   Remote: " + remoteJudgement);
//                this.add(label);
//            }
        } else {
            JLabel label = new JLabel("Comparison map is null or empty");
            this.add(label);
        }
        
    }

}
