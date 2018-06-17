package net.sf.flatpack.parserutils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

/**
 * Test misc methods in the ParserUtils
 * class
 *
 * @author Paul Zepernick
 */
public class ParserUtilsTest extends TestCase {

    public void testStripNonDouble() {
        checkDoubleStrip("  $10.00   ", "10.00");
        checkDoubleStrip("random chars  $10.00   more random", "10.00");
        checkDoubleStrip(" $ 1 0 . 0 0 ", "10.00");
        checkDoubleStrip("- $ 1 0 . 0 0 ", "-10.00");
        checkDoubleStrip("1a2b3c4d.01234", "1234.01234");
        checkDoubleStrip("-", "0");
        checkDoubleStrip("  -  ", "0");
        checkDoubleStrip("", "0");
    }

    private void checkDoubleStrip(final String txtToStrip, final String expected) {
        final String stripRes = ParserUtils.stripNonDoubleChars(txtToStrip);
        assertEquals("expecting...", stripRes, expected);
    }

    public void testStripNonLong() {
        checkLongStrip("  $10.00   ", "10");
        checkLongStrip("random chars  $10.00   more random", "10");
        checkLongStrip(" $ 1 0 . 0 0 ", "10");
        checkLongStrip("- $ 1 0 . 0 0 ", "-10");
        checkLongStrip("1a2b3c4d.01234", "1234");
        checkLongStrip("-", "0");
        checkLongStrip("  -  ", "0");
        checkLongStrip("", "0");
    }

    private void checkLongStrip(final String txtToStrip, final String expected) {
        final String stripRes = ParserUtils.stripNonLongChars(txtToStrip);
        assertEquals("expecting...", stripRes, expected);
    }

    public void testPZConverter() throws IOException {
        final Properties convertProps = ParserUtils.loadConvertProperties();

        assertEquals(ParserUtils.runPzConverter(convertProps, "$5.00C", Double.class), new Double("5.00"));
        assertEquals(ParserUtils.runPzConverter(convertProps, "$5.00C", Integer.class), new Integer("5"));
        assertEquals(ParserUtils.runPzConverter(convertProps, "$5.3556", BigDecimal.class), new BigDecimal("5.3556"));
    }

    public void testEmptyRow() {
        final String data = ",,,";
        final List l = ParserUtils.splitLine(data, ',', FPConstants.NO_QUALIFIER, 4, false, false);
        assertEquals("list should be empty and is not...", ParserUtils.isListElementsEmpty(l), true);
    }

