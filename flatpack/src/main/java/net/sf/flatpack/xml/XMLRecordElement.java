/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */
package net.sf.flatpack.xml;

import java.util.List;
import java.util.Map;

import net.sf.flatpack.Parser;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.ParserUtils;

/**
 * @author Paul Zepernick
 *
 * Definition for a &lt;RECORD&gt; element in a pzmap file
 */
public class XMLRecordElement {
    private int startPosition = 0;
    private int endPositition = 0;
    private int elementNumber = 0;
    private int elementCount = 0;
    private String indicator;
    private List<ColumnMetaData> columns;
    private Map<String, Integer> columnIndex;

    /**
     * @return Returns the elementNumber.
     */
    public int getElementNumber() {
        return elementNumber;
    }

    /**
     * @param elementNumber
     *            The elementNumber to set.
     */
    public void setElementNumber(final int elementNumber) {
        this.elementNumber = elementNumber;
    }

    /**
     * @return Returns the endPostition.
     */
    public int getEndPositition() {
        return endPositition;
    }

    /**
     * @param endPositition
     *            The endPositition to set.
     */
    public void setEndPositition(final int endPositition) {
        this.endPositition = endPositition;
    }

    /**
     * @return Returns the indicator.
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * @param indicator
     *            The indicator to set.
     */
    public void setIndicator(final String indicator) {
        this.indicator = indicator;
    }

    /**
     * @return Returns the startPosition.
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition
     *            The startPosition to set.
     */
    public void setStartPosition(final int startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * @return List Collection of ColumnMetaData objects
     * @see net.sf.flatpack.structure.ColumnMetaData
     */
    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    /**
     * @param columnsToUse
     *            The columns to set.
     * @param p
     *          PZParser being used.  Can be null.
     */
    public void setColumns(final List<ColumnMetaData> columnsToUse, final Parser p) {
        this.columns = columnsToUse;
        this.columnIndex = ParserUtils.buidColumnIndexMap(columns, p);
    }

    /**
     * Returns the index of the column name.
     *
     * @author Benoit Xhenseval
     * @param colName
     * @return -1 if the column name does not exist.
     */
    public int getColumnIndex(final String colName) {
        int idx = -1;
        if (columnIndex != null) {
            final Integer i = columnIndex.get(colName);
            if (i != null) {
                idx = i.intValue();
            }
        }
        return idx;
    }

    /**
     * Used to determine the &lt;record&gt; mapping the row belongs to.  Will
     * only be evaluated if &gt; 0.
     *
     * @return the elementCount
     */
    public int getElementCount() {
        return elementCount;
    }

    /**
     * Used to determine the &lt;record&gt; mapping the row belongs to.  Will
     * only be evaluated if &gt; 0.
     *
     * @param elementCount the elementCount to set
     */
    public void setElementCount(final int elementCount) {
        this.elementCount = elementCount;
    }
}
