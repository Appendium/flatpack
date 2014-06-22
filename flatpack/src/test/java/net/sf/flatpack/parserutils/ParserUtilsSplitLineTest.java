package net.sf.flatpack.parserutils;

import java.util.List;

import junit.framework.TestCase;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;
import net.sf.flatpack.utilities.UnitTestUtils;

/**
 * Test the functionality of the splitLine method. This method returns a List of
 * Strings. Each element of the list represents a column created by the parser
 * from the delimited String.
 *
 * @author Paul Zepernick
 */
public class ParserUtilsSplitLineTest extends TestCase {
    private static final String[] DELIMITED_DATA_NO_BREAKS = { "Column 1", "Column 2", "Column 3", "Column 4", "Column 5" };

    private static final String[] DELIMITED_DATA_WITH_BREAKS = { "Column 1 \r\n\r\n Test After Break \r\n Another Break", "Column 2",
            "Column 3 \r\n\r\n Test After Break", "Column 4", "Column 5 \r\n\r\n Test After Break\r\n Another Break" };

    // TODO think of a situation that actually breaks the parse. This still
    // works because of the way it is coded
    // to handle the excel CSV. Excel CSV has some elements qualified and others
    // not
    private static final String DELIMITED_BAD_DATA = "\"column 1\",\"column 2 ,\"column3\"";

    // 0 = delimiter
    // 1 = qualifier
    private static final char[][] DELIM_QUAL_PAIR = { { ',', '\"' }, { '\t', '\"' }, { '|', '\"' }, { '_', '\"' }, { ',', 0 }, { '|', 0 },
            { '\t', 0 } };

