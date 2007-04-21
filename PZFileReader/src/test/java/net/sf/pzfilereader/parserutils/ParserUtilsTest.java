package net.sf.pzfilereader.parserutils;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Properties;

import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.DefaultPZParserFactory;
import net.sf.pzfilereader.PZParser;
import net.sf.pzfilereader.util.PZConstants;
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
     
     public void testPZConverter() throws IOException{
         final Properties convertProps = ParserUtils.loadConvertProperties();
         
         assertEquals(ParserUtils.runPzConverter(convertProps, "$5.00C", Double.class), new Double("5.00"));
         assertEquals(ParserUtils.runPzConverter(convertProps, "$5.00C", Integer.class), new Integer("5"));
         assertEquals(ParserUtils.runPzConverter(convertProps, "$5.3556", BigDecimal.class), new BigDecimal("5.3556"));
     }
     
     public void testCaseSensitiveMetaData() {
         DataSet ds;
         final String cols = "COLUMN1,column2,Column3\r\n value1,value2,value3";
         PZParser p = DefaultPZParserFactory.getInstance().newDelimitedParser(
                 new StringReader(cols), ',', PZConstants.NO_QUALIFIER);
         
         //check that column names are case sensitive
         p.setColumnNamesCaseSensitive(true);
         ds = p.parse();
         ds.next();
         try {
             ds.getString("COLUMN2");
             fail("Column was mapped as 'column2' and lookup was 'COLUMN2'...should fail with case sensitivity turned on");
         } catch (NoSuchElementException e) {
             //this should happen since we are matching case
         }
         
         //check that column names are NOT case sensitive
         p = DefaultPZParserFactory.getInstance().newDelimitedParser(
                 new StringReader(cols), ',', PZConstants.NO_QUALIFIER);
         p.setColumnNamesCaseSensitive(false);
         ds = p.parse();
         ds.next();
         try {
             ds.getString("COLUMN2");
         } catch (NoSuchElementException e) {
             fail("Column was mapped as 'column2' and lookup was 'COLUMN2'...should NOT fail with case sensitivity turned OFF");
         }
     }
    
    
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsTest.class);
    }
}
