/**
 * 
 */
package net.sf.pzfilereader.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xhensevb
 * 
 */
public class RegExParser {

    // ///////////////////////////
    /**
     * The rather involved pattern used to match CSV's consists of three
     * alternations: the first matches aquoted field, the second unquoted, the
     * third a null field.
     */
    public static final String CSV_PATTERN = "\"(.*?)\",|(\\w+),|\"(.*?)\"|(\\w+),|,";
    public static final String ORIGINAL_CSV_PATTERN = "\"([^\"]+?)\",?|([^,]+),?|,";

    // public static final String CSV_PATTERN = "\"([^\"]+?)\",?|([^,]+),?|,";

//    private static Pattern csvRE = Pattern.compile(CSV_PATTERN);

    public static List splitLine(String line, final char delimiter, char qualifier) {
        StringBuilder patternBuilder = new StringBuilder();

        if (qualifier == 0) {
            qualifier = '\"';
        }

        String qualif = escapeIfRequired(qualifier);
        String delim = escapeIfRequired(delimiter);

        // first Pattern
        if (qualifier != 0) {
            patternBuilder.append(qualif);
        }
        patternBuilder.append("(.*?)");
        if (qualifier != 0) {
            patternBuilder.append(qualif);
        }
        patternBuilder.append(delim);

        // second Pattern
        patternBuilder.append("|(\\w+)");
        patternBuilder.append(delim);

        // Third Pattern
        patternBuilder.append("|");
        if (qualifier != 0) {
            patternBuilder.append(qualif);
        }
        patternBuilder.append("(.*?)");
        if (qualifier != 0) {
            patternBuilder.append(qualif);
        }
        patternBuilder.append("|(\\w+)");
        patternBuilder.append(delim);

        // Fourth Pattern
        patternBuilder.append("|").append(delim);

        String pat = patternBuilder.toString();

        System.out.println(pat);

        Pattern pattern = Pattern.compile(pat);

        return parse(pattern, line, String.valueOf(delimiter), String.valueOf(qualifier));
    }

    private static String escapeIfRequired(final char c) {
        if (c == 0) {
            return "";
        }
        if ("([{\\^-$|]})?*+.\"\'".indexOf(c) >= 0) {
            return "\\" + c;
        }
        return String.valueOf(c);
    }

    /**
     * Parse one line.
     * 
     * @return List of Strings, minus their double quotes
     */
    public static List parse(Pattern pattern, String line, String delimiter, String qualifier) {
        List list = new ArrayList();
        Matcher m = pattern.matcher(line);
        // For each field
        while (m.find()) {
            String match = m.group();
            if (match == null)
                break;
            if (match.endsWith(delimiter)) { // trim trailing ,
                match = match.substring(0, match.length() - 1);
            }
            if (match.startsWith(qualifier)) { // assume also ends with
                match = match.substring(1, match.length() - 1);
            }
            if (match.length() == 0)
                match = null;
            list.add(match);
        }
        return list;
    }

}
