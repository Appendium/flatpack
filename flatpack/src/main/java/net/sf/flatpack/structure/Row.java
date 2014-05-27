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
package net.sf.flatpack.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * This holds a collection of columns and their values along with the actual
 * row number they appear in the flat file
 *
 * @author Paul Zepernick
 */
public class Row {
    /** List to hold all columns that exist in the row */
    private List<String> cols;

    /** Row number in flat file */
    private int rowNumber;

    /** key to the MD for this row, null will indicate it is "detail" MD */
    private String mdkey;

    private boolean empty;

    private String rawData;

    /**
     * Constructs a new Row
     *
     */
    public Row() {
        cols = new ArrayList<String>();
    }

    /**
     * Adds a column to a row
     *
     * @param colValue -
     *            String value to add to the row
     */
    public void addColumn(final String colValue) {
        cols.add(colValue);
    }

    /**
     * Appends the List of Strings to the existing columns in the row
     *
     * @param columns -
     *            List of Strings to append to the row
     */
    public void addColumn(final List<String> columns) {
        if (cols == null) {
            cols = new ArrayList<String>();
        }
        cols.addAll(columns);
    }

    /**
     * Returns the value of a column for a specified column name
     *
     * @param colPosition -
     *            int position of the column in the array
     * @return String value of column
     */
    public String getValue(final int colPosition) {
        return cols.get(colPosition);
    }

    /**
     * Set the value of a column for a specified column name
     *
     * @param columnIndex -
     *            column number to change
     * @param value -
     *            String column value
     */
    public void setValue(final int columnIndex, final String value) {
        cols.set(columnIndex, value);
    }

    /**
     * Returns the rowNumber.
     *
     * @return int
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * Sets the rowNumber.
     *
     * @param rowNumber
     *            The rowNumber to set
     */
    public void setRowNumber(final int rowNumber) {
        this.rowNumber = rowNumber;
    }

    /**
     * Returns the cols for the row.
     *
     * @return Vector
     */
    public List<String> getCols() {
        return cols;
    }

    /**
     * Set the columns for the row.
     *
     * @param cols -
     *            Vector of Strings
     */
    public void setCols(final List<String> cols) {
        this.cols = cols;
    }

    /**
     * @return Returns the mdkey.
     */
    public String getMdkey() {
        return mdkey;
    }

    /**
     * @param mdkey
     *            The mdkey to set.
     */
    public void setMdkey(final String mdkey) {
        this.mdkey = mdkey;
    }

    /**
     * All columns in the row are empty
     *
     * @return the empty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * When true, all columns in the row are empty
     *
     * @param empty the empty to set
     */
    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }

    /**
     * Raw data used to create the columns for the row
     *
     * @return the rawData
     */
    public String getRawData() {
        return rawData;
    }

    /**
     * Raw data used to create the columns for the row
     *
     * @param rawData the rawData to set
     */
    public void setRawData(final String rawData) {
        this.rawData = rawData;
    }
}