package net.sf.flatpack.examples.multilinedelimitedrecord;

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
public class DelimitedMultiLine {

    public static void main(final String[] args) throws Exception {
        final String data = getDefaultDataFile();
        try {
            call(data);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifierMultiLine.txt";
    }

    public static void call(final String data) throws Exception {
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        final Parser pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new File(data), ',', '\"');
        final DataSet ds = pzparser.parse();

        final String[] colNames = ds.getColumns();

        while (ds.next()) {
            for (int i = 0; i < colNames.length; i++) {
                System.out.println("COLUMN NAME: " + colNames[i] + " VALUE: " + ds.getString(colNames[i]));
            }

            System.out.println("===========================================================================");
        }

        if (ds.getErrors() != null && ds.getErrors().size() > 0) {
            System.out.println("FOUND ERRORS IN FILE");
        }

    }
}
