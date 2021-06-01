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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.structure.Row;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

/**
 * @author Benoit Xhenseval
 * @author Paul Zepernick
 *
 */
public abstract class AbstractDelimiterParser extends AbstractParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDelimiterParser.class);
    private static final String LINE_BREAK = System.lineSeparator();

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
     * This is the new version of doDelimitedFile using InputStream instead of
     * File. This is more flexible especially it is working with WebStart.
     *
     * puts together the dataset for a DELIMITED file. This is used for PZ XML
     * mappings, and SQL table mappings
     */
    private DataSet doDelimitedFile(final Reader dataSource, final boolean createMDFromFile) throws IOException {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource is null");
        }
        final DefaultDataSet ds = new DefaultDataSet(getPzMetaData(), this);
        try (BufferedReader br = new BufferedReader(dataSource)) {
            // gather the conversion properties
            ds.setPZConvertProps(ParserUtils.loadConvertProperties());

            boolean processedFirst = false;
            /** loop through each line in the file */
            String line = null;
            int estimatedColCount = FPConstants.SPLITLINE_SIZE_INIT;
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

                // check number of Qualifier, if ODD number --> Incorrect!!!
                if (oddNumberOfQualifier(line, getQualifier())) {
                    addError(ds, "Odd number of Qualifier characters", lineCount, 1, isStoreRawDataToDataError() ? line : null);
                    continue;
                }

                List<String> columns = ParserUtils.splitLine(line, getDelimiter(), getQualifier(), estimatedColCount, isPreserveLeadingWhitespace(),
                        isPreserveTrailingWhitespace());
                final String mdkey = ParserUtils.getCMDKeyForDelimitedFile(getPzMetaData(), columns);
                final List<ColumnMetaData> metaData = ParserUtils.getColumnMetaData(mdkey, getPzMetaData());
                final int columnCount = metaData.size();
                estimatedColCount = columnCount;

                if (columns.size() > columnCount) {
                    // Incorrect record length on line log the error. Line
                    // will not be included in the dataset log the error
                    if (isIgnoreExtraColumns()) {
                        // user has chosen to ignore the fact that we have too many columns in the data from
                        // what the mapping has described. sublist the array to remove un-needed columns
                        columns = columns.subList(0, columnCount);
                        addError(ds, "Flatpack truncated line to correct number of columns", lineCount, 1, isStoreRawDataToDataError() ? line : null);
                    } else {
                        addError(ds, "Too many columns expected: " + columnCount + " Flatpack got: " + columns.size(), lineCount, 2,
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
                        addError(ds, "Flatpack padded line to correct number of columns", lineCount, 1, isStoreRawDataToDataError() ? line : null);

                    } else {
                        addError(ds, "Too few columns expected: " + columnCount + " only got: " + columns.size(), lineCount, 2,
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
            closeReaders();
        }
        return ds;
    }

    private boolean oddNumberOfQualifier(final String line, final char q) {
        if (line == null || line.isEmpty()) {
            return false;
        }
        int count = 0;
        int idx = 0;
        while ((idx = line.indexOf(q, idx)) != -1) {
            count++;
            idx++;
        }

        return count % 2 != 0;
    }

    /**
     * Reads a record from a delimited file.  This will account for records which
     * could span multiple lines.
     * NULL will be returned when the end of the file is reached
     *
     * @param aContentReader
     *          Open reader being used to read through the file
     * @param aQualifier
     *          Qualifier being used for parse
     * @param aDelimiter
     *          Delimiter being used for parse
     * @return String
     *          Record from delimited file
     * @throws IOException if any problem with the stream of data (e.g. file reader)
     *
     * Improved version of line fetching that solves some of the issues of flatpack parser.
     */
    protected String fetchNextRecord(BufferedReader aContentReader, char aQualifier, char aDelimiter) throws IOException
    {
        if (aQualifier == FPConstants.NO_QUALIFIER)
        {
            // no qualifier defined, then there can't be line breaks in the line
            return aContentReader.readLine();
        }

        StringBuilder lineData = null;
        String line = null;
        boolean multiline = false;

        // consuming lines until we find end of the data row
        while ((line = aContentReader.readLine()) != null)
        {
            if(lineData == null)
            {
                lineData = new StringBuilder(line);
            }
            else
            {
                lineData.append(LINE_BREAK).append(line);
            }

            multiline = isMultiline(line.toCharArray(), multiline, aQualifier, aDelimiter);
            if(! multiline)
            {
                // data row ended
                break;
            }
        }

        if(lineData != null)
        {
            lineCount++;

            String result = lineData.toString();
            // no line break character at the end of data row
            return result.endsWith(LINE_BREAK) ? result.substring(0, result.length() - LINE_BREAK.length()) : result;
        }

        return null;
    }

    /**
     * Checks if we need to consume one more line because data row was splitted to multiple lines.
     * @param aСhrArray
     * @param aMultiline
     * @param aQualifier
     * @param aDelimiter
     * @return
     */
    protected boolean isMultiline(char[] aСhrArray, boolean aMultiline, char aQualifier, char aDelimiter)
    {
        // do not trim the line, according to rfc4180:
        // Spaces are considered part of a field and should not be ignored
        int position = 0;

        do
        {
            // field processing here
            if (! aMultiline && aСhrArray[position] == aDelimiter)
            {
                // empty field
                position++;
            }
            else if (!aMultiline && aСhrArray[position] != aQualifier)
            {
                // if the first char of the line is NOT a qualifier, then the field should not
                // contain CRLF, double quotes, and commas
                // therefore find the end of the field by looking for the first delimiter

                while (++position < aСhrArray.length)
                {
                    if (aСhrArray[position] == aDelimiter)
                    {
                        position++;
                        break;
                    }
                }

                if (position >= aСhrArray.length)
                {
                    // end of the line without any delimiters so it's safe to say its the end of the line
                    // and not multiline
                    return false;
                }
            }
            else
            {
                // the first char is a qualifier, the field may contain CRLF, double quotes, and commas
                // double quotes must be escaped with a double quote (i.e. "some ""data"" here").
                // newline won't be present in the line because it's removed by the reader during
                // readLine() call. so look for dangling "

                aMultiline = true;
                if(aСhrArray[position] == aQualifier)
                {
                    // if we have just now found a qualifier we need to pome cursor to the next char
                    position++;
                }

                // looking for the end of the text field
                while(position < aСhrArray.length)
                {
                    if(aСhrArray[position] == aQualifier)
                    {
                        if(position == (aСhrArray.length - 1) || aСhrArray[position + 1] != aQualifier)
                        {
                            // end of text found
                            position++;
                            aMultiline = false;
                            break;
                        }
                        else
                        {
                            // skipping escaped qualified like ""
                            position += 2;
                        }
                    }
                    else
                    {
                        position++;
                    }
                }
            }
        }
        while( position < aСhrArray.length - 1 );

        return aMultiline;
    }
}
