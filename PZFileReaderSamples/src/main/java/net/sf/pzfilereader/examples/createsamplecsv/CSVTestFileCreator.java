package net.sf.pzfilereader.examples.createsamplecsv;

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
    
    public static void createFile(int cols, int rows) {
        createFile(cols,rows,"SampleCSV.csv");
    }
    
    public static void createFile(int cols, int rows, final String filename) {
        FileWriter fw = null;
        PrintWriter out = null;
        try {

            fw = new FileWriter(filename);
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
                if (i % 100000 == 0) {
                    System.out.print(".");
                }
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
