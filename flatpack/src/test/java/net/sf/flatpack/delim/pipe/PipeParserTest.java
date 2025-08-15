package net.sf.flatpack.delim.pipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.DataSet;
import net.sf.flatpack.Parser;
import net.sf.flatpack.brparse.BuffReaderParseFactory;

/**
 * @author xhensevalb
 *
 */
class PipeParserTest {
    /**
     */
    @Test
    void testPipe() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"val1\"|\"val2\"|\"val3\"";

        final String[] expectedResult = { "val1", "val2", "val3" };

        parseAndCheck(testCsv, expectedResult);
    }

    @Test
    void testPipeMultiline() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"val" + System.lineSeparator() + "1\"|\"val2\"|\"val3\"";

        final String[] expectedResult = { "val" + System.lineSeparator() + "1", "val2", "val3" };
        parseAndCheck(testCsv, expectedResult);
    }

    @Test
    void testPipeMultilineAfterDelimiter() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"val1\"|\"" + System.lineSeparator() + "val2\"|\"val3\"";

        final String[] expectedResult = { "val1", System.lineSeparator() + "val2", "val3" };
        parseAndCheck(testCsv, expectedResult);
    }

    @Test
    void testPipeMultilineDelimiterIsFirst() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"" + System.lineSeparator() + "val1\"|\"val2\"|\"val3\"";

        final String[] expectedResult = { System.lineSeparator() + "val1", "val2", "val3" };

        parseAndCheck(testCsv, expectedResult);
    }

    @Test
    void testPipeMultilineDoubleLine() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"val" //
                + System.lineSeparator() + System.lineSeparator() + "1" + System.lineSeparator() + "2\"|\"val2\"|\"val3\"";

        final String[] expectedResult = { "val" + System.lineSeparator() + System.lineSeparator() + "1" + System.lineSeparator() + "2", "val2",
                "val3" };

        parseAndCheck(testCsv, expectedResult);
    }

    @Test
    void testPipeMultilineEmpty() {
        final String testCsv = "\"col1\"|\"col2\"|\"col3\"" + System.lineSeparator() + "\"val" + System.lineSeparator() + "2\"|\"\"|\"\"";

        final String[] expectedResult = { "val" + System.lineSeparator() + "2", "", "" };

        parseAndCheck(testCsv, expectedResult);
    }

    private void parseAndCheck(final String testCsv, final String[] expectedResult) {
        final Reader bis = new StringReader(testCsv);
        final Parser parser = BuffReaderParseFactory.getInstance().newDelimitedParser(bis, '|', '"');
        final DataSet result = parser.parse();

        // no errors should be in result, we should have 1 row with 7 columns
        // assertThat(result.getErrorCount()).isEqualTo(0);
        // assertThat(result.getRowCount()).isEqualTo(1);

        result.next();
        assertThat(result.getColumns().length).isEqualTo(expectedResult.length);
        final String[] columns = result.getColumns();

        for (int i = 0; i < expectedResult.length; ++i) {
            assertThat(expectedResult[i]).isEqualTo(result.getString(columns[i]));
        }
    }

}
