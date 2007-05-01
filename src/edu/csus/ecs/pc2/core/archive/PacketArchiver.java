package edu.csus.ecs.pc2.core.archive;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Save and Load {@link edu.csus.ecs.pc2.core.packet.Packet}.
 * 
 * @see #writeNextPacket(Packet)
 * @see #getLastArchiveFilename()
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/archive/PacketArchiver.java$
public class PacketArchiver implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private static int nextPacketNumber = 1;

    private static long serialNumber = ((new Date()).getTime()) % 10000;

    private String outputDirectroryName = "packets";

    @SuppressWarnings("unused")
    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;
    
    private String lastArchiveFilename;

    /**
     * Default constructor.
     */
    public PacketArchiver() {
        outputDirectroryName = getPC2BaseDirectory() + File.separator + outputDirectroryName;
        Utilities.insureDir(outputDirectroryName);
    }

    /**
     * 
     * @param outputDirectroryName
     */
    public PacketArchiver(String outputDirectroryName) {
        this.outputDirectroryName = outputDirectroryName;
        Utilities.insureDir(outputDirectroryName);
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
        VersionInfo versionInfo = new VersionInfo();
        return versionInfo.locateHome();
    }

    /**
     * Write next packet file name.
     * 
     * Internal variables are incremented as packets are written.
     * 
     * @param packet
     * @return if the packet can be saved return true, otherwise false
     * @throws IOException 
     */
    public boolean writeNextPacket(Packet packet) throws IOException {
        lastArchiveFilename = outputDirectroryName + File.separator + "packet" + serialNumber + "." + nextPacketNumber + ".packet";
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
     */
    public Packet loadPacket(String filename) throws IOException, ClassNotFoundException {
        if (!Utilities.isFileThere(filename)) {
            return null;
        }

        Object object = Utilities.readObjectFromFile(filename);
        return (Packet) object;
    }

    /**
     * Save packet to disk.
     * 
     * @param filename
     * @param packet
     * @return if the file could be written to disk return true, otherwise return false
     * @throws IOException
     */
    public boolean savePacket(String filename, Packet packet) throws IOException {
        return Utilities.writeObjectToFile(filename, packet);
    }

    /**
     * Get the outputDirectroryName where the packets will be written.
     * @return the dir name where packet are written.
     */
    protected String getOutputDirectroryName() {
        return outputDirectroryName;
    }

 

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Packet Archiver";
    }

}
