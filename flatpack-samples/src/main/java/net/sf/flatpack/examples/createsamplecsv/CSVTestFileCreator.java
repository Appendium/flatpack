package net.sf.flatpack.examples.createsamplecsv;

import java.io.FileWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created on Nov 30, 2005
 *
 */

/**
 * @author zepernick
 *
 * Creates a sample csv file with the specified number of columns and rows
 */
public class CSVTestFileCreator {
    private static final Logger LOG = LoggerFactory.getLogger(CSVTestFileCreator.class);

    public static void main(final String[] args) {
        int cols = 0;
        int rows = 0;

        if (args.length != 2) {
            printUsage();
            return;
        }

        try {
            cols = Integer.parseInt(args[0]);
            rows = Integer.parseInt(args[1]);
        } catch (final Exception ex) {
            printUsage();
            return;
        }

        createFile(cols, rows);
    }

    public static void createFile(final int cols, final int rows) {
        createFile(cols, rows, "SampleCSV.csv");
    }

    public static void createFile(final int cols, final int rows, final String filename) {
        try (FileWriter fw = new FileWriter(filename); PrintWriter out = new PrintWriter(fw)) {

            // write the column names across the top of the file
            for (int i = 1; i <= cols; i++) {
                if (i > 1) {
                    out.write(",");
                }
                out.write("\"column " + i + "\"");
            }
            out.write("\r\n");
            out.flush();

            // write the rows
            for (int i = 1; i <= rows; i++) {
                for (int j = 1; j <= cols; j++) {
                    if (j > 1) {
                        out.write(",");
                    }
                    out.write("\"data " + j + "\"");
                }

                out.write("\r\n");
                out.flush();
                if (i % 100000 == 0) {
                    System.out.print(".");
                }
            }

        } catch (final Exception ex) {
            LOG.error("Issue", ex);
        }
    }

    private static void printUsage() {
        System.out.println("INVALID USAGE...");
        System.out.println("PARAMETER 1 = # OF COLUMNS");
        System.out.println("PARAMETER 2 = # OF ROWS");
        System.out.println("Example - java CSVTestFileCreator 10 100");
    }
}
