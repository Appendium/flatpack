package net.sf.pzfilereader.examples.largedataset.largecsvperformancetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.pzfilereader.DataError;
import net.sf.pzfilereader.LargeDataSet;

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
public class CSVLarge {

    public static void main(final String[] args) {
        try {
            Map settings = readSettings();
            String data = (String) settings.get("csvFile");
            call(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getDefaultDataFile() {
        return "LargeSampleCSV.csv";
    }

    public static void call(String data) throws Exception {
        LargeDataSet ds = null;
        try {


            // delimited by a comma
            // text qualified by double quotes
            // ignore first record
            ds = new LargeDataSet(new File(data), ",", "\"", false);

            final long timeStarted = System.currentTimeMillis();
            int totalCount = 0;
            int tmpCount = 0;
            while (ds.next()) {
                totalCount++;
                tmpCount++;
                if (tmpCount >= 2500) {
                    System.out.println("Read " + totalCount + " Records...");
                    tmpCount = 0;
                }
            }
            final long timeFinished = System.currentTimeMillis();

            String timeMessage = "";

            if (timeFinished - timeStarted < 1000) {
                timeMessage = (timeFinished - timeStarted) + " Milleseconds...";
            } else {
                timeMessage = ((timeFinished - timeStarted) / 1000) + " Seconds...";
            }

            System.out.println("");
            System.out.println("********FILE PARSED IN: " + timeMessage + " ******");

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
