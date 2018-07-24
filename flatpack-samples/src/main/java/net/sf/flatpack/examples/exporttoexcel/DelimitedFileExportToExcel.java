package net.sf.flatpack.examples.exporttoexcel;

/*
 * Created on Dec 31, 2004
 *
 */

import java.io.File;
import java.io.FileReader;

import net.sf.flatpack.DataError;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;
import net.sf.flatpack.excel.ExcelTransformer;
import net.sf.flatpack.ordering.OrderBy;
import net.sf.flatpack.ordering.OrderColumn;

/**
 * @author zepernick
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DelimitedFileExportToExcel {
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
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        final Parser pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new FileReader(mapping), new FileReader(data), ',', '"', true);
        final DataSet ds = pzparser.parse();

        // re order the data set by last name
        final OrderBy orderby = new OrderBy();
        orderby.addOrderColumn(new OrderColumn("CITY", false));
        orderby.addOrderColumn(new OrderColumn("LASTNAME", true));
        ds.orderRows(orderby);

        if (ds.getErrors() != null && !ds.getErrors().isEmpty()) {
            for (int i = 0; i < ds.getErrors().size(); i++) {
                final DataError de = ds.getErrors().get(i);
                System.out.println("Error Msg: " + de.getErrorDesc() + " Line: " + de.getLineNo());
            }
        }

        // lets write this file out to excel
        final File xlFile = new File("MyExcelExport.xls");
        final ExcelTransformer xlTransformer = new ExcelTransformer(ds, xlFile);
        xlTransformer.writeExcelFile();
        System.out.println("Excel Workbook Written To: " + xlFile.getAbsolutePath());

    }
}
