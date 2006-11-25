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
import java.net.URL;
import java.util.List;
import java.util.Properties;

import net.sf.pzfilereader.structure.Row;
import net.sf.pzfilereader.util.PZConstants;
import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author xhensevb
 * 
 */
public abstract class AbstractDelimiterPZParser extends AbstractPZParser {
    private char delimiter = 0;

    private char qualifier = 0;

    private boolean ignoreFirstRecord = false;

    public AbstractDelimiterPZParser(final InputStream dataSourceStream, final String dataDefinition, final char delimiter,
            final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceStream, dataDefinition);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    public AbstractDelimiterPZParser(final File dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSource);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    public AbstractDelimiterPZParser(final InputStream dataSourceStream, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSourceStream);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    public IDataSet doParse() {
        try {
            if (getDataSourceStream() != null) {
                return doDelimitedFile(getDataSourceStream(), getDelimiter(), getQualifier(), isIgnoreFirstRecord(),
                        shouldCreateMDFromFile());
            } else {
                InputStream stream = null;
                try {
                    stream = ParserUtils.createInputStream(getDataSource());
                    return doDelimitedFile(stream, getDelimiter(), getQualifier(), isIgnoreFirstRecord(),
                            shouldCreateMDFromFile());
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

    protected abstract boolean shouldCreateMDFromFile();

    protected char getDelimiter() {
        return delimiter;
    }

    protected void setDelimiter(final char delimiter) {
        this.delimiter = delimiter;
    }

    protected boolean isIgnoreFirstRecord() {
        return ignoreFirstRecord;
    }

    protected void setIgnoreFirstRecord(final boolean ignoreFirstRecord) {
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    protected char getQualifier() {
        return qualifier;
    }

    protected void setQualifier(final char qualifier) {
        this.qualifier = qualifier;
    }

    /*
     * This is the new version of doDelimitedFile using InputStrem instead of
     * File. This is more flexible especially it is working with WebStart.
     * 
     * puts together the dataset for a DELIMITED file. This is used for PZ XML
     * mappings, and SQL table mappings
     */
    private IDataSet doDelimitedFile(final InputStream dataSource, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord, final boolean createMDFromFile) throws IOException, Exception {
        if (dataSource == null) {
            throw new NullPointerException("dataSource is null");
        }

        InputStreamReader isr = null;
        BufferedReader br = null;
        final DefaultDataSet ds = new DefaultDataSet(getColumnMD());
        try {
            //gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());
            
            // get the total column count
            // columnCount = columnMD.size();

            /** Read in the flat file */
            // fr = new FileReader(dataSource.getAbsolutePath());
            isr = new InputStreamReader(dataSource);
            br = new BufferedReader(isr);

            boolean processedFirst = false;
            boolean processingMultiLine = false;
            int lineCount = 0;
            String lineData = "";
            /** loop through each line in the file */
            String line = null;
            while ((line = br.readLine()) != null) {
                lineCount++;
                /** empty line skip past it */
                final String trimmed = line.trim();
                if (!processingMultiLine && trimmed.length() == 0) {
                    continue;
                }

                // check to see if the user has elected to skip the first record
                if (!processedFirst && ignoreFirstRecord) {
                    processedFirst = true;
                    continue;
                } else if (!processedFirst && createMDFromFile) {
                    processedFirst = true;
                    setColumnMD(ParserUtils.getColumnMDFromFile(line, delimiter, qualifier));
                    ds.setColumnMD(getColumnMD());
                    continue;
                }

                // ********************************************************
                // new functionality as of 2.1.0 check to see if we have
                // any line breaks in the middle of the record, this will only
                // be checked if we have specified a delimiter
                // ********************************************************
                final char[] chrArry = trimmed.toCharArray();
                if (!processingMultiLine && delimiter > 0) {
                    processingMultiLine = ParserUtils.isMultiLine(chrArry, delimiter, qualifier);
                }

                // check to see if we have reached the end of the linebreak in
                // the record

                final String trimmedLineData = lineData.trim();
                if (processingMultiLine && trimmedLineData.length() > 0) {
                    // need to do one last check here. it is possible that the "
                    // could be part of the data
                    // excel will escape these with another quote; here is some
                    // data "" This would indicate
                    // there is more to the multiline
                    if (trimmed.charAt(trimmed.length() - 1) == qualifier && !trimmed.endsWith("" + qualifier + qualifier)) {
                        // it is safe to assume we have reached the end of the
                        // line break
                        processingMultiLine = false;
                        if (trimmedLineData.length() > 0) { // + would always be
                            // true surely....
                            lineData += "\r\n";
                        }
                        lineData += line;
                    } else {
                        // check to see if this is the last line of the record
                        // looking for a qualifier followed by a delimiter
                        if (trimmedLineData.length() > 0) { // + here again,
                            // this should
                            // always be true...
                            lineData += "\r\n";
                        }
                        lineData += line;
                        boolean qualiFound = false;
                        for (int i = 0; i < chrArry.length; i++) {
                            if (qualiFound) {
                                if (chrArry[i] == ' ') {
                                    continue;
                                } else {
                                    // not a space, if this char is the
                                    // delimiter, then we have reached the end
                                    // of
                                    // the record
                                    if (chrArry[i] == delimiter) {
                                        // processingMultiLine = false;
                                        // fix put in, setting to false caused
                                        // bug when processing multiple
                                        // multi-line
                                        // columns on the same record
                                        processingMultiLine = ParserUtils.isMultiLine(chrArry, delimiter, qualifier);
                                        break;
                                    }
                                    qualiFound = false;
                                    continue;
                                }
                            } else if (chrArry[i] == qualifier) {
                                qualiFound = true;
                            }
                        }
                        // check to see if we are still in multi line mode, if
                        // so grab the next line
                        if (processingMultiLine) {
                            continue;
                        }
                    }
                } else {
                    // throw the line into lineData var.
                    lineData += line;
                    if (processingMultiLine) {
                        continue; // if we are working on a multiline rec, get
                        // the data on the next line
                    }
                }
                // ********************************************************************
                // end record line break logic
                // ********************************************************************

                //TODO
                //seems like we may want to try doing something like this.  I have my reservations because
                //it is possible that we don't get a "detail" id and this might generate NPE
                //is it going to create too much overhead to do a null check here as well???
                //final int intialSize =  ParserUtils.getColumnMetaData(PZConstants.DETAIL_ID, getColumnMD()).size();
                // column values
                final List columns = ParserUtils.splitLine(lineData, delimiter, qualifier, PZConstants.SPLITLINE_SIZE_INIT);
                lineData = "";
                final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getColumnMD(), columns);
                final List cmds = ParserUtils.getColumnMetaData(mdkey, getColumnMD());
                final int columnCount = cmds.size();
                // DEBUG

                // Incorrect record length on line log the error. Line
                // will not be included in the dataset
                if (columns.size() > columnCount) {
                    // log the error
                    addError(ds, "TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                    continue;
                } else if (columns.size() < columnCount) {
                    if (isHandlingShortLines()) {
                        // We can pad this line out
                        while (columns.size() < columnCount) {
                            columns.add("");
                        }

                        // log a warning
                        addError(ds, "PADDED LINE TO CORRECT NUMBER OF COLUMNS", lineCount, 1);

                    } else {
                        addError(ds, "TOO FEW COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                        continue;
                    }
                }

                final Row row = new Row();
                row.setMdkey(mdkey.equals(PZConstants.DETAIL_ID) ? null : mdkey); // try
                // to limit the memory use
                row.setCols(columns);
                row.setRowNumber(lineCount);
                /** add the row to the array */
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