    /**
     * Test without any line breaks
     * 
     */
    public void testNoLineBreaks() {
        // loop down all delimiter qualifier pairs to test
        for (final char[] element : DELIM_QUAL_PAIR) {
            final char d = element[0];
            final char q = element[1];

            final String txtToParse = UnitTestUtils.buildDelimString(DELIMITED_DATA_NO_BREAKS, d, q);

            final List splitLineResults = ParserUtils.splitLine(txtToParse, d, q, 10, false, false);

            // check to make sure we have the same amount of elements which were
            // expected
            assertEquals("Expected size (d = [" + d + "] q = [" + (q != 0 ? String.valueOf(q) : "") + "] txt [" + txtToParse + "])",
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
        for (final char[] element : DELIM_QUAL_PAIR) {
            final char d = element[0];
            final char q = element[1];

            final String txtToParse = UnitTestUtils.buildDelimString(DELIMITED_DATA_WITH_BREAKS, d, q);

            final List splitLineResults = ParserUtils.splitLine(txtToParse, d, q, 10, false, false);

            // check to make sure we have the same amount of elements which were
            // expected
            assertEquals("Did Not Get Amount Of Elements Expected (d = " + d + " q = " + q + ")", DELIMITED_DATA_WITH_BREAKS.length,
                    splitLineResults.size());

            // loop through each value and compare what came back
            for (int j = 0; j < DELIMITED_DATA_WITH_BREAKS.length; j++) {
                assertEquals("Data Element Value Does Not Match (d = " + d + " q = " + q + ")", DELIMITED_DATA_WITH_BREAKS[j],
                        (String) splitLineResults.get(j));
            }
        }

        ParserUtils.splitLine("26,\"10726/1996\",551,\"Extra\",08/04/2005 00:00:00,0,0,,\"The unanimous judgement of the Team is that:\n" + "\n"
                + "(i) The members have to pay the amount on time. \n" + "\n" + "(ii) There would be regular meeting biweekly. \"", ',', '"', 10,
                false, false);
    }

    /**
     * Test to make sure we get the correct amount of elements for malformed
     * data
     */
    public void testMalformedData() {
        final List splitLineResults = ParserUtils.splitLine(DELIMITED_BAD_DATA, ',', '\"', 10, false, false);

        assertEquals("Expecting 2 Data Elements From The Malformed Data", 2, splitLineResults.size());
    }

    /**
     * Test some extreme cases
     */
    public void testSomeExtremeCases() {
        check(null, ',', '\"', new String[] {});
        check("a", ',', '\"', new String[] { "a" });
        check("", ',', '\"', new String[] { "" });
        check(" ", ',', '\"', new String[] { "" });
        check("    ", ',', '\"', new String[] { "" });
        check(",", ',', '\"', new String[] { "", "" });
        check(",,", ',', '\"', new String[] { "", "", "" });
        check(",a,", ',', '\"', new String[] { "", "a", "" });

        check("\"a,b,c\"", ',', '\"', new String[] { "a,b,c" });
        check("\"a,b\",\"c\"", ',', '\"', new String[] { "a,b", "c" });
        check("\"a , b\",\"c\"", ',', '\"', new String[] { "a , b", "c" });
        check("a,b,c", ',', '\"', new String[] { "a", "b", "c" });
        check("a b,c", ',', '\"', new String[] { "a b", "c" });
        check("  a,b,c ", ',', '\"', new String[] { "a", "b", "c" });
        check("  a, b ,c", ',', '\"', new String[] { "a", "b", "c" });

        // example typically from Excel.
        check("\"test1\",test2,\"0.00\",\"another, element here\",lastone", ',', '\"', new String[] { "test1", "test2", "0.00",
                "another, element here", "lastone" });

        check("\"FRED\",\"ZNAME\",\"Text Qualifier \" and seperator, in string\",\"ELYRIA\",\"OH\",\"\"", ',', '\"', new String[] { "FRED", "ZNAME",
                "Text Qualifier \" and seperator, in string", "ELYRIA", "OH", "" });

        check("a\",b,c\"", ',', '\"', new String[] { "a\"", "b", "c\"" });
        check("  a, b ,c ", ',', '\"', new String[] { "a", "b", "c" });
        check("\"a\",     b  ,    \"c\"", ',', '\"', new String[] { "a", "b", "c" });

        check("\"\",,,,\"last one\"", ',', '\"', new String[] { "", "", "", "", "last one" });
        check("\"first\",\"second\",", ',', '\"', new String[] { "first", "second", "" });
        check("\"  a,b,c\"", ',', '\"', new String[] { "  a,b,c" });
        check("\"  a,b,c\",d", ',', '\"', new String[] { "  a,b,c", "d" });
        check("\"a, b,\"\"c\"", ',', '\"', new String[] { "a, b,\"c" });
        check("\"a, b,\"\"c\", \"     test\"", ',', '\"', new String[] { "a, b,\"c", "     test" });
        check("\"a, b,\"\"c\",      test", ',', '\"', new String[] { "a, b,\"c", "test" });

        check("one two three", ' ', '\u0000', new String[] { "one", "two", "three" });
        check("\"one\" \"two\" three", ' ', '\"', new String[] { "one", "two", "three" });
        check("\"one\"  \"two\"  three", ' ', '\"', new String[] { "one", "", "two", "", "three" });

        check(" , , ", ',', '"', new String[] { "", "", "" });
        check(" , , ", ',', FPConstants.NO_QUALIFIER, new String[] { "", "", "" });
        check(" \t \t ", '\t', '"', new String[] { "", "", "" });
        check(" \t \t ", '\t', FPConstants.NO_QUALIFIER, new String[] { "", "", "" });
        check("  ", ' ', '"', new String[] { "", "", "" });
        check("  ", ' ', FPConstants.NO_QUALIFIER, new String[] { "", "", "" });

        check("\"3881\",\"2272\",\"\",\"\"We don't have any medical records on file, but claim was precerted under PME 5490-6125-0000.  I'm following up with Jeri and Megan regarding the Never Event question.  based upon the ICD 9 procedure codes on the sample claim & the diagnosis codes and claim history, appears patient had a laminotomy on 6/4/2010.  Based upon the sample claim, appears that there was post-op infection + a laceration of the dura.  Based on the EWM screen information from INQ62, the post-op infection diagnosis and staph infection diagnosis were indicated as yes, these conditions were present on admission.  This doesn't mean that the patient got the infection during the outpatient surgery on 6/4/2010--it doesn't mean he didn't either.\r\n"
                + "Based upon the Never event wording in e.Policies, we do not consider a diagnosis of post-op infection or laceration during a procedure a Never event.  Therefore, we would not deny charges.  For purposes of this policy,  Aetna has determined the following events to be �Never Events:�\r\n"
                + "*\",\"1\",\"2011-06-17\",\"1\"",
                ',',
                '"',
                new String[] {
                        "3881",
                        "2272",
                        "",
                        "\"We don't have any medical records on file, but claim was precerted under PME 5490-6125-0000.  I'm following up with Jeri and Megan regarding the Never Event question.  based upon the ICD 9 procedure codes on the sample claim & the diagnosis codes and claim history, appears patient had a laminotomy on 6/4/2010.  Based upon the sample claim, appears that there was post-op infection + a laceration of the dura.  Based on the EWM screen information from INQ62, the post-op infection diagnosis and staph infection diagnosis were indicated as yes, these conditions were present on admission.  This doesn't mean that the patient got the infection during the outpatient surgery on 6/4/2010--it doesn't mean he didn't either.\r\n"
                                + "Based upon the Never event wording in e.Policies, we do not consider a diagnosis of post-op infection or laceration during a procedure a Never event.  Therefore, we would not deny charges.  For purposes of this policy,  Aetna has determined the following events to be �Never Events:�\r\n"
                                + "*", "1", "2011-06-17", "1" });

    }

    /**
     * Test some extreme cases
     */
    public void testSomeExtremeCases2() {
        check("\"a,b,c\"", ',', '\'', new String[] { "\"a", "b", "c\"" });
        check("\"a,b\",\"c\"", ',', '\'', new String[] { "\"a", "b\"", "\"c\"" });
        check("a,b,c", ',', '\'', new String[] { "a", "b", "c" });
        check("  a,b,c", ',', '\'', new String[] { "a", "b", "c" });
        check("  a,b,c", ',', '\'', new String[] { "a", "b", "c" });

        // example typically from Excel.
        check("\"test1\",test2,\"0.00\",\"another, element here\",lastone", ',', '\'', new String[] { "\"test1\"", "test2", "\"0.00\"", "\"another",
                "element here\"", "lastone" });

        // what would you expect of these ones?

        // +++++The parser allows qualified and unqualified elements to be
        // contained
        // on the same line. so it should break the elements down like so
        // 1 = a" -->" is part of the data since the element did not start with
        // a qualifier
        // 2 = b
        // 3 = c" --> same as #1
        // a",b,c"
        check("a\",b,c\"", ',', '\'', new String[] { "a\"", "b", "c\"" });

        check("\"  a,b,c\"", ',', '\'', new String[] { "\"  a", "b", "c\"" });
        check("  a, b ,c ", ',', '\'', new String[] { "a", "b", "c" });

    }

    private void check(final String txtToParse, final char delim, final char qualifier, final String[] expected) {
        final List splitLineResults = ParserUtils.splitLine(txtToParse, delim, qualifier, 10, false, false);

        assertEquals("Did Not Get Amount Of Elements Expected (d = " + delim + " q = " + qualifier + ") txt [" + txtToParse + "]", expected.length,
                splitLineResults.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals("expecting...", expected[i], splitLineResults.get(i));
        }
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(ParserUtilsSplitLineTest.class);
    }
}
