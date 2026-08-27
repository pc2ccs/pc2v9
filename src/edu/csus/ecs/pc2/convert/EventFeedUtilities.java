// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import edu.csus.ecs.pc2.core.exception.SubmissionRejectedException;
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
     * Reject a zip if the claimed uncompressed size of its file entries exceeds {@code maxSourceSizeBytes}.
     * Uses zip central-directory metadata ({@link ZipEntry#getSize()}); does not inflate entry contents.
     * A {@code maxSourceSizeBytes} of 0 or less means there is no source-size limit.
     *
     * @param base64Data base64-encoded zip bytes
     * @param maxSourceSizeBytes contest maximum combined source size in bytes
     * @throws SubmissionRejectedException if the claimed uncompressed size exceeds the limit
     * @throws IllegalArgumentException if the data is not valid base64, the zip is unreadable, or an entry size is unknown
     */
    public static void enforceUncompressedZipSourceSizeLimit(String base64Data, long maxSourceSizeBytes)
            throws SubmissionRejectedException {
        enforceUncompressedZipSourceSizeLimit(Base64.getDecoder().decode(base64Data), maxSourceSizeBytes);
    }

    /**
     * Reject a zip if the claimed uncompressed size of its file entries exceeds {@code maxSourceSizeBytes}.
     * Uses zip central-directory metadata ({@link ZipEntry#getSize()}); does not inflate entry contents.
     * A {@code maxSourceSizeBytes} of 0 or less means there is no source-size limit.
     *
     * @param zipBytes raw zip file bytes
     * @param maxSourceSizeBytes contest maximum combined source size in bytes
     * @throws SubmissionRejectedException if the claimed uncompressed size exceeds the limit
     * @throws IllegalArgumentException if the zip is unreadable or an entry size is unknown
     */
    public static void enforceUncompressedZipSourceSizeLimit(byte[] zipBytes, long maxSourceSizeBytes)
            throws SubmissionRejectedException {
        if (maxSourceSizeBytes <= 0) {
            return;
        }
        long sourceSize = getUncompressedZipSize(zipBytes);
        if (sourceSize > maxSourceSizeBytes) {
            String sizeMsg = "Source file(s) are too large (" + sourceSize + " bytes) - maximum is " + maxSourceSizeBytes + " bytes.";
            throw new SubmissionRejectedException(sizeMsg, SubmissionRejectedException.SubmissionRejectionReason.SOURCE_TOO_BIG);
        }
    }

    /**
     * Sum the claimed uncompressed sizes of file entries in a zip, without extracting those entries.
     * Uses {@link ZipFile} so sizes come from the central directory rather than by inflating payloads.
     *
     * @param zipBytes raw zip file bytes
     * @return total uncompressed size in bytes of non-directory entries
     * @throws IllegalArgumentException if {@code zipBytes} is null, the zip cannot be read, or any file entry has unknown size ({@link ZipEntry#getSize()} {@code < 0})
     */
    public static long getUncompressedZipSize(byte[] zipBytes) {
        if (zipBytes == null) {
            throw new IllegalArgumentException("zip data is null");
        }
        File tmp = null;
        try {
            // Create a temp file for holding the (unexpanded) zip data. Note that createTempFile 
        	// appends a unique random value to the file name so concurrent POSTs do not share a temp file.
            tmp = File.createTempFile("srcszchk", ".zip");
            Files.write(tmp.toPath(), zipBytes);
            long total = 0;
            try (ZipFile zipFile = new ZipFile(tmp)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                //check each entry in the (unexpanded) zip file
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entry.isDirectory() || entryName == null || entryName.isEmpty() || entryName.endsWith("/")) {
                        continue;
                    }
                    long size = entry.getSize();
                    if (size < 0) {
                        throw new IllegalArgumentException("zip entry uncompressed size is unknown: " + entryName);
                    }
                    total += size;
                }
            }
            return total;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (tmp != null) {
                try {
                    Files.deleteIfExists(tmp.toPath());
                } catch (Exception e) {
                    ; // best-effort immediate delete; do not wait for JVM exit
                }
            }
        }
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
     * Get files from a zipfile's bytes with no uncompressed-size cap.
     * Callers such as shadow replay that must extract whatever the remote CCS stored should use this overload.
     *
     * @param bytes bytes comprising a zip file.
     * @return list of IFiles extracted from the input bytes
     */
    public static List<IFile> getIFiles(byte[] bytes) {
        try {
            return getIFiles(bytes, 0);
        } catch (SubmissionRejectedException e) {
            // max of 0 means unlimited, so this should not occur
            throw new RuntimeException(e);
        }
    }

    /**
     * Get files from a zipfile's bytes, optionally stopping if inflated contents exceed a size limit.
     * A {@code maxUncompressedBytes} of 0 or less means there is no limit (same behavior as {@link #getIFiles(byte[])}).
     * The limit is applied to the running total of bytes actually inflated, so a zip whose headers understate
     * entry sizes cannot expand without bound in memory.
     *
     * @param bytes bytes comprising a zip file
     * @param maxUncompressedBytes maximum combined uncompressed file bytes allowed; {@code <= 0} for unlimited
     * @return list of IFiles extracted from the input bytes
     * @throws SubmissionRejectedException if inflated contents would exceed {@code maxUncompressedBytes}
     */
    public static List<IFile> getIFiles(byte[] bytes, long maxUncompressedBytes) throws SubmissionRejectedException {

        List<IFile> files = new ArrayList<IFile>();

        ZipInputStream zipStream = null;

        try {
            zipStream = new ZipInputStream(new ByteArrayInputStream(bytes));
            ZipEntry entry = null;
            long extracted = 0;
            /**
             * Read each zip entry, add IFile.
             */
            while ((entry = zipStream.getNextEntry()) != null) {

                String entryName = entry.getName();

                // Only add the file to the list if the file name is not an empty string.
                // and it's not a directory entry.
                if(!entryName.isEmpty() && !entryName.endsWith("/")) {
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[8096];
                    int bytesRead = 0;
                    while ((bytesRead = zipStream.read(buffer)) != -1)
                    {
                        if (maxUncompressedBytes > 0 && extracted + bytesRead > maxUncompressedBytes) {
                            throw new SubmissionRejectedException(
                                    "Source file(s) are too large (" + (extracted + bytesRead) + " bytes) - maximum is "
                                            + maxUncompressedBytes + " bytes.",
                                    SubmissionRejectedException.SubmissionRejectionReason.SOURCE_TOO_BIG);
                        }
                        byteOutputStream.write(buffer, 0, bytesRead);
                        extracted += bytesRead;
                    }

                    String base64Data = getBase64Data(byteOutputStream.toByteArray());
                    IFile iFile = new IFileImpl(entryName, base64Data);
                    files.add(iFile);

                    byteOutputStream.close();
                }
                zipStream.closeEntry();
            }
            zipStream.close();

        } catch (SubmissionRejectedException e) {
            if (zipStream != null){
                try {
                    zipStream.close();
                } catch (Exception ze) {
                    ; // problem closing stream, ignore.
                }
            }
            throw e;
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
