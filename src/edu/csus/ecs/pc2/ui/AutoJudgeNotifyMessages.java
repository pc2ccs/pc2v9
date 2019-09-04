// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

/**
 * Auto Judge messages. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface AutoJudgeNotifyMessages extends UIPlugin{

    void updateStatusLabel(String string);

    void updateMessage(String string);

}
