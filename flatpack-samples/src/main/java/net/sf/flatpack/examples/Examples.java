/**
 *
 */
package net.sf.flatpack.examples;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.objectlab.kit.console.ConsoleMenu;
import net.objectlab.kit.console.Repeater;
import net.sf.flatpack.examples.createsamplecsv.CSVTestFileCreator;
import net.sf.flatpack.examples.csvheaderandtrailer.CSVHeaderAndTrailer;
import net.sf.flatpack.examples.csvperformancetest.CSVPerformanceTest;
import net.sf.flatpack.examples.delimiteddynamiccolumns.DelimitedWithPZMap;
import net.sf.flatpack.examples.delimiteddynamiccolumnswitherrors.DelimitedWithPZMapErrors;
import net.sf.flatpack.examples.exporttoexcel.DelimitedFileExportToExcel;
import net.sf.flatpack.examples.fixedlengthdynamiccolumns.FixedLengthWithPZMap;
import net.sf.flatpack.examples.fixedlengthheaderandtrailer.FixedLengthHeaderAndTrailer;
import net.sf.flatpack.examples.largedataset.delimiteddynamiccolumns.LargeDelimitedWithPZMap;
import net.sf.flatpack.examples.largedataset.fixedlengthdynamiccolumns.LargeFixedLengthWithPZMap;
import net.sf.flatpack.examples.largedataset.largecsvperformancetest.CSVLarge;
import net.sf.flatpack.examples.lowlevelparse.LowLevelParse;
import net.sf.flatpack.examples.multilinedelimitedrecord.DelimitedMultiLine;
import net.sf.flatpack.examples.numericsanddates.NumericsAndDates;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

/**
 * @author Benoit Xhenseval
 */
public class Examples implements Repeater {
    private static final Logger LOG = LoggerFactory.getLogger(Examples.class);

    public void tearDown() {
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Examples examples = new Examples();
        examples.run();
    }

    @Override
    public void repeat(final Method target) {
    }

    public void doCall() {
        System.err.println("");
        System.err.println("           ___  _     _           _   _          _");
        System.err.println("          / _ \\| |__ (_) ___  ___| |_| |    __ _| |__");
        System.err.println("         | | | | '_ \\| |/ _ \\/ __| __| |   / _` | '_ \\");
        System.err.println("         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |");
        System.err.println("          \\___/|_.__// |\\___|\\___|\\__|_____\\__,_|_.__/");
        System.err.println("                   |__/");
        System.err.println("");
        System.err.println("             w w w . O b j e c t L a b . c o . u k");
    }

    private void run() {
        final ConsoleMenu menu = new ConsoleMenu(this);
        menu.addMenuItem("CSVTestFileCreator", "doCSVTestFileCreator", false);
        menu.addMenuItem("CSVHeaderAndTrailer", "doCSVHeaderAndTrailer", false);
        menu.addMenuItem("CSVPerformanceTest", "doCSVPerformanceTest", false);
        menu.addMenuItem("DelimitedWithPZMap", "doDelimitedWithPZMap", false);
        menu.addMenuItem("DelimitedWithPZMapErrors", "doDelimitedWithPZMapErrors", false);
        menu.addMenuItem("DelimitedFileExportToExcel", "doDelimitedFileExportToExcel", false);
        menu.addMenuItem("FixedLengthWithPZMap", "doFixedLengthWithPZMap", false);
        menu.addMenuItem("FixedLengthHeaderAndTrailer", "doFixedLengthHeaderAndTrailer", false);
        menu.addMenuItem("LargeDelimitedWithPZMap", "doLargeDelimitedWithPZMap", false);
        menu.addMenuItem("LargeFixedLengthWithPZMap", "doLargeFixedLengthWithPZMap", false);
        menu.addMenuItem("CSVLarge", "doCSVLarge", false);
        menu.addMenuItem("LowLevelParse", "doLowLevelParse", false);
        menu.addMenuItem("DelimitedMultiLine", "doDelimitedMultiLine", false);
        menu.addMenuItem("NumericsAndDates", "doNumericsAndDates", false);
        menu.addMenuItem("Ask for GC", "doGC", false);
        menu.addMenuItem("Test StringBuffer", "doStringBuffer", false);
        menu.addMenuItem("Test Delim Parser Perform", "doTestParsers", false);

        menu.addMenuItem("Who you gonna call?", "doCall", false);
        menu.displayMenu();
    }

    public void doCSVTestFileCreator() {
        final int cols = ConsoleMenu.getInt("Number of cols", 10);
        final int rows = ConsoleMenu.getInt("Number of rows", 100);
        CSVTestFileCreator.createFile(cols, rows);
    }

