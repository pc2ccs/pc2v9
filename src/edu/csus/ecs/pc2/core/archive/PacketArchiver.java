package edu.csus.ecs.pc2.core.archive;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Save and Load {@link edu.csus.ecs.pc2.core.packet.Packet}.
 * 
 * @see #writeNextPacket(Packet)
 * @see #getLastArchiveFilename()
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/archive/PacketArchiver.java$
public class PacketArchiver implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5563690515605317578L;

    public static final String SVN_ID = "$Id$";

    private static int nextPacketNumber = 1;

    private static long serialNumber = ((new Date()).getTime()) % 10000;

    private String outputDirectoryName = "packets";

    @SuppressWarnings("unused")
    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;
    
    private String lastArchiveFilename;
    
    private IStorage storage;

    /**
     * 
     * @param outputDirectroryName
     */
    public PacketArchiver(IStorage storage, String outputDirectroryName) {
        Utilities.insureDir(outputDirectroryName);
        this.storage = storage;
    }
    
    public int packetsWritten (){
        return nextPacketNumber - 1;
    }
    

    /**
     * Get the name of the last file written using {@link #writeNextPacket(Packet)}.
     */
    public String getLastArchiveFilename() {
        return lastArchiveFilename;
    }
    
    public String getPC2BaseDirectory () {
        // TODO this should be overridable via an ini entry and/or command line option
        return ".";
    }

    /**
     * Write next packet file name.
     * 
     * Internal variables are incremented as packets are written.
     * 
     * @param packet
     * @return if the packet can be saved return true, otherwise false
     * @throws IOException 
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     */
    public boolean writeNextPacket(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {
        lastArchiveFilename = outputDirectoryName + File.separator + "packet" + serialNumber + "." + nextPacketNumber + ".packet";
        boolean wasWritten = savePacket(lastArchiveFilename, packet);
        nextPacketNumber ++;
        return wasWritten;
    }

    /**
     * Get a packet from a disk file.
     * 
     * @param filename
     * @return the packet
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileSecurityException 
     */
    public Packet loadPacket(String filename) throws IOException, ClassNotFoundException, FileSecurityException {
        if (!Utilities.isFileThere(filename)) {
            return null;
        }

        Object object = storage.load(filename);
        return (Packet) object;
    }

    /**
     * Save packet to disk.
     * 
     * @param filename
     * @param packet
     * @return if the file could be written to disk return true, otherwise return false
     * @throws IOException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     */
    public boolean savePacket(String filename, Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {
        return storage.store(filename, packet);
    }

    /**
     * Get the outputDirectroryName where the packets will be written.
     * @return the dir name where packet are written.
     */
    protected String getOutputDirectoryName() {
        return outputDirectoryName;
    }

 

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Packet Archiver";
    }

}
