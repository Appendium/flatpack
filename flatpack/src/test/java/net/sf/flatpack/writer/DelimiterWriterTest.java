package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Map;

import junit.framework.Assert;

/**
 *
 * @author Dirk Holmes and Holger Holger Hoffstatte
 * @author Benoit Xhenseval
 */
public class DelimiterWriterTest extends PZWriterTestCase {

    public void testCloseable() throws Exception {
        final StringWriter out = new StringWriter();

        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"')//
                .addColumnTitle("FIRSTNAME") // new fluent
                .addColumnTitle("LASTNAME") //
                .addColumnTitle("ADDRESS") //
                .addColumnTitle("CITY") //
                .addColumnTitle("STATE") //
                .addColumnTitle("REVENUE") //
                .addColumnTitle("ZIP");

        try (Writer writer = factory.createWriter(out)) {
            // write one line of data ... not in the correct order of fields
            writer.addRecordEntry("LASTNAME", "ANAME") //
                    .addRecordEntry("FIRSTNAME", "JOHN") //
                    .addRecordEntry("ZIP", "44035") //
                    .addRecordEntry("CITY", "ELYRIA") //
                    .addRecordEntry("STATE", "OH") //
                    .addRecordEntry("ADDRESS", "1234 CIRCLE CT") //
                    .addRecordEntry("REVENUE", BigDecimal.ZERO) //
                    .nextRecord() //
                    .flush();
        }

    }

    public void testWriteCsvNoMappingFile() throws Exception {
        final StringWriter out = new StringWriter();

        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"')//
                .addColumnTitle("FIRSTNAME") // new fluent
                .addColumnTitle("LASTNAME") //
                .addColumnTitle("ADDRESS") //
                .addColumnTitle("CITY") //
                .addColumnTitle("STATE") //
                .addColumnTitle("ZIP").addColumnTitle("REVENUE");

        final Writer writer = factory.createWriter(out);
        // write one line of data ... not in the correct order of fields
        writer.addRecordEntry("LASTNAME", "ANAME") //
                .addRecordEntry("FIRSTNAME", "JOHN") //
                .addRecordEntry("ZIP", "44035") //
                .addRecordEntry("CITY", "ELYRIA") //
                .addRecordEntry("STATE", "OH") //
                .addRecordEntry("ADDRESS", "1234 CIRCLE CT") //
                .addRecordEntry("REVENUE", BigDecimal.TEN) //
                .nextRecord() //
                .flush();

        // make sure the tests work on Windows and on Linux
        final String expected = this.joinLines("FIRSTNAME;LASTNAME;ADDRESS;CITY;STATE;ZIP;REVENUE", "JOHN;ANAME;1234 CIRCLE CT;ELYRIA;OH;44035;10");

        Assert.assertEquals(expected, out.toString());
    }

    public void testWritingALong() throws Exception {
        final StringWriter out = new StringWriter();

        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"')//
                .addColumnTitle("LONGNUMBER") // new fluent
        ;

        final Writer writer = factory.createWriter(out);
        // write one line of data ... not in the correct order of fields
        writer.addRecordEntry("LONGNUMBER", 123_456_789_101_123L) //
                .nextRecord() //
                .flush();

        // make sure the tests work on Windows and on Linux
        final String expected = this.joinLines("LONGNUMBER", "123456789101123");

        Assert.assertEquals(expected, out.toString());
    }

    public void testWriteCsvWithMappingFile() throws Exception {
        final InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("DelimitedWithHeader.pzmap.xml");
        final Reader mappingReader = new InputStreamReader(mapping);
        final StringWriter out = new StringWriter();

        final Writer writer = new DelimiterWriterFactory(mappingReader, ';', '"').createWriter(out);
        writer.addRecordEntry("LASTNAME", "ANAME");
        writer.addRecordEntry("FIRSTNAME", "JOHN");
        writer.addRecordEntry("ZIP", "44035");
        writer.addRecordEntry("CITY", "ELYRIA");
        writer.addRecordEntry("STATE", "OH");
        writer.addRecordEntry("ADDRESS", "1234 CIRCLE CT");
        writer.nextRecord();
        writer.flush();

        final String expected = this.joinLines("FIRSTNAME;LASTNAME;ADDRESS;CITY;STATE;ZIP", "JOHN;ANAME;1234 CIRCLE CT;ELYRIA;OH;44035");

        Assert.assertEquals(expected, out.toString());
    }

    public void testWriteCsvWithMissingColumns() throws Exception {
        final InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("DelimitedWithHeader.pzmap.xml");
        final InputStreamReader mappingReader = new InputStreamReader(mapping);
        final StringWriter out = new StringWriter();

        final Writer writer = new DelimiterWriterFactory(mappingReader, ';', '"').createWriter(out);
        // note that we do not provide values for FIRSTNAME and ADDRESS
        writer.addRecordEntry("LASTNAME", "ANAME");
        writer.addRecordEntry("ZIP", "44035");
        writer.addRecordEntry("CITY", "ELYRIA");
        writer.addRecordEntry("STATE", "OH");
        writer.nextRecord();
        writer.flush();

        final String expected = this.joinLines("FIRSTNAME;LASTNAME;ADDRESS;CITY;STATE;ZIP", ";ANAME;;ELYRIA;OH;44035");

        Assert.assertEquals(expected, out.toString());
    }

