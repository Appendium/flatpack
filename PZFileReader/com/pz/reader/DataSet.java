/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
 */
package com.pz.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.pz.reader.ordering.OrderBy;
import com.pz.reader.structure.ColumnMetaData;
import com.pz.reader.structure.Row;
import com.pz.reader.util.ExcelTransformer;
import com.pz.reader.util.ParserUtils;
import com.pz.reader.xml.PZMapParser;

/**
 * This class parses a datafile and holds methods to scroll back and forth through the datafile
 * along with methods to retreive values from columns.
 * @author Paul Zepernick
 * @version 2.0.1
 */
public class DataSet {

    /** Array to hold the rows and their values in the text file */
    public List     rows               = null; 


    /** Array of errors that have occured during processing */
    public List     errors             = null;

    /** Map of column metadata's */
    public Map     columnMD           = null;

    /** Pointer for the current row in the array we are on */
    private int     pointer            = -1;

    /** flag to indicate if data should be pulled as lower case */
    private boolean lowerCase          = false;

    /** flag to inidicate if data should be pulled as upper case */
    private boolean upperCase          = false;

    /**
     * flag to indicate if a strict parse should be used when getting doubles and ints
     */
    private boolean strictNumericParse = false;

    /**
     * Flag to indicate that we can cope with lines shorter than the required lengh
     */
    private boolean handleShortLines   = false;

    /**
     * empty constructor. THIS SHOULD ONLY BE USED FOR CUSTOM DataSet implementations. It provides
     * NO parsing abilities
     */
    public DataSet() {
    }


    /**
     * Constructs a new DataSet using the database table file layout method. This is used for a
     * FIXED LENGTH text file.
     * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
     * @param dataSource - Fixed length file to read from
     * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
     * @param handleShortLines - Pad lines out to fit the fixed length
     * @exception Exception
     */
    public DataSet(Connection con, File dataSource, String dataDefinition, boolean handleShortLines) throws Exception {
        this(con, ParserUtils.createInputStream(dataSource), dataDefinition, handleShortLines);
    }

