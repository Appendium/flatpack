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
package net.sf.pzfilereader.util;

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

import net.sf.pzfilereader.structure.ColumnMetaData;
import net.sf.pzfilereader.xml.XMLRecordElement;

/**
 * @author zepernick Static utilities that are used to perform parsing in the
 *         DataSet class These can also be used for low level parsing, if not
 *         wishing to use the DataSet class.
 * @version 2.0
 */
public final class ParserUtils {
    private ParserUtils() {
    }

    /**
     * @deprecated should only use the splitLine with a CHAR.
     * @param line
     * @param delimiter
     * @param qualifier
     * @return
     */
    public static List splitLine(String line, final String delimiter, final String qualifier) {
        return splitLine(line, delimiter != null ? delimiter.charAt(0) : 0, qualifier != null ? qualifier.charAt(0) : 0);
    }

    /**
     * Returns an ArrayList of items in a delimited string. If there is no
     * qualifier around the text, the qualifier parameter can be left null, or
     * empty. There should not be any line breaks in the string. Each line of
     * the file should be passed in individually.
     * 
     * @param line -
     *            String of data to be parsed
     * @param delimiter -
     *            Delimiter seperating each element
     * @param qualifier -
     *            qualifier which is surrounding the text
     * @return ArrayList
     */
    public static List splitLine(String line, final char delimiter, final char qualifier) {
        final ArrayList list = new ArrayList();
        // String temp = "";
        boolean beginQualifier = false;
        // this will be used for delimted files that have some items qualified
        // and some items dont
        boolean beginNoQualifier = false;
        StringBuffer sb = new StringBuffer();

        // trim hard leading spaces at the begining of the line
        line = lTrim(line);
        for (int i = 0; i < line.length(); i++) {
            final String remainderOfLine = line.substring(i); // data of the
            // line which has not yet been read
            // check to see if there is a text qualifier
            final char currentChar = line.charAt(i);
//            final String currentString = String.valueOf(currentChar);
            if (qualifier > 0) {
                if (currentChar == qualifier && !beginQualifier && !beginNoQualifier) {
                    // begining of a set of data
                    beginQualifier = true;
                } else if (!beginQualifier && !beginNoQualifier && currentChar != qualifier
                        && lTrim(remainderOfLine).charAt(0) != qualifier) {
                    // try to account for empty space before qualifier starts
                    // we have not yet begun a qualifier and the char we are on
                    // is NOT
                    // a qualifier. Start reading data
                    beginNoQualifier = true;
                    // make sure that this is not just an empty column with no
                    // qualifiers. ie
                    // "data",,"data"
                    if (currentChar == delimiter) {
                        list.add(sb.toString());
                        sb.delete(0, sb.length());
                        beginNoQualifier = false;
                        continue;// grab the next char
                    }
                    sb.append(currentChar);
                } else if (!beginNoQualifier && currentChar == qualifier && beginQualifier
                        && (i == line.length() - 1 || lTrim(remainderOfLine.substring(1)).length() == 0
                        // this will be true on empty undelmited columns at the
                        // end of theline
                        || lTrimKeepTabs(remainderOfLine).charAt(1) == delimiter)) {
                    // end of a set of data that was qualified
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                    beginQualifier = false;
                    // add to "i" so we can get past the qualifier, otherwise it
                    // is read into a set
                    // of data which
                    // may not be qualified. Find out how many spaces to the
                    // delimiter
                    final int offset = getDelimiterOffset(line, i, delimiter) - 1;
                    // subtract 1 since i is going to get incremented again at
                    // the top of the loop
                    if (offset < 1) {
                        i++;
                    } else {
                        i += offset;
                    }
                } else if (beginNoQualifier && currentChar == delimiter) {
                    // check to see if we are done with an element that was not
                    // being qulified
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                    beginNoQualifier = false;
                } else if (beginNoQualifier || beginQualifier) {
                    // getting data in a NO qualifier element or qualified
                    // element
                    sb.append(currentChar);
                }

            } else {
                // not using a qualifier. Using a delimiter only
                if (currentChar == delimiter) {
                    list.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(currentChar);
                }
            }
        }

        // + this needs to be revisited...
        final String trimmed = sb.toString().trim();
        // remove the ending text qualifier if needed
        //only if the last element was truly qualified
        if (beginQualifier && qualifier > 0 && trimmed.length() > 0) {
            if (trimmed.charAt(trimmed.length() - 1) == qualifier) {
               // System.out.println(">>>>>>>Triming Off Qualifier");
                final String s = trimmed.substring(0, trimmed.length() - 1);
                sb.delete(0, sb.length());
                sb.append(s);
            }
        }

        final String trimmed2 = line.trim();
        int lengthLeft = trimmed2.length();
        if (qualifier <= 0 || beginQualifier || beginNoQualifier || lengthLeft > 0
                && trimmed2.charAt(lengthLeft - 1) == delimiter) {
            // also account for a delimiter with an empty column at the end that
            // was not qualified
            // check to see if we need to add the last column in..this will
            // happen on empty columns
            // add the last column
            list.add(sb.toString());
        }

        sb = null;

        list.trimToSize();
        
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
            // idx++;
            // idx-=start;
            idx -= start - 1;
        }
        return idx;

