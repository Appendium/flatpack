package net.sf.flatpack.brparse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.Parser;
import net.sf.flatpack.structure.Row;

/**
 * Test misc methods in the BuffReaderFixedParser class
 *
 * @author Tim Zimmerman
 */
class BuffReaderDelimParserTest {

    private static final char DELIMTER = ',';
    private static final char QUALIFIER = '\"';
    private static final String PZ_MAP_XML_STRING = "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE PZMAP SYSTEM	\"flatpack.dtd\" >\n" + "<PZMAP>\n"
            + "	<COLUMN name=\"FIELD_ONE\"/>\n" + "	<COLUMN name=\"FIELD_TWO\"/>\n" + "	<COLUMN name=\"FIELD_THREE\"/>\n"
            + "	<COLUMN name=\"FIELD_FOUR\"/>\n" + "</PZMAP> ";
    private static final String EXACT_LINE_STRING = "exactly,four,values,here";
    private static final String SHORT_LINE_STRING = "shorter,than,four";
    private static final String LONG_LINE_STRING = "longer,than,four,fields,fifth";

    private String parseRawData(final String pzMapXML, final String dataString) {
        String rawData = null;
        final StringReader pzReader = new StringReader(pzMapXML);
        final StringReader lineReader = new StringReader(dataString);
        try {
            final Parser parser = BuffReaderParseFactory.getInstance().newDelimitedParser(pzReader, lineReader, DELIMTER, QUALIFIER, false);
            assertTrue(parser instanceof BuffReaderDelimParser, "Parser is not an instance of " + BuffReaderDelimParser.class);

            final BuffReaderDelimParser delimParser = (BuffReaderDelimParser) parser;
            delimParser.setIgnoreExtraColumns(true);
            delimParser.setHandlingShortLines(true);
            delimParser.setStoreRawDataToDataSet(true);

            final DefaultDataSet dataset = (DefaultDataSet) delimParser.parse();
            final Row parsedRow = delimParser.buildRow(dataset);
            rawData = parsedRow.getRawData();
        } finally {
            pzReader.close();
            lineReader.close();
        }
        return rawData;
    }

    @Test
    void testBuildExactRow() {
        final String rawData = this.parseRawData(PZ_MAP_XML_STRING, EXACT_LINE_STRING);
        assertEquals(EXACT_LINE_STRING, rawData, "The raw data does not match the orginal line");
    }

    @Test
    void testBuildShortRow() {
        final String rawData = this.parseRawData(PZ_MAP_XML_STRING, SHORT_LINE_STRING);
        assertEquals(SHORT_LINE_STRING, rawData, "The raw data does not match the orginal line");
    }

    @Test
    void testBuildLongRow() {
        final String rawData = this.parseRawData(PZ_MAP_XML_STRING, LONG_LINE_STRING);
        assertEquals(LONG_LINE_STRING, rawData, "The raw data does not match the orginal line");
    }
}
