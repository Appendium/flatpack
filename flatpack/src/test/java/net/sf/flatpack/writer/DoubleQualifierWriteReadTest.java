package net.sf.flatpack.writer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DelimiterParser;
import net.sf.flatpack.Record;

public class DoubleQualifierWriteReadTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(DoubleQualifierWriteReadTest.class);

    public void testDelimiter() throws Exception {
        final java.io.Writer writer = new StringWriter();

        final Set<String> header = new LinkedHashSet<String>();
        header.add("Header1");
        header.add("Header2");
        header.add("Header3");

        final List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 2; i++) {
            final Map<String, String> tempMap = new HashMap<String, String>(header.size());
            for (final String it : header) {
                tempMap.put(it, it + "_" + "Data \"\" " + i + " \"");
            }
            dataList.add(tempMap);
        }
        final DelimiterWriterFactory delimiterWriterFactory = new DelimiterWriterFactory(';', '"');
        for (final String it : header) {
            delimiterWriterFactory.addColumnTitle(it);
        }
        final Writer createWriter = delimiterWriterFactory.createWriter(writer, WriterOptions.getInstance().autoPrintHeader(true));

        for (final Map<String, String> data : dataList) {
            for (final Map.Entry<String, String> entry : data.entrySet() ) {
                createWriter.addRecordEntry(entry.getKey(), entry.getValue());
            }
            createWriter.nextRecord();
        }
        createWriter.flush();
        LOG.info("Content \n{}", writer.toString());


        final ByteArrayInputStream file = new ByteArrayInputStream(writer.toString().getBytes());
        final DelimiterParser parser = new DelimiterParser(file, ';', '"', false);
        final DataSet parse = parser.parse();
        parse.next();
        final Optional<Record> record = parse.getRecord();
        assertEquals("Header2_Data \"\"\"\" 0 \"\"", record.get().getString("Header2"));
        LOG.info("Header2 \n{}", record.get().getString("Header2"));
    }

}
