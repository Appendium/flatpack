package net.sf.pzfilereader.parserutils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.util.FixedWidthParserUtils;
import net.sf.pzfilereader.util.ParserUtils;

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
        check(new String[] { "test", "test", "test" }, new int[] { 5, 10, 20 }, new String[] { "test", "test", "test" });

        check(new String[] { "test with some space", "test", "test" }, new int[] { 300, 10, 20 }, new String[] { "test with some space", "test",
                "test" });
    }

    private void check(final String[] columnData, final int[] lengths, final String[] expected) {
        final List columnMetaData = new ArrayList();

        assertEquals("data and col lengths different size...", columnData.length, lengths.length);

        for (int i = 0; i < lengths.length; i++) {
            final ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColLength(lengths[i]);
            columnMetaData.add(cmd);
        }

        final StringBuffer lineToParse = new StringBuffer();
        for (int i = 0; i < columnData.length; i++) {
            // padd each column
            lineToParse.append(columnData[i]).append(ParserUtils.padding(lengths[i] - columnData[i].length(), ' '));
        }

        final List splitResult = FixedWidthParserUtils.splitFixedText(columnMetaData, lineToParse.toString());

        //compare the parse results to the expected results
        assertEquals("did not return correct number of cols...", expected.length, splitResult.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("col values don't match...", expected[i], (String) splitResult.get(i));
        }

    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(FixedWidthParserUtilsTest.class);
    }
}
