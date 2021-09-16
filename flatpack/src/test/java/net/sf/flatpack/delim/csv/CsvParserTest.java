package net.sf.flatpack.delim.csv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DelimiterParser;
import net.sf.flatpack.Parser;
import net.sf.flatpack.brparse.BuffReaderParseFactory;
import net.sf.flatpack.util.FPConstants;

/**
 * Thanks to dmitryallen for providing an example, see https://github.com/Appendium/flatpack/issues/53
 * @author xhensevalb
 *
 */
public class CsvParserTest extends TestCase {

    private final String csvData = "RefDate,Program,MEMBERID,LNAME,FNAME,DOB,GENDER_CD,ADDR_LINE_1,ADDR_LINE_2,CITY_NM,ST_CD,ZIP_CD,PRIMARY_PHONE_NUM,SECONDARY_PHONE_NUM,LANG_NM,Hearing,PLan_Level,ENROLLED_GR,GR_TOTAL_REWARD_AMOUNT,GR_ACTIVITIES_ HRA1_$10,GR_ACTIVITIES_ HRA2_$10,GR_ACTIVITIES_ AWV_$15,GR_ACTIVITIES_ BONUS_$50,GR_ACTIVITIES_ BCS_$75,GR_ACTIVITIES_ DSC_$100,GR_ACTIVITIES_ CO_$50,TRANSPORTATION,OTC_AMOUNT_Q,OTC_AMOUNT_Y,Flu_shot,MEMBERID2,CHANGE_INDICATOR"
            + System.getProperty("line.separator")
            + "2/12/2020,12,22548000*01,P,J L,8/13/1972,F,707 LOUCKS RD,,YORK,PA,17404,7177181215,,ENGLISH,Y,Diamond,N,$75.00 ,N,Y,Y,Y,N,N,N,50,$300 (Diamond),$1200 (Diamond),Y,22548000,"
            + System.getProperty("line.separator")
            + "2/12/2020,12,22548000*01,N,L K,9/17/1979,F,5621 HAYS ST,,PITTSBURGH,PA,15206,4125033775,,ENGLISH,N,Diamond,N,$75.00 ,N,Y,Y,Y,N,N,N,50,$300 (Diamond),$1200 (Diamond),N,22548000,";

    private final String columnMapping = "<?xml version='1.0'?>" + System.getProperty("line.separator") //
            + "<!DOCTYPE PZMAP SYSTEM \"flatpack.dtd\" >" + System.getProperty("line.separator") //
            + "<PZMAP>" + System.getProperty("line.separator")//
            + "    <COLUMN name=\"Program\" />" + System.getProperty("line.separator") //
            + "    <COLUMN name=\"MEMBERID\" />" + System.getProperty("line.separator") //
            + "    <COLUMN name=\"PRIMARY_PHONE_NUM\" />" + System.getProperty("line.separator") //
            + "    <COLUMN name=\"SECONDARY_PHONE_NUM\" />" + System.getProperty("line.separator") //
            + "    <COLUMN name=\"CHANGE_INDICATOR\" />" + System.getProperty("line.separator") //
            + "</PZMAP>";

    public void testCsvWithWrongPzMap() {

        try {
            final Parser parser = BuffReaderParseFactory.getInstance().newDelimitedParser(new StringReader(columnMapping), new StringReader(csvData),
                    ',', FPConstants.NO_QUALIFIER, true);
            parser.setHandlingShortLines(true);
            parser.setIgnoreParseWarnings(true);
            parser.setIgnoreExtraColumns(true);

            final DataSet ds = parser.parse();

            assertThat(ds.getErrors()).isEmpty();
            final String[] colNames = ds.getColumns();
            assertThat(colNames).hasSize(5);
            assertThat(colNames).containsExactly("Program", "MEMBERID", "PRIMARY_PHONE_NUM", "SECONDARY_PHONE_NUM", "CHANGE_INDICATOR");

            ds.next();

            assertThat(ds.getString("Program")).isEqualTo("2/12/2020"); // it is the FIRST COLUMN as specified in the PZMAP!
            assertThat(ds.getString("MEMBERID")).isEqualTo("12"); // it is the Second COLUMN as specified in the PZMAP!

        } catch (final Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read mapping", e);
        }

    }