    public void testQualifiedNonMultiLine() {
        final String data = "data 1-1,data 1-2,\"qualified,data 1-3,\"\n";
        assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), false);
    }

    public void testQualifiedMultiLine() {
        final String data = "data 1-1,data 1-2,\"qualified,data 1-3,\n" + "qualified data 1-3 continued from previous line\"\n";
        assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), true);
    }

    public void testNonQualifiedNonMultiLine() {
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n";
        assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), false);
    }

    public void testNonQualifiedMultiLine() {
        // can't really have multiline without qualifier
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n" + "qualified data 1-3 continued from previous line\n";
        assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), false);
    }

    private void testCsvSplit(String title, String line, String... expected) {
        final List<String> splitLine = ParserUtils.splitLine(line, ',', '"', 5, false, false);
        System.out.println(title + " [" + line + "] ==> " + splitLine);
        assertThat(splitLine).as(title).containsExactly(expected);
    }

    private void testFancyCsvSplit(String title, String line, String... expected) {
        final List<String> splitLine = ParserUtils.splitLine(line, ',', '|', 5, false, false);
        System.out.println(title + " [" + line + "] ==> " + splitLine);
        assertThat(splitLine).as(title).containsExactly(expected);
    }

    public void testCsvSplit() {
        testCsvSplit("Simple CSV Split", "col1,col2,col3", "col1", "col2", "col3");
    }

    public void testCsvSplitWithDelimiter() {
        testCsvSplit("Simple CSV Split with Delimiter", "col1,\"col2\",col3", "col1", "col2", "col3");
    }

    public void testCsvSplitWithDelimiterOnAll() {
        testCsvSplit("Simple CSV Split with Delimiter on All", "\"col1\",\"col2\",\"col3\"", "col1", "col2", "col3");
    }

    public void testCsvSplitWithDelimiterInsideOnFirstSingleCol() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on 1st Colt", "\"col\"\"1\"", "col\"1");
    }

    public void testCsvSplitWithMultiDelimiterInsideOnFirstSingleCol() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside on 1st Colt", "\"col\"\"\"\"1\"", "col\"\"1");
    }

    public void testCsvSplitWithDelimiterInsideOnLastColTwoCol() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on last 2Col", "\"col1\",\"col\"\"2\"", "col1", "col\"2");
    }

    public void testCsvSplitWithMultiDelimiterInsideOnLastColTwoCol() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside on last 2Col", "\"col1\",\"col\"\"\"\"2\"", "col1", "col\"\"2");
    }

    public void testCsvSplitWithDelimiterInsideOnLastCol3Col() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on last 3 Col", "\"col1\",\"col2\",\"col\"\"3\"", "col1", "col2", "col\"3");
    }

    public void testCsvSplitWithDelimiterInsideOnFirst() {
        testCsvSplit("Simple CSV Split with Delimiter Inside 1st of 3 col", "\"col\"\"1\",\"col2\",\"col3\"", "col\"1", "col2", "col3");
    }

    public void testCsvSplitWithDelimiterInside() {
        testCsvSplit("Simple CSV Split with Delimiter Inside 2nd of 3 Col", "\"col1\",\"col\"\"2\",\"col3\"", "col1", "col\"2", "col3");
    }

    public void testCsvSplitWithMultiDelimiterInside() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside 2nd of 3 Col", "\"col1\",\"col\"\"\"\"2\",\"col3\"", "col1", "col\"\"2", "col3");
    }

    public void testFancyQualifierCsvSplit() {
        testFancyCsvSplit("Simple Fancy Qualifier Split", "col1,col2,col3", "col1", "col2", "col3");
    }

    public void testFancyQualifierSplitWithDelimiter() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter", "col1,|col2|,col3", "col1", "col2", "col3");
    }

    public void testFancyQualifierSplitWithDelimiterOnAll() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter on All", "|col1|,|col2|,|col3|", "col1", "col2", "col3");
    }

    public void testFancyQualifierCsvSplitWithDelimiterInsideOnFirstSingleCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on 1st Colt", "|col||1|", "col|1");
    }

    public void testFancyQualifierCsvSplitWithMultiDelimiterInsideOnFirstSingleCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside on 1st Colt", "|col||||1|", "col||1");
    }

    public void testFancyQualifierCsvSplitWithDelimiterInsideOnLastColTwoCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on last 2Col", "|col1|,|col||2|", "col1", "col|2");
    }

    public void testFancyQualifierCsvSplitWithMultiDelimiterInsideOnLastColTwoCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside on last 2Col", "|col1|,|col||||2|", "col1", "col||2");
    }

    public void testFancyQualifierCsvSplitWithDelimiterInsideOnLastCol3Col() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on last 3 Col", "|col1|,|col2|,|col||3|", "col1", "col2", "col|3");
    }

    public void testFancyQualifierCsvSplitWithDelimiterInsideOnFirst() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside 1st of 3 col", "|col||1|,|col2|,|col3|", "col|1", "col2", "col3");
    }

    public void testFancyQualifierCsvSplitWithDelimiterInside() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside 2nd of 3 Col", "|col1|,|col||2|,|col3|", "col1", "col|2", "col3");
    }

    public void testFancyQualifierCsvSplitWithMultiDelimiterInside() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside 2nd of 3 Col", "|col1|,|col||||2|,|col3|", "col1", "col||2",
                "col3");
    }

    public void testFancyQualifierCsvSplitWithMultiDelimiterInsideLast() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside 2nd of 3 Col", "|col1|,|col2|,|col3|||", "col1", "col2",
                "col3|");
    }
}