    /**
     * Constructs a new DataSet using the database table file layout method. This is used for a
     * FIXED LENGTH text file.
     * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
     * @param dataSourceStream - text file datasource InputStream to read from
     * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
     * @param handleShortLines - Pad lines out to fit the fixed length
     * @exception Exception
     */
    public DataSet(Connection con, InputStream dataSourceStream, String dataDefinition, boolean handleShortLines) throws Exception {
        super();
        this.handleShortLines = handleShortLines;

        String sql = null;
        ResultSet rs = null;
        Statement stmt = null;
        ColumnMetaData column = null;
        boolean hasResults = false;
        int recPosition = 1;
        List cmds = new ArrayList();

        try {
            columnMD = new LinkedHashMap();
            stmt = con.createStatement();

            sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
                    + "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO " + "WHERE DATAFILE.DATAFILE_DESC = '"
                    + dataDefinition + "' " + "ORDER BY DATASTRUCTURE_COL_ORDER";

            rs = stmt.executeQuery(sql);

            // put array of columns together. These will be used to put together
            // the dataset when reading in the file
            while (rs.next()) {

                column = new ColumnMetaData();
                column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
                column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
                column.setStartPosition(recPosition);
                column.setEndPosition(recPosition + (rs.getInt("DATASTRUCTURE_LENGTH") - 1));
                recPosition += rs.getInt("DATASTRUCTURE_LENGTH");

                cmds.add(column);

                hasResults = true;
            }
            
            columnMD.put("detail",cmds);

            if (!hasResults) {
                throw new FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + dataDefinition);
            }

            // read in the fixed length file and construct the DataSet object
            doFixedLengthFile(dataSourceStream);

        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }

    }


    /**
     * Constructs a new DataSet using the database table file layout method. This is used for a
     * DELIMITED text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
     * @param dataSource - text file datasource to read from
     * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param ignoreFirstRecord - skips the first line that contains data in the file
     * @param handleShortLines - Adds missing columns as empty's to the DataSet instead of logging
     *            them as an error
     * @exception Exception
     */
    public DataSet(Connection con, File dataSource, String dataDefinition, String delimiter, String qualifier,
            boolean ignoreFirstRecord, boolean handleShortLines) throws Exception {
        this(con, ParserUtils.createInputStream(dataSource), dataDefinition, delimiter, qualifier, ignoreFirstRecord, handleShortLines);
    }

    /**
     * New constructor based on InputStream.
     * Constructs a new DataSet using the database table file layout method. This is used for a
     * DELIMITED text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param con - Connection to database with DATAFILE and DATASTRUCTURE tables
     * @param dataSourceStream - text file datasource InputStream to read from
     * @param dataDefinition - Name of dataDefinition in the DATAFILE table DATAFILE_DESC column
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param ignoreFirstRecord - skips the first line that contains data in the file
     * @param handleShortLines - Adds missing columns as empty's to the DataSet instead of logging
     *            them as an error
     * @exception Exception
     */
    public DataSet(Connection con, InputStream dataSourceStream, String dataDefinition, String delimiter, String qualifier,
            boolean ignoreFirstRecord, boolean handleShortLines) throws Exception {
        super();

        this.handleShortLines = handleShortLines;

        String sql = null;
        ResultSet rs = null;
        Statement stmt = null;
        ColumnMetaData column = null;
        boolean hasResults = false;
        List cmds = new ArrayList();

        try {
            columnMD = new LinkedHashMap();
            stmt = con.createStatement();

            sql = "SELECT * FROM DATAFILE INNER JOIN DATASTRUCTURE ON "
                    + "DATAFILE.DATAFILE_NO = DATASTRUCTURE.DATAFILE_NO " + "WHERE DATAFILE.DATAFILE_DESC = '"
                    + dataDefinition + "' " + "ORDER BY DATASTRUCTURE_COL_ORDER";

            rs = stmt.executeQuery(sql);

            // put array of columns together. These will be used to put together
            // the dataset when reading in the file
            while (rs.next()) {

                column = new ColumnMetaData();
                column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
                column.setColLength(rs.getInt("DATASTRUCTURE_LENGTH"));
                cmds.add(column);

                hasResults = true;
            }
            
            columnMD.put("detail",cmds);

            if (!hasResults) {
                throw new FileNotFoundException("DATA DEFINITION CAN NOT BE FOUND IN THE DATABASE " + dataDefinition);
            }

            // read in the fixed length file and construct the DataSet object
            doDelimitedFile(dataSourceStream, delimiter, qualifier, ignoreFirstRecord, false);

        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        }

    }

 
    /**
     * Constructs a new DataSet using the PZMAP XML file layout method. This is used for a DELIMITED
     * text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param pzmapXML - Reference to the xml file holding the pzmap
     * @param dataSource - text file datasource to read from
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param ignoreFirstRecord - skips the first line that contains data in the file
     * @param handleShortLines - Adds missing columns as empty's to the DataSet instead of logging
     *            them as an error
     * @exception Exception
     */
    public DataSet(File pzmapXML, File dataSource, String delimiter, String qualifier, boolean ignoreFirstRecord,
            boolean handleShortLines) throws Exception {
        this(ParserUtils.createInputStream(pzmapXML), ParserUtils.createInputStream(dataSource), delimiter, qualifier, ignoreFirstRecord, handleShortLines);
    }

    /**
     * New constructor based on InputStream.
     * Constructs a new DataSet using the PZMAP XML file layout method. This is used for a DELIMITED
     * text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param pzmapXMLStream - Reference to the xml file holding the pzmap
     * @param dataSourceStream - text file datasource InputStream to read from
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param ignoreFirstRecord - skips the first line that contains data in the file
     * @param handleShortLines - Adds missing columns as empty's to the DataSet instead of logging
     *            them as an error
     * @exception Exception
     */
    public DataSet(InputStream pzmapXMLStream, InputStream dataSourceStream, String delimiter, String qualifier, boolean ignoreFirstRecord,
            boolean handleShortLines) throws Exception {

        this.handleShortLines = handleShortLines;
        columnMD = PZMapParser.parse(pzmapXMLStream);

        doDelimitedFile(dataSourceStream, delimiter, qualifier, ignoreFirstRecord, false);

    }

    /**
     * Constructs a new DataSet using the first line of data found in the text file as the column
     * names. This is used for a DELIMITED text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param dataSource - text file datasource to read from
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param handleShortLines - when flaged as true, lines with less columns then the amount of
     *            column headers will be added as empty's instead of producing an error
     * @exception Exception
     */
    public DataSet(File dataSource, String delimiter, String qualifier, boolean handleShortLines) throws Exception {

        this.handleShortLines = handleShortLines;
        InputStream dataSourceStream = null;
        
        try{
	        dataSourceStream = ParserUtils.createInputStream(dataSource);
   	        doDelimitedFile(dataSourceStream, delimiter, qualifier, false, true);
        }finally{
            if (dataSourceStream != null) dataSourceStream.close();
        }
    }
    
    /** 
    * Constructs a new DataSet using the first line of data found in the text file as the column
    * names. This is used for a DELIMITED text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
    * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
    * @param dataSource - text file InputStream to read from
    * @param delimiter - Char the file is delimited By
    * @param qualifier - Char text is qualified by
    * @param handleShortLines - when flaged as true, lines with less columns then the amount of
    *            column headers will be added as empty's instead of producing an error
    * @exception Exception
    */
   public DataSet(InputStream dataSource, String delimiter, String qualifier, boolean handleShortLines) throws Exception {

       this.handleShortLines = handleShortLines;
       
       try{
           doDelimitedFile(dataSource, delimiter, qualifier, false, true);
       }finally{
           if (dataSource != null) dataSource.close();
       }
   }


    /**
     * Constructs a new DataSet using the PZMAP XML file layout method. This is used for a FIXED
     * LENGTH text file.
     * @param pzmapXML - Reference to the xml file holding the pzmap
     * @param dataSource - Delimited file to read from
     * @param handleShortLines - Pad lines out to fit the fixed length
     * @exception Exception
     */
    public DataSet(File pzmapXML, File dataSource, boolean handleShortLines) throws Exception {
        this(ParserUtils.createInputStream(pzmapXML), ParserUtils.createInputStream(dataSource), handleShortLines);
    }

    /**
     * New constructor based on InputStream.
     * Constructs a new DataSet using the PZMAP XML file layout method. This is used for a FIXED
     * LENGTH text file.
     * @param pzmapXMLStream - Reference to the xml file InputStream holding the pzmap
     * @param dataSourceStream - Delimited file InputStream to read from
     * @param handleShortLines - Pad lines out to fit the fixed length
     * @exception Exception
     */
    public DataSet(InputStream pzmapXMLStream, InputStream dataSourceStream, boolean handleShortLines) throws Exception {

        this.handleShortLines = handleShortLines;

        columnMD = PZMapParser.parse(pzmapXMLStream);

        // read in the fixed length file and construct the DataSet object
        doFixedLengthFile(dataSourceStream);

    }

    /*
     * This is the new version of  doDelimitedFile using InputStrem instead of File. This is more flexible
     * especially it is working with WebStart.
     * 
     * puts together the dataset for fixed length file. This is used for PZ XML mappings, and SQL
     * table mappings
     */
    private void doFixedLengthFile(InputStream dataSource) throws Exception {
        String line = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        Row row = null;
        int recordLength = 0;
        int lineCount = 0;
        int recPosition = 0;
        //map of record lengths corrisponding to the ID's in the columnMD array
        Map recordLengths = null;
        String mdkey = null;
        List cmds = null;

        try {
            rows = new ArrayList();
            errors = new ArrayList();

            recordLengths = ParserUtils.calculateRecordLengths(columnMD);

            // Read in the flat file 
            isr = new InputStreamReader(dataSource);
            br = new BufferedReader(isr);
            //loop through each line in the file
            while ((line = br.readLine()) != null) {
                lineCount++;
                // empty line skip past it
                if (line.trim().length() == 0) {
                    continue;
                }

                
                mdkey = ParserUtils.getCMDKeyForFixedLengthFile(columnMD, line);
                recordLength = ((Integer)recordLengths.get(mdkey)).intValue();
                cmds = ParserUtils.getColumnMetaData(mdkey, columnMD);
                
                 //Incorrect record length on line log the error. Line will not be included in the
                 //dataset
                if (line.length() > recordLength) {
                    addError("LINE TOO LONG. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength, lineCount,
                            2);
                    continue;
                } else if (line.length() < recordLength) {
                    if (handleShortLines) {
                        // We can pad this line out
                        while (line.length() < recordLength) {
                            line = line + " ";
                        }

                        // log a warning
                        addError("PADDED LINE TO CORRECT RECORD LENGTH", lineCount, 1);

                    } else {
                        addError("LINE TOO SHORT. LINE IS " + line.length() + " LONG. SHOULD BE " + recordLength,
                                lineCount, 2);
                        continue;
                    }
                }

                recPosition = 1;
                row = new Row();
                row.setMdkey(mdkey.equals("detail") ? null : mdkey);  //try to limit the memory use
                //Build the columns for the row 
                for (int i = 0; i < cmds.size(); i++) {
                    String tempValue = null;
                    tempValue = line.substring(recPosition - 1, recPosition
                            + (((ColumnMetaData) cmds.get(i)).getColLength() - 1));
                    recPosition += ((ColumnMetaData) cmds.get(i)).getColLength();
                    row.addColumn(tempValue.trim());
                }
                row.setRowNumber(lineCount);
                // add the row to the array 
                rows.add(row);
            }
        } finally {
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    /*
     * This is the new version of  doDelimitedFile using InputStrem instead of File. This is more flexible
     * especially it is working with WebStart.
     * 
     * puts together the dataset for a DELIMITED file. This is used for PZ XML mappings, and SQL
     * table mappings
     */
    private void doDelimitedFile(InputStream dataSource, String delimiter, String qualifier, boolean ignoreFirstRecord, boolean createMDFromFile) throws Exception {
        if (dataSource == null) {
            throw new NullPointerException("dataSource is null");
        }
        String line = null;

        InputStreamReader isr = null;
        BufferedReader br = null;
        Row row = null;
        int columnCount = 0;
        int lineCount = 0;
        List columns = null;
        boolean processedFirst = false;
        boolean processingMultiLine = false;
        String lineData = "";
        List cmds = null;
        String mdkey = null;
        
        try {
            rows = new ArrayList();
            errors = new ArrayList();
            
            // get the total column count
            //columnCount = columnMD.size();

            /** Read in the flat file */
            // fr = new FileReader(dataSource.getAbsolutePath());
            isr = new InputStreamReader(dataSource);
            br = new BufferedReader(isr);
            /** loop through each line in the file */
            while ((line = br.readLine()) != null) {
                lineCount++;
                /** empty line skip past it */
                if (!processingMultiLine && line.trim().length() == 0) {
                    continue;
                }
                
                // check to see if the user has elected to skip the first record
                if (!processedFirst && ignoreFirstRecord) {
                    processedFirst = true;
                    continue;
                }else if (!processedFirst && createMDFromFile){
                    processedFirst = true;
                    columnMD = ParserUtils.getColumnMDFromFile(line, delimiter, qualifier);
                    continue;
                }
                

                //********************************************************
                //new functionality as of 2.1.0  check to see if we have 
                //any line breaks in the middle of the record, this will only
                //be checked if we have specified a delimiter
                //********************************************************
                char[] chrArry = line.trim().toCharArray();
                if (!processingMultiLine && delimiter != null && delimiter.trim().length() > 0){
                    processingMultiLine = ParserUtils.isMultiLine(chrArry, delimiter, qualifier);
                }
                
                
                //check to see if we have reached the end of the linebreak in the record
                
                if (processingMultiLine && lineData.trim().length() > 0){
                    //need to do one last check here.  it is possible that the " could be part of the data
                    //excel will escape these with another quote; here is some data ""  This would indicate
                    //there is more to the multiline                    
                    if (line.trim().endsWith(qualifier) && !line.trim().endsWith(qualifier + qualifier)){
                        //it is safe to assume we have reached the end of the line break
                        processingMultiLine = false;
                        if (lineData.trim().length() > 0) lineData += "\r\n";
                        lineData += line;
                    }else{
                    
	                    //check to see if this is the last line of the record
                        //looking for a qualifier followed by a delimiter
                        if (lineData.trim().length() > 0) lineData += "\r\n";
                        lineData += line;
	                    boolean qualiFound = false;
	                    for (int i = 0; i < chrArry.length; i ++){
	                        if (qualiFound){
	                            if (chrArry[i] ==  ' '){
	                                continue;
	                            }else{
	                                //not a space, if this char is the delimiter, then we have reached the end of 
	                                //the record
	                                if (chrArry[i] == delimiter.charAt(0)){
	                                    //processingMultiLine = false;
                                        //fix put in, setting to false caused bug when processing multiple multi-line
                                        //columns on the same record
                                        processingMultiLine = ParserUtils.isMultiLine(chrArry, delimiter, qualifier);
		                                break;
	                                }
	                                qualiFound = false;
	                                continue;
	                            }
	                        }else if (chrArry[i] == qualifier.charAt(0)){
	                            qualiFound = true;
	                        }
	                    }
	                    //check to see if we are still in multi line mode, if so grab the next line
	                    if (processingMultiLine){
	                        continue;
	                    }
                    }
                }else{
                    //throw the line into lineData var.
                    lineData += line;
                    if (processingMultiLine){
                        continue; //if we are working on a multiline rec, get the data on the next line
                    }
                }
                //********************************************************************
                //end record line break logic
                //********************************************************************


                // column values
                columns = ParserUtils.splitLine(lineData, delimiter, qualifier);
                lineData = "";
                mdkey = ParserUtils.getCMDKeyForDelimitedFile(columnMD, columns);
                cmds = ParserUtils.getColumnMetaData(mdkey, columnMD);
                columnCount = cmds.size();
                // DEBUG
                
                // for (int i = 0; i < columns.size(); i++){ System.out.println(columns.get(i)); }
                 

                // Incorrect record length on line log the error. Line
                // will not be included in the dataset
                if (columns.size() > columnCount) {
                    // log the error
                    addError("TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                    continue;
                } else if (columns.size() < columnCount) {
                    if (handleShortLines) {
                        // We can pad this line out
                        while (columns.size() < columnCount) {
                            columns.add("");
                        }

                        // log a warning
                        addError("PADDED LINE TO CORRECT NUMBER OF COLUMNS", lineCount, 1);

                    } else {
                        addError("TOO FEW COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                        continue;
                    }
                }

                row = new Row();
                row.setMdkey(mdkey.equals("detail") ? null : mdkey);  //try to limit the memory use
                row.setCols(columns);
                row.setRowNumber(lineCount);
                /** add the row to the array */
                rows.add(row);
            }
        } finally {
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * Changes the value of a specified column in a row in the set. This change is in memory, and
     * does not actually change the data in the file that was read in.
     * @param columnName - String Name of the column
     * @param value - String value to assign to the column.
     * @exception Exception - exception will be thrown if pointer in not on a valid row
     */
    public void setValue(String columnName, String value) throws Exception {
        Row row = null;
        
       /** get a reference to the row */
        row = (Row) rows.get(pointer);
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);
        /** change the value of the column */
        row.setValue(ParserUtils.findColumn(columnName, cmds), value);  
      
     }

    /**
     * Goes to the top of the data set. This will put the pointer one record before the first in the
     * set. Next() will have to be called to get the first record after this call.
     */
    public void goTop() {
        pointer = -1;
    }

    /**
     * Goes to the last record in the dataset
     */
    public void goBottom() {
        pointer = rows.size() - 1;
    }

    /**
     * Moves to the next record in the set. Returns true if move was a success, false if not
     * @return boolean
     */
    public boolean next() {
        if (pointer < rows.size() && pointer + 1 != rows.size()) {
            pointer++;
            return true;
        }
        return false;
    }

    /**
     * Moves back to the previous record in the set return true if move was a success, false if not
     * @return boolean
     */
    public boolean previous() {
        if (pointer <= 0) {
            return false;
        }
        pointer--;
        return true;
    }

    /**
     * Returns the string value of a specified column
     * @param column - Name of the column
     * @exception NoSuchElementException
     * @return String
     */
    public String getString(String column) throws NoSuchElementException {
        Row row = (Row)rows.get(pointer);
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);
        
        if (upperCase) {
            // convert data to uppercase before returning
            return row.getValue(ParserUtils.findColumn(column, cmds)).toUpperCase();
        }

        if (lowerCase) {
            // convert data to lowercase before returning
            return row.getValue(ParserUtils.findColumn(column, cmds)).toLowerCase();
        }

        // return value as how it is in the file
        return row.getValue(ParserUtils.findColumn(column, cmds));
    }

    /**
     * Returns the double value of a specified column
     * @param column - Name of the column
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return double
     */
    public double getDouble(String column) throws NoSuchElementException, NumberFormatException {
        String s = null;
        StringBuffer newString = new StringBuffer();
        String[] allowedChars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "-"};
        Row row = (Row)rows.get(pointer);
        
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);
        s = ((Row) rows.get(pointer)).getValue(ParserUtils.findColumn(column, cmds));

        if (!strictNumericParse) {
            if (s.trim().length() == 0) {
                return 0;
            }
            for (int i = 0; i < s.length(); i++) {
                for (int j = 0; j < allowedChars.length; j++) {
                    if (s.substring(i, i + 1).equals(allowedChars[j])) {
                        newString.append(s.substring(i, i + 1));
                        break;
                    }
                }
            }
            if (newString.length() == 0 || (newString.length() == 1 && newString.toString().equals("."))
                    || (newString.length() == 1 && newString.toString().equals("-"))) {
                newString.append("0");
            }
        } else {
            newString.append(s);
        }

        return Double.parseDouble(newString.toString());
    }

    /**
     * Returns the interger value of a specified column
     * @param column - Name of the column
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return double
     */
    public int getInt(String column) throws NoSuchElementException, NumberFormatException {
        String s = null;
        StringBuffer newString = new StringBuffer();
        String[] allowedChars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-"};
        Row row = (Row)rows.get(pointer);
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);

        s = row.getValue(ParserUtils.findColumn(column, cmds));

        if (!strictNumericParse) {
            if (s.trim().length() == 0) {
                return 0;
            }
            for (int i = 0; i < s.length(); i++) {
                for (int j = 0; j < allowedChars.length; j++) {
                    if (s.substring(i, i + 1).equals(allowedChars[j])) {
                        newString.append(s.substring(i, i + 1));
                        break;
                    }
                }
            }
            // check to make sure we do not have a single length string with
            // just a minus sign
            if (newString.length() == 0 || (newString.length() == 1 && newString.toString().equals("-"))) {
                newString.append("0");
            }
        } else {
            newString.append(s);
        }

        return Integer.parseInt(newString.toString());
    }

    /**
     * Returns the date value of a specified column. This assumes the date is in yyyyMMdd. If your
     * date is not in this format, see getDate(String,SimpleDateFormat)
     * @param column - Name of the column
     * @exception ParseException
     * @return Date
     */
    public Date getDate(String column) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s = null;
        Row row = (Row)rows.get(pointer);
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);
        
        s = row.getValue(ParserUtils.findColumn(column, cmds));
        return sdf.parse(s);
    }

    /**
     * Returns the date value of a specified column. This should be used if the date is NOT in
     * yyyyMMdd format. The SimpleDateFormat object will specify what kind of format the date is in.
     * @param column - Name of the column
     * @param sdf - SimpleDateFormat of the date
     * @exception ParseException
     * @see java.text.SimpleDateFormat
     * @return Date
     */
    public Date getDate(String column, SimpleDateFormat sdf) throws ParseException {
        String s = null;
        Row row = (Row)rows.get(pointer);
        List cmds = ParserUtils.getColumnMetaData(row.getMdkey(), columnMD);
        
        s = row.getValue(ParserUtils.findColumn(column, cmds));
        return sdf.parse(s);
    }

    /**
     * Returns a String array of column names in the DataSet.  This will assume
     * 'detail' <RECORD> ID.
     * @return String[]
     */
    public String[] getColumns() {
        ColumnMetaData column = null;
        String[] array = null;

        if (columnMD != null) {
            List cmds = ParserUtils.getColumnMetaData("detail", columnMD);
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                column = (ColumnMetaData) cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }
    
    /**
     * Returns a String array of column names in the DataSet for a given <RECORD> id
     * 
     * @param recordID
     * @return String[]
     */
    public String[] getColumns(String recordID) {
        ColumnMetaData column = null;
        String[] array = null;

        if (columnMD != null) {
            List cmds = ParserUtils.getColumnMetaData(recordID, columnMD);
            array = new String[cmds.size()];
            for (int i = 0; i < cmds.size(); i++) {
                column = (ColumnMetaData) cmds.get(i);
                array[i] = column.getColName();
            }
        }

        return array;
    }

    /**
     * Returns the line number the pointer is on. These are the actual line numbers from the flat
     * file, before any sorting.
     * @exception NoSuchElementException
     * @exception NumberFormatException
     * @return int
     */
    public int getRowNo() {
        return ((Row) rows.get(pointer)).getRowNumber();
    }

    /**
     * Returns A Collection Of DataErrors that happened during processing
     * @return Vector
     */
    public List getErrors() {
        return errors;
    }

    /**
     * Adds a new error to this DataSet. These can be collected, and retreived after processing
     * @param errorDesc - String description of error
     * @param lineNo - int line number error occured on
     * @param errorLevel - int errorLevel 1,2,3 1=warning 2=error 3= severe error
     */
    public void addError(String errorDesc, int lineNo, int errorLevel) {
        DataError de = new DataError();
        de.setErrorDesc(errorDesc);
        de.setLineNo(lineNo);
        de.setErrorLevel(errorLevel);
        errors.add(de);
    }

    /**
     * Removes a row from the dataset. Once the row is removed the pointer will be sitting on the
     * record previous to the deleted row.
     */
    public void remove() {
        rows.remove(pointer);
        pointer--;
    }

    /**
     * Returns the index the pointer is on for the array
     * @return int
     */
    public int getIndex() {
        return pointer;
    }

    /**
     * Sets the absolute position of the record pointer
     * @param localPointer - int
     * @exception IndexOutOfBoundsException
     */
    public void absolute(int localPointer) throws IndexOutOfBoundsException {
        if (localPointer < 0 || localPointer > rows.size() - 1) {
            throw new IndexOutOfBoundsException("INVALID POINTER LOCATION: " + localPointer);
        }

        pointer = localPointer;
    }
    
    /**
     * Checks to see if the row has the given <RECORD> id
     * 
     * @param recordID
     * @return boolean
     */
     public boolean isRecordID(String recordID){
         String rowID = ((Row)rows.get(pointer)).getMdkey();
         if (rowID == null)rowID = "detail";

         return rowID.equals(recordID);
     }
    
    
    /**
     * Returns the total number of rows parsed in from the file
     * 
     * 
     * @return int - Row Count
     */
    public int getRowCount(){
        return rows.size();
    }

    
    /**
     * Returns total number of records which contained a parse error in the file.
     * 
     * @return int - Record Error Count
     */
    public int getErrorCount(){
        if (getErrors() != null){
            return getErrors().size();
        }
        
        return 0;
    }
    
    
    
    /**
     * Returns true or false as to whether or not the line number contains an error. The import will
     * skip the line if it contains an error and it will not be processed
     * @param lineNo - int line number
     * @return boolean
     */
    public boolean isAnError(int lineNo) {
        for (int i = 0; i < errors.size(); i++) {
            if (((DataError) errors.get(i)).getLineNo() == lineNo && ((DataError) errors.get(i)).getErrorLevel() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Orders the data by column(s) specified. This will reposition the cursor to the top of the
     * DataSet when executed.  This is currently not supported when specying <RECORD> elements in 
     * the mapping.  An exception will be thrown if this situation occurs
     * 
     * @param ob - OrderBy object
     * @exception Exception
     * @see com.pz.reader.ordering.OrderBy
     * @see com.pz.reader.ordering.OrderColumn
     */
    public void orderRows(OrderBy ob) throws Exception{
        if (columnMD.size() > 1){
            throw new Exception("orderRows does not currently support ordering with <RECORD> mappings");
        }        
        List cmds = ParserUtils.getColumnMetaData("detail", columnMD);
        if (ob != null && rows != null) {
            ob.setColumnMD(cmds);
            Collections.sort(rows, ob);
            goTop();
        }
    }

    /**
     * Sets data in the DataSet to lowercase
     */
    public void setLowerCase() {
        upperCase = false;
        lowerCase = true;
    }

    /**
     * Sets data in the DataSet to uppercase
     */
    public void setUpperCase() {
        upperCase = true;
        lowerCase = false;
    }

    /**
     * Setting this to True will parse text as is and throw a NumberFormatException. Setting to
     * false, which is the default, will remove any non numeric charcter from the field. The
     * remaining numeric chars's will be returned. If it is an empty string,or there are no numeric
     * chars, 0 will be returned for getInt() and getDouble()
     * @param strictNumericParse The strictNumericParse to set.
     */
    public void setStrictNumericParse(boolean strictNumericParse) {
        this.strictNumericParse = strictNumericParse;
    }

    /**
     * erases the dataset and releases memory for the JVM to reclaim
     */
    public void freeMemory() {
        if (rows != null)
            rows.clear();
        if (errors != null)
            errors.clear();
        if (columnMD != null)
            columnMD.clear();
    }
    
    
    /**
     * Writes this current DataSet out to the specified Excel file
     *
     * @param excelFileToBeWritten 
     * @exception Exception
     */
    public void writeToExcel(File excelFileToBeWritten) throws Exception{
        
        ExcelTransformer et = new ExcelTransformer(this, excelFileToBeWritten);
        et.writeExcelFile();
        
    }
    
    
    /**
     * Returns the version number of this pzFileReader
     *
     *@return String
     */
    public String getReaderVersion(){
       return Version.VERSION;
    }
    /**
     * @return Returns the handleShortLines.
     */
    public boolean isHandleShortLines() {
        return handleShortLines;
    }
    /**
     * This is used for LargeDataSet compatability.  Setting this will have no affect on the DataSet parser. 
     * It must be passed on the constructor
     * 
     * @param handleShortLines The handleShortLines to set.
     */
    public void setHandleShortLines(boolean handleShortLines) {
        this.handleShortLines = handleShortLines;
    }
}
