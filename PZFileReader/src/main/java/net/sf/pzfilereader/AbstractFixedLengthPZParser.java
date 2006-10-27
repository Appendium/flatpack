/**
 * 
 */
package net.sf.pzfilereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import net.sf.pzfilereader.structure.Row;
import net.sf.pzfilereader.util.FixedWidthParserUtils;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author xhensevb
 * 
 */
public abstract class AbstractFixedLengthPZParser extends AbstractPZParser {

    protected AbstractFixedLengthPZParser(File dataSource, String dataDefinition) {
        super(dataSource, dataDefinition);
    }

    protected AbstractFixedLengthPZParser(File dataSource) {
        super(dataSource);
    }

    protected AbstractFixedLengthPZParser(InputStream dataSourceStream, String dataDefinition) {
        super(dataSourceStream, dataDefinition);
    }

    protected AbstractFixedLengthPZParser(InputStream dataSourceStream) {
        super(dataSourceStream);
    }

    public IDataSet doParse() {
        try {
            if (getDataSourceStream() != null) {
                return doFixedLengthFile(getDataSourceStream());
            } else {
                InputStream stream;
                stream = ParserUtils.createInputStream(getDataSource());
                try {
                    return doFixedLengthFile(stream);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * This is the new version of doDelimitedFile using InputStrem instead of
     * File. This is more flexible especially it is working with WebStart.
     * 
     * puts together the dataset for fixed length file. This is used for PZ XML
     * mappings, and SQL table mappings
     */
    private IDataSet doFixedLengthFile(final InputStream dataSource) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;

        DefaultDataSet ds = new DefaultDataSet(getColumnMD());

        try {
            final Map recordLengths = ParserUtils.calculateRecordLengths(getColumnMD());

            // Read in the flat file
            isr = new InputStreamReader(dataSource);
            br = new BufferedReader(isr);
            String line = null;
            int lineCount = 0;
            // map of record lengths corrisponding to the ID's in the columnMD
            // array
            // loop through each line in the file
            while ((line = br.readLine()) != null) {
                lineCount++;
                // empty line skip past it
                if (line.trim().length() == 0) {
                    continue;
                }

                final String mdkey = FixedWidthParserUtils.getCMDKey(getColumnMD(), line);
                final int recordLength = ((Integer) recordLengths.get(mdkey)).intValue();

                // Incorrect record length on line log the error. Line will not
                // be included in the
                // dataset
                if (line.length() > recordLength) {
                    addError(ds, "LINE TOO LONG. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount, 2);
                    continue;
                } else if (line.length() < recordLength) {
                    if (isHandlingShortLines()) {
                        // We can pad this line out
                        line += ParserUtils.padding(recordLength - line.length(), ' ');

                        // log a warning
                        addError(ds, "PADDED LINE TO CORRECT RECORD LENGTH", lineCount, 1);

                    } else {
                        addError(ds, "LINE TOO SHORT. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount,
                                2);
                        continue;
                    }
                }

                // int recPosition = 1;
                final Row row = new Row();
                row.setMdkey(mdkey.equals(PZConstants.DETAIL_ID) ? null : mdkey); // try

                final List cmds = ParserUtils.getColumnMetaData(mdkey, getColumnMD());
                row.addColumn(FixedWidthParserUtils.splitFixedText(cmds, line));
                // to limit the memory use
                // Build the columns for the row
                // for (int i = 0; i < cmds.size(); i++) {
                // final String tempValue = line.substring(recPosition - 1,
                // recPosition
                // + (((ColumnMetaData) cmds.get(i)).getColLength() - 1));
                // recPosition += ((ColumnMetaData) cmds.get(i)).getColLength();
                // row.addColumn(tempValue.trim());
                // }
                row.setRowNumber(lineCount);
                // add the row to the array
                ds.addRow(row);
            }
        } finally {
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return ds;
    }
}
