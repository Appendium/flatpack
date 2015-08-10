package net.sf.flatpack.brparse;

import java.io.StringReader;
import junit.framework.TestCase;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;
import net.sf.flatpack.structure.Row;

/**
 * Test misc methods in the BuffReaderFixedParser class
 *
 * @author Tim Zimmerman
 */
public class BuffReaderDelimParserTest extends TestCase {

    private static final char DELIMTER = ',';
    private static final char QUALIFER = '\"';
    private static final String PZ_MAP_XML_STRING
            = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE PZMAP SYSTEM	\"flatpack.dtd\" >\n"
            + "<PZMAP>\n"
            + "	<COLUMN name=\"FIELD_ONE\"/>\n"
            + "	<COLUMN name=\"FIELD_TWO\"/>\n"
            + "	<COLUMN name=\"FIELD_THREE\"/>\n"
            + "	<COLUMN name=\"FIELD_FOUR\"/>\n"
            + "</PZMAP> ";
    private static final String SHORT_LINE_STRING = "shorter,than,four";
    private static final String LONG_LINE_STRING = "longer,than,four,fields,fifth";

    public void testBuildShortRow() {
        StringReader pzReader = new StringReader(PZ_MAP_XML_STRING);
        StringReader lineReader = new StringReader(SHORT_LINE_STRING);
        try {

            Parser parser = BuffReaderParseFactory.getInstance().newDelimitedParser(pzReader, lineReader, DELIMTER, DELIMTER, false);
            assertTrue("Parser is not an instance of " + BuffReaderDelimParser.class, parser instanceof BuffReaderDelimParser);

            BuffReaderDelimParser delimParser = (BuffReaderDelimParser) parser;
            delimParser.setHandlingShortLines(true);
            delimParser.setStoreRawDataToDataSet(true);

            DefaultDataSet dataset = (DefaultDataSet) delimParser.parse();
            Row parsedRow = delimParser.buildRow(dataset);
            String rawData = parsedRow.getRawData();
            assertTrue("The raw data does not match the orginal line", rawData.equals(SHORT_LINE_STRING));
        } finally {
            pzReader.close();
            lineReader.close();
        }
    }

    public void testBuildLongRow() {
        StringReader pzReader = new StringReader(PZ_MAP_XML_STRING);
        StringReader lineReader = new StringReader(LONG_LINE_STRING);

        try {
            Parser parser = BuffReaderParseFactory.getInstance().newDelimitedParser(pzReader, lineReader, DELIMTER, QUALIFER, false);
            assertTrue("Parser is not an instance of " + BuffReaderDelimParser.class, parser instanceof BuffReaderDelimParser);

            BuffReaderDelimParser delimParser = (BuffReaderDelimParser) parser;
            delimParser.setIgnoreExtraColumns(true);
            delimParser.setStoreRawDataToDataSet(true);

            DefaultDataSet dataset = (DefaultDataSet) delimParser.parse();
            Row parsedRow = delimParser.buildRow(dataset);
            String rawData = parsedRow.getRawData();
            assertTrue("The raw data does not match the orginal line", rawData.equals(LONG_LINE_STRING));
        } finally {
            pzReader.close();
            lineReader.close();
        }
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(BuffReaderDelimParserTest.class);
    }
}
