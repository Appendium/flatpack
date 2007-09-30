package net.sf.pzfilereader.examples.fixedlengthdynamiccolumns;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FixedLengthWithPZMap {
    public static void main(final String[] args) throws Exception {
        final String mapping = getDefaultMapping();
        final String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-FixedLength.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-FixedLength.pzmap.xml";
    }

    public static void call(final String mapping, final String data) throws Exception {
        final Parser pzparser = DefaultParserFactory.getInstance().newFixedLengthParser(new File(mapping), new File(data));
        final DataSet ds = pzparser.parse();

        final String[] colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

    }
}
