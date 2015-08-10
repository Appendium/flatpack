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
public class BuffReaderFixedParserTest extends TestCase {

    private static final String PZ_MAP_XML_STRING
            = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE PZMAP SYSTEM	\"flatpack.dtd\" >\n"
            + "<PZMAP>\n"
            + "	<COLUMN name=\"FIELD_ONE\" length=\"10\" />\n"
            + "	<COLUMN name=\"FIELD_TWO\" length=\"10\" />\n"
            + "	<COLUMN name=\"FIELD_THREE\" length=\"10\" />\n"
            + "</PZMAP> ";
    private static final String SHORT_LINE_STRING = "shorter   than      thirty";
    private static final String LONG_LINE_STRING = "longer    than      thirty    characters";

    public void testBuildShortRow() {
        StringReader pzReader = new StringReader(PZ_MAP_XML_STRING);
        StringReader lineReader = new StringReader(SHORT_LINE_STRING);

        Parser parser = BuffReaderParseFactory.getInstance().newFixedLengthParser(pzReader, lineReader);
        assertTrue("Parser is not an instance of " + BuffReaderFixedParser.class, parser instanceof BuffReaderFixedParser);

        BuffReaderFixedParser fixedParser = (BuffReaderFixedParser) parser;
        fixedParser.setHandlingShortLines(true);
        fixedParser.setStoreRawDataToDataSet(true);

        DefaultDataSet dataset = (DefaultDataSet) fixedParser.parse();
        Row parsedRow = fixedParser.buildRow(dataset);
        String rawData = parsedRow.getRawData();
        assertTrue("The raw data does not match the orginal line", rawData.equals(SHORT_LINE_STRING));
    }

    public void testBuildLongRow() {
        StringReader pzReader = new StringReader(PZ_MAP_XML_STRING);
        StringReader lineReader = new StringReader(LONG_LINE_STRING);

        Parser parser = BuffReaderParseFactory.getInstance().newFixedLengthParser(pzReader, lineReader);
        assertTrue("Parser is not an instance of " + BuffReaderFixedParser.class, parser instanceof BuffReaderFixedParser);

        BuffReaderFixedParser fixedParser = (BuffReaderFixedParser) parser;
        fixedParser.setIgnoreExtraColumns(true);
        fixedParser.setStoreRawDataToDataSet(true);

        DefaultDataSet dataset = (DefaultDataSet) fixedParser.parse();
        Row parsedRow = fixedParser.buildRow(dataset);
        String rawData = parsedRow.getRawData();
        assertTrue("The raw data does not match the orginal line", rawData.equals(LONG_LINE_STRING));
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(BuffReaderFixedParserTest.class);
    }
}
