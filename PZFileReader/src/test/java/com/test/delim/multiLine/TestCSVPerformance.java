package com.test.delim.multiLine;

import java.io.File;

import com.pz.reader.DataError;
import com.pz.reader.LargeDataSet;

/*
 * Created on Dec 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TestCSVPerformance {

    public static void main(final String[] args) {

        LargeDataSet ds = null;
        String[] colNames = null;

        try {

            // delimited by a comma
            // text qualified by double quotes
            // ignore first record
            long timeStarted = System.currentTimeMillis();
            ds = new LargeDataSet(new File("com/test/delim/multiLine/PEOPLE-CommaDelimitedWithQualifier.txt"), ",", "\"", false);
            long timeFinished = System.currentTimeMillis();

            String timeMessage = "";

            if (timeFinished - timeStarted < 1000) {
                timeMessage = (timeFinished - timeStarted) + " Milleseconds...";
            } else {
                timeMessage = ((timeFinished - timeStarted) / 1000) + " Seconds...";
            }

            System.out.println("");
            System.out.println("********FILE PARSED IN: " + timeMessage + " ******");
            Thread.sleep(2000); // sleep for a couple seconds to the message
                                // above can be read

            timeStarted = System.currentTimeMillis();

            int times = 0;
            while (ds.next()) {
                colNames = ds.getColumns();
                for (int i = 0; i < colNames.length; i++) {
                    System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
                }

                System.out.println("===========================================================================");
                times++;
            }
            timeFinished = System.currentTimeMillis();

            if (timeFinished - timeStarted < 1000) {
                timeMessage = (timeFinished - timeStarted) + " Milleseconds...";
            } else {
                timeMessage = ((timeFinished - timeStarted) / 1000) + " Seconds...";
            }

            System.out.println("");
            System.out.println("********Displayed Data To Console In: " + timeMessage + " ******");

            if (ds.getErrors() != null && ds.getErrors().size() > 0) {
                System.out.println("FOUND ERRORS IN FILE....");
                for (int i = 0; i < ds.getErrors().size(); i++) {
                    final DataError de = (DataError) ds.getErrors().get(i);
                    System.out.println("Error: " + de.getErrorDesc() + " Line: " + de.getLineNo());
                }
            }

            // clear out the DataSet object for the JVM to collect
            ds.freeMemory();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

    }

}