    public void doCSVHeaderAndTrailer() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", CSVHeaderAndTrailer.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", CSVHeaderAndTrailer.getDefaultDataFile());
            CSVHeaderAndTrailer.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doCSVPerformanceTest() {
        try {
            final String mapping = ConsoleMenu.getString("CSV File ", "SampleCSV.csv");
            final boolean data = ConsoleMenu.getBoolean("Traverse the entire parsed file", true);
            final boolean verbose = ConsoleMenu.getBoolean("Verbose", false);
            CSVPerformanceTest.call(mapping, verbose, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doDelimitedWithPZMap() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", DelimitedWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", DelimitedWithPZMap.getDefaultDataFile());
            DelimitedWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doDelimitedWithPZMapErrors() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", DelimitedWithPZMapErrors.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", DelimitedWithPZMapErrors.getDefaultDataFile());
            DelimitedWithPZMapErrors.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doDelimitedFileExportToExcel() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", DelimitedFileExportToExcel.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", DelimitedFileExportToExcel.getDefaultDataFile());
            DelimitedFileExportToExcel.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doFixedLengthWithPZMap() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", FixedLengthWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", FixedLengthWithPZMap.getDefaultDataFile());
            FixedLengthWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doFixedLengthHeaderAndTrailer() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", FixedLengthHeaderAndTrailer.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", FixedLengthHeaderAndTrailer.getDefaultDataFile());
            FixedLengthHeaderAndTrailer.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doLargeDelimitedWithPZMap() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", LargeDelimitedWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", LargeDelimitedWithPZMap.getDefaultDataFile());
            LargeDelimitedWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doLargeFixedLengthWithPZMap() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", LargeFixedLengthWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", LargeFixedLengthWithPZMap.getDefaultDataFile());
            LargeFixedLengthWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doCSVLarge() {
        try {
            final int cols = ConsoleMenu.getInt("Number of cols", 10);
            final int rows = ConsoleMenu.getInt("Number of rows", 2000000);
            final String filename = "LargeSampleCSV.csv";
            CSVTestFileCreator.createFile(cols, rows, filename);

            System.err.println("Large file created");

            CSVLarge.call(filename);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doLowLevelParse() {
        try {
            final String data = ConsoleMenu.getString("Data   ", LowLevelParse.getDefaultDataFile());
            LowLevelParse.call(data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doDelimitedMultiLine() {
        try {
            final String data = ConsoleMenu.getString("Data   ", DelimitedMultiLine.getDefaultDataFile());
            DelimitedMultiLine.call(data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doNumericsAndDates() {
        try {
            final String mapping = ConsoleMenu.getString("Mapping ", NumericsAndDates.getDefaultMapping());
            final String data = ConsoleMenu.getString("Data   ", NumericsAndDates.getDefaultDataFile());
            NumericsAndDates.call(mapping, data);
        } catch (final Exception e) {
            LOG.error("Issue", e);
        }
    }

    public void doGC() {
        System.gc();
    }

    public void doStringBuffer() {
        final int repeat = ConsoleMenu.getInt("How many times?", 100000);
        final int characters = ConsoleMenu.getInt("How many char?", 20);

        long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            final StringBuilder sb = new StringBuilder();
            for (int u = 0; u < 1000; u++) {
                sb.append("h");
            }
        }
        long stop = System.currentTimeMillis();

        System.err.println("Creating new SB " + (stop - start) + " ms.");

        start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            for (int u = 0; u < characters; u++) {
                sb.append("h");
            }
            sb.delete(0, sb.length());
        }
        stop = System.currentTimeMillis();

        System.err.println("Deleting existing SB " + (stop - start) + " ms.");

    }

    public void doTestParsers() {
        final int repeat = ConsoleMenu.getInt("How many Rows?", 1000);
        final int numberOfCols = ConsoleMenu.getInt("How many columns?", 100);
        final boolean qualif = ConsoleMenu.getBoolean("With qualifier?", true);

        final StringBuilder aRow = new StringBuilder();
        for (int i = 0; i < numberOfCols; i++) {
            if (qualif) {
                aRow.append("\"");
            }
            aRow.append("Column ").append(i);
            if (qualif) {
                aRow.append("\"");
            }
        }

        final String line = aRow.toString();

        final long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            ParserUtils.splitLine(line, ',', '\"', FPConstants.SPLITLINE_SIZE_INIT, false, false);
        }
        final long stop = System.currentTimeMillis();

        System.err.println("ParserUtil " + (stop - start) + " ms.");

    }

}
