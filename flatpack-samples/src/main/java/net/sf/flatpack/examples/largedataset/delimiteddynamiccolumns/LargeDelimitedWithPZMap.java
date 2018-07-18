package net.sf.flatpack.examples.largedataset.delimiteddynamiccolumns;

import java.io.FileReader;

import net.sf.flatpack.DataSet;
import net.sf.flatpack.brparse.BuffReaderDelimParser;
import net.sf.flatpack.brparse.BuffReaderParseFactory;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LargeDelimitedWithPZMap {
    public static void main(final String[] args) throws Exception {
        final String mapping = getDefaultMapping();
        final String data = getDefaultDataFile();
        call(mapping, data);

    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifier.txt";
    }

    public static String getDefaultMapping() {
        return "PEOPLE-Delimited.pzmap.xml";
    }

    public static void call(final String mapping, final String data) throws Exception {
        String[] colNames = null;
        try (FileReader pzmap = new FileReader(mapping);
                FileReader fileToParse = new FileReader(data);
                BuffReaderDelimParser pzparse = (BuffReaderDelimParser) BuffReaderParseFactory.getInstance().newDelimitedParser(pzmap, fileToParse,
                        ',', '"', true)) {
            // delimited by a comma
            // text qualified by double quotes
            // ignore first record

            final DataSet ds = pzparse.parse();

            colNames = ds.getColumns();

            while (ds.next()) {
                for (final String colName : colNames) {
                    System.out.println("COLUMN NAME: " + colName + " VALUE: " + ds.getString(colName));
                }

                System.out.println("===========================================================================");
            }

            if (ds.getErrors() != null && !ds.getErrors().isEmpty()) {
                System.out.println("FOUND ERRORS IN FILE");
            }
        }
    }
}
