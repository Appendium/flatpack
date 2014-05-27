package net.sf.flatpack.writer;

import junit.framework.TestCase;

/**
 *
 * @author Dirk Holmes and Holger Holger Hoffstatte
 */
public abstract class PZWriterTestCase extends TestCase {
    private final String lineSeparator = System.getProperty("line.separator");

    protected String joinLines(final String line1, final String line2) {
        if (line1 == null) {
            throw new IllegalArgumentException("parameter string1 may not be null");
        }

        final StringBuilder result = new StringBuilder(line1);
        result.append(lineSeparator);
        if (line2 != null) {
            result.append(line2);
            result.append(lineSeparator);
        }

        return result.toString();
    }

    protected String normalizeLineEnding(final String line) {
        return this.joinLines(line, null);
    }
}
