package edu.csus.ecs.pc2.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        return (String[]) set.toArray(new String[set.size()]);
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

}
