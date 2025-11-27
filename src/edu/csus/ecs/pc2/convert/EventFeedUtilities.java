// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IFileImpl;

/**
 * Event Feed Utilities
 *
 * @author ICPC
 *
 */
public final class EventFeedUtilities {

    public static final long MS_PER_SECOND = 1000;

    private EventFeedUtilities() {
        super();
    }

    public static String[] getAllLanguages(List<EventFeedRun> runs) {

        Map<String, String> map = new HashMap<String, String>();
        for (EventFeedRun eventFeedRun : runs) {
            map.put(eventFeedRun.getLanguage(), "");
        }
        Set<String> set = map.keySet();
        return set.toArray(new String[set.size()]);
    }

    public static int getMaxProblem(List<EventFeedRun> runs) {
        int max = 0;
        for (EventFeedRun eventFeedRun : runs) {
            int id = Integer.parseInt(eventFeedRun.getProblem());

            max = Math.max(id, max);
        }
        return max;
    }

    public static int getMaxTeam(List<EventFeedRun> runs) {

        int max = 0;
        for (EventFeedRun eventFeedRun : runs) {
            int id = Integer.parseInt(eventFeedRun.getTeam());

            max = Math.max(id, max);
        }
        return max;
    }

    /**
     * Convert decimal string to ms.
     *
     * @param decimalSeconds
     *            - declimal second
     * @return ms
     */
    public static long toMS(String decimalSeconds) {

        if (decimalSeconds != null) {
            if (decimalSeconds.indexOf('.') > -1) {
                String[] parts = decimalSeconds.split("[.]");
                long ms = Long.parseLong(parts[0]) * MS_PER_SECOND;
                String mantstr = parts[1];
                if (mantstr.length() > 3) {
                    mantstr = mantstr.substring(0, 3);
                }
                long mant = Long.parseLong(mantstr);
                if (mant > 0) {
                    int digits = parts[1].length();
                    switch (digits) {
                        case 3:
                            mant = mant * 1;
                            break;
                        case 2:
                            mant = mant * 10;
                            break;
                        case 1:
                            mant = mant * 100;
                            break;
                        default:
                            break;
                    }
                }
                return ms + mant;

            } else {
                return Long.parseLong(decimalSeconds) * MS_PER_SECOND;
            }
        }
        return 0;
    }

    /**
     * Fetch list of filenames, full path
     *
     * @param dirname
     *            location of submission files
     * @param runId
     *            run id.
     */
    public static List<String> fetchRunFileNames(String dirname, String runId) {

        ArrayList<String> list = new ArrayList<>();

        String fulldirname = dirname + File.separator + runId;
        File dir = new File(fulldirname);
        if (dir.isDirectory()) {

            String[] entries = dir.list();
            for (String name : entries) {
                String fullname = fulldirname + File.separator + name;
                if (new File(fullname).isFile()) {
                    list.add(fullname);
                }
            }
        }
        return list;
    }

    /**
     * Get files from a zipfile's base64 encoded string data
     *
     * @param base64Data String comprising a zip file encoded as base64
     * @return list of IFiles extracted from the input bytes
     * @throws IllegalArgumentException from the base64 decoder on a data error
     */
    public static List<IFile> getIFiles(String base64Data) {

        // use the decoder to both check the validity of, and to store, the byte data.
        // this will throw an IllegalArgumentException if the data is basd
        return(getIFiles(Base64.getDecoder().decode(base64Data)));
    }

    /**
     * Get files from a zipfile's bytes.
     *
     * @param bytes bytes comprising a zip file.
     * @return list of IFiles extracted from the input bytes
     */
    public static List<IFile> getIFiles(byte[] bytes) {

        List<IFile> files = new ArrayList<IFile>();

        ZipInputStream zipStream = null;

        try {
            zipStream = new ZipInputStream(new ByteArrayInputStream(bytes));
            ZipEntry entry = null;
            /**
             * Read each zip entry, add IFile.
             */
            while ((entry = zipStream.getNextEntry()) != null) {

                String entryName = entry.getName();

//                ByteOutputStream byteOutputStream = new ByteOutputStream();
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[8096];
                int bytesRead = 0;
                while ((bytesRead = zipStream.read(buffer)) != -1)
                {
                    byteOutputStream.write(buffer, 0, bytesRead);
                }

//                String base64Data = getBase64Data(byteOutputStream.getBytes());
                String base64Data = getBase64Data(byteOutputStream.toByteArray());
                IFile iFile = new IFileImpl(entryName, base64Data);
                files.add(iFile);

                byteOutputStream.close();

                zipStream.closeEntry();
            }
            zipStream.close();

        } catch (Exception e) {
            if (zipStream != null){
                try {
                    zipStream.close();
                } catch (Exception ze) {
                    ; // problem closing stream, ignore.
                }
            }
            throw new RuntimeException(e);
        }

        return files;

    }

    /**
     * Encode bytes into BASE64.
     * @param data
     * @return
     */
    public static String getBase64Data( byte [] bytes) {
        // TODO REFACTOR move to FileUtilities
        Base64.Encoder encoder = Base64.getEncoder();
        String base64String = encoder.encodeToString(bytes);
        return base64String;
    }

}
