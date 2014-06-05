/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting FlatPack.
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
package net.sf.flatpack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.FixedWidthParserUtils;
import net.sf.flatpack.util.ParserUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xhensevb
 *
 */
public abstract class AbstractFixedLengthParser extends AbstractParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFixedLengthParser.class);

    protected AbstractFixedLengthParser(final Reader dataSourceReader, final String dataDefinition) {
        super(dataSourceReader, dataDefinition);
    }

    protected AbstractFixedLengthParser(final Reader dataSourceReader) {
        super(dataSourceReader);
    }

    protected DataSet doParse() {
        try {
            return doFixedLengthFile(getDataSourceReader());
        } catch (final IOException e) {
            LOGGER.error("error accessing/reading data", e);
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
    private DataSet doFixedLengthFile(final Reader dataSource) throws IOException {
        BufferedReader br = null;

        final DefaultDataSet ds = new DefaultDataSet(getPzMetaData(), this);

        try {
            //gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());

            final Map recordLengths = ParserUtils.calculateRecordLengths(getPzMetaData());

            // Read in the flat file
            br = new BufferedReader(dataSource);
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

                final String mdkey = FixedWidthParserUtils.getCMDKey(getPzMetaData(), line);
                final int recordLength = ((Integer) recordLengths.get(mdkey)).intValue();

                if (line.length() > recordLength) {
                    // Incorrect record length on line log the error. Line will not
                    // be included in the
                    // dataset
                    if (isIgnoreExtraColumns()) {
                        //user has choosen to ignore the fact that we have too many bytes in the fixed
                        //width file.  Truncate the line to the correct length
                        line = line.substring(0, recordLength);
                        addError(ds, "TRUNCATED LINE TO CORRECT LENGTH", lineCount, 1);
                    } else {
                        addError(ds, "LINE TOO LONG. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount, 2, 
                                isStoreRawDataToDataError() ? line : null);
                        continue;
                    }
                } else if (line.length() < recordLength) {
                    if (isHandlingShortLines()) {
                        // We can pad this line out
                        line += ParserUtils.padding(recordLength - line.length(), ' ');

                        // log a warning
                        addError(ds, "PADDED LINE TO CORRECT RECORD LENGTH", lineCount, 1);

                    } else {
                        addError(ds, "LINE TOO SHORT. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount, 2, 
                                isStoreRawDataToDataError() ? line : null);
                        continue;
                    }
                }

                // int recPosition = 1;
                final Row row = new Row();
                row.setMdkey(mdkey.equals(FPConstants.DETAIL_ID) ? null : mdkey); // try

                final List<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(mdkey, getPzMetaData());
                row.addColumn(FixedWidthParserUtils.splitFixedText(cmds, line, isPreserveLeadingWhitespace(), isPreserveTrailingWhitespace()));
                row.setRowNumber(lineCount);
                
                if (isFlagEmptyRows()) {
                    //user has elected to have the parser flag rows that are empty
                    row.setEmpty(ParserUtils.isListElementsEmpty(row.getCols()));
                }
                if (isStoreRawDataToDataSet()) {
                    //user told the parser to keep a copy of the raw data in the row
                    //WARNING potential for high memory usage here
                    row.setRawData(line);
                }   
                
                // add the row to the array
                ds.addRow(row);
            }
        } finally {
            if (br != null) {
                br.close();
            }
            closeReaders();
        }
        return ds;
    }
}
