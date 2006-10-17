package net.sf.pzfilereader.examples.exporttoexcel;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.ordering.OrderBy;
import net.sf.pzfilereader.ordering.OrderColumn;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedFileExportToExcel {
    public static void main(final String[] args) throws Exception {
        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifierAndHeaderTrailer.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-Delimited.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {
        DataSet ds = null;
        OrderBy orderby = null;

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        ds = new DataSet(new File(mapping), new File(data), ",", "\"", true, false);

        // re order the data set by last name
        orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        ds.orderRows(orderby);

        if (ds.getErrors() != null && ds.getErrors().size() > 0) {
            System.out.println("FOUND ERRORS IN FILE");
        }

        // lets write this file out to excel :)
        ds.writeToExcel(new File("MyExcelExport.xls"));

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }
}
