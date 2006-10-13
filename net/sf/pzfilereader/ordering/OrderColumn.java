/*
Copyright 2006 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
/*
 * Created on Nov 23, 2004
 *
 */
package net.sf.pzfilereader.ordering;

/**
 * @author paul zepernick
 *
 *Used in conjunction with the OrderBy class.  Holds the name 
 *of the column to order by and the direction the order should go,
 *ASC, DESC.
 *
 *@version 2.0
 */
public class OrderColumn {
    /**property name to sort by*/
    private String columnName = null;
    /**1  = ASC -1 = DESC*/
    private int sortIndicator = 1;
    
    /**
     * Constructs a new order by element
     * 
     * @param columnName - column to sort by
     * @param desc - boolean sort DESC?
     */
    public OrderColumn(String columnName,boolean desc){
        this.columnName = columnName;
        
        if (desc){
            this.sortIndicator = -1;
        }
    }
    
    
    /**
     * @return Returns the propertyName.
     */
    public String getColumnName() {
        return columnName;
    }
    /**
     * @param columnName The columnName to set.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    /**
     * @return Returns the sortIndicator.
     */
    public int getSortIndicator() {
        return sortIndicator;
    }
}
