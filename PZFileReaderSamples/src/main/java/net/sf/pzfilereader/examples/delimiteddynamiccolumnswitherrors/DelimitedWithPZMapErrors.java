package net.sf.pzfilereader.examples.delimiteddynamiccolumnswitherrors;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.util.Iterator;

import net.sf.pzfilereader.DataError;
import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.ordering.OrderBy;
import net.sf.pzfilereader.ordering.OrderColumn;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedWithPZMapErrors {
    public static void main(final String[] args) throws Exception {
        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifierAndHeaderTrailerRecError.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-DelimitedWithHeaderTrailer.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {

        DataSet ds = null;
        String[] colNames = null;
        OrderBy orderby = null;
        Iterator errors = null;
        DataError dataError = null;

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        ds = new DataSet(new File(mapping), new File(data), ",", "\"", true, false);

        // re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        ds.orderRows(orderby);

        colNames = ds.getColumns();

        while (ds.next()) {
            
            if (ds.isRecordID("header")) {
                System.out.println(">>>>found header");
                System.out.println("COLUMN NAME: INDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: HEADERDATA VALUE: " + ds.getString("HEADERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            if (ds.isRecordID("trailer")) {
                System.out.println(">>>>found trailer");
                System.out.println("COLUMN NAME: INDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: TRAILERDATA VALUE: " + ds.getString("TRAILERDATA"));
                System.out.println("===========================================================================");
                continue;
            }
            
            
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

        System.out.println(">>>>>>ERRORS!!!");
        errors = ds.getErrors().iterator();

        while (errors.hasNext()) {
            dataError = (DataError) errors.next();

            System.out.println("ERROR: " + dataError.getErrorDesc() + " LINE NUMBER: " + dataError.getLineNo());
        }

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }
}
