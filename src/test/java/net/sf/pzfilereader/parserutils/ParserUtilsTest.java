package net.sf.pzfilereader.parserutils;

import java.util.List;

import net.sf.pzfilereader.util.ParserUtils;
import junit.framework.TestCase;
/**
 * Test misc methods in the ParserUtils
 * class
 * 
 * @author Paul Zepernick
 */
public class ParserUtilsTest extends TestCase{
    
    public void testStripNonDouble(){
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
    
    
    public void testStripNonLong(){
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
    
    
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsTest.class);
    }
}
