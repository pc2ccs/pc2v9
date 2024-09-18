// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.exception.YamlLoadException;

/**
 * Utilities to help with importing contest data
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestImportUtilities {
    /**
     * Get boolean value for input key in map.
     *
     * Returns defaultVaue if no entry matches key.
     *
     * @param content
     * @param key
     * @param defaultValue
     * @return defaultValue or value from item in map.
     */
    public static boolean fetchBooleanValue(Map<String, Object> content, String key, boolean defaultValue) {
        Object object = content.get(key);
        Boolean value = false;
        if (object == null) {
            return defaultValue;
        } else if (object instanceof Boolean) {
            value = (Boolean) content.get(key);
        } else if (object instanceof String) {
            value = StringUtilities.getBooleanValue((String) content.get(key), defaultValue);
        }
        return value;
    }

    public static Object fetchObjectValue(Map<String, Object> content, String key) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        return value;
    }

    @SuppressWarnings("unused")
    private boolean fetchBooleanValue(Map<String, Object> content, String key) {
        return fetchBooleanValue(content, key, false);
    }

    /**
     * Fetch value from a map.
     *
     * @param content
     * @param key
     * @return null if content does not contain a value for the key, else the value for the key.
     */
    public static String fetchValue(Map<String, Object> content, String key) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) content.get(key);
        } else {
            return content.get(key).toString();
        }
    }

    public static String fetchValue(Map<String, Object> content, String key, String defaultValue) {
        if (content == null) {
            return null;
        }
        Object value = content.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof String) {
            return (String) content.get(key);
        } else {
            return content.get(key).toString();
        }
    }


    public static boolean isValuePresent(Map<String, Object> content, String key) {
        if (content == null) {
            return false;
        }
        Object value = content.get(key);
        return value != null;
    }

    public static String fetchValueDefault(Map<String, Object> map, String key, String defaultValue) {
        String value = fetchValue(map, key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public static Integer fetchIntValue(Map<String, Object> map, String key, int defaultValue) {
        Integer value = null;
        if (map != null) {
            value = (Integer) map.get(key);
        }
        if (value != null) {
            try {
                return value;
            } catch (Exception e) {
                syntaxError("Expecting number after " + key + ": field, found '" + value + "'");
            }
        }
        return defaultValue;
    }

    public static Long fetchLongValue(Map<String, Object> map, String key, long defaultValue) {
        Long value = null;
        if (map != null) {
            value = (Long) map.get(key);
        }
        if (value != null) {
            try {
                return value;
            } catch (Exception e) {
                syntaxError("Expecting number after " + key + ": field, found '" + value + "'");
            }
        }
        return defaultValue;
    }

    public static Integer fetchIntValue(Map<String, Object> map, String key) {
        if (map == null) {
            // SOMEDAY figure out why map would every be null
            return null;
        }
        Integer value = (Integer) map.get(key);
        if (value != null) {
            try {
                return value;
            } catch (Exception e) {
                syntaxError("Expecting number after " + key + ": field, found '" + value + "'");
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> fetchMap(Map<String, Object> content, String key) {
        Object object = content.get(key);
        if (object != null) {
            if (object instanceof Map) {
                return (Map<String, Object>) content.get(key);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String[] fetchStringList(String[] yamlLines, String key) {
        Map<String, Object> content = loadYaml(null, yamlLines);
        ArrayList list = fetchList(content, key);
        if (list == null) {
            return new String[0];
        } else {
            return (String[]) list.toArray(new String[list.size()]);
        }
    }


    public static Map<String, Object> getContent(String filename) {
        return(loadYaml(filename));
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList fetchList(Map<String, Object> content, String key) {
        return (ArrayList) content.get(key);
    }

    public static String fetchValue(File file, String key) {
        Map<String, Object> content = getContent(file.getAbsolutePath());
        return (String) content.get(key);
    }

    public static String fetchFileValue(String filename, String key) {
        return fetchValue(new File(filename), key);
    }

    public static void syntaxError(String string) {
        YamlLoadException exception = new YamlLoadException("Syntax error: " + string);
        throw exception;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYaml(String filename) {
        try {
            Yaml yaml = new Yaml();
            return (Map<String, Object>) yaml.load(new FileInputStream(filename));
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e), e, filename);
        } catch (FileNotFoundException e) {
            throw new YamlLoadException("File not found " + filename, e, filename);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYaml(String filename, String[] yamlLines) {
        try {
            Yaml yaml = new Yaml();
            String fullString = StringUtilities.join("\n", yamlLines);
            InputStream stream = new ByteArrayInputStream(fullString.getBytes(StandardCharsets.UTF_8));
            return (Map<String, Object>) yaml.load(stream);
        } catch (MarkedYAMLException e) {
            throw new YamlLoadException(getSnakeParserDetails(e), e, filename);
        }
    }

    /**
     * Create a simple string with parse info.
     *
     * @param markedYAMLException
     * @return
     */

    public static String getSnakeParserDetails(MarkedYAMLException markedYAMLException) {

        Mark mark = markedYAMLException.getProblemMark();

        int lineNumber = mark.getLine() + 1; // starts at zero
        int columnNumber = mark.getColumn() + 1; // starts at zero

        return "Parse error at line=" + lineNumber + " column=" + columnNumber + " message=" + markedYAMLException.getProblem();

    }


}
