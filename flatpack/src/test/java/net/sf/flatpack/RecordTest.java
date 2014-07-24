package net.sf.flatpack;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import junit.framework.TestCase;
import net.sf.flatpack.util.FPConstants;

/**
 * Test methods in the DataSet
 *
 * @author Paul Zepernick
 */
public class RecordTest extends TestCase {

    public void testContains() {
        final String cols = "stringCol,doubleCol,dateCol,bigDecimalCol,intCol\r\n"//
                + "hello,2.20,20140523,123.45,6\r\n"//
                + ",,,,"//
        ;
        final Parser p = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader(cols), ',', FPConstants.NO_QUALIFIER);
        final StreamingDataSet ds = p.parseAsStream();
        ds.next();
        final Optional<Record> record1 = ds.getRecord();

        // test record 1 with Data in file!
        assertEquals("rec1 string", "hello", record1.get().getString("stringCol"));
        assertTrue("rec1 doubleCol", Double.compare(2.2, record1.get().getDouble("doubleCol")) == 0);
        try {
            assertEquals("rec1 dateCol", new Date(114, Calendar.MAY, 23), record1.get().getDate("dateCol"));
        } catch (final ParseException e) {
            fail();
        }
        assertEquals("rec1 intCol", 6, record1.get().getInt("intCol"));
        assertEquals("rec1 bigDecimalCol", new BigDecimal("123.45"), record1.get().getBigDecimal("bigDecimalCol"));

        // NOW RECORD 2 with ALL defaults
        ds.next();
        final Optional<Record> record2 = ds.getRecord();
        assertEquals("rec2 string", "Hi", record2.get().getString("stringCol", () -> "Hi"));
        assertTrue("rec2 doubleCol", Double.compare(3.76, record2.get().getDouble("doubleCol", () -> 3.76d)) == 0);
        try {
            assertEquals("rec2 dateCol", new Date(114, Calendar.JUNE, 11), record2.get().getDate("dateCol", () -> new Date(114, Calendar.JUNE, 11)));
        } catch (final ParseException e) {
            fail();
        }
        assertEquals("rec2 intCol", 8, record2.get().getInt("intCol", () -> 8));
        assertEquals("rec2 bigDecimalCol", new BigDecimal("555"), record2.get().getBigDecimal("bigDecimalCol", () -> new BigDecimal("555")));

    }

}
