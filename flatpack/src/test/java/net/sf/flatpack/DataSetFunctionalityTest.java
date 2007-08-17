package net.sf.flatpack;

import java.io.StringReader;

import net.sf.flatpack.util.FPConstants;
import junit.framework.TestCase;

/**
 * Test methods in the DataSet
 * 
 * @author Paul Zepernick
 */
public class DataSetFunctionalityTest extends TestCase {
    
    public void testContains() {
        DataSet ds;
        final String cols = "column1,column2,column3\r\n value1,value2,value3";
        Parser p = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader(cols), ',', FPConstants.NO_QUALIFIER);
        ds = p.parse();
        ds.next();
        assertEquals("column should NOT be found...", false, ds.contains("shouldnotcontain"));
           
        assertEquals("column should be found...", true, ds.contains("column1"));
        
    }
}
