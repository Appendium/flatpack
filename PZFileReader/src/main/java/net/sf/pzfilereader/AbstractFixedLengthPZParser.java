/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

    protected AbstractFixedLengthPZParser(final File dataSource, final String dataDefinition) {
        super(dataSource, dataDefinition);
    }

    protected AbstractFixedLengthPZParser(final File dataSource) {
        super(dataSource);
    }

    protected AbstractFixedLengthPZParser(final InputStream dataSourceStream, final String dataDefinition) {
        super(dataSourceStream, dataDefinition);
    }

    protected AbstractFixedLengthPZParser(final InputStream dataSourceStream) {
        super(dataSourceStream);
    }

    public DataSet doParse() {
        try {
            if (getDataSourceStream() != null) {
                return doFixedLengthFile(getDataSourceStream());
            } else {
                InputStream stream;
                stream = ParserUtils.createInputStream(getDataSource());
                try {
                    return doFixedLengthFile(stream);
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
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
    private DataSet doFixedLengthFile(final InputStream dataSource) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;

        final DefaultDataSet ds = new DefaultDataSet(getColumnMD());

        try {
            //gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());

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
