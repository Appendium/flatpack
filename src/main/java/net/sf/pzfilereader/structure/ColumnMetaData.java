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
package com.pz.reader.structure;

/**
 * @author Paul zepernick
 * 
 * Holds information about the columns in the data set. This is an improvment
 * over 1.0.x. This information was being repeated for every row, causing a
 * spike in memory usage
 * 
 * @version 2.0
 */
public class ColumnMetaData {

    /** Column Name */
    private String colName = null;

    /** column length */
    private int colLength = 0;

    /** starting position */
    private int startPosition = 0;

    /** ending position */
    private int endPosition = 0;

    /**
     * constructor
     * 
     */
    public ColumnMetaData() {
        super();
    }

    /**
     * Returns the colLength.
     * 
     * @return int
     */
    public int getColLength() {
        return colLength;
    }

    /**
     * Returns the colName.
     * 
     * @return String
     */
    public String getColName() {
        return colName;
    }

    /**
     * Returns the endPosition.
     * 
     * @return int
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Returns the startPosition.
     * 
     * @return int
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Sets the colLength.
     * 
     * @param colLength
     *            The colLength to set
     */
    public void setColLength(final int colLength) {
        this.colLength = colLength;
    }

    /**
     * Sets the colName.
     * 
     * @param colName
     *            The colName to set
     */
    public void setColName(final String colName) {
        this.colName = colName;
    }

    /**
     * Sets the endPosition.
     * 
     * @param endPosition
     *            The endPosition to set
     */
    public void setEndPosition(final int endPosition) {
        this.endPosition = endPosition;
    }

    /**
     * Sets the startPosition.
     * 
     * @param startPosition
     *            The startPosition to set
     */
    public void setStartPosition(final int startPosition) {
        this.startPosition = startPosition;
    }

}
