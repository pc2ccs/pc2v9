package edu.csus.ecs.pc2.ui.judge;

import edu.csus.ecs.pc2.ui.SubmitClarificationPane;

public class SubmitClarificationJudgePane extends SubmitClarificationPane {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public SubmitClarificationJudgePane() {
        super();
    }
    
    @Override
    protected void initialize() {
        this.setLayout(null);
        this.setSize(new java.awt.Dimension(456, 285));
        this.add(getProblemPane(), null);
        this.add(getsubmitAnnouncement(),null);
        this.add(getQuestionPane(), null);
        this.add(getSubmitClarificationButton(), null);
        
        int x = getQuestionPane().getBounds().x;
        int y = getQuestionPane().getBounds().y;
        int width = getQuestionPane().getBounds().width;
        int height = getQuestionPane().getBounds().height;
        getQuestionPane().setBounds(x,y + 20,width,height); 
        
        x = getSubmitClarificationButton().getLocation().x;
        y = getSubmitClarificationButton().getLocation().y;

        getSubmitClarificationButton().setLocation(x,y + 20); 
        
    }
    
}
