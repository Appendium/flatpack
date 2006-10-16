/*
Copyright 2006 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
/*
*  Created on May 27, 2006
*/
package com.pz.reader.xml;

import java.util.List;

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
    
    /**
     * @return Returns the elementNumber.
     */
    public int getElementNumber() {
        return elementNumber;
    }
    /**
     * @param elementNumber The elementNumber to set.
     */
    public void setElementNumber(int elementNumber) {
        this.elementNumber = elementNumber;
    }
    /**
     * @return Returns the endPostition.
     */
    public int getEndPositition() {
        return endPositition;
    }
    /**
     * @param endPostition The endPostition to set.
     */
    public void setEndPositition(int endPositition) {
        this.endPositition = endPositition;
    }
    /**
     * @return Returns the indicator.
     */
    public String getIndicator() {
        return indicator;
    }
    /**
     * @param indicator The indicator to set.
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
    /**
     * @return Returns the startPosition.
     */
    public int getStartPosition() {
        return startPosition;
    }
    /**
     * @param startPosition The startPosition to set.
     */
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
    /**
     * @return Returns the columns.
     */
    public List getColumns() {
        return columns;
    }
    /**
     * @param columns The columns to set.
     */
    public void setColumns(List columns) {
        this.columns = columns;
    }
}
