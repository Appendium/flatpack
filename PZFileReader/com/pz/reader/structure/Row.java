/*
Copyright 2005 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/

package com.pz.reader.structure;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * This holds a collection of columns and their values along with the actual
 * rownumber they appear in the flat file
 * 
 * Last Updated: 11-27-2005  By: Paul Zepernick
 * @author	Paul Zepernick
 * @version 2.0
*/
public class Row implements Serializable{
	/**Vector to hold all columns that exist in the row*/
	private Vector cols = null;
	/**Row number in flat file*/
	private int rowNumber = 0;
	
	/**Constructs a new row*/
	public Row(){
		super();
		cols = new Vector();
	}
	
	/**
	 * Adds a column to a row
	 * 
	 * @param c - Column to add to the row
	 * @see Column class
	*/
	public void addColumn(Column c){
		cols.add(c);
	}
	
	/**
	 * Returns the value of a column for a specified column name 
	 * 
	 * @param colPosition - int position of the column in the array
	 * @return String value of column
	*/
	public String getValue(int colPosition){
	    return ((Column)cols.get(colPosition)).getValue();
	}
	
	/**
	 * Set the value of a column for a specified column name 
	 * 
	 * @param columnIndex - column number to change
	 * @param value - String column value
	*/
	public void setValue(int columnIndex,String value){
		/**loop through the columns in the row until we find the name*/	
		Column column = null;
		column = (Column)cols.get(columnIndex);
		/**get reference to column in the row and change the value*/
		column.setValue(value);

	}
	/**
	 * Returns the rowNumber.
	 * @return int
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * Sets the rowNumber.
	 * @param rowNumber The rowNumber to set
	 */
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * Returns the cols for the row.
	 * @return Vector
	 */
	public Vector getCols() {
		return cols;
	}

}
