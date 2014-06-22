package net.sf.flatpack.examples.lowlevelparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import net.sf.flatpack.util.ParserUtils;

/*
 * Created on Nov 27, 2005
 *
 */

/**
 * @author zepernick
 * 
 */
public class LowLevelParse {

    public static void main(final String[] args) {
        final String data = getDefaultDataFile();
        try {
            call(data);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifier.txt";
    }

    public static void call(final String data) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        final File textFile = new File(data);
        String line = null;
        List elements = null;

        try {
            fr = new FileReader(textFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                // tell the parser to split using a comma delimiter with a "
                // text qualifier. The text qualifier is optional, it can be
                // null
                // or empty
                elements = ParserUtils.splitLine(line, ',', '"', 10, false, false);

                for (int i = 0; i < elements.size(); i++) {
                    System.out.println("Column " + i + ": " + (String) elements.get(i));
                }

                System.out.println("===========================================================================");

            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (final Exception ignore) {
            }
        }

    }
}