    public void testCreateWriterWithoutColumnMapping() throws Exception {
        try {
            final Writer writer = new DelimiterWriterFactory(';', '"').createWriter(new StringWriter());
            writer.addRecordEntry("ThisColumnDoesNotExist", "foo");
            Assert.fail("Writing to a DelimiterWriter without column mapping is not supported");
        } catch (final IllegalArgumentException iae) {
            // exception was expected
        }
    }

    public void testCreateWriterWithNullOutputStream() throws IOException {
        try {
            new DelimiterWriterFactory((Map) null).createWriter(null);
        } catch (final NullPointerException npe) {
            // this one was expected
        }
    }

    public void testAllowWriteWithNoMapping() throws Exception {
        final StringWriter sw = new StringWriter();

        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"');
        // final DelimiterWriter dwriter =
        // (DelimiterWriter)factory.createWriter(sw);

        factory.addColumnTitle("col1");
        factory.addColumnTitle("col2");
        factory.addColumnTitle("col3");
        factory.addColumnTitle("col4");

        final StringWriter out = new StringWriter();
        final Writer writer = factory.createWriter(out, WriterOptions.getInstance().autoPrintHeader(false));
        writer.addRecordEntry("col1", "a");
        writer.addRecordEntry("col2", "b");
        writer.addRecordEntry("col3", "c");
        writer.addRecordEntry("col4", "d");
        writer.nextRecord();
        writer.flush();

        Assert.assertTrue(out.toString().startsWith("a;b;c;d"));
    }

    public void testWriteValueWithQualifier() throws Exception {
        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"');
        factory.addColumnTitle("col1");
        factory.addColumnTitle("col2");
        factory.addColumnTitle("col3");
        factory.addColumnTitle("col4");

        final StringWriter out = new StringWriter();
        final Writer writer = factory.createWriter(out);
        writer.addRecordEntry("col1", "value;with;delimiter");
        writer.addRecordEntry("col2", "normal value");
        writer.addRecordEntry("col3", "value \"with qualifier\"");
        writer.addRecordEntry("col4", "value \"with qualifier\" and ;delimiter;");
        writer.nextRecord();
        writer.flush();

        final String expected = this.joinLines("col1;col2;col3;col4",
                "\"value;with;delimiter\";normal value;\"value \"\"with qualifier\"\"\";\"value \"\"with qualifier\"\" and ;delimiter;\"");

        Assert.assertEquals(expected, out.toString());
    }

    public void testWriteMultiLine() throws Exception {
        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"');
        factory.addColumnTitle("col1");
        factory.addColumnTitle("col2");
        factory.addColumnTitle("col3");
        final StringWriter out = new StringWriter();
        final Writer writer = factory.createWriter(out);
        writer.addRecordEntry("col1", "value");
        final String newLine = System.getProperty("line.separator");
        writer.addRecordEntry("col2", "value2" + newLine + "Hello");
        writer.addRecordEntry("col3", "value3");
        writer.nextRecord();
        writer.flush();
        final String expected = this.joinLines("col1;col2;col3", "value;\"value2" + newLine + "Hello\";value3");
        final String result = out.toString();
        Assert.assertEquals(expected, result);
    }

    public void testWriteMultiLineSuppressed() throws Exception {
        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"');
        factory.addColumnTitle("col1");
        factory.addColumnTitle("col2");
        factory.addColumnTitle("col3");
        final StringWriter out = new StringWriter();
        final Writer writer = factory.createWriter(out, WriterOptions.getInstance().setReplaceCarriageReturnWith(":"));
        writer.addRecordEntry("col1", "value");
        final String newLine = System.getProperty("line.separator");
        writer.addRecordEntry("col2", "value2" + newLine + "Hello");
        writer.addRecordEntry("col3", "value3");
        writer.nextRecord();
        writer.flush();
        final String expected = this.joinLines("col1;col2;col3", "value;value2:Hello;value3");
        final String result = out.toString();
        Assert.assertEquals(expected, result);
    }

    public void testWriteMultiLineAtTheEnd() throws Exception {
        final DelimiterWriterFactory factory = new DelimiterWriterFactory(';', '"');
        factory.addColumnTitle("col1");
        factory.addColumnTitle("col2");
        factory.addColumnTitle("col3");
        final StringWriter out = new StringWriter();
        final Writer writer = factory.createWriter(out);
        writer.addRecordEntry("col1", "value");
        final String newLine = System.getProperty("line.separator");
        writer.addRecordEntry("col2", "value2" + newLine);
        writer.addRecordEntry("col3", "value3");
        writer.nextRecord();
        writer.flush();
        final String expected = this.joinLines("col1;col2;col3", "value;\"value2" + newLine + "\";value3");
        final String result = out.toString();
        Assert.assertEquals(expected, result);
    }
}
