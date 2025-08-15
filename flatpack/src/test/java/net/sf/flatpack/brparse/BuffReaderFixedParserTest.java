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
class BuffReaderFixedParserTest {

    private static final String PZ_MAP_XML_STRING = "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE PZMAP SYSTEM	\"flatpack.dtd\" >\n" + "<PZMAP>\n"
            + "	<COLUMN name=\"FIELD_ONE\" length=\"10\" />\n" + "	<COLUMN name=\"FIELD_TWO\" length=\"10\" />\n"
            + "	<COLUMN name=\"FIELD_THREE\" length=\"10\" />\n" + "</PZMAP> ";
    private static final String EXACT_LINE_STRING = "exactly   thirty    characters";
    private static final String SHORT_LINE_STRING = "shorter   than      thirty";
    private static final String LONG_LINE_STRING = "longer    than      thirty    characters";

    private String parseRawData(final String pzMapXML, final String dataString) {
        String rawData = null;
        final StringReader pzReader = new StringReader(pzMapXML);
        final StringReader lineReader = new StringReader(dataString);
        try {
            final Parser parser = BuffReaderParseFactory.getInstance().newFixedLengthParser(pzReader, lineReader);
            assertTrue(parser instanceof BuffReaderFixedParser, "Parser is not an instance of " + BuffReaderFixedParser.class);

            final BuffReaderFixedParser fixedWidthParser = (BuffReaderFixedParser) parser;
            fixedWidthParser.setIgnoreExtraColumns(true);
            fixedWidthParser.setHandlingShortLines(true);
            fixedWidthParser.setStoreRawDataToDataSet(true);

            final DefaultDataSet dataset = (DefaultDataSet) fixedWidthParser.parse();
            final Row parsedRow = fixedWidthParser.buildRow(dataset);
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
