package net.sf.pzfilereader.parserutils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

import junit.framework.TestCase;
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

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsTest.class);
    }
}
