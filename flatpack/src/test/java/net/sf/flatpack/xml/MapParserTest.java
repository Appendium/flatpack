package net.sf.flatpack.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

public class MapParserTest extends TestCase {
    private static final String PZ_FIXED_MAP = "<?xml version='1.0'?>\n" +//
            "<!DOCTYPE PZMAP SYSTEM  \"flatpack.dtd\" >\n" //
            + "<PZMAP>\n" //
            + "    <RECORD id=\"header\" startPosition=\"1\" endPosition=\"7\" indicator=\"HEADER \">\n"//
            + "        <COLUMN name=\"INDICATOR\" length=\"7\" />\n"//
            + "        <COLUMN name=\"HEADERDATA\" length=\"19\" />\n" //
            + "    </RECORD>\n" //
            + "    <COLUMN name=\"FIRSTNAME\" length=\"35\" />\n" //
            + "    <COLUMN name=\"LASTNAME\" length=\"35\" />\n" //
            + "    <COLUMN name=\"ADDRESS\" length=\"100\" />\n" //
            + "    <COLUMN name=\"CITY\" length=\"100\" />\n" //
            + "    <COLUMN name=\"STATE\" length=\"2\" />\n" //
            + "    <COLUMN name=\"ZIP\" length=\"5\" />\n"
            + "    <RECORD id=\"trailer\" startPosition=\"1\" endPosition=\"7\" indicator=\"TRAILER\">\n"//
            + "        <COLUMN name=\"INDICATOR\" length=\"7\" />\n" //
            + "        <COLUMN name=\"TRAILERDATA\" length=\"19\" />\n" //
            + "    </RECORD>\n"//
            + "</PZMAP> \n";
    private static final String PZ_MAP = "<?xml version='1.0'?>\n" +  //
            "<!DOCTYPE PZMAP SYSTEM\n" + //
            "    \"flatpack.dtd\" >\n" + //
            "<PZMAP>\n" + //
            "    <COLUMN name=\"FIRSTnAME\" />\n" + //
            "    <COLUMN name=\"LASTNAME\" />\n" + //
            "    <COLUMN name=\"ADDRESS\" />\n" + //
            "    <COLUMN name=\"CITY\" />\n" + //
            "    <COLUMN name=\"STATE\" />\n" + //
            "    <COLUMN name=\"ZIP\" />\n" + //
            "</PZMAP> \n";
    private static final String INVALID_DUPLICATE_COL = "<?xml version='1.0'?>\n" +  //
            "<!DOCTYPE PZMAP SYSTEM\n" + //
            "    \"flatpack.dtd\" >\n" + //
            "<PZMAP>\n" + //
            "    <COLUMN name=\"LASTNAME\" />\n" + //
            "    <COLUMN name=\"LASTNAME\" />\n" + //
            "    <COLUMN name=\"ZIP\" />\n" + //
            "</PZMAP> \n";

