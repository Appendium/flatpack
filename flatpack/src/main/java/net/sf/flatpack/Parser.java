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

/**
 * PZParser is ready to parse the data and return an object that can then be
 * traversed. The default parser should NOT handle short lines, the user can
 * change it prior to calling parse.
 *
 * @author Benoit Xhenseval
 */
public interface Parser {

    /**
     * Start the parsing.  Will return "null" if the
     * parse fails and the DataSet cannot be created
     *
     * @return the data set resulting from parsing
     */
    DataSet parse();

    /**
     * @return true, lines with less columns then the amount of column headers
     *         will be added as empty's instead of producing an error
     */
    boolean isHandlingShortLines();

    /**
     * @param handleShortLines -
     *            when flaged as true, lines with less columns then the amount
     *            of column headers will be added as empty's instead of
     *            producing an error
     */
    void setHandlingShortLines(final boolean handleShortLines);

    /**
     *
     * @return true, detail lines with a length or column count > the mapping
     *         definition will be truncated and the reader will NOT register these
     *         lines as erros in the DataError collection.
     */
    boolean isIgnoreExtraColumns();

    /**
     *
     * @param ignoreExtraColumns when true, detail lines with a length or column
     *         count > the mapping definition will be truncated and the reader
     *         will NOT register these lines as erros in the DataError collection.
     */
    void setIgnoreExtraColumns(final boolean ignoreExtraColumns);

    /**
     * Default is false
     *
     * @return true, column names will have to be an exact match when retrieving the value of a column.
     *         Example when true: Column name = AColumnName ; getString("acolumnname") would fail
     *         Example when false: Column name = AColumnName ; getString("acolumnname") would pass
     */
    boolean isColumnNamesCaseSensitive();

    /**
     * Default is false
     *
     * @param columnNamesCaseSensitive when true, column names will have to be an exact match when retrieving
     *         the value of a column.
     *         Example when true: Column name = AColumnName ; getString("acolumnname") would fail
     *         Example when false: Column name = AColumnName ; getString("acolumnname") would pass
     */
    void setColumnNamesCaseSensitive(final boolean columnNamesCaseSensitive);

    /**
     * Default is false
     *
     * @return true, warnings encountered durring parsing will not be included in the DataSet errors
     */
    boolean isIgnoreParseWarnings();

    /**
     *
     * @param ignoreParseWarnings when true, warnings encountered durring parsing will not be included
     *                            in the DataSet errors
     */
    void setIgnoreParseWarnings(final boolean ignoreParseWarnings);

    /**
     *
     * @return true, empty Strings will get returned as NULL when calling DataSet.getString()
     */
    boolean isNullEmptyStrings();

    /**
     *
     * @param nullEmptyStrings when true, empty Strings will get returned as NULL when calling DataSet.getString()
     */
    void setNullEmptyStrings(final boolean nullEmptyStrings);
}
