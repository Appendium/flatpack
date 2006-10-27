package net.sf.pzfilereader.columninfile;

/*
 * Created on Nov 27, 2005
 *
 */

import java.io.File;

import net.sf.pzfilereader.DataError;
import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.DefaultPZParserFactory;
import net.sf.pzfilereader.IDataSet;
import net.sf.pzfilereader.PZParser;
import net.sf.pzfilereader.ordering.OrderBy;
import net.sf.pzfilereader.ordering.OrderColumn;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedColumnNamesInFile {
    public static void main(final String[] args) throws Exception {
        DataSet ds = null;
        String[] colNames = null;
        OrderBy orderby = null;

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        ds = new DataSet(new File("net/sf/pzfilereader/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"), ",", "\"", false);

        // re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        ds.orderRows(orderby);

        colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
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

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }

    // used for Junit test

    public IDataSet getDsForTest() throws Exception {

        final PZParser parser = DefaultPZParserFactory.getInstance().newDelimitedParser(
                new File("src/test/java/net/sf/pzfilereader/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"), ',', '\"');

        return parser.parse();
        
        // return new DataSet(new
        // File("src/test/java/net/sf/pzfilereader/columninfile/PEOPLE-CommaDelimitedWithQualifier.txt"),
        // ",",
        // "\"", false);
    }
}
