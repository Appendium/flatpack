/*
Copyright 2006 Paul Zepernick

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
*/
package com.pz.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pz.reader.ordering.OrderBy;
import com.pz.reader.structure.ColumnMetaData;
import com.pz.reader.structure.Row;
import com.pz.reader.util.ParserUtils;
import com.pz.reader.xml.PZMapParser;

/**
 * @author Paul Zepernick
 *
 *Provides limited DataSet functionality for large files.  This will not read the file into memory.
 *The following methods have been disabled previous(), goTop(), goBottom(), remove(), getIndex(),
 *absolute(), orderRows() 
 */
public class LargeDataSet extends DataSet{
    
    private final String DELIMITED_FILE = "delimited";
    
    private final String FIXEDLENGTH_FILE = "fixed";
    
    private String fileType; //file type being parsed
    
    private BufferedReader br = null; //reader used to read the file
    
    private InputStreamReader isr = null;
    
    private InputStream is = null; //stream used to read the file
    
    private int lineCount = 0; //keeps track of the current line being procssed in the file
    
    //  used for delimited files
    private boolean ignoreFirstRecord = false;
    private boolean createMDFromFile = false;
    private boolean processedFirst = false;
    private String delimiter = null;
    private String qualifier = null;
    private int columnCount = 0;
    
