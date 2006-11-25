/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting PZFileReader.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: ColorProvider.java 74 2006-10-24 22:19:05Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.pzfilereader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import net.sf.pzfilereader.converter.PZConvertException;
import net.sf.pzfilereader.converter.PZConverter;
import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.xml.XMLRecordElement;

/**
 *  Static utilities that are used to perform parsing in the
 *         DataSet class These can also be used for low level parsing, if not
 *         wishing to use the DataSet class.
 *         
 * @author Paul Zepernick
 * @author Benoit Xhenseval
 */
public final class ParserUtils {
    private ParserUtils() {
    }

    /**
     * @deprecated should only use the splitLine with a CHAR.
     * @param line
     * @param delimiter
     * @param qualifier
     * @return List
     */
    public static List splitLine(final String line, final String delimiter, final String qualifier) {
        return splitLine(line, delimiter != null ? delimiter.charAt(0) : 0, qualifier != null ? qualifier.charAt(0) : 0, 
                PZConstants.SPLITLINE_SIZE_INIT);
    }

    /**
     * Returns an ArrayList of items in a delimited string. If there is no
     * qualifier around the text, the qualifier parameter can be left null, or
     * empty. There should not be any line breaks in the string. Each line of
     * the file should be passed in individually.
     * Elements which are not qualified will have leading and trailing white
     * space removed.  This includes unqualified elements, which may be
     * contained in an unqualified parse: "data",  data  ,"data"
     * 
     * Special thanks to Benoit for contributing this much improved speedy parser :0)
     *
     * @author Benoit Xhenseval
     * @param line -
     *            String of data to be parsed
     * @param delimiter -
     *            Delimiter seperating each element
     * @param qualifier -
     *            qualifier which is surrounding the text
     * @param initialSize -
     *            intial capacity of the List size
     * @return List
     */
    public static List splitLine(String line, final char delimiter, final char qualifier, int initialSize) {
        List list = new ArrayList(initialSize);

        if (delimiter == 0) {
            list.add(line);
            return list;
        } else if (line == null) {
            return list;
        }

        final String trimmedLine = line.trim();
        int size = trimmedLine.length();

        if (size == 0) {
            list.add("");
            return list;
        }

        boolean insideQualifier = false;
        char previousChar = 0;
        int startBlock = 0;
        int endBlock = 0;
        boolean blockWasInQualifier = false;

        final String doubleQualifier = String.valueOf(qualifier) + String.valueOf(qualifier);
        for (int i = 0; i < size; i++) {

            final char currentChar = trimmedLine.charAt(i);
            //System.out.println(currentChar);
            if (currentChar != delimiter && currentChar != qualifier) {
                previousChar = currentChar;
                endBlock = i + 1;
                continue;
            }

            if (currentChar == delimiter) {
                // we've found the delimiter (eg ,)
                if (!insideQualifier) {
                    String trimmed = trimmedLine.substring(startBlock, endBlock > startBlock ? endBlock : startBlock + 1);
                    if (!blockWasInQualifier) {
                        trimmed = trimmed.trim();
                        trimmed = trimmed.replaceAll(doubleQualifier, String.valueOf(qualifier));
                    }

                    if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
                        list.add("");
                    } else {
                        list.add(trimmed);
                    }
                    blockWasInQualifier = false;
                    startBlock = i + 1;
                }
            } else if (currentChar == qualifier) {
                if (!insideQualifier && previousChar != qualifier) {
                    if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
                        insideQualifier = true;
                        startBlock = i + 1;
                    } else {
                        endBlock = i + 1;
                    }
                }
                //TODO
                //this is probably a pretty costly check, maybe Benoit will have a better idea of how
                //to handle
                else if (i + 1 < size && delimiter != ' ' && 
                        lTrimKeepTabs(trimmedLine.substring(i + 1)).charAt(0) != delimiter) {
                    previousChar = currentChar;
                    endBlock = i + 1;
                    continue;
                } else {
                    insideQualifier = false;
                    blockWasInQualifier = true;
                    endBlock = i;
                    // last column (e.g. finishes with ")
                    if (i == size - 1) {
                        list.add(trimmedLine.substring(startBlock, size - 1));
                        startBlock = i + 1;
                    }
                }
            }
            previousChar = currentChar;
        }

        if (startBlock < size) {
            String str = trimmedLine.substring(startBlock, size);
            str = str.replaceAll(doubleQualifier, String.valueOf(qualifier));
            if (blockWasInQualifier) {
                if (str.charAt(str.length() - 1) == qualifier) {
                    list.add(str.substring(0, str.length() - 1));
                } else {
                    list.add(str);
                }
            } else {
                list.add(str.trim());
            }
        } else if (trimmedLine.charAt(size - 1) == delimiter) {
            list.add("");
        }

        return list;
    }

    /**
     * reads from the specified point in the line and returns how many chars to
     * the specified delimter
     *
     * @param line
     * @param start
     * @param delimiter
     * @return int
     */
    public static int getDelimiterOffset(final String line, final int start, final char delimiter) {
        int idx = line.indexOf(delimiter, start);
        if (idx >= 0) {
            idx -= start - 1;
        }
        return idx;
    }

    /**
     * Removes empty space from the begining of a string
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrim(final String value) {
        if (value == null) {
            return null;
        }
        
        String trimmed = value;
        int offset = 0;
        final int maxLength = value.length();
        while (offset < maxLength && (value.charAt(offset) == ' ' || value.charAt(offset) == '\t')) {
            offset++;
        }

        if (offset > 0) {
            trimmed = value.substring(offset);
        }

        return trimmed;
    }

    /**
     * Removes empty space from the begining of a string, except for tabs
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrimKeepTabs(final String value) {
        if (value == null) {
            return null;
        }
        
        String trimmed = value;
        int offset = 0;
        final int maxLength = value.length();
        while (offset < maxLength && value.charAt(offset) == ' ') {
            offset++;
        }

        if (offset > 0) {
            trimmed = value.substring(offset);
        }

        return trimmed;
    }
    
    /**
     * Will return a null if the String is empty returns the 
     * trimmed string otherwise.
     * 
     * @param value 
     *          to be trimmed
     * @return String
     */
    public static String trimToNull(final String value) {
        if (value == null) {
            return null;
        }
        
        final String ret = value.trim();
        
        return ret.length() == 0 ? null : ret;
        
    }

    /**
     * Removes a single string character from a given string
     *
     * @param theChar -
     *            string char
     * @param theString -
     *            string to search
     * @return String
     */
    public static String removeChar(final char theChar, final String theString) {
        final StringBuffer s = new StringBuffer();
        for (int i = 0; i < theString.length(); i++) {
            final char currentChar = theString.charAt(i);
            if (currentChar != theChar) {
                s.append(currentChar);
            }
        }

        return s.toString();
    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param theStream
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return Map - ColumnMetaData
     * @deprecated see getColumMDFromFile(String, String, String)
     */
    public static Map getColumnMDFromFile(final InputStream theStream, final String delimiter, final String qualifier)
            throws Exception {
        InputStreamReader isr = null;
        BufferedReader br = null;
        // FileReader fr = null;
        String line = null;
        List lineData = null;
        final List results = new ArrayList();
        final Map columnMD = new LinkedHashMap();

        try {
            isr = new InputStreamReader(theStream);
            br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter.charAt(0), qualifier.charAt(0), PZConstants.SPLITLINE_SIZE_INIT);
                for (int i = 0; i < lineData.size(); i++) {
                    final ColumnMetaData cmd = new ColumnMetaData();
                    cmd.setColName((String) lineData.get(i));
                    results.add(cmd);
                }
                break;
            }
        } finally {
            if (lineData != null) {
                lineData.clear();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
        }

        columnMD.put(PZConstants.DETAIL_ID, results);
        columnMD.put(PZConstants.COL_IDX, buidColumnIndexMap(results));

        return columnMD;
    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param line
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return ArrayList - ColumnMetaData
     */
    public static Map getColumnMDFromFile(final String line, final char delimiter, final char qualifier) throws Exception {
        List lineData = null;
        final List results = new ArrayList();
        final Map columnMD = new LinkedHashMap();

        lineData = splitLine(line, delimiter, qualifier, PZConstants.SPLITLINE_SIZE_INIT);
        for (int i = 0; i < lineData.size(); i++) {
            final ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColName((String) lineData.get(i));
            results.add(cmd);
        }

        columnMD.put(PZConstants.DETAIL_ID, results);
        columnMD.put(PZConstants.COL_IDX, buidColumnIndexMap(results));

        return columnMD;
    }

    /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param theFile
     * @param delimiter
     * @param qualifier
     * @exception Exception
     * @return ArrayList - ColumnMetaData
     */
    public static List getColumnMDFromFile(final File theFile, final String delimiter, final String qualifier) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        String line = null;
        List lineData = null;
        final List results = new ArrayList();

        try {
            fr = new FileReader(theFile);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter.charAt(0), qualifier.charAt(0), PZConstants.SPLITLINE_SIZE_INIT);
                for (int i = 0; i < lineData.size(); i++) {
                    final ColumnMetaData cmd = new ColumnMetaData();
                    cmd.setColName((String) lineData.get(i));
                    results.add(cmd);
                }
                break;
            }
        } finally {
            if (lineData != null) {
                lineData.clear();
            }
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
        return results;
    }

    /**
     * @param columnName
     * @param columnMD -
     *            vector of ColumnMetaData objects
     * @return int - position of the column in the file
     * @throws NoSuchElementException
     * @deprecated surely not...
     */
    public static int findColumn(final String columnName, final List columnMD) {
        for (int i = 0; i < columnMD.size(); i++) {
            final ColumnMetaData cmd = (ColumnMetaData) columnMD.get(i);
            if (cmd.getColName().equalsIgnoreCase(columnName)) {
                return i;
            }
        }

        throw new NoSuchElementException("Column Name: " + columnName + " does not exist");
    }

    /**
     * Determines if the given line is the first part of a multiline record
     *
     * @param chrArry -
     *            char data of the line
     * @param delimiter -
     *            delimiter being used
     * @param qualifier -
     *            qualifier being used
     * @return boolean
     * @deprecated use the char version
     */
    public static boolean isMultiLine(final char[] chrArry, final String delimiter, final String qualifier) {
        return isMultiLine(chrArry, delimiter != null ? delimiter.charAt(0) : 0, qualifier != null ? qualifier.charAt(0) : 0);
    }

    /**
     * Determines if the given line is the first part of a multiline record
     *
     * @param chrArry -
     *            char data of the line
     * @param delimiter -
     *            delimiter being used
     * @param qualifier -
     *            qualifier being used
     * @return boolean
     */
    public static boolean isMultiLine(final char[] chrArry, final char delimiter, final char qualifier) {

        // check if the last char is the qualifier, if so then this a good
        // chance it is not multiline
        if (chrArry[chrArry.length - 1] != qualifier) {
            // could be a potential line break
            boolean qualiFound = false;
            for (int i = chrArry.length - 1; i >= 0; i--) {
                // check to see if we can find a qualifier followed by a
                // delimiter
                // remember we are working are way backwards on the line
                if (qualiFound) {
                    if (chrArry[i] == ' ') {
                        continue;
                    } else {
                        // not a space, if this char is the delimiter, then we
                        // have a line break in the record
                        if (chrArry[i] == delimiter) {
                            return true;
                        }
                        qualiFound = false;
                        continue;
                    }
                } else if (chrArry[i] == delimiter) {
                    // if we have a delimiter followed by a qualifier, then we
                    // have moved on to a new element and this could not be multiline.
                    // start a new loop here in case there is
                    // space between the delimiter and qualifier
                    for (int j = i - 1; j >= 0; j--) {
                        if (chrArry[j] == ' ') {
                            continue;
                        } else if (chrArry[j] == qualifier) {
                            return false;
                        }
                        break;
                    }

                } else if (chrArry[i] == qualifier) {
                    qualiFound = true;
                }
            }
        } else {
            // we have determined that the last char on the line is a qualifier.
            // This most likely means
            // that this is not multiline, however we must account for the
            // following scenario
            // data,data,"
            // data
            // /data"
            for (int i = chrArry.length - 1; i >= 0; i--) {
                if (i == chrArry.length - 1 || chrArry[i] == ' ') {
                    // skip the first char, or any spaces we come across between
                    // the delimiter and qualifier
                    continue;
                }
                if (chrArry[i] == delimiter) {
                    return true;
                }
                break;
            }
        }

        return false;
    }

    /**
     * Returns a map with the MD id's and their record lengths. This is used for
     * fixed length parsing
     *
     * @param columnMD
     * @return Map
     */
    public static Map calculateRecordLengths(final Map columnMD) {
        final Map recordLengths = new HashMap();
        List cmds = null;

        final Iterator columnMDIt = columnMD.keySet().iterator();
        while (columnMDIt.hasNext()) {
            final String key = (String) columnMDIt.next();
            if (key.equals(PZConstants.DETAIL_ID) || key.equals(PZConstants.COL_IDX)) {
                cmds = (List) columnMD.get(PZConstants.DETAIL_ID);
            } else {
                cmds = ((XMLRecordElement) columnMD.get(key)).getColumns();
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
     * Returns the key to the list of ColumnMetaData objects. Returns the
     * correct MetaData per the mapping file and the data contained on the line
     *
     *
     * @param columnMD
     * @param line
     * @return List - ColumMetaData
     * @deprecated Moved to FixedWidthParserUtils.getCMDKey()
     *
     */
    public static String getCMDKeyForFixedLengthFile(final Map columnMD, final String line) {
        if (columnMD.size() == 1) {
            // no <RECORD> elments were specifed for this parse, just return the
            // detail id
            return PZConstants.DETAIL_ID;
        }
        final Iterator keys = columnMD.keySet().iterator();
        // loop through the XMLRecordElement objects and see if we need a
        // different MD object
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            if (key.equals(PZConstants.DETAIL_ID) || key.equals(PZConstants.COL_IDX)) {
                continue; // skip this key will be assumed if none of the
                // others match
            }
            final XMLRecordElement recordXMLElement = (XMLRecordElement) columnMD.get(key);

            if (recordXMLElement.getEndPositition() > line.length()) {
                // make sure our substring is not going to fail
                continue;
            }
            final int subfrm = recordXMLElement.getStartPosition() - 1; // convert
            // to 0
            // based
            final int subto = recordXMLElement.getEndPositition();
            if (line.substring(subfrm, subto).equals(recordXMLElement.getIndicator())) {
                // we found the MD object we want to return
                return key;
            }

        }

        // must be a detail line
        return PZConstants.DETAIL_ID;

    }

    /**
     * Returns the key to the list of ColumnMetaData objects. Returns the
     * correct MetaData per the mapping file and the data contained on the line
     *
     *
     * @param columnMD
     * @param lineElements
     * @return List - ColumMetaData
     */
    public static String getCMDKeyForDelimitedFile(final Map columnMD, final List lineElements) {
        if (columnMD.size() == 1) {
            // no <RECORD> elments were specifed for this parse, just return the
            // detail id
            return PZConstants.DETAIL_ID;
        }
        final Iterator keys = columnMD.keySet().iterator();
        // loop through the XMLRecordElement objects and see if we need a
        // different MD object
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            if (key.equals(PZConstants.DETAIL_ID) || key.equals(PZConstants.COL_IDX)) {
                continue; // skip this key will be assumed if none of the
                // others match
            }
            final XMLRecordElement recordXMLElement = (XMLRecordElement) columnMD.get(key);

            if (recordXMLElement.getElementNumber() > lineElements.size()) {
                // make sure the element referenced in the mapping exists
                continue;
            }
            final String lineElement = (String) lineElements.get(recordXMLElement.getElementNumber() - 1);
            if (lineElement.equals(recordXMLElement.getIndicator())) {
                // we found the MD object we want to return
                return key;
            }

        }

        // must be a detail line
        return PZConstants.DETAIL_ID;
    }

    /**
     * Returns a list of ColumMetaData objects for the given key
     *
     * @param key
     * @param columnMD
     * @return List
     */
    public static List getColumnMetaData(final String key, final Map columnMD) {
        if (key == null || key.equals(PZConstants.DETAIL_ID) || key.equals(PZConstants.COL_IDX)) {
            return (List) columnMD.get(PZConstants.DETAIL_ID);
        }

        return ((XMLRecordElement) columnMD.get(key)).getColumns();
    }

    /**
     * Use this method to find the index of a column.
     *
     * @author Benoit Xhenseval
     * @param key
     * @param columnMD
     * @param colName
     * @return -1 if it does not find it
     */
    public static int getColumnIndex(final String key, final Map columnMD, final String colName) {
        int idx = -1;
        if (key != null && !key.equals(PZConstants.DETAIL_ID) && !key.equals(PZConstants.COL_IDX)) {
            // if ("header".equals(key)) {
            // System.out.println("Columsn====header == "+ ((XMLRecordElement)
            // columnMD.get(key)).getColumns());
            // }
            idx = ((XMLRecordElement) columnMD.get(key)).getColumnIndex(colName);
        } else if (key == null || key.equals(PZConstants.DETAIL_ID)) {
            final Map map = (Map) columnMD.get(PZConstants.COL_IDX);
            // System.out.println("Map == " + map);
            // System.out.println("look for == " + colName);
            idx = ((Integer) map.get(colName)).intValue();
            // System.out.println("-------------> " + idx);
        }

        if (idx < 0) {
            throw new NoSuchElementException("Column " + colName + " does not exist, check case/spelling. key:" + key);
        }
        return idx;
    }

    /**
     * Create an InputStream based on a File.
     *
     * @param file
     *            The file.
     * @return the InputStream.
     * @throws FileNotFoundException
     */
    public static InputStream createInputStream(final File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("null not allowed");
        }
        return new FileInputStream(file);
    }

    /**
     * Closes the given reader
     *
     * @param reader
     *
     */
    public static void closeReader(final Reader reader) {
        try {
            reader.close();
        } catch (final Exception ignore) {
        }
    }

    /**
     * Closes the given reader
     *
     * @param reader
     *
     */
    public static void closeReader(final InputStream reader) {
        try {
            reader.close();
        } catch (final Exception ignore) {
        }
    }

    /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     *
     * <pre>
     *                StringUtils.padding(0, 'e')  = &quot;&quot;
     *                StringUtils.padding(3, 'e')  = &quot;eee&quot;
     *                StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>
     * Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
     * Supplementary Characters</a> as they require a pair of <code>char</code>s
     * to be represented. If you are needing to support full I18N of your
     * applications consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param repeat
     *            number of times to repeat delim
     * @param padChar
     *            character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException
     *             if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    public static String padding(final int repeat, final char padChar) {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    /**
     * Build a map of name/position based on a list of ColumnMetaData.
     *
     * @author Benoit Xhenseval
     * @param columns
     * @return a new Map
     */
    public static Map buidColumnIndexMap(final List columns) {
        Map map = null;
        if (columns != null && !columns.isEmpty()) {
            map = new HashMap();
            int idx = 0;
            for (final Iterator it = columns.iterator(); it.hasNext(); idx++) {
                final ColumnMetaData meta = (ColumnMetaData) it.next();
                map.put(meta.getColName(), new Integer(idx));
            }
        }
        return map;
    }
    
    /**
     * Removes chars from the String that could not 
     * be parsed into a Long value
     *
     *      StringUtils.stripNonLongChars("1000.25") = "1000"
     *
     * Method will truncate everything to the right of the decimal
     * place when encountered.
     *  
     * @param value
     * @return String
     */
    public static String stripNonLongChars(final String value) {
        final StringBuffer newString = new StringBuffer();
        
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == '.') {
                //stop if we hit a decimal point
                break;
            } else if (c >= '0' && c <= '9' || c == '-') {
                newString.append(c);
            }
        }
        // check to make sure we do not have a single length string with
        // just a minus sign
        final int sLen = newString.length();
        final String s = newString.toString();
        if (sLen == 0 || (sLen == 1 && s.equals("-"))) {
           return "0";
        }
        
        return newString.toString();
    }
    
    /**
     * Removes chars from the String that could not 
     * be parsed into a Double value
     * 
     * @param value
     * @return String
     */
    public static String stripNonDoubleChars(final String value) {
        final StringBuffer newString = new StringBuffer();
        
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c >= '0' && c <= '9' || c == '-'
                    || c == '.') {
                newString.append(c);
            }
        }
        final int sLen = newString.length();
        final String s = newString.toString();
        if (sLen == 0 || (sLen == 1 && s.equals(".")) || (sLen == 1 && s.equals("-"))) {
            return "0";
        }
        
        return newString.toString();
    }
    
    /**
     * Retrieves the conversion table for use with the getObject()
     * method in IDataSet
     * 
     * @throws IOException
     * @return Properties
     *              Properties contained in the pzconvert.properties file
     */
    public static Properties loadConvertProperties() throws IOException{
        final Properties pzConvertProps = new Properties();
        final URL url = ParserUtils.class.getClassLoader().getResource("pzconvert.properties");
        pzConvertProps.load(url.openStream());
        
        return pzConvertProps;        
    }
    
    /**
     * Converts a String value to the appropriate Object via
     * the correct net.sf.pzfilereader.converter.PZConverter implementation
     * 
     * @param classXref
     *             Properties holding class cross reference
     * @param value
     *             Value to be converted to the Object
     * @param typeToReturn
     *             Type of object to be returned
     * @throws PZConvertExeption
     * @return Object
     */
    public static Object runPzConverter(final Properties classXref, final String value, final Class typeToReturn) {
        final String sConverter = classXref.getProperty(typeToReturn.getName());
        if (sConverter == null) {
            throw new PZConvertException (typeToReturn.getName() + " is not registered in pzconvert.properties");
        }
        try {
            final PZConverter pzconverter = (PZConverter)Class.forName(sConverter).newInstance();
            return pzconverter.convertValue(value);
        } catch(IllegalAccessException ex) {
            throw new PZConvertException(ex);
        } catch(InstantiationException ex) {
            throw new PZConvertException(ex);
        } catch(ClassNotFoundException ex) {
            throw new PZConvertException(ex);
        }
    }
    
   
    //LEAVE AS A REFERENCE FOR POSSIBLE LATER USE
   /* public static List splitLineWithBuf(String line, final char delimiter, char qualifier, int initialSize) {
        List list = new ArrayList(initialSize);

        if (delimiter == 0) {
            list.add(line);
            return list;
        } else if (line == null) {
            return list;
        }

        final String trimmedLine = line.trim();
        int size = trimmedLine.length();

        if (size == 0) {
            list.add("");
            return list;
        }

        boolean insideQualifier = false;
        char previousChar = 0;
        boolean blockWasInQualifier = false;
        StringBuffer buf = new StringBuffer(32);

        // final String doubleQualifier = String.valueOf(qualifier) +
        // String.valueOf(qualifier);
        for (int i = 0; i < size; i++) {
            final char currentChar = trimmedLine.charAt(i);
            if (currentChar != delimiter && currentChar != qualifier) {
                previousChar = currentChar;
                if (' ' != currentChar || insideQualifier || buf.length() > 0) {
                    buf.append(currentChar);
                }
                continue;
            }

            if (currentChar == delimiter) {
                // we've found the delimiter (eg ,)
                if (!insideQualifier) {
                    // String trimmed = trimmedLine.substring(startBlock,
                    // endBlock > startBlock ? endBlock : startBlock + 1);
                    String trimmed = buf.toString();
                    if (!blockWasInQualifier) {
                        trimmed = trimmed.trim();
                        // trimmed = trimmed.replaceAll(doubleQualifier,
                        // String.valueOf(qualifier));
                    }

                    if (trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
                        list.add("");
                    } else {
                        list.add(trimmed);
                    }
                    blockWasInQualifier = false;
                    buf.delete(0, buf.length());
                } else if (buf.length() != 1 || buf.charAt(0) != qualifier) {
                    buf.append(currentChar);
                } else {
                    buf.delete(0, buf.length());
                    insideQualifier = false;
                    list.add("");
                }
            } else if (currentChar == qualifier) {
                if (!insideQualifier && previousChar != qualifier) {
                    if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
                        insideQualifier = true;
                        int l = buf.length();
                        if (l > 0) {
                            buf.delete(0, l); // just entered a
                            // qualifier, remove
                            // whatever was
                        }
                    } else {
                        buf.append(currentChar);
                    }
                } else {
                    insideQualifier = false;
                    blockWasInQualifier = true;
                    if (previousChar == qualifier) {
                        buf.append(qualifier);
                        insideQualifier = true;
                        previousChar = 0;
                        continue;
                    }
                    // last column (e.g. finishes with ")
                    if (i == size - 1) {
                        // list.add(trimmedLine.substring(startBlock, size -
                        // 1));
                        list.add(buf.toString());
                        buf.delete(0, buf.length());
                    }
                }
            }
            previousChar = currentChar;
        }

        if (buf.length() > 0) {
            list.add(buf.toString().trim());
        } else if (trimmedLine.charAt(size - 1) == delimiter) {
            list.add("");
        }

        return list;
    }*/
}