    public void testInvalidMap() throws IOException, ParserConfigurationException, SAXException {
        try {
            final Map<String, Object> parse = MapParser
                    .parse2(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("BrokenMapping.pzmap.xml")), null);
            fail("Expected Exception SAXException");
        } catch (SAXException io) {
            assertThat(io.getMessage()).contains("The element type \"COLUMN\" must be terminated by the matching end-tag \"</COLUMN>\"");
        }
    }

    public void testParseFixedMap() throws IOException, ParserConfigurationException, SAXException {
        final Map<String, Object> parse = MapParser.parse2(new StringReader(PZ_FIXED_MAP), null);
        assertThat(parse).hasSize(6);
        final List<ColumnMetaData> details = (List<ColumnMetaData>) parse.get(FPConstants.DETAIL_ID);
        assertThat(details).isNotNull();
        assertThat(details).hasSize(6);
        assertThat(details).extracting("colName").containsOnly("FIRSTNAME", "LASTNAME", "ADDRESS", "CITY", "STATE", "ZIP");
        assertThat(details).extracting("colLength").containsOnly(35, 100, 2, 5);
    }

    public void testParseComplexFixedMap() throws IOException, ParserConfigurationException, SAXException {
        final Map<String, Object> parse = MapParser.parse2(new InputStreamReader(getClass().getResourceAsStream("test-complex-fixed.xml")), null);
        assertThat(parse).hasSize(8);
        final XMLRecordElement details = (XMLRecordElement) parse.get("exchange");
        assertThat(details).isNotNull();
        assertThat(details.getIndicator()).isEqualToIgnoringCase("TE");
        assertThat(details.getStartPosition()).isEqualTo(1);
        assertThat(details.getEndPositition()).isEqualTo(2);
        assertThat(details.getColumns()).isNotNull();
        assertThat(details.getColumns()).hasSize(4);

        assertThat(details.getColumns()).extracting("colName").containsOnly("RecordType", "StatusIndicator", "ExchangeCode", "ExchangeName");
        assertThat(details.getColumns()).extracting("colLength").containsOnly(2, 1, 8, 259);
    }

    public void testParse() throws IOException, ParserConfigurationException, SAXException {
        final Map<String, Object> parse = MapParser.parse2(new StringReader(PZ_MAP), null);
        assertThat(parse).hasSize(2);
        final List<ColumnMetaData> details = (List<ColumnMetaData>) parse.get(FPConstants.DETAIL_ID);
        assertThat(details).isNotNull();
        assertThat(details).hasSize(6);
        assertThat(details).extracting("colName").containsOnly("FIRSTnAME", "LASTNAME", "ADDRESS", "CITY", "STATE", "ZIP");
    }

    public void testInvalidDuplicate() throws IOException, ParserConfigurationException, SAXException {
        final Parser parser = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader("hello"), ',', '"');
        parser.setColumnNamesCaseSensitive(true);
        try {
            MapParser.parse2(new StringReader(INVALID_DUPLICATE_COL), parser);
            fail("Expecting exception due to duplicate col");
        } catch (IllegalArgumentException iae) {
            assertThat(iae.getMessage()).contains("'LASTNAME'");
        }
    }

    public void testParseCaseSensitive() throws IOException, ParserConfigurationException, SAXException {
        final Parser parser = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader("hello"), ',', '"');
        parser.setColumnNamesCaseSensitive(true);
        final Map<String, Object> parse = MapParser.parse2(new StringReader(PZ_MAP), parser);
        assertThat(parse).hasSize(2);
        final List<ColumnMetaData> details = (List<ColumnMetaData>) parse.get(FPConstants.DETAIL_ID);
        assertThat(details).isNotNull();
        assertThat(details).hasSize(6);
        assertThat(details).extracting("colName").containsOnly("FIRSTnAME", "LASTNAME", "ADDRESS", "CITY", "STATE", "ZIP");
        final Map<String, Integer> colIndex = (Map<String, Integer>) parse.get(FPConstants.COL_IDX);
        assertThat(colIndex).isNotEmpty();
        assertThat(colIndex).containsOnlyKeys("FIRSTnAME", "LASTNAME", "ADDRESS", "CITY", "STATE", "ZIP");
    }

    public void testParseIgnoreCase() throws SAXException, IOException, ParserConfigurationException {
        final Parser parser = DefaultParserFactory.getInstance().newDelimitedParser(new StringReader("hello"), ',', '"');
        parser.setColumnNamesCaseSensitive(false);
        final Map<String, Object> parse = MapParser.parse2(new StringReader(PZ_MAP), parser);
        assertThat(parse).hasSize(2);
        final List<ColumnMetaData> details = (List<ColumnMetaData>) parse.get(FPConstants.DETAIL_ID);
        assertThat(details).isNotNull();
        assertThat(details).hasSize(6);
        assertThat(details).extracting("colName").containsOnly("FIRSTnAME", "LASTNAME", "ADDRESS", "CITY", "STATE", "ZIP");
        final Map<String, Integer> colIndex = (Map<String, Integer>) parse.get(FPConstants.COL_IDX);
        assertThat(colIndex).isNotEmpty();
        assertThat(colIndex).containsOnlyKeys("firstname", "lastname", "address", "city", "state", "zip");
    }
}
