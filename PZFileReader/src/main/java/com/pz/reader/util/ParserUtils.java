/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
 */
package com.pz.reader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.pz.reader.structure.ColumnMetaData;
import com.pz.reader.xml.XMLRecordElement;



/**
 * @author zepernick Static utilities that are used to perform parsing in the DataSet class These
 *         can also be used for low level parsing, if not wishing to use the DataSet class.
 * @version 2.0
 */
public class ParserUtils {

    /**
     * Returns an ArrayList of items in a delimited string. If there is no qualifier around the
     * text, the qualifier parameter can be left null, or empty. There should not be any line breaks
     * in the string. Each line of the file should be passed in individually.
     * @param line - String of data to be parsed
     * @param delimiter - Delimiter seperating each element
     * @param qualifier - qualifier which is surrounding the text
     * @return ArrayList
     */
    public static List splitLine(String line, String delimiter, String qualifier) {
        ArrayList list = new ArrayList();
        // String temp = "";
        boolean beginQualifier = false;
        // this will be used for delimted files that have some items qualified and some items dont
        boolean beginNoQualifier = false;
        StringBuffer sb = new StringBuffer();

        // trim hard leading spaces at the begining of the line
        line = lTrim(line);
        for (int i = 0; i < line.length(); i++) {
            String remainderOfLine = line.substring(i); // data of the line which has not yet been
            // read
            // check to see if there is a text qualifier
            if (qualifier != null && qualifier.trim().length() > 0) {
                if (line.substring(i, i + 1).equals(qualifier) && !beginQualifier && !beginNoQualifier) {
                    // begining of a set of data
                    beginQualifier = true;
                } else if (!beginQualifier && !beginNoQualifier
                        && !line.substring(i, i + 1).equals(qualifier)
                        && !lTrim(remainderOfLine).startsWith(qualifier)) {
                    // try to account for empty space before qualifier starts
                    // we have not yet begun a qualifier and the char we are on is NOT
                    // a qualifier. Start reading data
                    beginNoQualifier = true;
                    // make sure that this is not just an empty column with no qualifiers. ie
                    // "data",,"data"
                    if (line.substring(i, i + 1).equals(delimiter)) {
                        list.add(sb.toString());
                        sb.delete(0, sb.length());
                        beginNoQualifier = false;
                        continue;// grab the next char
                    }
                    sb.append(line.substring(i, i + 1));
                } else if ((!beginNoQualifier) && line.substring(i, i + 1).equals(qualifier)
                        && beginQualifier && (lTrim(line.substring(i + 1)).length() == 0 ||
                        // this will be true on empty undelmited columns at the end of theline
                        lTrimKeepTabs(line.substring(i + 1)).substring(0, 1).equals(delimiter))) {
                    // end of a set of data that was qualified
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                    beginQualifier = false;
                    // add to "i" so we can get past the qualifier, otherwise it is read into a set
                    // of data which
                    // may not be qualified. Find out how many spaces to the delimiter
                    int offset = getDelimiterOffset(line, i, delimiter) - 1;
                    // subtract 1 since i is going to get incremented again at the top of the loop

                    // System.out.println("offset: " + offset);
                    if (offset < 1) {
                        i++;
                    } else {
                        i += offset;
                    }
                } else if (beginNoQualifier && line.substring(i, i + 1).equals(delimiter)) {
                    // check to see if we are done with an element that was not being qulified
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                    beginNoQualifier = false;
                } else if (beginNoQualifier || beginQualifier) {
                    // getting data in a NO qualifier element or qualified element
                    sb.append(line.substring(i, i + 1));
                }

            } else {
                // not using a qualifier. Using a delimiter only
                if (line.substring(i, i + 1).equals(delimiter)) {
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(line.substring(i, i + 1));
                }
            }
        }

        // remove the ending text qualifier if needed
        if (qualifier != null && qualifier.trim().length() > 0 && sb.toString().trim().length() > 0) {
            if (sb.toString().trim().substring(sb.toString().trim().length() - 1).equals(qualifier)) {
             //   System.out.println(sb.toString());
                String s = sb.toString().trim().substring(0, sb.toString().trim().length() - 1);
                sb.delete(0, sb.length());
                sb.append(s);
            }

        }

        if (qualifier == null || 
                qualifier.trim().length() == 0 || 
                beginQualifier || 
                beginNoQualifier || 
                line.trim().endsWith(delimiter)) {
            // also account for a delimiter with an empty column at the end that was not qualified
            // check to see if we need to add the last column in..this will happen on empty columns
            // add the last column
            list.add(sb.toString());
        }

        sb = null;

        return (List) list;
    }

