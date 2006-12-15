package net.sf.pzfilereader.examples.largedataset.fixedlengthdynamiccolumns;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.io.FileInputStream;


/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LargeFixedLengthWithPZMap {
    public static void main(final String[] args) throws Exception {
        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-FixedLength.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-FixedLength.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {
        System.out.println("Not Currently Available...Check back later.");
      /*  LargeDataSet ds = null;
        String[] colNames = null;

        ds = new LargeDataSet(new FileInputStream(new File(mapping)), new FileInputStream(new File(data)), false);

        colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();*/

    }
}
