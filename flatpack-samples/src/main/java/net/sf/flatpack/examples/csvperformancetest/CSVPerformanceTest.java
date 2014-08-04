package net.sf.flatpack.examples.csvperformancetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.flatpack.DataError;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;

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
public class CSVPerformanceTest {

    public static void main(final String[] args) {

        Map settings = null;

        try {

            settings = readSettings();
            final String filename = (String) settings.get("csvFile");
            final String verbose = (String) settings.get("verbose");

            call(filename, Boolean.valueOf(verbose).booleanValue(), true);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void call(final String filename, final boolean verbose, final boolean traverse) throws Exception, InterruptedException {
        String[] colNames = null;
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        System.out.println("Parsing....");
        final Parser pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new File(filename), ',', '"');
        long timeStarted = System.currentTimeMillis();
        final DataSet ds = pzparser.parse();
        long timeFinished = System.currentTimeMillis();

        String timeMessage = "";

        if (timeFinished - timeStarted < 1000) {
            timeMessage = timeFinished - timeStarted + " Milleseconds...";
        } else {
            timeMessage = (float) ((timeFinished - timeStarted) / 1000.0) + " Seconds...";
        }

        System.out.println("");
        System.out.println("********FILE PARSED IN: " + timeMessage + " ******");

        if (traverse) {
            if (verbose) {
                Thread.sleep(2000); // sleep for a couple seconds to the message
                // above can be read
            }
            timeStarted = System.currentTimeMillis();
            colNames = ds.getColumns();
            int rowCount = 0;
            final int colCount = colNames.length;
            while (ds.next()) {
                rowCount++;
                for (final String colName : colNames) {
                    final String string = ds.getString(colName);

                    if (verbose) {
                        System.out.println("COLUMN NAME: " + colName + " VALUE: " + string);
                    }
                }

                if (verbose) {
                    System.out.println("===========================================================================");
                }
            }
            timeFinished = System.currentTimeMillis();

            if (timeFinished - timeStarted < 1000) {
                timeMessage = timeFinished - timeStarted + " Milleseconds...";
            } else {
                timeMessage = (float) ((timeFinished - timeStarted) / 1000.0) + " Seconds...";
            }

            System.out.println("");
            System.out.println("********Traversed Data In: " + timeMessage + " (rows: " + rowCount + " Col:" + colCount + ") ******");

        }

        if (ds.getErrors() != null && !ds.getErrors().isEmpty()) {
            System.out.println("FOUND ERRORS IN FILE....");
            for (int i = 0; i < ds.getErrors().size(); i++) {
                final DataError de = (DataError) ds.getErrors().get(i);
                System.out.println("Error: " + de.getErrorDesc() + " Line: " + de.getLineNo());
            }
        }

    }

    private static Map readSettings() throws Exception {
        final Map result = new HashMap();
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;

        try {
            fr = new FileReader("settings.properties");
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#") || line.indexOf("=") == -1) {
                    continue;
                }

                result.put(line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1));
            }
        } finally {
            if (fr != null) {
                fr.close();
            }
            if (br != null) {
                br.close();
            }
        }

        return result;

    }

}
