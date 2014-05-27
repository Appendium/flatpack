package net.sf.flatpack.columninfile;

/*
 * Created on Nov 27, 2005
 *
 */

import java.io.File;

import net.sf.flatpack.DataError;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;
import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.ordering.OrderColumn;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedColumnNamesInFile {
    public static void main(final String[] args) throws Exception {
        String[] colNames = null;
        OrderBy orderby = null;

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        final Parser pzparser = DefaultParserFactory.getInstance().newDelimitedParser(
                new File("net/sf/flatpack/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"), ',', '\"');
        final DataSet ds = pzparser.parse();

        // re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        ds.orderRows(orderby);

        colNames = ds.getColumns();

        while (ds.next()) {
            for (final String colName : colNames) {
                System.out.println("COLUMN NAME: " + colName + " VALUE: " + ds.getString(colName));
            }

            System.out.println("===========================================================================");
        }

        if (ds.getErrors() != null && ds.getErrors().size() > 0) {
            System.out.println("FOUND ERRORS IN FILE....");
            for (int i = 0; i < ds.getErrors().size(); i++) {
                final DataError de = (DataError) ds.getErrors().get(i);
                System.out.println("Error: " + de.getErrorDesc() + " Line: " + de.getLineNo());
            }
        }

    }

    // used for Junit test

    public DataSet getDsForTest() throws Exception {

        final Parser parser = DefaultParserFactory.getInstance().newDelimitedParser(
                new File("src/test/java/net/sf/flatpack/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"), ',', '\"');

        return parser.parse();

        // return new DataSet(new
        // File("src/test/java/net/sf/flatpack/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"),
        // ",",
        // "\"", false);
    }
}