        // int offset = 0;
        // for (int i = start; i < line.length(); i++) {
        // offset++;
        // if (line.substring(i, i + 1).equals(delimiter)) {
        // if (offset != idx) {
        // System.out.println("String [" + line + "] start:" + start + "(" +
        // line.charAt(start) + ") delim ["
        // + delimiter + "] length:" + delimiter.length() + " Old:" + offset + "
        // new:" + idx);
        // }
        //
        // return offset;
        // }
        // }
        // return -1;
    }

    /**
     * Removes empty space from the begining of a string
     * 
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrim(final String value) {
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
        //        
        //        
        // final StringBuffer returnVal = new StringBuffer();
        // boolean gotAChar = false;
        //
        // for (int i = 0; i < value.length(); i++) {
        // if (value.substring(i, i + 1).trim().length() == 0 && !gotAChar) {
        // continue;
        // } else {
        // gotAChar = true;
        // returnVal.append(value.substring(i, i + 1));
        // }
        // }
        //
        // return returnVal.toString();
        //
    }

    /**
     * Removes empty space from the begining of a string, except for tabs
     * 
     * @param value -
     *            to be trimmed
     * @return String
     */
    public static String lTrimKeepTabs(final String value) {
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
        // final StringBuffer returnVal = new StringBuffer();
        // boolean gotAChar = false;
        //
        // for (int i = 0; i < value.length(); i++) {
        // if (!value.substring(i, i + 1).equals("\t") && value.substring(i, i +
        // 1).trim().length() == 0 && !gotAChar) {
        // continue;
        // } else {
        // gotAChar = true;
        // returnVal.append(value.substring(i, i + 1));
        // }
        // }
        //
        // return returnVal.toString();

    }

    /**
     * Removes a single string character from a given string
     * 
     * @param character -
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
        // final StringBuffer s = new StringBuffer();
        // for (int i = 0; i < theString.length(); i++) {
        // if (theString.substring(i, i + 1).equalsIgnoreCase(character)) {
        // continue;
        // }
        // s.append(theString.substring(i, i + 1));
        // }
        //
        // return s.toString();

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
            // fr = new FileReader(theFile);
            // br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                lineData = splitLine(line, delimiter.charAt(0), qualifier.charAt(0));
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

        lineData = splitLine(line, delimiter, qualifier);
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

                lineData = splitLine(line, delimiter.charAt(0), qualifier.charAt(0));
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
                        // have a line break
                        // in the record
                        if (chrArry[i] == delimiter) {
                            return true;
                        }
                        qualiFound = false;
                        continue;
                    }
                } else if (chrArry[i] == delimiter) {
                    // if we have a delimiter followed by a qualifier, then we
                    // have moved on
                    // to a new element and this could not be multiline. start a
                    // new loop here in case there is
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
                // make sure our substring is not going to fail
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
//        if (!file.exists()) {
//            throw new FileNotFoundException("file does not exist " + file.getAbsolutePath());
//        }
//        if (!file.canRead()) {
//            throw new FileNotFoundException("file cannot be read " + file.getAbsolutePath());
//        }
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
     *               StringUtils.padding(0, 'e')  = &quot;&quot;
     *               StringUtils.padding(3, 'e')  = &quot;eee&quot;
     *               StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
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
                // map.put(meta.getColName(), Integer.valueOf(idx)); breaks 1.4
                // compile
                map.put(meta.getColName(), new Integer(idx));
            }
        }
        return map;
    }
}
