/*
Copyright 2005 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
package com.pz.reader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.pz.reader.ordering.OrderBy;
import com.pz.reader.structure.Column;
import com.pz.reader.structure.Row;
import com.pz.reader.xml.PZMapParser;

/**
 * This class parses a datafile and holds methods to scroll back 
 * and forth through the datafile along with methods to retreive values 
 * from columns.
 * 
 * @author	Paul Zepernick
 * @version 1.0
*/
public class DataSet implements Serializable{
	/**Array to hold the rows and their values in the text file*/
	public Vector rows = null;
	/**Array of errors that have occured during processing*/
	private Vector errors = null;
	/**Pointer for the current row in the array we are on*/
	private int pointer = -1;
	/**flag to indicate if data should be pulled as lower case*/
	private boolean lowerCase = false;
	/**flag to inidicate if data should be pulled as upper case*/
	private boolean upperCase = false;
	/**flag to indicate if a strict parse should be used when getting doubles and ints*/
	private boolean strictNumericParse = false;
	
	
	/**
	 * Constructs a new DataSet using the database table file layout method.  
	 * This is used for a FIXED LENGTH text file.
	 * 
	 * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
	 * @param dataSource - Fixed length file to read from
	 * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
	 * @exception Exception
	 * 
	*/
	public DataSet(Connection con, File dataSource,String dataDefinition) throws Exception{
		super();
		
		
		String sql = null;
		ResultSet rs = null;
		Statement stmt = null;
		Column column = null;
		boolean hasResults = false;
		ArrayList columnObjs = new ArrayList();
		
		try{
			stmt = con.createStatement();
			
			sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
				+ "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO "
				+ "WHERE DATAFILE.DATAFILE_DESC = '" + dataDefinition + "' "
				+ "ORDER BY DATASTRUCTURE_COL_ORDER";
			
			rs = stmt.executeQuery(sql);
			
			//put array of columns together.  These will be used to put together the dataset when reading in the file
			while (rs.next()){
			    
			    column = new Column();
			    column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
			    column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
			    columnObjs.add(column);
			    
			    
				hasResults = true;
			}
			
			if (!hasResults){
				throw new 	FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + dataDefinition);
			}
			
			//read in the fixed length file and construct the DataSet object
			doFixedLengthFile(dataSource,columnObjs);
			
		}finally{
		    if (rs != null) rs.close();
		    if (stmt != null) stmt.close();
		}
		
	}
	
	/**
	 * Constructs a new DataSet using the database table file layout method.  
	 * This is used for a DELIMITED text file.
	 * 
	 * esacpe sequence reference  
	 * \n  	newline  
	 * \t 	tab 	
	 * \b 	backspace 	
	 * \r 	return 	
	 * \f 	form feed 	
	 * \\ 	backslash 	
	 * \' 	single quote 	
	 * \" 	double quote 	
	 *
	 * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
	 * @param dataSource - text file datasource to read from
	 * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
	 * @param delimiter - Char the file is delimited By
	 * @param qualifier - Char text is qualified by  
	 * @param ignoreFirstRecord - skips the first line that contains data in the file
	 * @exception Exception
	 * 
	*/
	public DataSet(Connection con, File dataSource,String dataDefinition,String delimiter, String qualifier, boolean ignoreFirstRecord) throws Exception{
		super();
		
		
		String sql = null;
		ResultSet rs = null;
		Statement stmt = null;
		Column column = null;
		boolean hasResults = false;
		ArrayList columnObjs = new ArrayList();
		
		try{
			stmt = con.createStatement();
			
			sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
				+ "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO "
				+ "WHERE DATAFILE.DATAFILE_DESC = '" + dataDefinition + "' "
				+ "ORDER BY DATASTRUCTURE_COL_ORDER";
			
			rs = stmt.executeQuery(sql);
			
			//put array of columns together.  These will be used to put together the dataset when reading in the file
			while (rs.next()){
			    
			    column = new Column();
			    column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
			    column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
			    columnObjs.add(column);
			    
			    
				hasResults = true;
			}
			
			if (!hasResults){
				throw new 	FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + dataDefinition);
			}
			
			//read in the fixed length file and construct the DataSet object
			doDelimitedFile(dataSource,delimiter,qualifier,ignoreFirstRecord,columnObjs);
			
		}finally{
		    if (rs != null) rs.close();
		    if (stmt != null) stmt.close();
		}
		
	}
	
	/**
	 * Constructs a new DataSet using the PZMAP XML file layout method.  
	 * This is used for a DELIMITED text file.
	 * 
	 * esacpe sequence reference  
	 * \n  	newline  
	 * \t 	tab 	
	 * \b 	backspace 	
	 * \r 	return 	
	 * \f 	form feed 	
	 * \\ 	backslash 	
	 * \' 	single quote 	
	 * \" 	double quote 	
	 *
	 * @param pzmapXML - Reference to the xml file holding the pzmap
	 * @param dataSource - text file datasource to read from
	 * @param delimiter - Char the file is delimited By
	 * @param qualifier - Char text is qualified by  
	 * @param ignoreFirstRecord - skips the first line that contains data in the file
	 * @exception Exception
	 * 
	*/
	public DataSet(File pzmapXML, File dataSource,String delimiter, String qualifier, boolean ignoreFirstRecord) throws Exception{
		super();
		
		
		PZMapParser parser = null;
		ArrayList columnObjs = new ArrayList();
		
	
		if (!pzmapXML.exists()){
			throw new 	FileNotFoundException("pzmap XML file does not exist ");
		}
		
        parser = new PZMapParser(pzmapXML);
        
        parser.parse();
        
        columnObjs = parser.getColumnDescriptors();
		
		//read in the delimited file and construct the DataSet object
		doDelimitedFile(dataSource,delimiter,qualifier,ignoreFirstRecord,columnObjs);
			
		
	}
	
	/**
	 * Constructs a new DataSet using the PZMAP XML file layout method.  
	 * This is used for a FIXED LENGTH text file.
	 * 
	 * @param pzmapXML - Reference to the xml file holding the pzmap
	 * @param dataSource - Delimited file to read from
	 * @exception Exception
	 * 
	*/
	public DataSet(File pzmapXML, File dataSource) throws Exception{
		super();
		
		
		PZMapParser parser = null;
		ArrayList columnObjs = new ArrayList();
		
	
		if (!pzmapXML.exists()){
			throw new 	FileNotFoundException("pzmap XML file does not exist ");
		}
		
        parser = new PZMapParser(pzmapXML);
        
        parser.parse();
        
        columnObjs = parser.getColumnDescriptors();
		
		//read in the fixed length file and construct the DataSet object
		doFixedLengthFile(dataSource,columnObjs);
			
		
	}

	/*
	 * puts together the dataset for fixed length file.   This is used for PZ XML mappings, and SQL table
	 * mappings
	 */	
	private void doFixedLengthFile(File dataSource, ArrayList columnObjs) throws Exception{
		String line = null;
		FileReader fr = null;
		BufferedReader br = null;
		Column column = null;
		Row row  = null;
		int recordLength = 0;
		int lineCount = 0;
		int recPosition = 0;

		try{
			rows = new Vector();
			errors = new Vector();
			/**make sure that the file we need to read from exists*/
			if (dataSource == null || !dataSource.exists()){
				throw new FileNotFoundException("DATASOURCE DOES NOT EXIST, OR IS NULL.  BAD FILE PATH.");	
			}
			
			
			//loop through columns described and get the total line length
			for (int i = 0; i < columnObjs.size(); i++){
			    recordLength += ((Column)columnObjs.get(i)).getColLength();
			}

			/**Read in the flat file*/
			fr = new FileReader (dataSource.getAbsolutePath());
			br = new BufferedReader(fr);
			/**loop through each line in the file*/
			while ((line = br.readLine()) != null){
				lineCount ++;
				/**empty line skip past it*/
				if (line.trim().length() == 0){
					continue;
				}
				/**Incorrect record length on line log the error.  Line
				 * will not be included in the dataset*/
				if (line.length() != recordLength){
					/**log the error*/
					addError("INCORRECT LINE LENGTH. LINE IS "+ line.trim().length() + " LONG. SHOULD BE " + recordLength,
								lineCount,2);
					continue;			
				}

				recPosition = 1;
				row = new Row();
				/**Build the columns for the row*/
				for (int i = 0; i < columnObjs.size(); i++){
					String tempValue = null;
					column = new Column();
					column.setColName(((Column)columnObjs.get(i)).getColName());
					column.setColLength(((Column)columnObjs.get(i)).getColLength());
					column.setStartPosition(recPosition);
					column.setEndPosition(recPosition + (((Column)columnObjs.get(i)).getColLength()-1));
					tempValue = line.substring(recPosition - 1,recPosition + (((Column)columnObjs.get(i)).getColLength()-1));
					column.setValue(tempValue.trim());
					recPosition += ((Column)columnObjs.get(i)).getColLength();
					row.addColumn(column);
				}
				row.setRowNumber(lineCount);
				/**add the row to the array*/
				rows.add(row);
			}
		}finally{
			if (fr != null){
			    fr.close();
			}
			if (br != null){
			    br.close();
			}
		}						
	}	
	
	/*
	 * puts together the dataset for a DELIMITED file.   This is used for PZ XML mappings, and SQL table
	 * mappings
	 */	
	private void doDelimitedFile(File dataSource,String delimiter, String qualifier, boolean ignoreFirstRecord, ArrayList columnObjs) throws Exception{
		String line = null;
		FileReader fr = null;
		BufferedReader br = null;
		Column column = null;
		Row row  = null;
		int columnCount = 0;
		int lineCount = 0;
		ArrayList columns = null;
		boolean processedFirst = false;
		try{
			rows = new Vector();
			errors = new Vector();
			/**make sure that the file we need to read from exists*/
			if (dataSource == null || !dataSource.exists()){
				throw new FileNotFoundException("DATASOURCE DOES NOT EXIST, OR IS NULL.  BAD FILE PATH.");	
			}
			
			
			//loop through columns described and get the total column count
			for (int i = 0; i < columnObjs.size(); i++){
			    columnCount ++;	
			}
			
			/**Read in the flat file*/
			fr = new FileReader (dataSource.getAbsolutePath());
			br = new BufferedReader(fr);
			/**loop through each line in the file*/
			while ((line = br.readLine()) != null){
				lineCount ++;
				/**empty line skip past it*/
				if (line.trim().length() == 0){
					continue;
				}
				//check to see if the user has elected to skip the first record
				if (!processedFirst && ignoreFirstRecord){
				    processedFirst = true;
				    continue;
				}
				
				//column values
				columns = splitLine(line,delimiter,qualifier);
				//DEBUG
				/*for (int i = 0; i < columns.size(); i++){
				    System.out.println(columns.get(i));
				}*/
				
				/**Incorrect record length on line log the error.  Line
				 * will not be included in the dataset*/
				if (columnCount != columns.size()){
					/**log the error*/
					addError("INCORRECT NUMBER OF ELEMENTS, WANTED:  "+ columnCount + " GOT:  " + columns.size(),
								lineCount,2);
					continue;			
				}

				row = new Row();
				/**Build the columns for the row*/
				for (int i = 0; i < columnObjs.size(); i++){
					column = new Column();
					column.setColName(((Column)columnObjs.get(i)).getColName());
					column.setColLength(((Column)columnObjs.get(i)).getColLength());
					column.setValue((String)columns.get(i));
					row.addColumn(column);
				}
				row.setRowNumber(lineCount);
				/**add the row to the array*/
				rows.add(row);
			}
		}finally{
			if (fr != null){
			    fr.close();
			}
			if (br != null){
			    br.close();
			}
		}						
	}	

	//helper function to return an array of elements
	//from a line
	private ArrayList splitLine(String line, String delimiter, String qualifier){
	    ArrayList list = new ArrayList();
	    String temp = "";
	    boolean beginQualifier = false;
	    //this will be used for delimted files that have some items qualified and some items dont
	    boolean beginNoQualifier = false;
	    
	    //trim hard leading spaces at the begining of the line
	    line = lTrim(line);
	    
	    for (int i = 0; i < line.length(); i++){
            String remainderOfLine = line.substring(i); //data of the line which has not yet been read
	        //check to see if there is a text qualifier
	        if (qualifier != null && qualifier.trim().length() > 0){
	            if (line.substring(i, i + 1).equals(qualifier) && !beginQualifier && !beginNoQualifier){
	                //begining of a set of data
	                beginQualifier = true;
	            }else if (!beginQualifier && !beginNoQualifier &&
	                    	!line.substring(i, i +1).equals(qualifier) && 
                            !lTrim(remainderOfLine).startsWith(qualifier)){ //try to account for empty space before qualifier starts
	                	//we have not yet begun a qualifier and the char we are on is NOT
	                	//a qualifier.  Start reading data
	                	beginNoQualifier = true;
                        //make sure that this is not just an empty column with no qualifiers. ie "data",,"data"
                        if (line.substring(i, i +1).equals(delimiter)){
                            list.add(temp);
                            temp = "";
                            beginNoQualifier = false;
                            continue;//grab the next char
                        }
                        temp += line.substring(i, i + 1);
	            }else if ((!beginNoQualifier) && line.substring(i, i + 1).equals(qualifier) && beginQualifier &&
	                    //i + 2 < line.length() && //looks to be an unnecessary check 
                        (lTrim(line.substring(i + 1)).length() == 0 || //this will be true on empty undelmited columns at the end of the line
	                    lTrim(line.substring(i + 1)).substring(0,1).equals(delimiter))){
	                //end of a set of data that was qualified
	                list.add(temp);
	                temp = "";
	                beginQualifier = false;
	                //add to "i" so we can get past the qualifier, otherwise it is read into a set of data which 
	                //may not be qualified.  Find out how many spaces to the delimiter
                    int offset = getDelimiterOffset(line,i,delimiter) -1;//subtract 1 since i is going to get incremented again at the top of the loop
                    //System.out.println("offset: " + offset);
                    if (offset < 1){
                        i++;
                    }else{
                        i += offset;
                    }
	            }else if (beginNoQualifier && line.substring(i, i + 1).equals(delimiter)){
	                //check to see if we are done with an element that was not being qulified
	                list.add(temp);
	                temp = "";
	                beginNoQualifier = false;
	            }else if (beginNoQualifier || beginQualifier){
	                //getting data in a NO qualifier element or qualified element
	                temp += line.substring(i, i + 1);
	            }
                        
	        }else{
	            //not using a qualifier.  Using a delimiter only
	            if (line.substring(i, i + 1).equals(delimiter)){
	                list.add(temp);
	                temp = "";
	            }else{
	                temp += line.substring(i,i + 1);
	            }
	        }
	    }
	    
	    //remove the ending text qualifier if needed
	    if (qualifier != null && qualifier.trim().length() > 0 && temp.trim().length()> 0){
	        if (temp.trim().substring(temp.trim().length() -1).equals(qualifier)){
	            temp = temp.trim().substring(0,temp.length() - 1);
	        }
	        
	    }
	    
	    if (beginQualifier || beginNoQualifier || line.trim().endsWith(delimiter)){  
	        //also account for a delimiter with an empty column at the end that was not qualified
	        //check to see if we need to add the last column in..this will happen on empty columns
	        //add the last column
	        list.add(temp);
	    }

	    
	    return list;
	}
	
    
    //reads from the specified point in the line and returns how 
    //many chars to the specified delimter
    private int getDelimiterOffset(String line, int start,String delimiter){
       int offset = 0;
       for (int i = start; i < line.length(); i++){
           offset++;
           if (line.substring(i,i+1).equals(delimiter)){
               return offset;
           }
       }
       return -1;
    }
    
	
	 //trims the left side of the string  
    private String lTrim(String value){  
        String returnVal = "";  
        boolean gotAChar = false;  
          
        for (int i = 0; i < value.length(); i++){  
            if(value.substring(i,i+1).trim().length() == 0  
                    && !gotAChar){  
                continue;  
            }else{  
                gotAChar = true;  
                returnVal += value.substring(i,i+1);  
            }  
        }  
          
        return returnVal;  
          
    }
    
    //removes the specified char from a String
    private String removeChar(String character,String theString){
        String s = "";
        for (int i = 0; i < theString.length(); i++){
            if (theString.substring(i,i+1).equalsIgnoreCase(character)){
                continue;
            }
            s += theString.substring(i,i+1);
        }
        
        return s;
        
    }
	
	
	/**
	 * Changes the value of a specified column in a row in the set.  This change 
	 * is in memory, and does not actually change the data in the file that was
	 * read in.
	 * 
	 * @param columnName - String Name of the column
	 * @param value - String value to assign to the column.
	 * @exception Exception - exception will be thrown if pointer in not on a valid row 
	*/
	public void setValue(String columnName,String value) throws Exception{
		Row row = null;
		
		if (pointer > -1 && pointer <= rows.size()  -1){
			/**get a reference to the row*/
			row = (Row)rows.get(pointer);
			/**change the value of the column*/
			row.setValue(columnName,value);
			/**update the row in the array*/
			rows.set(pointer,row);
			
			return;
		}
		
		throw new Exception ("POINTER IS SITTING ON AN INVALID ROW.");
	}
	
	
	/**
	 * Goes to the top of the data set. This will put the pointer
	 * one record before the first in the set.  Next() will have to be called
	 * to get the first record after this call.
	 * 
	*/
	public void goTop(){
		pointer = -1;
		return;
	}
	
	/**
	 * Goes to the last record in the dataset
	 * 
	*/
	public void goBottom(){
		pointer  = rows.size() -1 ;
		return;	
	}
	
	/**
	 * Moves to the next record in the set.  
	 * Returns true if move was a success, false if not
	 * 
	 * @return boolean
	*/
	public boolean next(){
		if (pointer < rows.size() && pointer + 1 != rows.size()){
			pointer ++;
			return true;	
		}	
		return false;
	}
	
	/**
	 * Moves back to the previous record in the set
	 * return true if move was a success, false if not
	 * 
	 * @return boolean
	*/
	public boolean previous(){
		if (pointer <= 0){
			return false;
		}
		pointer --;
		return true;			
	}
	
	/**
	 * Returns the string value of a specified column
	 * 
	 * @param column - Name of the column
	 * @exception NoSuchElementException
	 * @return String
	*/
	public String getString(String column) throws NoSuchElementException{
	    
	    if (upperCase){
	        //convert data to uppercase before returning
	        return ((Row)rows.get(pointer)).getValue(column).toUpperCase();
	    }
	    
	    if (lowerCase){
	        //convert data to lowercase before returning
	        return ((Row)rows.get(pointer)).getValue(column).toLowerCase();
	    }
	    
	    //return value as how it is in the file
		return ((Row)rows.get(pointer)).getValue(column);
	}
	
	/**
	 * Returns the double value of a specified column
	 * 
	 * @param column - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return double
	*/
	public double getDouble(String column) throws NoSuchElementException,NumberFormatException{
		String s = null;
		String newString = "";
		String[] allowedChars = {"0","1","2","3","4","5","6","7","8","9",".","-"};
		
		s = ((Row)rows.get(pointer)).getValue(column);
		
		if (!strictNumericParse){
			if (s.trim().length() == 0){
				return 0;
			}
		    for (int i = 0; i < s.length(); i++){
		        for (int j = 0; j < allowedChars.length; j++){
		            if (s.substring(i,i+1).equals(allowedChars[j])){
		                newString += s.substring(i,i+1);
		                break;
		            }
		        }
		    }
		    if (newString.trim().length() == 0 || (newString.length() == 1 && newString.equals("."))
		            || (newString.length() == 1 && newString.equals("-"))){
		        newString = "0";
		    }
		}else{
		    newString = s;
		}
		
		return Double.parseDouble(newString);
	}
	
	/**
	 * Returns the interger value of a specified column
	 * 
	 * @param column - Name of the column
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return double
	*/
	public int getInt(String column) throws NoSuchElementException,NumberFormatException{
		String s = null;
		String newString = "";
		String[] allowedChars = {"0","1","2","3","4","5","6","7","8","9","-"};
		
		s = ((Row)rows.get(pointer)).getValue(column);
		
		if (!strictNumericParse){
			if (s.trim().length() == 0){
				return 0;
			}
		    for (int i = 0; i < s.length(); i++){
		        for (int j = 0; j < allowedChars.length; j++){
		            if (s.substring(i,i+1).equals(allowedChars[j])){
		                newString += s.substring(i,i+1);
		                break;
		            }
		        }
		    }
		    //check to make sure we do not have a single length string with just a minus sign
		    if (newString.trim().length() == 0 || (newString.length() == 1 && newString.equals("-"))){
		        newString = "0";
		    }
		}else{
		    newString = s;
		}
		
		return Integer.parseInt(newString);
	}
	/**
	 * Returns the date value of a specified column.  This assumes the date is in 
	 * yyyyMMdd.  If your date is not in this format, see getDate(String,SimpleDateFormat)
	 * 
	 * @param column - Name of the column
	 * @exception ParseException
	 * @return Date
	*/
	public Date getDate(String column) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String s = null;
		s = ((Row)rows.get(pointer)).getValue(column);
		return sdf.parse(s);		
	}
	
	/**
	 * Returns the date value of a specified column.  This should be used if
	 * the date is NOT in yyyyMMdd format.  The SimpleDateFormat object will specify
	 * what kind of format the date is in.
	 * 
	 * @param column - Name of the column
	 * @param sdf - SimpleDateFormat of the date
	 * @exception ParseException
	 * @see java.text.SimpleDateFormat
	 * @return Date
	*/
	public Date getDate(String column, SimpleDateFormat sdf) throws ParseException{
		String s = null;
		s = ((Row)rows.get(pointer)).getValue(column);
		return sdf.parse(s);		
	}
	
	
	/**
	 * Returns a String array of column names in the DataSet
	 * 
	 * @return String[]
	*/
	public String[] getColumns(){
		Row row = null;
		Column column = null;
		Vector cols = null;
		String[] array = null;
		
		if (rows != null && rows.size() > 0){
			row = (Row)rows.get(0);
			cols = row.getCols();
			array = new String[cols.size()];
			for (int i=0; i<cols.size(); i++){
				column = (Column)cols.get(i);
				array[i] = column.getColName();	
			}
		}
		return array;
	}
	
	/**
	 * Returns the line number the pointer is on.  These are the actual line numbers from
	 * the flat file, before any sorting.
	 * 
	 * @exception NoSuchElementException
	 * @exception NumberFormatException
	 * @return int
	*/
	public int getRowNo(){
		return ((Row)rows.get(pointer)).getRowNumber();
	}
	
	/**
	 * Returns A Collection Of DataErrors that happened during processing
	 * @return Vector
	 */
	public Vector getErrors() {
		return errors;
	}
	

	/**
	 * Adds a new error to this DataSet.  These can be collected, and retreived after processing
	 * 
	 * @param errorDesc - String description of error
	 * @param lineNo - int line number error occured on
	 * @param errorLevel - int errorLevel 1,2,3 1=warning 2=error  3= severe error
	 */
	public void addError(String errorDesc,int lineNo,int errorLevel) {
		DataError de = new DataError();
		de.setErrorDesc(errorDesc);
		de.setLineNo(lineNo);
		de.setErrorLevel(errorLevel);
		errors.add(de);
	}
	
	/**
	 * Removes a row from the dataset.  Once the row is removed the pointer will be sitting on the
	 * record previous to the deleted row.
	 * 
	*/
	public void remove(){
		rows.remove(pointer);
		pointer --;	
	}
	
	/**
     * Returns the index the pointer is on for the array
     * 
     * @return int
     */
	public int getIndex(){
	    return pointer;
	}
	
	/**
     * Sets the absolute position of the record pointer
     * 
     * @param localPointer - int
     * @exception IndexOutOfBoundsException
     */
	public void absolute(int localPointer) throws IndexOutOfBoundsException{
	    if (localPointer < 0 || localPointer > rows.size() - 1){
	        throw new IndexOutOfBoundsException ("INVALID POINTER LOCATION: " + localPointer);
	    }
	    
	    pointer = localPointer;
	}

	

	/**
	 * Returns true or false as to whether or not the line number contains an error.
	 * The import will skip the line if it contains an error and it will not be processed
	 * 
	 * @param lineNo - int line number
	 * @return boolean
	 */
	public boolean isAnError(int lineNo){
		for (int i=0; i<errors.size(); i++){
			if (((DataError)errors.get(i)).getLineNo() == lineNo){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Orders the data by column(s) specified.
	 * This will reposition the cursor to the top of the DataSet when executed
	 * 
	 * @param ob - OrderBy object
	 * @see com.pz.reader.ordering.OrderBy
	 * @see com.pz.reader.ordering.OrderColumn
	 */
	public void orderRows(OrderBy ob){
	    if (ob != null && rows != null){
	        Collections.sort(rows,ob);
	        goTop();
	    }
	}
	
	
	/**
	 * Sets data in the DataSet to lowercase
	 * 
	 */
	public void setLowerCase(){
	    upperCase = false;
	    lowerCase = true;
	}
	
	/**
	 * Sets data in the DataSet to uppercase
	 * 
	 */
	public void setUpperCase(){
	    upperCase = true;
	    lowerCase = false;
	}
	
    /**
     * Setting this to True will parse text as is and throw a NumberFormatException.
     * Setting to false, which is the default, will remove any non numeric charcter from
     * the field.  The remaining numeric chars's will be returned.  If it is an empty string,or 
     * there are no numeric chars, 0 will be returned for getInt() and getDouble()
     * 
     * @param strictNumericParse The strictNumericParse to set.
     */
    public void setStrictNumericParse(boolean strictNumericParse) {
        this.strictNumericParse = strictNumericParse;
    }
    
    /**
     * erases the dataset and releases memory for the JVM to reclaim
     *
     */
    public void freeMemory(){
        rows.clear();
        errors.clear();
    }
}
