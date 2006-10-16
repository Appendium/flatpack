package com.pz.reader.examples.numericsanddates;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.text.SimpleDateFormat;

import com.pz.reader.DataSet;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NumericsAndDates {
    public static void main(final String[] args) throws Exception {
        DataSet ds = null;
        // wll provide a clean format for printing the date to the screen
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        ds = new DataSet(new File("INVENTORY-Delimited.pzmap.xml"), new File("INVENTORY-CommaDelimitedWithQualifier.txt"), ",",
                "\"", true, false);

        // demonstrates the casting abilities of PZFileReader
        while (ds.next()) {
            System.out.println("Item Desc: " + ds.getString("ITEM_DESC") + " (String)");
            System.out.println("In Stock: " + ds.getInt("IN_STOCK") + " (int)");
            System.out.println("Price: " + ds.getDouble("PRICE") + " (double)");
            System.out.println("Received Dt: " + sdf.format(ds.getDate("LAST_RECV_DT")) + " (Date)");
            System.out.println("===========================================================================");
        }

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }
}
