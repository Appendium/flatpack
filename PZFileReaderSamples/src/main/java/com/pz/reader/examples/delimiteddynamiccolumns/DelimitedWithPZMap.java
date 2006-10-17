package com.pz.reader.examples.delimiteddynamiccolumns;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import com.pz.reader.DataSet;
import com.pz.reader.ordering.OrderBy;
import com.pz.reader.ordering.OrderColumn;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedWithPZMap {
    public static void main(final String[] args) throws Exception {

        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifierAndHeaderTrailer.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-DelimitedWithHeaderTrailer.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        DataSet ds = null;
        String[] colNames = null;
        OrderBy orderby = null;
        ds = new DataSet(new File(mapping), new File(data), ",", "\"", true, false);

        // re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        // ds.orderRows(orderby);

        colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

        if (ds.getErrors() != null && ds.getErrors().size() > 0) {
            System.out.println("FOUND ERRORS IN FILE");
        }

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();
    }
}
