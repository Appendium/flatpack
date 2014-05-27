package net.sf.flatpack.utilities;

/**
 * Static utility methods for Junit tests
 *
 * @author Paul Zepernick
 */
public class UnitTestUtils {

    /**
     * Builds a delimited qualified string containing the elements passed in
     *
     * @param elements
     *            Elements To Include In Delimited String
     * @param delimiter
     * @param qualifier
     * @return String
     */
    public static String buildDelimString(final String[] elements, final char delimiter, final char qualifier) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            if (qualifier != 0) {
                sb.append(qualifier);
            }
            sb.append(elements[i]);
            if (qualifier != 0) {
                sb.append(qualifier);
            }
        }
        return sb.toString();
    }
}
