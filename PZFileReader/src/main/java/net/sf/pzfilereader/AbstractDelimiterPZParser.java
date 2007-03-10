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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private int lineCount = 0;
   
    private final Logger logger = LoggerFactory.getLogger(AbstractDelimiterPZParser.class);
    
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

    public DataSet doParse() {
        try {
            lineCount = 0;
            if (getDataSourceStream() != null) {
                return doDelimitedFile(getDataSourceStream(),  shouldCreateMDFromFile());
            } else {
                InputStream stream = null;
                try {
                    stream = ParserUtils.createInputStream(getDataSource());
                    return doDelimitedFile(stream, shouldCreateMDFromFile());
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
        } catch (final IOException e) {
            logger.error("error accessing/creating inputstream", e);
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

    protected int getLineCount() {
        return lineCount;
    }
    
    /*
     * This is the new version of doDelimitedFile using InputStrem instead of
     * File. This is more flexible especially it is working with WebStart.
     * 
     * puts together the dataset for a DELIMITED file. This is used for PZ XML
     * mappings, and SQL table mappings
     */
    private DataSet doDelimitedFile(final InputStream dataSource, final boolean createMDFromFile) throws IOException {
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

            isr = new InputStreamReader(dataSource);
            br = new BufferedReader(isr);

            boolean processedFirst = false;
            /** loop through each line in the file */
            String line = null;
            while ((line = fetchNextRecord(br, getQualifier(), getDelimiter())) != null) {
                // check to see if the user has elected to skip the first record
                if (!processedFirst && isIgnoreFirstRecord()) {
                    processedFirst = true;
                    continue;
                } else if (!processedFirst && createMDFromFile) {
                    processedFirst = true;
                    setColumnMD(ParserUtils.getColumnMDFromFile(line, delimiter, qualifier));
                    ds.setColumnMD(getColumnMD());
                    continue;
                }

                // column values
                List columns = ParserUtils.splitLine(line, getDelimiter(), getQualifier(), PZConstants.SPLITLINE_SIZE_INIT);
                final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getColumnMD(), columns);
                final List cmds = ParserUtils.getColumnMetaData(mdkey, getColumnMD());
                final int columnCount = cmds.size();
                                
                if (columns.size() > columnCount) {
                    // Incorrect record length on line log the error. Line
                    // will not be included in the dataset log the error
                    if (isIgnoreExtraColumns()) {
                        //user has choosen to ignore the fact that we have too many columns in the data from
                        //what the mapping has described.  sublist the array to remove un-needed columns
                        columns = columns.subList(0, columnCount);
                        addError(ds, "TRUNCATED LINE TO CORRECT NUMBER OF COLUMNS", lineCount, 1);
                    } else {
                        addError(ds, "TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                        continue;
                    }
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
    
    /**
     * Reads a record from a delimited file.  This will account for records which
     * could span multiple lines.  
     * NULL will be returned when the end of the file is reached
     * 
     * @param br
     *          Open reader being used to read through the file
     * @param qual
     *          Qualifier being used for parse
     * @parma delim
     *          Delimiter being used for parse
     * @return String
     *          Record from delimited file
     *          
     */
    protected String fetchNextRecord(final BufferedReader br, final char qual,
            final char delim) throws IOException{
        String line = null;
        StringBuffer lineData = new StringBuffer();
        boolean processingMultiLine = false;
        
        while ((line = br.readLine()) != null) {
            lineCount++;
            final String trimmed = line.trim();
            if (!processingMultiLine && trimmed.length() == 0) {
                //empty line skip past it, as long as it 
                //is not part of the multiline
                continue;
            }

            // ********************************************************
            // new functionality as of 2.1.0 check to see if we have
            // any line breaks in the middle of the record, this will only
            // be checked if we have specified a delimiter
            // ********************************************************
            final char[] chrArry = trimmed.toCharArray();
            if (!processingMultiLine && delim > 0) {
                processingMultiLine = ParserUtils.isMultiLine(chrArry, delim, qual);
            }

            // check to see if we have reached the end of the linebreak in
            // the record

            final String trimmedLineData = lineData.toString().trim();
            if (processingMultiLine && trimmedLineData.length() > 0) {
                // need to do one last check here. it is possible that the "
                // could be part of the data
                // excel will escape these with another quote; here is some
                // data "" This would indicate
                // there is more to the multiline
                if (trimmed.charAt(trimmed.length() - 1) == qual && !trimmed.endsWith("" + qual + qual)) {
                    // it is safe to assume we have reached the end of the
                    // line break
                    processingMultiLine = false;
                    lineData.append("\r\n").append(line);
                } else {
                    // check to see if this is the last line of the record
                    // looking for a qualifier followed by a delimiter
                    lineData.append("\r\n").append(line);
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
                                if (chrArry[i] == delim) {
                                    // processingMultiLine = false;
                                    // fix put in, setting to false caused
                                    // bug when processing multiple
                                    // multi-line
                                    // columns on the same record
                                    processingMultiLine = ParserUtils.isMultiLine(chrArry, delim, qual);
                                    break;
                                }
                                qualiFound = false;
                                continue;
                            }
                        } else if (chrArry[i] == qual) {
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
                lineData.append(line);
                if (processingMultiLine) {
                    continue; // if we are working on a multiline rec, get
                    // the data on the next line
                }
            }
            
            break;
        }
        
        if (line == null && lineData.length() == 0) {
            //eof
            return null;
        }
        
        return lineData.toString();
    }
}
