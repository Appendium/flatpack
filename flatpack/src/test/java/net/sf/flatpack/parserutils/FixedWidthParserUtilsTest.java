package net.sf.flatpack.parserutils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FixedWidthParserUtils;
import net.sf.flatpack.util.ParserUtils;

/**
 * Test the functionality of a fixed width parse
 *
 * @author Paul Zepernick
 */
public class FixedWidthParserUtilsTest extends TestCase {

    /**
     * Test fixed width text
     *
     */
    public void testFixedParse() {
        check(new String[] { "test", "test", "test" }, new int[] { 5, 10, 20 }, new String[] { "test", "test", "test" }, true, false);

        check(new String[] { "test with some space", "test", "test" }, new int[] { 300, 10, 20 },
                new String[] { "test with some space", "test", "test" }, true, false);

        final String[] textWithLeadingAndTrailing = { "  test with leading and trailing    ", "  test ", "test" };
        check(textWithLeadingAndTrailing, new int[] { 36, 7, 4 }, textWithLeadingAndTrailing, true, true);
        check(textWithLeadingAndTrailing, new int[] { 36, 7, 4 }, new String[] { "  test with leading and trailing", "  test", "test" }, true, false);
        check(textWithLeadingAndTrailing, new int[] { 36, 7, 4 }, new String[] { "test with leading and trailing    ", "test ", "test" }, false,
                true);
    }

    private void check(final String[] columnData, final int[] lengths, final String[] expected, final boolean preserveLeading,
            final boolean preserveTrailing) {
        final List<ColumnMetaData> columnMetaData = new ArrayList<ColumnMetaData>();

        assertEquals("data and col lengths different size...", columnData.length, lengths.length);

        for (final int length : lengths) {
            final ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColLength(length);
            columnMetaData.add(cmd);
        }

        final StringBuilder lineToParse = new StringBuilder();
        for (int i = 0; i < columnData.length; i++) {
            // padd each column
            lineToParse.append(columnData[i]).append(ParserUtils.padding(lengths[i] - columnData[i].length(), ' '));
        }

        final List<String> splitResult = FixedWidthParserUtils.splitFixedText(columnMetaData, lineToParse.toString(), preserveLeading,
                preserveTrailing);

        // compare the parse results to the expected results
        assertEquals("did not return correct number of cols...", expected.length, splitResult.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("col values don't match...", expected[i], splitResult.get(i));
        }

    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(FixedWidthParserUtilsTest.class);
    }
}