    /**
     * reads from the specified point in the line and returns how many chars to the specified
     * delimter
     * @param line
     * @param start
     * @param delimiter
     * @return int
     */

    public static int getDelimiterOffset(String line, int start, String delimiter) {
        int offset = 0;
        for (int i = start; i < line.length(); i++) {
            offset++;
            if (line.substring(i, i + 1).equals(delimiter)) {
                return offset;
            }
        }
        return -1;
    }

    /**
     * Removes empty space from the begining of a string
     * @param value - to be trimmed
     * @return String
     */
    public static String lTrim(String value) {
        StringBuffer returnVal = new StringBuffer();
        boolean gotAChar = false;

        for (int i = 0; i < value.length(); i++) {
            if (value.substring(i, i + 1).trim().length() == 0 && !gotAChar) {
                continue;
            } else {
                gotAChar = true;
                returnVal.append(value.substring(i, i + 1));
            }
        }

        return returnVal.toString();

    }
    
    /**
     * Removes empty space from the begining of a string, except for tabs
     * @param value - to be trimmed
     * @return String
     */
    public static String lTrimKeepTabs(String value) {
        StringBuffer returnVal = new StringBuffer();
        boolean gotAChar = false;

        for (int i = 0; i < value.length(); i++) {
            if (!value.substring(i, i + 1).equals("\t") && value.substring(i, i + 1).trim().length() == 0 && !gotAChar) {
                continue;
            } else {
                gotAChar = true;
                returnVal.append(value.substring(i, i + 1));
            }
        }

        return returnVal.toString();

    }

