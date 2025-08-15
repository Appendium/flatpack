/*
 * Created on Feb 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.flatpack.columninfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sf.flatpack.DataSet;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class DelimitedColumnNamesInFileTest {

    // tests to make sure we have 0 errors
    @Test
    void testErrorCount() {
        DataSet ds = null;

        try {
            final DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();

            ds = testDelimted.getDsForTest();

            // check that we had no errors
            assertEquals(0, ds.getErrors().size());

        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
        }
    }

    // test to make sure we parsed the correct number
    // of rows in the file
    @Test
    void testRowCount() {
        DataSet ds = null;

        try {
            final DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();

            ds = testDelimted.getDsForTest();

            // check that we parsed in the right amount of rows
            assertEquals(6, ds.getRowCount());

        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
        }
    }

    // test to make sure we have the right number of column names from the file
    @Test
    void testColumnNameCount() {
        DataSet ds = null;

        try {
            final DelimitedColumnNamesInFile testDelimted = new DelimitedColumnNamesInFile();

            ds = testDelimted.getDsForTest();

            // check that we parsed in the right amount of column names
            assertEquals(6, ds.getColumns().length);

        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
        }
    }
}
