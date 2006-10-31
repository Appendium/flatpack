package net.sf.pzfilereader.examples.fixedlengthdynamiccolumns;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import net.sf.pzfilereader.DataSet;
import net.sf.pzfilereader.DefaultPZParserFactory;
import net.sf.pzfilereader.IDataSet;
import net.sf.pzfilereader.PZParser;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FixedLengthWithPZMap {
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
        final PZParser pzparser = DefaultPZParserFactory.getInstance().newFixedLengthParser(
                new File(mapping), new File(data));
        final IDataSet ds = pzparser.parse();

        final String[] colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

    }
}