    /**
     * Removes a single string character from a given string
     * @param character - string char
     * @param theString - string to search
     * @return String
     */
    public static String removeChar(String character, String theString) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < theString.length(); i++) {
            if (theString.substring(i, i + 1).equalsIgnoreCase(character)) {
                continue;
            }
            s.append(theString.substring(i, i + 1));
        }

        return s.toString();

    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited files. The first
     * line of the file which contains data will be used as the column names
     * @param theStream
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return Map - ColumnMetaData
     * @deprecated see getColumMDFromFile(String, String, String)
     */
    public static Map getColumnMDFromFile(InputStream theStream, String delimiter, String qualifier) throws Exception {
        InputStreamReader isr = null;
        BufferedReader br = null;
        //FileReader fr = null;
        String line = null;
        List lineData = null;
        List results = new ArrayList();
        Map columnMD = new LinkedHashMap();

        try {
            isr = new InputStreamReader(theStream);
            br = new BufferedReader(isr);
            //fr = new FileReader(theFile);
            //br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter, qualifier);
                for (int i = 0; i < lineData.size(); i++) {
                    ColumnMetaData cmd = new ColumnMetaData();
                    cmd.setColName((String) lineData.get(i));
                    results.add(cmd);
                }
                break;
            }
        } finally {
            if (lineData != null)
                lineData.clear();
            if (br != null)
                br.close();
            if (isr != null)
                isr.close();
        }
        
        columnMD.put("detail",results);
        
        return columnMD;
    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited files. The first
     * line of the file which contains data will be used as the column names
     * @param line
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return ArrayList - ColumnMetaData
     */
    public static Map getColumnMDFromFile(String line, String delimiter, String qualifier) throws Exception {
        List lineData = null;
        List results = new ArrayList();
        Map columnMD = new LinkedHashMap();        

        lineData = splitLine(line, delimiter, qualifier);
        for (int i = 0; i < lineData.size(); i++) {
            ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColName((String) lineData.get(i));
            results.add(cmd);
        }
        
        columnMD.put("detail",results);
        
        return columnMD;
    }
    
    
    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited files. The first
     * line of the file which contains data will be used as the column names
     * @param theFile
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return ArrayList - ColumnMetaData
     */
    public static List getColumnMDFromFile(File theFile, String delimiter, String qualifier) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        String line = null;
        List lineData = null;
        List results = new ArrayList();

        try {
            fr = new FileReader(theFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter, qualifier);
                for (int i = 0; i < lineData.size(); i++) {
                    ColumnMetaData cmd = new ColumnMetaData();
                    cmd.setColName((String) lineData.get(i));
                    results.add(cmd);
                }
                break;
            }
        } finally {
            if (lineData != null)
                lineData.clear();
            if (br != null)
                br.close();
            if (fr != null)
                fr.close();
        }
        return results;
    }

    /**
     * @param columnName
     * @param columnMD - vector of ColumnMetaData objects
     * @return int - position of the column in the file
     * @throws NoSuchElementException
     */
    public static int findColumn(String columnName, List columnMD) throws NoSuchElementException {
        for (int i = 0; i < columnMD.size(); i++) {
            ColumnMetaData cmd = (ColumnMetaData) columnMD.get(i);
            if (cmd.getColName().equalsIgnoreCase(columnName))
                return i;
        }

        throw new NoSuchElementException("Column Name: " + columnName + " does not exist");
    }
    
    
    
    /**
     * Determines if the given line is the first part of a multiline record
     * 
     * @param chrArry - char data of the line
     * @param delimiter - delimiter being used
     * @param qualifier - qualifier being used
     * @return boolean
     */
    public static boolean isMultiLine(char[] chrArry, String delimiter, String qualifier){
        
        //check if the last char is the qualifier, if so then this a good chance it is not multiline
        if (chrArry[chrArry.length -1] != qualifier.charAt(0)){
            //could be a potential line break
            boolean qualiFound = false;
            for (int i = chrArry.length - 1; i >= 0; i--){
               // System.out.println("char: " + chrArry[i]);
                //check to see if we can find a qualifier followed by a delimiter
                //remember we are working are way backwards on the line
                if (qualiFound){
                    if (chrArry[i] ==  ' '){
                        continue;
                    }else{
                        //not a space, if this char is the delimiter, then we have a line break
                        //in the record
                        if (chrArry[i] == delimiter.charAt(0)){
                            return true;
                        }
                        qualiFound = false;
                        continue;
                    }
                }else if (chrArry[i] == delimiter.charAt(0)){
                    //if we have a delimiter followed by a qualifier, then we have moved on
                    //to a new element and this could not be multiline.  start a new loop here in case there is
                    //space between the delimiter and qualifier
                    for (int j = i -1; j >= 0; j--){
                        if (chrArry[j] == ' '){
                            continue;
                        }else if (chrArry[j] == qualifier.charAt(0)){
                            return false;
                        }
                        break;
                    }
                    
                }else if (chrArry[i] == qualifier.charAt(0)){
                    qualiFound = true;
                }
            }
        }else{
            //we have determined that the last char on the line is a qualifier.  This most likely means
            //that this is not multiline, however we must account for the following scenario
            //data,data,"
            //data
            ///data"
            for (int i = chrArry.length - 1; i >= 0; i--){
                if (i == chrArry.length - 1 || chrArry[i] == ' '){
                    // skip the first char, or any spaces we come across between the delimiter and qualifier
                    continue; 
                }
                if (chrArry[i] == delimiter.charAt(0)){
                    return true;
                }
                break;
            }
        }
        
        return false;
    }
    

    /**
     * Returns a map with the MD id's and their record lengths.  This
     * is used for fixed length parsing
     * 
     * @param columnMD
     * @return Map
     * @exception Exception
     */
    public static Map calculateRecordLengths(Map columnMD) throws Exception{
        Map recordLengths = new HashMap();
        List cmds = null;
        
        Iterator columnMDIt = columnMD.keySet().iterator();
        while (columnMDIt.hasNext()){
            String key = (String)columnMDIt.next();                        
            if (key.equals("detail")){
                cmds = (List)columnMD.get(key);
            }else{
                cmds = ((XMLRecordElement)columnMD.get(key)).getColumns();
            }
            
            int recordLength = 0;
            for (int i = 0; i < cmds.size(); i++) {
                recordLength += ((ColumnMetaData) cmds.get(i)).getColLength();
            }
            
            recordLengths.put(key, new Integer(recordLength));
            
        }
        
        return recordLengths;
                
    }
    
    
    
    /**
     * Returns the key to the list of ColumnMetaData objects.  Returns the 
     * correct MetaData per the mapping file and the data contained on the line
     * 
     * 
     * @param columnMD
     * @param line
     * @return List - ColumMetaData
     */
    public static String getCMDKeyForFixedLengthFile(Map columnMD, String line){
        if (columnMD.size() == 1){
            //no <RECORD> elments were specifed for this parse, just return the detail id
            return "detail";
        }
        Iterator keys = columnMD.keySet().iterator();
        //loop through the XMLRecordElement objects and see if we need a different MD object
        while (keys.hasNext()){
            String key = (String)keys.next();
            if (key.equals("detail")) continue; //skip this key will be assumed if none of the others match
            XMLRecordElement recordXMLElement = (XMLRecordElement) columnMD.get(key);
            
            if (recordXMLElement.getEndPositition() > line.length()){
                //make sure our substring is not going to fail
                continue;
            }
            int subfrm = recordXMLElement.getStartPosition() -1; //convert to 0 based
            int subto = recordXMLElement.getEndPositition();
            if (line.substring(subfrm, subto).equals(recordXMLElement.getIndicator())){
               //we found the MD object we want to return
                return key;
            }
            
        }
        
        //must be a detail line
        return "detail";
        
    }
    
    /**
     * Returns the key to the list of ColumnMetaData objects.  Returns the 
     * correct MetaData per the mapping file and the data contained on the line
     * 
     * 
     * @param columnMD
     * @param lineElements
     * @return List - ColumMetaData
     */
    public static String getCMDKeyForDelimitedFile(Map columnMD, List lineElements){
        if (columnMD.size() == 1){
            //no <RECORD> elments were specifed for this parse, just return the detail id
            return "detail";
        }
        Iterator keys = columnMD.keySet().iterator();
        //loop through the XMLRecordElement objects and see if we need a different MD object
        while (keys.hasNext()){
            String key = (String)keys.next();
            if (key.equals("detail")) continue; //skip this key will be assumed if none of the others match
            XMLRecordElement recordXMLElement = (XMLRecordElement) columnMD.get(key);
            
            if (recordXMLElement.getElementNumber() > lineElements.size()){
                //make sure our substring is not going to fail
                continue;
            }
            String lineElement = (String)lineElements.get(recordXMLElement.getElementNumber() - 1);
            if (lineElement.equals(recordXMLElement.getIndicator())){
               //we found the MD object we want to return
                return key;
            }
            
        }
        
        //must be a detail line
        return "detail";
        
    }
    
    /**
     * Returns a list of ColumMetaData objects for the given key
     * 
     * @param key
     * @param columnMD
     * @return List
     */
    public static List getColumnMetaData(String key, Map columnMD){
        
        if (key == null || key.equals("detail")){
            return (List)columnMD.get("detail");
        }
        
        return ((XMLRecordElement)columnMD.get(key)).getColumns(); 
        
    }
    
    
    /**
     * Create an InputStream based on a File.
     * @param file The file.
     * @return the InputStream.
     * @throws Exception
     */
    public static InputStream createInputStream(File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("null not allowed");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("file does not exist "
                    + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new FileNotFoundException("file cannot be read "
                    + file.getAbsolutePath());
        }
        InputStream xmlStream = null;
        xmlStream = new FileInputStream(file.getAbsolutePath());
        return xmlStream;
    }
    
    /**
     * Closes the given reader
     * @param reader
     * 
     */
    public static void closeReader(Reader reader){
        try{
           reader.close();
        }catch(Exception ignore){}
        
    }
        
    /**
     * Closes the given reader
     * @param reader
     * 
     */
    public static void closeReader(InputStream reader){
        try{
           reader.close();
        }catch(Exception ignore){}
        
    }
}
