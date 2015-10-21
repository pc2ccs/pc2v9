package edu.csus.ecs.pc2.core.execute;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IPlugin;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.ui.IFileViewer;

/**
 * Methods to execute a run (or test run).  
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IExecutable extends IPlugin  {

    /**
     * Compile, execute and validate run - using Judge's data file(s).
     * 
     * @see #execute(boolean)
     */
    IFileViewer execute();

    /**
     * Compile, execute and validate run.
     * 
     * execute does the following:
     * <ol>
     * <li>Creates and clears execute directory (if clearDirFirst == true)
     * <li>Extracts source file(s)
     * <li>Compiles source
     * <li>If executable created, will executes program
     * <li>If not a team module, and successful execution, run validator.
     * </ol>
     * 
     * <br>
     * Will only run the validation on a run if not a {@link edu.csus.ecs.pc2.core.model.ClientType.Type#TEAM} client.
     * 
     * @param clearDirFirst -
     *            clear the directory before unpacking and executing
     * @return FileViewer with 1 or more tabs
     */
    IFileViewer execute(boolean clearDirFirst);

    /**
     * Compile, execute and validate run.
     * 
     * More parameters.
     * 
     * @see #execute(boolean)
     */
    IFileViewer execute (IInternalContest inContest, IInternalController inController, Run run, RunFiles runFiles, boolean clearDirFirst);

}
