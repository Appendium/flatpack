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
package net.sf.flatpack.brparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultDataSet;
import net.sf.flatpack.DelimiterParser;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffReaderDelimParser extends DelimiterParser implements InterfaceBuffReaderParse{
    private BufferedReader br;

    private boolean processedFirst = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(BuffReaderDelimParser.class);

    public BuffReaderDelimParser(final File pzmapXML, final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(pzmapXML, dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    public BuffReaderDelimParser(final InputStream pzmapXMLStream, final InputStream dataSourceStream, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(pzmapXMLStream, dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    public BuffReaderDelimParser(final File dataSource, final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSource, delimiter, qualifier, ignoreFirstRecord);
    }

    public BuffReaderDelimParser(final InputStream dataSourceStream, final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    public BuffReaderDelimParser(final Reader pzmapXML, final Reader dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSource, pzmapXML, delimiter, qualifier, ignoreFirstRecord);
    }

    public BuffReaderDelimParser(final Reader dataSourceStream, final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceStream, delimiter, qualifier, ignoreFirstRecord);
    }

    protected DataSet doParse() {
        //        final DataSet ds = new BuffReaderPZDataSet(getColumnMD(), this);
        final DataSet ds = new BuffReaderDataSet(getPzMetaData(), this);
        try {
            //gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());

            br = new BufferedReader(getDataSourceReader());

            return ds;

        } catch (final IOException ex) {
            LOGGER.error("error accessing/creating inputstream", ex);
        }

        return null;
    }

    /**
     * Reads in the next record on the file and return a row
     *
     * @param ds
     * @return Row
     * @throws IOException
     */
    public Row buildRow(final DefaultDataSet ds)  {
        /** loop through each line in the file */
        while (true) {
            String line;
			try {
				line = fetchNextRecord(br, getQualifier(), getDelimiter());
			} catch (IOException e) {
				throw new RuntimeException("Error Fetching Record From File...", e);
			}

            if (line == null) {
                return null;
            }

            // check to see if the user has elected to skip the first record
            if (!processedFirst && isIgnoreFirstRecord()) {
                processedFirst = true;
                continue;
            } else if (!processedFirst && shouldCreateMDFromFile()) {
                processedFirst = true;
                setPzMetaData(ParserUtils.getPZMetaDataFromFile(line, getDelimiter(), getQualifier(), this));
                //                setColumnMD(ParserUtils.getColumnMDFromFile(line, getDelimiter(), getQualifier(), this));
                continue;
            }

            //TODO
            //seems like we may want to try doing something like this.  I have my reservations because
            //it is possible that we don't get a "detail" id and this might generate NPE
            //is it going to create too much overhead to do a null check here as well???
            //final int intialSize =  ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, getColumnMD()).size();
            // column values
            List columns = ParserUtils.splitLine(line, getDelimiter(), getQualifier(), FPConstants.SPLITLINE_SIZE_INIT);
            //            final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getColumnMD(), columns);
            final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getPzMetaData(), columns);
            //            final List cmds = ParserUtils.getColumnMetaData(mdkey, getColumnMD());
            final List cmds = ParserUtils.getColumnMetaData(mdkey, getPzMetaData());
            final int columnCount = cmds.size();
            // DEBUG

            // Incorrect record length on line log the error. Line
            // will not be included in the dataset
            if (columns.size() > columnCount) {
                if (isIgnoreExtraColumns()) {
                    //user has choosen to ignore the fact that we have too many columns in the data from
                    //what the mapping has described.  sublist the array to remove un-needed columns
                    columns = columns.subList(0, columnCount);
                    addError(ds, "TRUNCATED LINE TO CORRECT NUMBER OF COLUMNS", getLineCount(), 1);
                } else {
                    //log the error
                    addError(ds, "TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), getLineCount(), 2, 
                            isStoreRawDataToDataError() ? line : null);
                    continue;
                }
            } else if (columns.size() < columnCount) {
                if (isHandlingShortLines()) {
                    // We can pad this line out
                    while (columns.size() < columnCount) {
                        columns.add("");
                    }

                    // log a warning
                    addError(ds, "PADDED LINE TO CORRECT NUMBER OF COLUMNS", getLineCount(), 1);

                } else {
                    addError(ds, "TOO FEW COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), getLineCount(), 2, 
                            isStoreRawDataToDataError() ? line : null);
                    continue;
                }
            }

            final Row row = new Row();
            row.setMdkey(mdkey.equals(FPConstants.DETAIL_ID) ? null : mdkey); // try
            // to limit the memory use
            row.setCols(columns);
            row.setRowNumber(getLineCount());

            if (isFlagEmptyRows()) {
                //user has elected to have the parser flag rows that are empty
                row.setEmpty(ParserUtils.isListElementsEmpty(columns));
            }
            if (isStoreRawDataToDataSet()) {
                //user told the parser to keep a copy of the raw data in the row
                //WARNING potential for high memory usage here
                row.setRawData(line);
            }   
            
            return row;
        }
    }

    /**
     * Closes out the file readers
     *
     *@throws IOException
     */
    public void close() throws IOException {
        if (br != null) {
            br.close();
            br = null;
        }
    }

    //try to clean up the file handles automatically if
    //the close was not called
    protected void finalize() throws Throwable {
        try {
            close();
        } catch (final IOException ex) {
            LOGGER.warn("Problem trying to auto close file handles...", ex);
        } finally {
            super.finalize();
        }
    }
}
