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
package net.sf.pzfilereader.xml;

import java.util.List;
import java.util.Map;

import net.sf.pzfilereader.util.ParserUtils;

/**
 * @author Paul Zepernick
 * 
 * Definition for a <RECORD> element in a pzmap file
 */
public class XMLRecordElement {
    private int startPosition = 0;

    private int endPositition = 0;

    private int elementNumber = 0;

    private String indicator;

    private List columns;

    private Map columnIndex;

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
     * @param endPostition
     *            The endPostition to set.
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
     * @see net.sf.pzfilereader.structure.ColumnMetaData
     */
    public List getColumns() {
        return columns;
    }

    /**
     * @param columns
     *            The columns to set.
     */
    public void setColumns(final List columns) {
        this.columns = columns;
        this.columnIndex = ParserUtils.buidColumnIndexMap(columns);
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
            final Integer i = (Integer) columnIndex.get(colName);
            if (i != null) {
                idx = i.intValue();
            }
        }
        return idx;
    }
}
