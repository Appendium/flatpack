package net.sf.flatpack.examples.lowlevelparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(LowLevelParse.class);

    public static void main(final String[] args) {
        final String data = getDefaultDataFile();
        try {
            call(data);
        } catch (final Exception e) {
            LOG.error("issue", e);
        }
    }

    public static String getDefaultDataFile() {
        return "PEOPLE-CommaDelimitedWithQualifier.txt";
    }

    public static void call(final String data) throws Exception {
        final File textFile = new File(data);
        String line = null;
        List elements = null;

        try (FileReader fr = new FileReader(textFile); BufferedReader br = new BufferedReader(fr)) {

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
            LOG.error("issue", ex);
        }

    }
}
