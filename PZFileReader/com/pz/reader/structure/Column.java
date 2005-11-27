/*
Copyright 2005 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/

package com.pz.reader.structure;

import java.io.Serializable;

/**
 *This class holds properties for a column in a flat text file.
 * Information such as the column name, length, starting position, and 
 * ending position will be stored in this class
 * 
 * Last Updated: 10-16-2003  By: Paul Zepernick
 * @author	Paul Zepernick
 * @version 1.0
*/
public class Column implements Serializable{
	/**column value*/
	private String value = null;
	
	/**constructs a new column*/
	public Column(){
		super();
	}
	
	/**
	 * Returns the value.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
