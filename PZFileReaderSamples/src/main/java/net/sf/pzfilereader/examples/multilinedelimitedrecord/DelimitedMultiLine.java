package net.sf.pzfilereader.examples.multilinedelimitedrecord;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import net.sf.pzfilereader.DataSet;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedMultiLine {

    public static void main(final String[] args) throws Exception {
        String data = getDefaultDataFile();
        try {
            call(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifierMultiLine.txt";
    }

    public static void call(String data) throws Exception {
        DataSet ds = null;
        String[] colNames = null;
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        ds = new DataSet(new File(data), ",", "\"", false);

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