    //used for fixed length files
    Map recordLengths = null; //map of record lengths corrisponding to the ID's in the columnMD array
	 
    
    /**
     * Constructor based on InputStream.
     * Constructs a new LargeDataSet using the PZMAP XML file layout method. This is used for a DELIMITED
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
    public LargeDataSet(InputStream pzmapXMLStream, InputStream dataSourceStream, String delimiter, String qualifier, boolean ignoreFirstRecord,
            boolean handleShortLines) throws Exception {
        
        this.fileType = DELIMITED_FILE;
        this.is = dataSourceStream;
        this.isr = new InputStreamReader(is);
        this.br = new BufferedReader(this.isr);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = ignoreFirstRecord;
        setHandleShortLines(handleShortLines);
        columnMD = PZMapParser.parse(pzmapXMLStream);

    }
    
    /**
     * Constructs a new LargeDataSet using the first line of data found in the text file as the column
     * names. This is used for a DELIMITED text file. esacpe sequence reference: \n newline <br> \t tab <br> \b backspace <br> \r return <br> \f 
     * form feed <br> \\ backslash <br> \' single quote <br> \" double quote
     * @param dataSource - text file datasource to read from
     * @param delimiter - Char the file is delimited By
     * @param qualifier - Char text is qualified by
     * @param handleShortLines - when flaged as true, lines with less columns then the amount of
     *            column headers will be added as empty's instead of producing an error
     * @exception Exception
     */
    public LargeDataSet(File dataSource, String delimiter, String qualifier, boolean handleShortLines) throws Exception {
        this.fileType = DELIMITED_FILE;
        setHandleShortLines(handleShortLines);
        InputStream dataSourceStream = null;
        System.out.println("creating input stream");
        dataSourceStream = ParserUtils.createInputStream(dataSource);
        this.is = dataSourceStream;
        System.out.println("creating input stream reader");
        this.isr = new InputStreamReader(is);
        System.out.println("creating buffered reader");
        this.br = new BufferedReader(this.isr);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
        this.ignoreFirstRecord = false;
        this.createMDFromFile = true;
    }
    
    
    /**
     * New constructor based on InputStream.
     * Constructs a new LargeDataSet using the PZMAP XML file layout method. This is used for a FIXED
     * LENGTH text file.
     * @param pzmapXMLStream - Reference to the xml file InputStream holding the pzmap
     * @param dataSourceStream - Delimited file InputStream to read from
     * @param handleShortLines - Pad lines out to fit the fixed length
     * @exception Exception
     */
    public LargeDataSet(InputStream pzmapXMLStream, InputStream dataSourceStream, boolean handleShortLines) throws Exception {
        this.fileType = FIXEDLENGTH_FILE;
        this.is = dataSourceStream;
        this.isr = new InputStreamReader(is);
        this.br = new BufferedReader(this.isr);
        setHandleShortLines(handleShortLines);

        columnMD = PZMapParser.parse(pzmapXMLStream);

    }
    
    
    /**
     * Loads up the next record from the file, returns false if EOF
     * 
     * @return boolean
     */
    public boolean next(){
        try{
            if (this.fileType.equals(this.DELIMITED_FILE)){
                return readNextDelimited();
            }
            
            //assume fixed length file
            return readNextFixedLen();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Not Supported!
     * @return boolean
     */
	public boolean previous() {
	    throw new RuntimeException("previous() is Not Implemented For LargeDataSet");
	}
	
    /**
     * Not Supported!
     * @param excelFileToBeWritten
     * @exception Exception
     */
    public void writeToExcel(File excelFileToBeWritten) throws Exception{
        throw new RuntimeException("writeToExcel() is Not Implemented For LargeDataSet");        
    }
    
    /**
     * Not Supported! 
     * @param ob - OrderBy object
     * @exception Exception
     * @see com.pz.reader.ordering.OrderBy
     * @see com.pz.reader.ordering.OrderColumn
     */
    public void orderRows(OrderBy ob) throws Exception{
        throw new RuntimeException("orderRows() is Not Implemented For LargeDataSet"); 
    }
    
    /**
     * Not Supported!
     * @param localPointer - int
     * @exception IndexOutOfBoundsException
     */
    public void absolute(int localPointer) throws IndexOutOfBoundsException {
        throw new RuntimeException("absolute() is Not Implemented For LargeDataSet"); 
    }
    
    /**
     *Not Supported!
     */
    public void remove() {
        throw new RuntimeException("remove() is Not Implemented For LargeDataSet");
    }

    /**
     * Not Supported!
     * @return int
     */
    public int getIndex() {
        throw new RuntimeException("getIndex() is Not Implemented For LargeDataSet");
    }
    
    /**
     * Not Supported!
     */
    public void goBottom() {
        throw new RuntimeException("goBottom() is Not Implemented For LargeDataSet");
    }
    
    /**
     * Not Supported!
     */
    public void goTop() {
        throw new RuntimeException("goTop() is Not Implemented For LargeDataSet");
    }
    
    /**
     * Not Supported!
     * @param columnName - String Name of the column
     * @param value - String value to assign to the column.
     * @exception Exception - exception will be thrown if pointer in not on a valid row
     */
    public void setValue(String columnName, String value) throws Exception {
        throw new RuntimeException("setValue() is Not Implemented For LargeDataSet"); 
      
     }
    
    /**
     * erases the dataset and releases memory for the JVM to reclaim.  This will also close out
     * the readers used to read the file in.
     */
    public void freeMemory() {
        super.freeMemory();
        
        ParserUtils.closeReader(br);
        ParserUtils.closeReader(isr);
        ParserUtils.closeReader(is);
    }
	
	
	//reads the next record and sets it into the row array
	private boolean readNextDelimited() throws Exception{
        String line = null;
        Row row = null;
        List columns = null;
        boolean processingMultiLine = false;
        boolean readRecordOk = false;
        String lineData = "";
        String mdkey = null;
        List cmds = null;
        
        if (rows == null)rows = new ArrayList();
        if (errors == null)errors = new ArrayList();

        rows.clear();
        /** loop through each line in the file */
        while ((line = br.readLine()) != null) {
            lineCount++;
            /** empty line skip past it */
            if (!processingMultiLine && line.trim().length() == 0) {
                continue;
            }
            // check to see if the user has elected to skip the first record
            if (!this.processedFirst && this.ignoreFirstRecord) {
                this.processedFirst = true;
                continue;
            }
            //column names are coming from inside the CSV file
            else if (!this.processedFirst && this.createMDFromFile){ 
                this.processedFirst = true;
                this.columnMD = ParserUtils.getColumnMDFromFile(line, delimiter, qualifier);
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
                                    processingMultiLine = false;
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
            columns = ParserUtils.splitLine(line, this.delimiter, this.qualifier);
            lineData = "";
            mdkey = ParserUtils.getCMDKeyForDelimitedFile(columnMD, columns);
            cmds = ParserUtils.getColumnMetaData(mdkey, columnMD);
            this.columnCount = cmds.size();
            // DEBUG
            
            // for (int i = 0; i < columns.size(); i++){ System.out.println(columns.get(i)); }
             

            // Incorrect record length on line log the error. Line
            // will not be included in the dataset
            if (columns.size() > columnCount) {
                // log the error
                addError("TOO MANY COLUMNS WANTED: " + columnCount + " GOT: " + columns.size(), lineCount, 2);
                continue;
            } else if (columns.size() < columnCount) {
                if (isHandleShortLines()) {
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
            row.setCols(columns);
            row.setRowNumber(lineCount);
            //with the LargeDataSet we are never going to store more than 1 record in memory
            rows.add(row);
            readRecordOk = true;
            break;
        }
        
        return readRecordOk;

	}
	
	private boolean readNextFixedLen() throws Exception{
	    String line = null;
	    Row row = null;
	    int recordLength = 0;
	    int lineCount = 0;
	    int recPosition = 0;
	    String mdkey = null;
	    List cmds = null;
	    boolean readRecordOk = false;

        if (rows == null) rows = new ArrayList();
        if (errors == null) errors = new ArrayList();

        if (this.recordLengths == null) this.recordLengths = ParserUtils.calculateRecordLengths(columnMD);
        
        rows.clear();
        //loop through each line in the file
        while ((line = br.readLine()) != null) {
            this.lineCount++;
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
                if (isHandleShortLines()) {
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
            
            readRecordOk = true;
            break;
        }
        
        return readRecordOk;

	}
}
