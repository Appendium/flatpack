package net.sf.flatpack.parserutils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

/**
 * Test misc methods in the ParserUtils
 * class
 *
 * @author Paul Zepernick
 */
class ParserUtilsTest {

    @Test
    void testStripNonDouble() {
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
        assertEquals(expected, stripRes, "expecting...");
    }

    @Test
    void testStripNonLong() {
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
        assertEquals(expected, stripRes, "expecting...");
    }

    @Test
    void testPZConverter() throws IOException {
        final Properties convertProps = ParserUtils.loadConvertProperties();

        assertEquals(new Double("5.00"), ParserUtils.runPzConverter(convertProps, "$5.00C", Double.class));
        assertEquals(new Integer("5"), ParserUtils.runPzConverter(convertProps, "$5.00C", Integer.class));
        assertEquals(new BigDecimal("5.3556"), ParserUtils.runPzConverter(convertProps, "$5.3556", BigDecimal.class));
    }

    @Test
    void testEmptyRow() {
        final String data = ",,,";
        final List l = ParserUtils.splitLine(data, ',', FPConstants.NO_QUALIFIER, 4, false, false);
        assertEquals(true, ParserUtils.isListElementsEmpty(l), "list should be empty and is not...");
    }

    @Test
    void testQualifiedNonMultiLine() {
        final String data = "data 1-1,data 1-2,\"qualified,data 1-3,\"\n";
        assertEquals(false, ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'));
    }

    @Test
    void testQualifiedMultiLine() {
        final String data = "data 1-1,data 1-2,\"qualified,data 1-3,\n" + "qualified data 1-3 continued from previous line\"\n";
        assertEquals(true, ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'));
    }

    @Test
    void testNonQualifiedNonMultiLine() {
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n";
        assertEquals(false, ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'));
    }

    @Test
    void testNonQualifiedMultiLine() {
        // can't really have multiline without qualifier
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n" + "qualified data 1-3 continued from previous line\n";
        assertEquals(false, ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'));
    }

    private void testCsvSplit(final String title, final String line, final String... expected) {
        final List<String> splitLine = ParserUtils.splitLine(line, ',', '"', 5, false, false);
        System.out.println(title + " [" + line + "] ==> " + splitLine);
        assertThat(splitLine).as(title).containsExactly(expected);
    }

    private void testFancyCsvSplit(final String title, final String line, final String... expected) {
        final List<String> splitLine = ParserUtils.splitLine(line, ',', '|', 5, false, false);
        System.out.println(title + " [" + line + "] ==> " + splitLine);
        assertThat(splitLine).as(title).containsExactly(expected);
    }

    @Test
    void testCsvSplit() {
        testCsvSplit("Simple CSV Split", "col1,col2,col3", "col1", "col2", "col3");
    }

    @Test
    void testCsvSplitWithDelimiter() {
        testCsvSplit("Simple CSV Split with Delimiter", "col1,\"col2\",col3", "col1", "col2", "col3");
    }

    @Test
    void testCsvSplitWithDelimiterOnAll() {
        testCsvSplit("Simple CSV Split with Delimiter on All", "\"col1\",\"col2\",\"col3\"", "col1", "col2", "col3");
    }

    @Test
    void testCsvSplitWithDelimiterInsideOnFirstSingleCol() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on 1st Colt", "\"col\"\"1\"", "col\"1");
    }

    @Test
    void testCsvSplitWithMultiDelimiterInsideOnFirstSingleCol() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside on 1st Colt", "\"col\"\"\"\"1\"", "col\"\"1");
    }

