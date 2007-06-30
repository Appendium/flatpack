package net.sf.pzfilereader.examples.numericsanddates;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.text.SimpleDateFormat;

import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NumericsAndDates {
    public static void main(final String[] args) throws Exception {
        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);
    }

    public static String getDefaultDataFile() {
        return "INVENTORY-CommaDelimitedWithQualifier.txt";
    }

    public static String getDefaultMapping() {
        return "INVENTORY-Delimited.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {
        // wll provide a clean format for printing the date to the screen
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        final Parser pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new File(mapping), 
                new File(data), ',', '\"', true);
        final DataSet ds = pzparser.parse();
        // demonstrates the casting abilities of PZFileReader
        while (ds.next()) {
            System.out.println("Item Desc: " + ds.getString("ITEM_DESC") + " (String)");
            System.out.println("In Stock: " + ds.getInt("IN_STOCK") + " (int)");
            System.out.println("Price: " + ds.getDouble("PRICE") + " (double)");
            System.out.println("Received Dt: " + sdf.format(ds.getDate("LAST_RECV_DT")) + " (Date)");
            System.out.println("===========================================================================");
        }
    }
}
