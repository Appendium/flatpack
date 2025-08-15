package net.sf.flatpack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.util.FPConstants;

/**
 * Test methods in the DataSet
 *
 * @author Paul Zepernick
 */
class RecordTest {

    @Test
    void testContains() {
        final String cols = "stringCol,doubleCol,dateCol,bigDecimalCol,intCol\r\n"//
                + "hello,2.20,20140523,123.45,6\r\n"//
                + ",,,,"//
        ;
        final Parser p = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader(cols), ',', FPConstants.NO_QUALIFIER);
        final StreamingDataSet ds = p.parseAsStream();
        ds.next();
        final Optional<Record> record1 = ds.getRecord();

        // test record 1 with Data in file!
        assertEquals("hello", record1.get().getString("stringCol"), "rec1 string");
        assertTrue(Double.compare(2.2, record1.get().getDouble("doubleCol")) == 0, "rec1 doubleCol");
        try {
            assertEquals(new Date(114, Calendar.MAY, 23), record1.get().getDate("dateCol"), "rec1 dateCol");
        } catch (final ParseException e) {
            fail();
        }
        assertEquals(6, record1.get().getInt("intCol"), "rec1 intCol");
        assertEquals(new BigDecimal("123.45"), record1.get().getBigDecimal("bigDecimalCol"), "rec1 bigDecimalCol");

        // NOW RECORD 2 with ALL defaults
        ds.next();
        final Optional<Record> record2 = ds.getRecord();
        assertEquals("Hi", record2.get().getString("stringCol", () -> "Hi"), "rec2 string");
        assertTrue(Double.compare(3.76, record2.get().getDouble("doubleCol", () -> 3.76d)) == 0, "rec2 doubleCol");
        try {
            assertEquals(new Date(114, Calendar.JUNE, 11), record2.get().getDate("dateCol", () -> new Date(114, Calendar.JUNE, 11)),
                    "rec2 dateCol");
        } catch (final ParseException e) {
            fail();
        }
        assertEquals(8, record2.get().getInt("intCol", () -> 8), "rec2 intCol");
        assertEquals(new BigDecimal("555"), record2.get().getBigDecimal("bigDecimalCol", () -> new BigDecimal("555")), "rec2 bigDecimalCol");

    }

}