    @Test
    void testCsvSplitWithDelimiterInsideOnLastColTwoCol() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on last 2Col", "\"col1\",\"col\"\"2\"", "col1", "col\"2");
    }

    @Test
    void testCsvSplitWithMultiDelimiterInsideOnLastColTwoCol() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside on last 2Col", "\"col1\",\"col\"\"\"\"2\"", "col1", "col\"\"2");
    }

    @Test
    void testCsvSplitWithDelimiterInsideOnLastCol3Col() {
        testCsvSplit("Simple CSV Split with Delimiter Inside on last 3 Col", "\"col1\",\"col2\",\"col\"\"3\"", "col1", "col2", "col\"3");
    }

    @Test
    void testCsvSplitWithDelimiterInsideOnFirst() {
        testCsvSplit("Simple CSV Split with Delimiter Inside 1st of 3 col", "\"col\"\"1\",\"col2\",\"col3\"", "col\"1", "col2", "col3");
    }

    @Test
    void testCsvSplitWithDelimiterInside() {
        testCsvSplit("Simple CSV Split with Delimiter Inside 2nd of 3 Col", "\"col1\",\"col\"\"2\",\"col3\"", "col1", "col\"2", "col3");
    }

    @Test
    void testCsvSplitWithMultiDelimiterInside() {
        testCsvSplit("Simple CSV Split with Multi Delimiter Inside 2nd of 3 Col", "\"col1\",\"col\"\"\"\"2\",\"col3\"", "col1", "col\"\"2", "col3");
    }

    @Test
    void testDodgyCharacter() {
        testCsvSplit("Simple CSV Split Dodgy character in middle", "col1,\uFEFFcol2,col3", "col1", "col2", "col3");
    }

    @Test
    void testStartDodgyCharacter() {
        testCsvSplit("Simple CSV Split Starting with Dodgy character", "\uFEFFcol1,col2,col3", "col1", "col2", "col3");
    }

    @Test
    void testDodgyCharacterInQualifier() {
        testCsvSplit("Simple CSV Split with Dodgy character in qualifier", "\"\uFEFFcol1\",col2,col3", "col1", "col2", "col3");
    }

    @Test
    void testFancyQualifierCsvSplit() {
        testFancyCsvSplit("Simple Fancy Qualifier Split", "col1,col2,col3", "col1", "col2", "col3");
    }

    @Test
    void testFancyQualifierSplitWithDelimiter() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter", "col1,|col2|,col3", "col1", "col2", "col3");
    }

    @Test
    void testFancyQualifierSplitWithDelimiterOnAll() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter on All", "|col1|,|col2|,|col3|", "col1", "col2", "col3");
    }

    @Test
    void testFancyQualifierCsvSplitWithDelimiterInsideOnFirstSingleCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on 1st Colt", "|col||1|", "col|1");
    }

    @Test
    void testFancyQualifierCsvSplitWithMultiDelimiterInsideOnFirstSingleCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside on 1st Colt", "|col||||1|", "col||1");
    }

    @Test
    void testFancyQualifierCsvSplitWithDelimiterInsideOnLastColTwoCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on last 2Col", "|col1|,|col||2|", "col1", "col|2");
    }

    @Test
    void testFancyQualifierCsvSplitWithMultiDelimiterInsideOnLastColTwoCol() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside on last 2Col", "|col1|,|col||||2|", "col1", "col||2");
    }

    @Test
    void testFancyQualifierCsvSplitWithDelimiterInsideOnLastCol3Col() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside on last 3 Col", "|col1|,|col2|,|col||3|", "col1", "col2", "col|3");
    }

    @Test
    void testFancyQualifierCsvSplitWithDelimiterInsideOnFirst() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside 1st of 3 col", "|col||1|,|col2|,|col3|", "col|1", "col2", "col3");
    }

    @Test
    void testFancyQualifierCsvSplitWithDelimiterInside() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Delimiter Inside 2nd of 3 Col", "|col1|,|col||2|,|col3|", "col1", "col|2", "col3");
    }

    @Test
    void testFancyQualifierCsvSplitWithMultiDelimiterInside() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside 2nd of 3 Col", "|col1|,|col||||2|,|col3|", "col1", "col||2",
                "col3");
    }

    @Test
    void testFancyQualifierCsvSplitWithMultiDelimiterInsideLast() {
        testFancyCsvSplit("Simple Fancy Qualifier CSV Split with Multi Delimiter Inside 2nd of 3 Col", "|col1|,|col2|,|col3|||", "col1", "col2",
                "col3|");
    }
}
