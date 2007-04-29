package edu.csus.ecs.pc2.core.archive;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IModel;
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

    private String dirname = "packets";

    @SuppressWarnings("unused")
    private IModel model;

    @SuppressWarnings("unused")
    private IController controller;
    
    private String lastArchiveFilename;

    /**
     * Default constructor.
     */
    public PacketArchiver() {
        Utilities.insureDir(dirname);
    }

    /**
     * 
     * @param dirname
     */
    public PacketArchiver(String dirname) {
        this.dirname = dirname;
        Utilities.insureDir(dirname);
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
        lastArchiveFilename = dirname + File.separator + "packet" + serialNumber + "." + nextPacketNumber + ".packet";
        boolean wasWritten = savePacket(lastArchiveFilename, packet);
        nextPacketNumber ++;
        System.err.println("debug writeNextPacket wrote to  "+lastArchiveFilename);
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
     * Get the dirname where the packets will be written.
     * @return the dir name where packet are written.
     */
    protected String getDirname() {
        return dirname;
    }

    /**
     * @param dirname
     *            directory for writeNextPacket to use
     */
    protected void setDirname(String dirname) {
        this.dirname = dirname;
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Packet Archiver";
    }

}