    /**
     * Fails with the error "Odd number of qualifiers"
     */
    public void testCsvDocumentWithMultilineString() {
        final String testCsv = "col1,col2,col3,col4,col5,col6,col7" + System.lineSeparator()
                + "Bob,Smith,bsmiht@test.com,\"This is a long fragment of text" + System.lineSeparator()
                + "that should be processed as a single field\", 1988, 111-222-33,\"another field with new line character" + System.lineSeparator()
                + "that should be considered as a field of the same data row\"";

        final String[] expectedResult = { "Bob", "Smith", "bsmiht@test.com",
                "This is a long fragment of text" + System.lineSeparator() + "that should be processed as a single field", " 1988", " 111-222-33",
                "another field with new line character" + System.lineSeparator() + "that should be considered as a field of the same data row" };

        final ByteArrayInputStream bis = new ByteArrayInputStream(testCsv.getBytes(StandardCharsets.UTF_8));
        final DelimiterParser parser = new DelimiterParser(bis, ',', '"', false);
        final DataSet result = parser.parse();

        // no errors should be in result, we should have 1 row with 7 columns
        assertThat(result.getErrorCount()).isEqualTo(0);
        assertThat(result.getColumns().length).isEqualTo(expectedResult.length);
        assertThat(result.getRowCount()).isEqualTo(1);

        String[] columns = result.getColumns();

        result.next();
        for (int i = 0; i < expectedResult.length; ++i) {
            assertThat(expectedResult[i]).isEqualTo(result.getString(columns[i]));
        }
    }

    /**
     */
    public void testCsvDocumentWithMultilineEmptyString() {
        final String testCsv = "col1,col2,col3" + System.lineSeparator() //
                + "B,\"S" + System.lineSeparator() + System.lineSeparator() + "\",val3" + System.lineSeparator() //
                + "v1,v2,v3" //
        ;

        final String[] expectedResult = { "B", "S" + System.lineSeparator() + System.lineSeparator(), "val3" };
        final String[] expectedResultR2 = { "v1", "v2", "v3" };

        final ByteArrayInputStream bis = new ByteArrayInputStream(testCsv.getBytes(StandardCharsets.UTF_8));
        final DelimiterParser parser = new DelimiterParser(bis, ',', '"', false);
        final DataSet result = parser.parse();

        // no errors should be in result, we should have 1 row with 7 columns
        assertThat(result.getErrorCount()).isEqualTo(0);
        assertThat(result.getColumns().length).isEqualTo(expectedResult.length);
        assertThat(result.getRowCount()).isEqualTo(2);

        String[] columns = result.getColumns();

        result.next();
        for (int i = 0; i < expectedResult.length; ++i) {
            assertThat(expectedResult[i]).isEqualTo(result.getString(columns[i]));
        }

        result.next();
        for (int i = 0; i < expectedResult.length; ++i) {
            assertThat(expectedResultR2[i]).isEqualTo(result.getString(columns[i]));
        }
    }

    /**
     * Fails with the error "Odd number of qualifiers"
     */
    public void testCsvDocumentWithMultilineStringFirstLine() {
        final String testCsv = "col1,col2,col3,col4,col5,col6,col7" + System.lineSeparator() + "\"Bob" + System.lineSeparator()
                + "by\",\"Smith\",\"bsmiht@test.com\",\"This is a long fragment of text" + System.lineSeparator()
                + "that should be processed as a single field\", 1988, 111-222-33,\"another field with new line character" + System.lineSeparator()
                + "that should be considered as a field of the same data row\"";

        final String[] expectedResult = { "Bob" + System.lineSeparator() + "by",//
                "Smith",//
                "bsmiht@test.com",//
                "This is a long fragment of text" + System.lineSeparator() + "that should be processed as a single field",//
                " 1988",//
                " 111-222-33",//
                "another field with new line character" + System.lineSeparator() + "that should be considered as a field of the same data row" };

        final ByteArrayInputStream bis = new ByteArrayInputStream(testCsv.getBytes(StandardCharsets.UTF_8));
        final DelimiterParser parser = new DelimiterParser(bis, ',', '"', false);
        final DataSet result = parser.parse();

        // no errors should be in result, we should have 1 row with 7 columns
        assertThat(result.getErrorCount()).isEqualTo(0);
        assertThat(result.getColumns().length).isEqualTo(expectedResult.length);
        assertThat(result.getRowCount()).isEqualTo(1);

        String[] columns = result.getColumns();

        result.next();
        for (int i = 0; i < expectedResult.length; ++i) {
            assertThat(expectedResult[i]).isEqualTo(result.getString(columns[i]));
        }
    }
}
