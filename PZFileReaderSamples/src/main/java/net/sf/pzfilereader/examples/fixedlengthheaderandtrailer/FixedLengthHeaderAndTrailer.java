package net.sf.pzfilereader.examples.fixedlengthheaderandtrailer;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.util.Iterator;

import net.sf.pzfilereader.DataError;
import net.sf.pzfilereader.DataSet;

/**
 * @author zepernick
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FixedLengthHeaderAndTrailer {
    public static void main(final String[] args) throws Exception {
        String mapping = getDefaultMapping();
        String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-FixedLengthWithHeaderTrailer.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-FixedLengthWithHeaderTrailer.pzmap.xml";
    }

    public static void call(String mapping, String data) throws Exception {
        DataSet ds = null;
        Iterator errors = null;
        DataError dataError = null;

        ds = new DataSet(new File(mapping), new File(data), false);

        while (ds.next()) {

            if (ds.isRecordID("header")) {
                System.out.println(">>>>found header");
                System.out.println("COLUMN NAME: INDICATOR VALUE: " + ds.getString("INDICATOR"));
                System.out.println("COLUMN NAME: HEADERDATA VALUE: " + ds.getString("HEADERDATA"));
                System.out.println("===========================================================================");
                continue;
            }

            if (ds.isRecordID("trailer")) {
                System.out.println(">>>>found trailer");
                System.out.println("COLUMN NAME: INDICATOR VALUE: " + ds.getString("INDICATOR"));
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
            errors = ds.getErrors().iterator();

            while (errors.hasNext()) {
                dataError = (DataError) errors.next();

                System.out.println("ERROR: " + dataError.getErrorDesc() + " LINE NUMBER: " + dataError.getLineNo());
            }
        }

    }
}
