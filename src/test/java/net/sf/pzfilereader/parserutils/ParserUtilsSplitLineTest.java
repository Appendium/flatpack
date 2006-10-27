package net.sf.pzfilereader.parserutils;

import java.util.List;

import junit.framework.TestCase;
import net.sf.pzfilereader.util.ParserUtils;
import net.sf.pzfilereader.utilities.UnitTestUtils;

/**
 * Test the functionality of the splitLine method. This method returns a List of
 * Strings. Each element of the list represents a column created by the parser
 * from the delimited String.
 * 
 * @author Paul Zepernick
 */
public class ParserUtilsSplitLineTest extends TestCase {
    private static final String[] DELIMITED_DATA_NO_BREAKS = { "Column 1", "Column 2", "Column 3", "Column 4", "Column 5" };

    private static final String[] DELIMITED_DATA_WITH_BREAKS = { "Column 1 \r\n\r\n Test After Break \r\n Another Break",
            "Column 2", "Column 3 \r\n\r\n Test After Break", "Column 4", "Column 5 \r\n\r\n Test After Break\r\n Another Break" };

    // TODO think of a situation that actually breaks the parse. This still
    // works because of the way it is coded
    // to handle the excel CSV. Excel CSV has some elements qualified and others
    // not
    private static final String DELIMITED_BAD_DATA = "\"column 1\",\"column 2 ,\"column3\"";

    // 0 = delimiter
    // 1 = qualifier
    private static final char[][] DELIM_QUAL_PAIR = { { ',', '\"' }, { '\t', '\"' }, { '|', '\"' }, { '_', '\"' }, { ',', 0 },
            { '|', 0 }, { '\t', 0 } };

    /**
     * Test without any line breaks
     * 
     */
    public void testNoLineBreaks() {
        // loop down all delimiter qualifier pairs to test
        for (int i = 0; i < DELIM_QUAL_PAIR.length; i++) {
            final char d = DELIM_QUAL_PAIR[i][0];
            final char q = DELIM_QUAL_PAIR[i][1];

            final String txtToParse = UnitTestUtils.buildDelimString(DELIMITED_DATA_NO_BREAKS, d, q);

            final List splitLineResults = ParserUtils.splitLine(txtToParse, d, q);

            // check to make sure we have the same amount of elements which were
            // expected
            assertEquals("Did Not Get Amount Of Elements Expected (d = [" + d + "] q = [" + q + "] txt [" + txtToParse + "])",
                    DELIMITED_DATA_NO_BREAKS.length, splitLineResults.size());

            // loop through each value and compare what came back
            for (int j = 0; j < DELIMITED_DATA_NO_BREAKS.length; j++) {
                assertEquals("Data Element Value Does Not Match (d = [" + d + "] q = [" + q + "] txt [" + txtToParse + "])",
                        DELIMITED_DATA_NO_BREAKS[j], (String) splitLineResults.get(j));
            }
        }

    }

    /**
     * Test with any line breaks
     * 
     */
    public void testLineBreaks() {
        // loop down all delimiter qualifier pairs to test
        for (int i = 0; i < DELIM_QUAL_PAIR.length; i++) {
            final char d = DELIM_QUAL_PAIR[i][0];
            final char q = DELIM_QUAL_PAIR[i][1];

            final String txtToParse = UnitTestUtils.buildDelimString(DELIMITED_DATA_WITH_BREAKS, d, q);

            final List splitLineResults = ParserUtils.splitLine(txtToParse, d, q);

            // check to make sure we have the same amount of elements which were
            // expected
            assertEquals("Did Not Get Amount Of Elements Expected (d = " + d + " q = " + q + ")",
                    DELIMITED_DATA_WITH_BREAKS.length, splitLineResults.size());

            // loop through each value and compare what came back
            for (int j = 0; j < DELIMITED_DATA_WITH_BREAKS.length; j++) {
                assertEquals("Data Element Value Does Not Match (d = " + d + " q = " + q + ")", DELIMITED_DATA_WITH_BREAKS[j],
                        (String) splitLineResults.get(j));
            }
        }
    }

    /**
     * Test to make sure we get the correct amount of elements for malformed
     * data
     */
    public void testMalformedData() {
        final List splitLineResults = ParserUtils.splitLine(DELIMITED_BAD_DATA, ',', '\"');

        assertEquals("Expecting 2 Data Elements From The Malformed Data", 2, splitLineResults.size());
    }

    /**
     * Test some extreme cases
     */
    public void testSomeExtremeCases() {
        check("\"a,b,c\"", ',', '\"', new String[] { "a,b,c" });
        check("\"a,b\",\"c\"", ',', '\"', new String[] { "a,b", "c" });
        check("a,b,c", ',', '\"', new String[] { "a", "b", "c" });
        check("  a,b,c", ',', '\"', new String[] { "a", "b", "c" });
        check("  a,b,c", ',', '\"', new String[] { "a","b","c" });
        
        // what would you expect of these ones?
        
        //+++++The parser allows qualified and unqualified elements to be contained
        //on the same line.  so it should break the elements down like so
        //1 = a" -->" is part of the data since the element did not start with a qualifier
        //2 = b
        //3 = c" --> same as #1
        check("a\",b,c\"", ',', '\"', new String[] { "a\"", "b", "c\"" });
        
        
        
        check("\"  a,b,c\"", ',', '\"', new String[] { "a,b,c" });
        //check("  a, b ,c ", ',', '\"', new String[] { "a","b","c" });
        //++++++I think this should probably generate this
        check("  a, b ,c ", ',', '\"', new String[] { "a, b ,c" });

        // Paul... please put some more whacky stuff here...

    }

    private void check(final String txtToParse, final char delim, final char qualifier, final String[] expected) {
        final List splitLineResults = ParserUtils.splitLine(txtToParse, delim, qualifier);

        assertEquals(
                "Did Not Get Amount Of Elements Expected (d = " + delim + " q = " + qualifier + ") txt [" + txtToParse + "]",
                expected.length, splitLineResults.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("expecting...", expected[i], splitLineResults.get(i));
        }
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsSplitLineTest.class);
    }
}
