package com.pz.reader.examples.createsamplecsv;

import java.io.FileWriter;
import java.io.PrintWriter;

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

    public static void main(final String[] args) {
        int cols = 0;
        int rows = 0;
        FileWriter fw = null;
        PrintWriter out = null;

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

        try {

            fw = new FileWriter("SampleCSV.csv");
            out = new PrintWriter(fw);

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
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (final Exception ignore) {
            }

        }

    }

    private static void printUsage() {
        System.out.println("INVALID USAGE...");
        System.out.println("PARAMETER 1 = # OF COLUMNS");
        System.out.println("PARAMETER 2 = # OF ROWS");
        System.out.println("Example - java CSVTestFileCreator 10 100");
    }
}
