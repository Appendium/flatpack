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

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benoit Xhenseval
 * @author Paul Zepernick
 *
 */
public abstract class AbstractDelimiterParser extends AbstractParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDelimiterParser.class);
    private char delimiter = 0;
    private char qualifier = 0;
    private boolean ignoreFirstRecord = false;

    private int lineCount = 0;

    public AbstractDelimiterParser(final Reader dataSourceReader, final String dataDefinition, final char delimiter, final char qualifier,
            final boolean ignoreFirstRecord) {
        super(dataSourceReader, dataDefinition);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    public AbstractDelimiterParser(final Reader dataSourceReader, final char delimiter, final char qualifier, final boolean ignoreFirstRecord) {
        super(dataSourceReader);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
    }

    @Override
    protected DataSet doParse() {
        try {
            lineCount = 0;
            return doDelimitedFile(getDataSourceReader(), shouldCreateMDFromFile());
        } catch (final IOException e) {
            LOGGER.error("error accessing/creating inputstream", e);
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
    private DataSet doDelimitedFile(final Reader dataSource, final boolean createMDFromFile) throws IOException {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource is null");
        }
        BufferedReader br = null;
        final DefaultDataSet ds = new DefaultDataSet(getPzMetaData(), this);
        try {
            // gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());

            br = new BufferedReader(dataSource);

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
                    setPzMetaData(ParserUtils.getPZMetaDataFromFile(line, delimiter, qualifier, this, isAddSuffixToDuplicateColumnNames()));
                    ds.setMetaData(getPzMetaData());
                    continue;
                }
                // column values
                List<String> columns = ParserUtils.splitLine(line, getDelimiter(), getQualifier(), FPConstants.SPLITLINE_SIZE_INIT,
                        isPreserveLeadingWhitespace(), isPreserveTrailingWhitespace());
                final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getPzMetaData(), columns);
                final List<ColumnMetaData> cmds = ParserUtils.getColumnMetaData(mdkey, getPzMetaData());
                final int columnCount = cmds.size();

                if (columns.size() > columnCount) {
                    // Incorrect record length on line log the error. Line
                    // will not be included in the dataset log the error
                    if (isIgnoreExtraColumns()) {
                        // user has chosen to ignore the fact that we have too many columns in the data from
                        // what the mapping has described. sublist the array to remove un-needed columns
                        columns = columns.subList(0, columnCount);
                        addError(ds, "TRUNCATED LINE TO CORRECT NUMBER OF COLUMNS", lineCount, 1);
                    } else {
                        addError(ds, "TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2,
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
                        addError(ds, "PADDED LINE TO CORRECT NUMBER OF COLUMNS", lineCount, 1);

                    } else {
                        addError(ds, "TOO FEW COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2,
                                isStoreRawDataToDataError() ? line : null);
                        continue;
                    }
                }

                final Row row = new Row();
                row.setMdkey(mdkey.equals(FPConstants.DETAIL_ID) ? null : mdkey); // try
                // to limit the memory use
                row.setCols(columns);
                row.setRowNumber(lineCount);
                if (isFlagEmptyRows()) {
                    // user has elected to have the parser flag rows that are empty
                    row.setEmpty(ParserUtils.isListElementsEmpty(columns));
                }
                if (isStoreRawDataToDataSet()) {
                    // user told the parser to keep a copy of the raw data in the row
                    // WARNING potential for high memory usage here
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

    /**
     * Reads a record from a delimited file.  This will account for records which
     * could span multiple lines.
     * NULL will be returned when the end of the file is reached
     *
     * @param br
     *          Open reader being used to read through the file
     * @param qual
     *          Qualifier being used for parse
     * @param delim
     *          Delimiter being used for parse
     * @return String
     *          Record from delimited file
     *
     */
    protected String fetchNextRecord(final BufferedReader br, final char qual, final char delim) throws IOException {
        String line = null;
        final StringBuilder lineData = new StringBuilder();
        final String linebreak = System.getProperty("line.separator");
        boolean processingMultiLine = false;

        while ((line = br.readLine()) != null) {
            lineCount++;
            final String trimmed = line.trim();
            final int trimmedLen = trimmed.length();
            if (!processingMultiLine && trimmed.length() == 0) {
                // empty line skip past it, as long as it
                // is not part of the multiline
                continue;
            }

            // ********************************************************
            // new functionality as of 2.1.0 check to see if we have
            // any line breaks in the middle of the record, this will only
            // be checked if we have specified a delimiter
            // ********************************************************
            final char[] chrArry = trimmed.toCharArray();
            if (!processingMultiLine && delim > 0 && qual != FPConstants.NO_QUALIFIER) {
                processingMultiLine = ParserUtils.isMultiLine(chrArry, delim, qual);
            }

            // check to see if we have reached the end of the linebreak in
            // the record

            final String trimmedLineData = lineData.toString().trim();
            if (processingMultiLine && trimmedLineData.length() > 0 && trimmedLen > 0) {
                // need to do one last check here. it is possible that the "
                // could be part of the data
                // excel will escape these with another quote; here is some
                // data "" This would indicate
                // there is more to the multiline
                if (trimmed.charAt(trimmed.length() - 1) == qual && !trimmed.endsWith("" + qual + qual)) {
                    // it is safe to assume we have reached the end of the
                    // line break
                    processingMultiLine = false;
                    lineData.append(linebreak).append(line);
                } else {
                    // check to see if this is the last line of the record
                    // looking for a qualifier followed by a delimiter
                    lineData.append(linebreak).append(line);
                    boolean qualiFound = false;
                    for (final char element : chrArry) {
                        if (qualiFound) {
                            if (element == ' ') {
                                continue;
                            } else if (element == delim) {
                                processingMultiLine = ParserUtils.isMultiLine(chrArry, delim, qual);
                                break;
                            }
                            qualiFound = false;
                        } else if (element == qual) {
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
                // need to check to see if we need to insert a line break.
                // The buffered reader excludes the breaks
                lineData.append(trimmedLen == 0 ? linebreak : line);
                if (processingMultiLine) {
                    continue; // if we are working on a multiline rec, get
                    // the data on the next line
                }
            }

            break;
        }

        if (line == null && lineData.length() == 0) {
            // eof
            return null;
        }

        return lineData.toString();

    }
}
