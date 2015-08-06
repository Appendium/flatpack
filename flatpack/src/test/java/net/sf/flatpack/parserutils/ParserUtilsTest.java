package net.sf.flatpack.parserutils;

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
        final String data = "data 1-1,data 1-2,\"qualified,data 1-3,\n" +
                            "qualified data 1-3 continued from previous line\"\n";
         assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), true);
    }

    public void testNonQualifiedNonMultiLine() {
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n";
         assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), false);
    }

    public void testNonQualifiedMultiLine() {
        // can't really have multiline without qualifier
        final String data = "data 1-1,data 1-2,qualified,data 1-3\n" +
                            "qualified data 1-3 continued from previous line\n";
         assertEquals(ParserUtils.isMultiLine(data.toCharArray(), ',', '\"'), false);
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsTest.class);
    }
}
