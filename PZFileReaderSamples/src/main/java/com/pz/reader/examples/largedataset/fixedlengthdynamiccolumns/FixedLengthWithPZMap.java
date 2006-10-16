package com.pz.reader.examples.largedataset.fixedlengthdynamiccolumns;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.io.FileInputStream;

import com.pz.reader.LargeDataSet;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FixedLengthWithPZMap {
    public static void main(final String[] args) throws Exception {
        LargeDataSet ds = null;
        String[] colNames = null;

        ds = new LargeDataSet(new FileInputStream(new File("PEOPLE-FixedLength.pzmap.xml")), new FileInputStream(new File(
                "PEOPLE-FixedLength.txt")), false);

        colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

        // clear out the DataSet object for the JVM to collect
        ds.freeMemory();

    }
}
