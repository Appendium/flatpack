/**
 * 
 */
package net.sf.pzfilereader.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xhensevb
 * 
 */
public class BXParser {
    public static List splitLine(String line, final char delimiter, char qualifier) {
        List list = new ArrayList();

        if (delimiter == 0) {
            list.add(line);
            return list;
        } else if (line == null) {
            return list;
        }

        final String trimmedLine = line.trim();
        int size = trimmedLine.length();

        if (size == 0) {
            list.add("");
            return list;
        }

        boolean insideQualifier = false;
        char previousChar = 0;
        int startBlock = 0;
        int endBlock = 0;
        boolean blockWasInQualifier = false;

        final String doubleQualifier = String.valueOf(qualifier) + String.valueOf(qualifier);
        for (int i = 0; i < size; i++) {

            final char currentChar = trimmedLine.charAt(i);

            if (currentChar != delimiter && currentChar != qualifier) {
                previousChar = currentChar;
                endBlock = i + 1;
                continue;
            }

            if (currentChar == delimiter) {
                // we've found the delimiter (eg ,)
                if (!insideQualifier) {
                    String trimmed = trimmedLine.substring(startBlock, endBlock > startBlock ? endBlock : startBlock + 1);
                    if (!blockWasInQualifier) {
                        trimmed = trimmed.trim();
                    }

                    trimmed = trimmed.replaceAll(doubleQualifier, String.valueOf(qualifier));

                    if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
                        list.add("");
                    } else {
                        list.add(trimmed);
                    }
                    blockWasInQualifier = false;
                    startBlock = i + 1;
                }
            } else if (currentChar == qualifier) {
                if (!insideQualifier && previousChar != qualifier) {
                    if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
                        insideQualifier = true;
                        startBlock = i + 1;
                    } else {
                        endBlock = i + 1;
                    }
                } else {
                    insideQualifier = false;
                    blockWasInQualifier = true;
                    endBlock = i;
                    // last column (e.g. finishes with ")
                    if (i == size - 1) {
                        list.add(trimmedLine.substring(startBlock, size - 1));
                        startBlock = i + 1;
                    }
                }
            }
            // antepenultimateChar = previousChar;
            previousChar = currentChar;
        }

        if (startBlock < size) {
            String str = trimmedLine.substring(startBlock, size);
            str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
            if (blockWasInQualifier) {
                if (str.charAt(str.length() - 1) == qualifier) {
                    list.add(str.substring(0, str.length() - 1));
                } else {
                    list.add(str);
                }
            } else {
                list.add(str.trim());
            }
        } else if (trimmedLine.charAt(size - 1) == delimiter) {
            list.add("");
        }

        return list;
    }
}
