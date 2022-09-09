// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.core.model.AvailableAJRun;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class AvailableAJRunListTest extends AbstractTestCase {

    public void testAddRemove() throws Exception {

        AvailableAJRunList list = new AvailableAJRunList();

        List<Run> runList = new ArrayList<Run>();

        Problem problemA = new Problem("A");
        Language language = new Language("APL");

        for (int i = 1; i < 22; i++) {
            ClientId submitter = new ClientId(3, Type.TEAM, i + 1);
            Run run = new Run(submitter, language, problemA);
            runList.add(run);
            AvailableAJRun ajrun = new AvailableAJRun(run.getElementId(), 3400, problemA.getElementId());
            list.add(ajrun);
        }

        assertEquals("Expecting count in list", 21, list.size());

        for (Run run : runList) {
            AvailableAJRun ajrun = new AvailableAJRun(run.getElementId(), 3400, problemA.getElementId());
            list.remove(ajrun);
        }

        assertEquals("Expecting count in list", 0, list.size());

    }

}
