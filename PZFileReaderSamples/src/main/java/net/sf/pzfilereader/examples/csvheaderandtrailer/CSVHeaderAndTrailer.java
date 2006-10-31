package net.sf.pzfilereader.examples.csvheaderandtrailer;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;

import net.sf.pzfilereader.DefaultPZParserFactory;
import net.sf.pzfilereader.IDataSet;
import net.sf.pzfilereader.PZParser;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CSVHeaderAndTrailer {
    public static void main(final String[] args) throws Exception {
        call(getDefaultMapping(), getDefaultDataFile());
    }

    public static String getDefaultMapping() {
        return "PEOPLE-DelimitedWithHeaderTrailer.pzmap.xml";
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifier.txt";
    }

    public static void call(final String mapping, final String data) throws Exception {
        final File mapFile = new File(mapping);
        final File dataFile = new File(data);
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record 
        final PZParser pzparser = DefaultPZParserFactory.getInstance().newDelimitedParser(mapFile, dataFile, 
                ',', '\"', true);
        final IDataSet ds = pzparser.parse();
        while (ds.next()) {

            if (ds.isRecordID("header")) {
                System.out.println(">>>>>>Found Header Record");
                System.out.println("COLUMN NAME: RECORDINDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: HEADERDATA VALUE: " + ds.getString("HEADERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            if (ds.isRecordID("trailer")) {
                System.out.println(">>>>>>Found Trailer Record");
                System.out.println("COLUMN NAME: RECORDINDICATOR VALUE: " + ds.getString("RECORDINDICATOR"));
                System.out.println("COLUMN NAME: TRAILERDATA VALUE: " + ds.getString("TRAILERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            System.out.println("COLUMN NAME: FIRSTNAME VALUE: " + ds.getString("FIRSTNAME"));
            System.out.println("COLUMN NAME: LASTNAME VALUE: " + ds.getString("LASTNAME"));
            System.out.println("COLUMN NAME: ADDRESS VALUE: " + ds.getString("ADDRESS"));
            System.out.println("COLUMN NAME: CITY VALUE: " + ds.getString("CITY"));
            System.out.println("COLUMN NAME: STATE VALUE: " + ds.getString("STATE"));
            System.out.println("COLUMN NAME: ZIP VALUE: " + ds.getString("ZIP"));
            System.out.println("===========================================================================");
        }

        if (ds.getErrors() != null && ds.getErrors().size() > 0) {
            System.out.println("FOUND ERRORS IN FILE");
        }
    }
}
