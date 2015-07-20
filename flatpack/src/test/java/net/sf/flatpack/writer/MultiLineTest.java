package net.sf.flatpack.writer;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

public class MultiLineTest extends TestCase {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String CONTENT = "this is a" + NEW_LINE + " multiline " + NEW_LINE + "....";

    private static final Logger LOG = LoggerFactory.getLogger(MultiLineTest.class);
    private Map<String, Object> mapping;

    @Override
    public void setUp() {
        mapping = new HashMap<String, Object>();
        final List<ColumnMetaData> listColumns = new ArrayList<ColumnMetaData>();
        for (final String column : new String[] { "Id", "Description" }) {
            final ColumnMetaData columnMetadata = new ColumnMetaData();
            columnMetadata.setColName(column);
            listColumns.add(columnMetadata);
        }
        mapping.put(FPConstants.DETAIL_ID, listColumns);
    }

    public void testMultipleLine() throws Exception {
        final DelimiterWriterFactory factory = new DelimiterWriterFactory(mapping, ',', '"');

        final StringWriter strWriter = new StringWriter();
        final Writer flatMapWriter = factory.createWriter(strWriter, new WriterOptions().autoPrintHeader(false));
        flatMapWriter.printHeader();
        flatMapWriter.addRecordEntry("Id", "1");
        flatMapWriter.addRecordEntry("Description", CONTENT);
        flatMapWriter.nextRecord();
        flatMapWriter.flush();
        flatMapWriter.close();
        LOG.info("CSV is \n{}", strWriter.toString());

        final String toRead = strWriter.toString();
        final DataSet ds = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader(toRead), ',', '"').parse();
        if (ds.next()) {
            LOG.info("Parsed content \n{}", ds.getString("Description"));
            assertEquals(ds.getString("Description"), CONTENT);
        }

    }

}